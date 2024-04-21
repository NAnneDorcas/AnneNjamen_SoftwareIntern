package ee.taltech.inbankbackend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.vladislavgoltjajev.personalcode.locale.estonia.EstonianPersonalCodeValidator;
import ee.taltech.inbankbackend.config.DecisionEngineConstants;
import ee.taltech.inbankbackend.config.ClientInfo;
import ee.taltech.inbankbackend.config.AgeChecker;
import ee.taltech.inbankbackend.exceptions.InvalidAgeException;
import ee.taltech.inbankbackend.exceptions.InvalidLoanAmountException;
import ee.taltech.inbankbackend.exceptions.InvalidLoanPeriodException;
import ee.taltech.inbankbackend.exceptions.InvalidPersonalCodeException;
import ee.taltech.inbankbackend.exceptions.NoValidLoanException;
import jakarta.annotation.PostConstruct;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.core.type.TypeReference;

import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A service class that provides a method for calculating an approved loan amount and period for a customer.
 * The loan amount is calculated based on the customer's credit modifier,
 * which is determined by the last four digits of their ID code.
 */
@Service
public class DecisionEngine {

    // Used to check for the validity of the presented ID code.
    private final EstonianPersonalCodeValidator validator = new EstonianPersonalCodeValidator();
    private int creditModifier = 0;
    private int AgeCheck = 0; 
    private Map<String, List<ClientInfo>> clientData = new HashMap<>();
    private static final Logger logger = LogManager.getLogger(Decision.class);

    public DecisionEngine() {
        loadClientData();
    } //Class constructor which initialises the class and loads the client

    /**
     * Calculates the maximum loan amount and period for the customer based on their ID code,
     * the requested loan amount and the loan period.
     * The loan period must be between 12 and 60 months (inclusive).
     * The loan amount must be between 2000 and 10000€ months (inclusive).
     *
     * @param personalCode ID code of the customer that made the request.
     * @param loanAmount Requested loan amount
     * @param loanPeriod Requested loan period
     * @return A Decision object containing the approved loan amount and period, and an error message (if any)
     * @throws InvalidPersonalCodeException If the provided personal ID code is invalid
     * @throws InvalidLoanAmountException If the requested loan amount is invalid
     * @throws InvalidLoanPeriodException If the requested loan period is invalid
     * @throws NoValidLoanException If there is no valid loan found for the given ID code, loan amount and loan period
     * @throws InvalidAgeException If the Age received is invalid
     */

    private void loadClientData() {
        logger.info("Attempting to load client data...");
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data/client_data.json");
        if (inputStream == null) {
            logger.error("Failed to load client data: resource 'data/client_data.json' not found.");
            this.clientData = new HashMap<>();  // Initialize to prevent NullPointerException
            return;
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            this.clientData = mapper.readValue(inputStream, new TypeReference<Map<String, List<ClientInfo>>>() {});
            logger.info("Client data loaded successfully.");
        } catch (IOException e) {
            logger.error("Error reading or parsing the JSON file", e);
            this.clientData = new HashMap<>();  // Initialize to prevent NullPointerException
        }
    }
    
    public Decision calculateApprovedLoan(String personalCode, String age, Long loanAmount, int loanPeriod)
            throws InvalidPersonalCodeException, InvalidLoanAmountException, InvalidLoanPeriodException,
            NoValidLoanException, InvalidAgeException {
        try {
            verifyInputs(personalCode, age, loanAmount, loanPeriod);
        } catch (Exception e) {
            logger.error("Verification failed: {}", e.getMessage(), e);
            return new Decision(null, null, e.getMessage());
        }

        int outputLoanAmount;
        creditModifier = getCreditModifier(personalCode);
        logger.info("CreditModifier: {}", creditModifier);

        AgeCheck = checkAge(personalCode, age);
        logger.info("AgeCheck result: {}", AgeCheck);

        if (AgeCheck == 0) {
            throw new InvalidAgeException("Please enter valid age."); // Throw a more specific exception
        }

        if (creditModifier == 0) {
            throw new NoValidLoanException("No valid loan found!");
        }

        while (highestValidLoanAmount(loanPeriod) < DecisionEngineConstants.MINIMUM_LOAN_AMOUNT) {
            loanPeriod++;
        }

        if (loanPeriod <= DecisionEngineConstants.MAXIMUM_LOAN_PERIOD) {
            outputLoanAmount = Math.min(DecisionEngineConstants.MAXIMUM_LOAN_AMOUNT, highestValidLoanAmount(loanPeriod));
        } else {
            throw new NoValidLoanException("No valid loan found!");
        }

        return new Decision(outputLoanAmount, loanPeriod, null);
    }

    /**
     * Calculates the largest valid loan for the current credit modifier and loan period.
     *
     * @return Largest valid loan amount
     */
    private int highestValidLoanAmount(int loanPeriod) {
        return creditModifier * loanPeriod;
    }

    /**
     * Retrieves the credit modifier for a customer based on their personal ID code.
     * This method looks up the personal ID in the loaded client data and returns the associated credit modifier.
     * If no matching record is found or if data is unavailable, it returns a default value or indicates that data could not be found.
     *
     * @param personalId The ID code of the customer making the request.
     * @return The credit modifier associated with the given personal ID. Returns 0 if no data is found or if the client data is not loaded.
     */
    public int getCreditModifier(String personalId) {
        if (clientData == null || clientData.get("Clients_Information") == null) {
            return 0; // Or consider throwing a more specific exception to indicate that the data is not available
        }
        for (ClientInfo client : clientData.get("Clients_Information")) {
            if (client.getPersonalID().equals(personalId)) {
                return client.getCreditModifier();
            }
        }
        return 0; // Consider defining what a return value of 0 means in the context of your application or use an Optional<Integer> to handle this more gracefully.
    }

    public int checkAge(String personalId, String age) throws InvalidAgeException {
        if (clientData == null || clientData.get("Clients_Information") == null) {
            logger.info("clientData: {}", clientData);
            return 0; // Data not loaded or no clients available
        }
        
        for (ClientInfo client : clientData.get("Clients_Information")) {
            if (client.getPersonalID().equals(personalId)) {
                String currentAge = client.getAge();
                if (currentAge.isEmpty()) {
                    if (age.isEmpty()) {
                        throw new InvalidAgeException("Age is required for first-time users.");
                    } else {
                        client.setAge(age); // Set the age if provided
                        
                        //saveClientData();  // Save changes to a remote database
                        return 1;
                    }
                } else if (!currentAge.equals(age)) {
                    throw new InvalidAgeException("Existing age data conflict. Please contact support.");
                }                
                return 1;
            }
        }
        return 0; // No client found with the given ID
    }
    
    public void saveClientData() {
       /**
        * Saves the clientData into a database
        */
    }

    public String getAgeByPersonalCode(String personalId) {
        if (clientData != null && clientData.get("Clients_Information") != null) {
            for (ClientInfo client : clientData.get("Clients_Information")) {
                if (client.getPersonalID().equals(personalId)) {
                    return client.getAge();
                }
            }
        }
        return null;  // Return null if no age or personal ID is found
    }    

    /**
     * Verify that all inputs are valid according to business rules.
     * If inputs are invalid, then throws corresponding exceptions.
     *
     * @param personalCode Provided personal ID code
     * @param loanAmount Requested loan amount
     * @param loanPeriod Requested loan period
     * @throws InvalidPersonalCodeException If the provided personal ID code is invalid
     * @throws InvalidLoanAmountException If the requested loan amount is invalid
     * @throws InvalidLoanPeriodException If the requested loan period is invalid
     */
    private void verifyInputs(String personalCode, String age, Long loanAmount, int loanPeriod)
            throws InvalidPersonalCodeException, InvalidLoanAmountException, InvalidLoanPeriodException, InvalidAgeException {

        if (!validator.isValid(personalCode)) {
            throw new InvalidPersonalCodeException("Invalid personal ID code!");
        }
        if (!(DecisionEngineConstants.MINIMUM_LOAN_AMOUNT <= loanAmount)
                || !(loanAmount <= DecisionEngineConstants.MAXIMUM_LOAN_AMOUNT)) {
            throw new InvalidLoanAmountException("Invalid loan amount!");
        }
        if (!(DecisionEngineConstants.MINIMUM_LOAN_PERIOD <= loanPeriod)
                || !(loanPeriod <= DecisionEngineConstants.MAXIMUM_LOAN_PERIOD)) {
            throw new InvalidLoanPeriodException("Invalid loan period!");
        }
        if (!AgeChecker.isAgeApproved(age)) {
            throw new InvalidAgeException("Invalid Age. Please retry");
        }
    }
}
