# Project Documentation

## Ticket Review

### TICKET-101: Decision Engine Implementation Review

To ensure the successful implementation of TICKET-101, we reviewed the code against the requirements specified in the ticket description. Here are the findings:

#### Requirements and Implementation:
1. **Decision Engine Functionality**:
   - Calculates the maximum loan amount and period based on personal code, loan amount, and loan period.
   - Determines the loan amount using the credit modifier derived from the last four digits of the personal code.
   - Ensures that the loan period is between 12 and 60 months and that the loan amount is between €2000 and €10000.
   - Adheres to the credit score calculation algorithm specified.
   - Blocks loan approval if the credit score is less than 1.

2. **Current Implementation**:
   - The `calculateApprovedLoan` method successfully handles the calculation logic based on the inputs.
   - Input validation is robust, verifying the validity of the personal code and the specified ranges for the loan amount and period.
   - The code structure is clear, and the logical flow is well maintained.

3. **Identified Issues**:
   - The credit modifier is incorrectly derived from the personal code instead of fetching it from InBank's client data database.

4. **Recommendations for Improvement**:
   - Implement fetching of the credit modifier directly from the bank's database to ensure accuracy.
   - Consider refinements to adhere to SOLID principles for better code maintainability.
   - Explore options for recalibrating the loan period to provide additional benefits to users.
   - Enhance the decision engine by allowing refinancing options when the loan amount exceeds the maximum threshold.

### TICKET-102: Age Constraint Implementation

#### Frontend Changes:
- Replaced the slider system with a textbox for both the Loan Amount and Loan Period.
- Added a list tile to handle the date of birth input.
- Updated `api_service` to parse the date of birth to the backend.
- Modified existing tests to accommodate the new changes and ensure they pass.

#### Backend Changes:
- Introduced `InvalidAgeException` in exceptions for handling age-related errors.
- Implemented `AgeChecker` in configs to verify client's age based on the provided data.
- Added `ClientInfo` class in configs to manage client data.
- Updated `DecisionEngine.java` to:
  - Load client data from `main/resources/data/client_data.json`.
  - Support the AgeChecker feature.
  - Enhance the `calculateApprovedLoan` function to accommodate new requirements.
- Modified `DecisionEngineController.java` and `DecisionRequest.java` to integrate the new age constraint checks.

## Conclusion

The implementations for both TICKET-101 and TICKET-102 were reviewed. The code meets most of the specified requirements with some areas noted for improvement. The team is encouraged to address these areas in future updates to enhance functionality and maintainability.
