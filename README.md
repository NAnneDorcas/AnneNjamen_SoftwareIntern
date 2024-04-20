## AnneNjamen_SoftwareIntern
To validate the implementation of TICKET-101, we need to ensure that the code meets the 
requirements specified in the ticket description:
1.The decision engine should calculate the maximum loan amount and period based on the provided personal code, loan amount, and loan period.
2.The loan amount should be determined by the credit modifier derived from the last 
four digits of the personal code.
3. The loan period must be between 12 and 60 months, and the loan amount must be 
between ?2000 and ?10000.
4. The credit score calculation should adhere to the specified algorithm, where the 
credit modifier is divided by the loan amount and multiplied by the loan period.
5. If the credit score is less than 1, the loan should not be approved.
Based on the provided code, the implementation appears to fulfill these requirements:
 - The calculateApprovedLoan method takes in the personal code, loan amount, and 
loan period, and calculates the maximum loan amount based on the credit modifier.
- Input validation is performed to ensure that the personal code is valid and that the 
loan amount and period are within the specified ranges.
- The credit modifier is correctly determined based on the last four digits of the 
personal code.
- The loan amount calculation considers both the credit modifier and the loan period.
- The code handles scenarios where the calculated loan amount is below the 
minimum threshold.
Overall, the intern has implemented the basic functionality of the decision engine 
effectively. The code structure is clear, and the logic appears to be sound.
However, there are areas for improvement to adhere to SOLID principles and enhance 
maintainability:
 - Assume That CS=1
 - The program could also consider a recalibration of the loan period in order to provide 
the user that added advantage
 - We can improve the decision engine by refinancing the loan period when the loan 
amount is above the maximum loan amoun
