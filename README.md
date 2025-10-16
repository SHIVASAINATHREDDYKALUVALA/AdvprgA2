Overview of the RMIT Care Home Management System:
The RMIT Care Home Management System is a Java-based application designed to manage high-care patients efficiently. It enables managers and medical staff to visualize and control resident information, bed allocation, staff scheduling, prescriptions, and medication administration through GUI interface. The system complies with care facility regulations regarding shift management, staff responsibilities, and medication tracking. All critical operations are logged, serialized, and auditable.

Key Functionalities of the system:
1)	Manager Functions:
a.	Can add new residents to beds that are available in the wards
b.	Add or update staff details including passwords and shift schedules.
c.	Discharge residents and archive their full medical history.
d.	View and manage compliance to ensure regulations are followed.
2)	Doctor Functions
a.	Attach new prescriptions to patients specifying medicine names, doses, and administration times.
b.	Can view patient details and prescription history for auditing or modification.
3)	Nurse Functions
a.	Can move the residents between beds when necessary.
b.	Record medication administration details for each patient such as medicine name, dose, time, and nurse id.
c.	View assigned patients and shifts.
4)	System Features
a.	Color-coded gender representation in GUI:
i.	Blue for male and
ii.	Red for female.
b.	Logging of all critical actions with timestamp, staff ID, and activity description.
c.	Exception handling for any unauthorized actions.
d.	Serialization of patient and staff data.
e.	Compliance checking to ensure:
i.	Nurses are not assigned more than 8 hours per day.
ii.	Each nurse has two shifts per day.
iii.	A doctor is rostered for at least 1 hour daily.
Overview of architecture and components:
Staff: It is a base class for manager, doctor and nurse.
Patient: It stores personal and medical information, prescriptions, and administered medicine logs.
Bed: It represents an individual bed in a ward which will be linked to a patient.
CareHome: It is basically the core class which manages all of the entities, shift compliance and operations.
Prescription: Represents prescriptions and administration details.
GUI components: They are build using the JavaFX with FXML for the interface layout.
Log manager: It is the one who records all of the system activities with timestamps for auditing.

JUnit Testing:
Junit tests are created:

BedAllocationTest: Which ensures the patients are assigned only to vacant and correct beds.
MedicationLogTest: It verifies accurate logging of the medicines and exception handling for the unauthorized staff.
CareHomeComplianceTest: Checks the staff scheduling.
All tests passed successfully.







------------------------------------------------------------------------------------------------------------------------------------------------------------------------------






Refacroting report:
1.	Grouped models and exceptions logically instead of just placing all of them in the same folder.
2.	Used a centralized method for roster validation which is checkCompliance().
3.	Encapsulated the file handling into methods: saveToFile() and loadFromFile() to reduce code duplication.
4.	Added helper methods such as addNurse(), addDoctor() and addManager() for the staff allocation for validation.
5.	Added logic in allocateResidentToBed() in resident allocation, which ensures gender match and vacant bed check.
6.	Introduced BedNotFound and BedOccupied exceptions instead of null because of the readability enhancing.
7.	Added getAuditLog() method for audit retrieval which allows displaying the logs through text-based UI.
8.	Added nurseMoveResident() with validation for target bed for the resident movement functionality to ensure proper vacancy and authorixation checks.
9.	Added test for logging dose administration and verifying time stamps for the MedicationLogTest test case.

