import 'dart:convert';

import 'package:http/http.dart' as http;

// ApiService is a class that provides methods for making API calls
// to the decision engine.
class ApiService {
  final String _baseUrl = 'http://localhost:8080';
  String responseAmount = '';
  String responsePeriod = '';
  String responseError = '';
  http.Client httpClient;

  ApiService({http.Client? client}) : httpClient = client ?? http.Client();

  Future<String> fetchAge(String personalCode) async {
    try {
      final response = await httpClient.get(
        Uri.parse('$_baseUrl/loan/age/$personalCode'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return data['age'] ?? '';  // Return an empty string if age is not found
      } else {
        // Handle non-200 responses or provide a default age or error message
        return ''; // Return an empty string indicating no age found or there was an error
      }
    } catch (e) {
      // Log error or handle exceptions
      return ''; // In case of error return empty
    }
  }

  // requestLoanDecision sends a request to the API to get a loan decision
  // based on the provided personalCode, loanAmount, and loanPeriod.
  Future<Map<String, String>> requestLoanDecision(
      String personalCode, String age, int loanAmount, int loanPeriod) async {
    final response = await httpClient.post(
      Uri.parse('$_baseUrl/loan/decision'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({
        'personalCode': personalCode,
        'loanAmount': loanAmount,
        'age': age,
        'loanPeriod': loanPeriod,
      }),
    );

    try {
      // Decode the API response and update response data variables
      final responseData = jsonDecode(response.body) as Map<String, dynamic>;
      responseAmount = responseData['loanAmount'].toString();
      responsePeriod = responseData['loanPeriod'].toString();
      responseError = responseData['errorMessage'].toString();

      // Return the response data as a map, handling null values if necessary
      return {
        'loanAmount': responseAmount != 'null' ? responseAmount : '0',
        'loanPeriod': responsePeriod != 'null' ? responsePeriod : '0',
        'errorMessage': responseError != 'null' ? responseError : '',
      };
    } catch (e) {
      // An unexpected error occurred when querying the server,
      // so an error is displayed.
      return {
        'loanAmount': '0',
        'loanPeriod': '0',
        'errorMessage': 'An unexpected error occurred.',
      };
    }
  }
}
