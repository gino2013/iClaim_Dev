# Case Creation and Trial Calculation Transmission API

This project is the API development for "Case Creation and Trial Calculation Transmission," primarily designed to receive patient claim application data from medical institutions and exchange data with Cathay Life Insurance's system to complete the trial calculation of deductible amounts.

**Project Goals:**

* Provide a fast and convenient claim application channel for medical institutions.
* Automate the claim trial calculation process, reducing manual processing time.
* Ensure the accuracy and security of data transmission.

**Key Features:**

* **Data Validation:** Checks the completeness and correctness of input data.
* **Case Inquiry:** Queries the status of existing cases to determine whether it is a new or resubmitted case.
* **Document Verification:** Checks if required documents are prepared.
* **Data Conversion:** Converts FHIR data into the format required by Cathay Life Insurance's system.
* **Amount Trial Calculation:** Calls Cathay Life Insurance's API to perform deductible amount trial calculations.
* **Data Writing:** Writes case data, document details, and trial calculation results into the database.
* **Error Handling:** Logs detailed error messages for easy problem tracking and resolution.

**Technical Stack:**

* **Backend Language:** (Please fill in your backend language here, e.g., Python, Java, Node.js)
* **Database:** PostgreSQL
* **API Framework:** (Please fill in your API framework here, e.g., Flask, Spring Boot, Express.js)
* **Version Control:** Git

**Project Highlights:**

* **Modular Design:** Each function is modularized, making it easy to maintain and expand.
* **Detailed Logging:** Records detailed operation logs for easy problem tracking.
* **Comprehensive Error Handling:** Provides clear error messages for easy troubleshooting.

**How to Use:**

* **Environment Setup:** Please refer to the project documentation for environment setup.
* **API Documentation:** Please refer to the `openapi.yaml` file.
* **Testing:** Please refer to the test cases for API testing.

**Contribution:**

Contributions are welcome! Please submit issues or pull requests to help improve this project.

**License:**

MIT