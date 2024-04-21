package ee.taltech.inbankbackend.config;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AgeChecker {
    private static final Logger logger = LogManager.getLogger(AgeChecker.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final int MINIMUM_AGE_YEARS = 18;
    private static final int EXPECTED_LIFETIME_YEARS = 80;
    private static final int MAXIMUM_LOAN_PERIOD_YEARS = 5;  // Assuming 5 years (60 months) as the maximum loan period.
    private static final int MINIMUM_LOAN_PERIOD_YEARS = 1; // 12 months is one year

    public static boolean isAgeApproved(String dateOfBirthStr) {
        try {
            LocalDate dateOfBirth = LocalDate.parse(dateOfBirthStr, FORMATTER);
            LocalDate currentDate = LocalDate.now();
            int age = Period.between(dateOfBirth, currentDate).getYears();

            int maximumAgeForLoanApproval = EXPECTED_LIFETIME_YEARS - MAXIMUM_LOAN_PERIOD_YEARS;
            int minimumAgeForLoanApproval = MINIMUM_AGE_YEARS + MINIMUM_LOAN_PERIOD_YEARS;

            return age >= minimumAgeForLoanApproval && age <= maximumAgeForLoanApproval;
        } catch (DateTimeParseException e) {
            logger.error("Date parsing error with input: {}", dateOfBirthStr, e);
            return false; // or handle the error appropriately
        }
    }
}
