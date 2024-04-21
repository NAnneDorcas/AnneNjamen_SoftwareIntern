// This file defines a `LoanForm` widget which is a stateful widget
// that displays a loan application form.

import 'dart:math';

import 'package:flutter/material.dart';
import 'package:inbank_frontend/fonts.dart';
import 'package:inbank_frontend/widgets/national_id_field.dart';
import 'package:intl/intl.dart';
import '../api_service.dart';
import '../colors.dart';

// LoanForm is a StatefulWidget that displays a loan application form.
class LoanForm extends StatefulWidget {
  const LoanForm({Key? key}) : super(key: key);

  @override
  _LoanFormState createState() => _LoanFormState();
}

class _LoanFormState extends State<LoanForm> {
  final _formKey = GlobalKey<FormState>();
  final _apiService = ApiService();
  String _nationalId = '';
  String _age = '';
  int _loanAmount = 2500;
  int _loanPeriod = 36;
  int _loanAmountResult = 0;
  int _loanPeriodResult = 0;
  String _errorMessage = '';
  DateTime? _selectedDate;

  // Submit the form and update the state with the loan decision results.
  // Only submits if the form inputs are validated.
  void _submitForm() async {
    if (_formKey.currentState!.validate()) {
      // Only call the API when the form is valid and the submit button is pressed
      final result = await _apiService.requestLoanDecision(
          _nationalId, _age, _loanAmount, _loanPeriod);
      setState(() {
        _loanAmountResult = int.parse(result['loanAmount'].toString());
        _loanPeriodResult = int.parse(result['loanPeriod'].toString());
        _errorMessage = result['errorMessage'].toString();
      });
    } else {
      _loanAmountResult = 0;
      _loanPeriodResult = 0;
      _errorMessage = 'Validation failed';  // Set error message if form is not valid
    }
  }

  // Function to present the DatePicker and set the selected date
  void _selectDate(BuildContext context) async {
    final DateTime? picked = await showDatePicker(
      context: context,
      initialDate: _selectedDate ?? DateTime.now(),
      firstDate: DateTime(1900),
      lastDate: DateTime.now(),
    );
    if (picked != null && picked != _selectedDate) {
      setState(() {
        _selectedDate = picked;
        _age = DateFormat('dd.MM.yyyy').format(picked); // Store the date as a formatted string
      });
    }
  }

  @override
  void initState() {
    super.initState();
    // Attempt to fetch the user's age from the backend when initializing.
    fetchUserAge();
  }

  void fetchUserAge() async {
    try {
      String age = await _apiService.fetchAge(_nationalId);
      if (age.isNotEmpty) {
        setState(() {
          _age = age;
          _selectedDate = DateFormat('dd.MM.yyyy').parse(age); // Ensure you handle parsing correctly
        });
      }
    } catch (e) {
      // Handle exceptions, e.g., age not found which means user must input age
      print('Age not set in the backend, user must input age.');
    }
  }

  // Ensure that your age picker is conditional based on whether age is already fetched/set
  Widget ageInputField() {
    if (_age.isEmpty) {
      return ListTile(
        title: Text(
          _selectedDate == null
              ? 'Enter your birth date'
              : 'Birth Date: ${DateFormat('dd.MM.yyyy').format(_selectedDate!)}',
          style: TextStyle(
            color: Colors.white,
            fontSize: 16,
          ),
        ),
        trailing: Icon(Icons.calendar_today, color: Colors.white),
        onTap: () => _selectDate(context),
      );
    } else {
      return Container(); // Return an empty container or some indication that age is already set
    }
  }

  // Builds the application form widget.
  // The widget automatically queries the endpoint for the latest data
  // when a field is changed.
  @override
  Widget build(BuildContext context) {
    final screenWidth = MediaQuery.of(context).size.width;
    final formWidth = screenWidth / 3;
    const minWidth = 500.0;
    return Expanded(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          SizedBox(
            width: max(minWidth, formWidth),
            child: Form(
              key: _formKey,
              child: Column(
                children: [
                  FormField<String>(
                    builder: (state) {
                      return Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          NationalIdTextFormField(
                            onChanged: (value) {
                              setState(() {
                                _nationalId = value ?? '';
                                _submitForm();
                              });
                            },
                          ),
                        ],
                      );
                    },
                  ),
                  const SizedBox(height: 24.0),
                  ListTile(
                    title: Text(
                      _selectedDate == null
                          ? 'Enter your birth date'
                          : 'Birth Date: ${DateFormat('dd.MM.yyyy').format(_selectedDate!)}',
                      style: TextStyle(
                        color: Colors.white, // Apply white color for visibility
                        fontSize: 16, // Adjust font size as needed
                      ),
                    ),
                    trailing: Icon(Icons.calendar_today, color: Colors.white),
                    onTap: () => _selectDate(context),
                  ),
                  const SizedBox(height: 60.0),
                  TextFormField(
                    initialValue: _loanAmount.toString(),
                    decoration: InputDecoration(
                      labelText: 'Loan Amount (€)',
                      hintText: 'Enter amount between 2000 and 10000',
                      labelStyle: TextStyle(
                        color: Colors.white, // Adjust label color
                      ),
                      hintStyle: TextStyle(
                        color: Colors.white, // Adjust hint color
                      ),
                      enabledBorder: UnderlineInputBorder(      
                        borderSide: BorderSide(color: Colors.white),   
                      ),  
                      focusedBorder: UnderlineInputBorder(
                        borderSide: BorderSide(color: Colors.white),
                      ),
                      border: UnderlineInputBorder(
                        borderSide: BorderSide(color: Colors.white),
                      ),
                    ),
                    keyboardType: TextInputType.number,
                    cursorColor: Colors.white, // Adjust cursor color
                    style: TextStyle(
                      color: Colors.white, // Adjust text color inside the field
                    ),
                    onChanged: (value) => setState(() {
                      _loanAmount = int.tryParse(value) ?? 2000;
                    }),
                    validator: (value) {
                      if (value == null || value.isEmpty) return 'Please enter an amount';
                      final n = int.tryParse(value) ?? 0;
                      if (n < 2000 || n > 10000) return 'Enter a value between 2000 and 10000';
                      return null;
                    },
                  ),
                  const SizedBox(height: 24.0),
                  TextFormField(
                    initialValue: _loanPeriod.toString(),
                    decoration: InputDecoration(
                      labelText: 'Loan Period (months)',
                      hintText: 'Enter period between 12 and 60',
                      labelStyle: TextStyle(
                        color: Colors.white, // Adjust label color
                      ),
                      hintStyle: TextStyle(
                        color: Colors.white, // Adjust hint color
                      ),
                      enabledBorder: UnderlineInputBorder(      
                        borderSide: BorderSide(color: Colors.white),   
                      ),  
                      focusedBorder: UnderlineInputBorder(
                        borderSide: BorderSide(color: Colors.white),
                      ),
                      border: UnderlineInputBorder(
                        borderSide: BorderSide(color: Colors.white),
                      ),
                    ),
                    keyboardType: TextInputType.number,
                    cursorColor: Colors.white, // Adjust cursor color
                    style: TextStyle(
                      color: Colors.white, // Adjust text color inside the field
                    ),
                    onChanged: (value) => setState(() {
                      _loanPeriod = int.tryParse(value) ?? 12;
                    }),
                    validator: (value) {
                      if (value == null || value.isEmpty) return 'Please enter a period';
                      final n = int.tryParse(value) ?? 0;
                      if (n < 12 || n > 60) return 'Enter a value between 12 and 60';
                      return null;
                    },
                  ),
                  const SizedBox(height: 24.0),
                  ElevatedButton(
                  onPressed: _submitForm,
                  child: const Text('Submit'),
                ),
                ],
              ),
            ),
          ),
          const SizedBox(height: 16.0),
          Column(
            children: [
              Text(
                    'Approved Loan Amount: ${_loanAmountResult != 0 ? _loanAmountResult : "--"} €'),
                const SizedBox(height: 8.0),
                Text(
                    'Approved Loan Period: ${_loanPeriodResult != 0 ? _loanPeriodResult : "--"} months'),
                Visibility(
                    visible: _errorMessage.isNotEmpty,
                    child: Text(_errorMessage, style: errorMedium))
            ],
          ),
        ],
      ),
    );
  }
}
