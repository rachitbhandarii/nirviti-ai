use NirvitiAI;
show tables;

-- 1) Patient History with Transactions and Lab Tests
SELECT 
    p.patientID,
    CONCAT(p.firstName, ' ', p.lastName) AS patientName,
    p.age,
    p.gender,
    p.bloodGroup,
    p.allergies,
    d.doctorID,
    CONCAT(d.firstName, ' ', d.lastName) AS doctorName,
    d.specialization,
    dep.name AS departmentName,
    a.dateTime AS appointmentDate,
    a.status AS appointmentStatus,
    r.prescription,
    lab.testName,
    lt.result AS labTestResult,
    t.transactionID,
    t.amount AS transactionAmount,
    t.paymentMode,
    t.discount
FROM Patient p
LEFT JOIN Appointment a ON p.patientID = a.patientID
LEFT JOIN Doctor d ON a.doctorID = d.doctorID
LEFT JOIN Department dep ON d.departmentID = dep.departmentID
LEFT JOIN Records r ON p.patientID = r.patientID
LEFT JOIN LabTest lt ON r.recordID = lt.recordID
LEFT JOIN Laboratory lab ON lt.labID = lab.labID -- Corrected connection to fetch testName
LEFT JOIN Transaction t ON r.recordID = t.recordID -- Corrected link to transactions
ORDER BY a.dateTime DESC;

-- 2) Hospital Performance Summary
SELECT 
    d.doctorID,
    CONCAT(d.firstName, ' ', d.lastName) AS doctorName,
    d.specialization,
    COUNT(DISTINCT a.appointmentID) AS totalAppointments,
    SUM(CASE WHEN a.status = 'Completed' THEN 1 ELSE 0 END) AS completedAppointments,
    SUM(CASE WHEN a.status = 'Cancelled' THEN 1 ELSE 0 END) AS cancelledAppointments,
    COUNT(DISTINCT r.recordID) AS totalMedicalRecords,
    COUNT(DISTINCT lt.recordID) AS totalLabTests,
    AVG(t.amount) AS avgRevenue,
    SUM(t.amount) AS totalRevenue,
    MAX(t.amount) AS highestTransaction,
    MIN(t.amount) AS lowestTransaction
FROM Doctor d
LEFT JOIN Appointment a ON d.doctorID = a.doctorID
LEFT JOIN Records r ON d.doctorID = r.doctorID
LEFT JOIN LabTest lt ON r.recordID = lt.recordID
LEFT JOIN Transaction t ON r.recordID = t.recordID
GROUP BY d.doctorID, d.firstName, d.lastName, d.specialization
ORDER BY totalAppointments DESC;

-- 3) Categorizing Patients Based on Age Groups
SELECT 
    CASE 
        WHEN age BETWEEN 0 AND 12 THEN 'Child'
        WHEN age BETWEEN 13 AND 19 THEN 'Teenager'
        WHEN age BETWEEN 20 AND 35 THEN 'Young Adult'
        WHEN age BETWEEN 36 AND 60 THEN 'Middle-Aged Adult'
        ELSE 'Senior Citizen'
    END AS ageCategory,
    gender,
    bloodGroup,
    COUNT(*) AS patientCount
FROM Patient
GROUP BY ageCategory, gender, bloodGroup
ORDER BY patientCount DESC;

-- 4) Top 3 Highest-Earning Doctors Based on Patient Transactions
SELECT 
    d.doctorID,
    CONCAT(d.firstName, ' ', d.lastName) AS doctorName,
    d.specialization,
    d.experience,
    totalRevenue
FROM Doctor d
JOIN (
    SELECT 
        r.doctorID, 
        SUM(t.amount) AS totalRevenue
    FROM Records r
    JOIN Transaction t ON r.recordID = t.recordID
    GROUP BY r.doctorID
    ORDER BY totalRevenue DESC
    LIMIT 0, 3  -- Retrieves top 3 highest-earning doctors
) AS revenueSubquery ON d.doctorID = revenueSubquery.doctorID;

-- 5) Department-Wise Revenue and Patient Flow Analysis
SELECT 
    dep.departmentID,
    dep.name AS departmentName,
    COUNT(DISTINCT a.appointmentID) AS totalAppointments,
    COUNT(DISTINCT r.recordID) AS totalMedicalRecords,
    (SELECT COUNT(*) FROM LabTest lt 
     JOIN Records r2 ON lt.recordID = r2.recordID 
     JOIN Doctor d2 ON r2.doctorID = d2.doctorID 
     WHERE d2.departmentID = dep.departmentID) AS totalLabTests,
    (SELECT SUM(t.amount) FROM Transaction t 
     JOIN Appointment a2 ON t.patientID = a2.patientID 
     JOIN Doctor d3 ON a2.doctorID = d3.doctorID 
     WHERE d3.departmentID = dep.departmentID) AS totalRevenue
FROM Department dep
LEFT JOIN Doctor d ON dep.departmentID = d.departmentID
LEFT JOIN Appointment a ON d.doctorID = a.doctorID
LEFT JOIN Records r ON d.doctorID = r.doctorID
GROUP BY dep.departmentID, dep.name
ORDER BY totalRevenue DESC;

-- 6) Patients Who Have Had the Most Lab Tests
SELECT 
    p.patientID, 
    CONCAT(p.firstName, ' ', p.lastName) AS PatientName, 
    COUNT(lt.recordID) AS TotalLabTests
FROM Patient p
JOIN Records r ON p.patientID = r.patientID
JOIN LabTest lt ON r.recordID = lt.recordID
GROUP BY p.patientID, p.firstName, p.lastName
ORDER BY TotalLabTests DESC
LIMIT 5;

-- 7) Departments with the Most Doctors
SELECT 
    d.departmentID, 
    d.name AS DepartmentName, 
    COUNT(doc.doctorID) AS TotalDoctors
FROM Department d
JOIN Doctor doc ON d.departmentID = doc.departmentID
GROUP BY d.departmentID, d.name
ORDER BY TotalDoctors DESC;

-- 8) List of Beds That Are Currently Occupied
SELECT 
    b.bedNumber, 
    b.bedType, 
    b.wardNumber, 
    p.patientID, 
    CONCAT(p.firstName, ' ', p.lastName) AS PatientName
FROM Bed b
JOIN Patient p ON b.patientID = p.patientID
WHERE b.availabilityStatus = FALSE;

-- 9) Average Experience of Doctors in Each Department
SELECT 
    d.departmentID, 
    d.name AS DepartmentName, 
    ROUND(AVG(doc.experience), 2) AS AvgExperienceYears
FROM Department d
JOIN Doctor doc ON d.departmentID = doc.departmentID
GROUP BY d.departmentID, d.name
ORDER BY AvgExperienceYears DESC;

-- 10) Patients Who Have Used Insurance for Payment
SELECT DISTINCT 
    p.patientID, 
    CONCAT(p.firstName, ' ', p.lastName) AS PatientName, 
    p.insuranceNumber
FROM Patient p
JOIN Transaction t ON p.patientID = t.patientID
WHERE t.paymentMode = 'Insurance';

-- 11) Most Common Allergies Among Patients
SELECT 
    p.allergies, 
    COUNT(*) AS TotalPatients
FROM Patient p
WHERE p.allergies IS NOT NULL AND p.allergies <> ''
GROUP BY p.allergies
ORDER BY TotalPatients DESC
LIMIT 5;