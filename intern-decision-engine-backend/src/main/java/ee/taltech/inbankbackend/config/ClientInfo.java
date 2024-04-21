package ee.taltech.inbankbackend.config;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class representing client information related to loan decisions.
 * This class stores the personal ID, age, and credit modifier (CM) of a client.
 * The CM is used by the DecisionEngine to determine the loan amount and terms offered.
 */
public class ClientInfo {
    @JsonProperty("Personal_ID")
    private String personalID;
    
    @JsonProperty("Age")
    private String age; // Age is stored but currently not utilized in loan calculations.
    
    @JsonProperty("CM")
    private int creditModifier; // The credit modifier (CM) used to influence loan decisions.

    /**
     * Gets the personal ID of the client.
     * @return A string representing the personal ID of the client.
     */
    public String getPersonalID() {
        return personalID;
    }

    /**
     * Sets the personal ID of the client.
     * @param personalID The personal ID to set for this client.
     */
    public void setPersonalID(String personalID) {
        this.personalID = personalID;
    }

    /**
     * Gets the credit modifier for the client.
     * @return An integer representing the credit modifier.
     */
    public int getCreditModifier() {
        return creditModifier;
    }

    /**
     * Sets the credit modifier for the client.
     * @param creditModifier The credit modifier to set for this client.
     */
    public void setCreditModifier(int creditModifier) {
        this.creditModifier = creditModifier;
    }

    /**
     * Gets the age of the client.
     * @return A string representing the age of the client.
     */
    public String getAge() {
        return age;
    }

    /**
     * Sets the age of the client.
     * @param age The age to set for this client.
     */
    public void setAge(String age) {
        this.age = age;
    }
}