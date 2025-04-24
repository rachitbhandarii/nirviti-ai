package com.nirviti.ai;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/complex")
public class ComplexQueryController {

    private final SqlExecutorService sqlExecutorService;

    public ComplexQueryController(SqlExecutorService service) {
        this.sqlExecutorService = service;
    }

    /* Testing */

    /*@GetMapping("/getDoctors")
    public List<Map<String, Object>> getDoctors() {
        return sqlExecutorService.runSelect("SELECT * FROM doctor");
    }

    @GetMapping("/getPatients")
    public List<Map<String, Object>> getPatients() {
        return sqlExecutorService.runSelect("SELECT * FROM patient");
    }*/

    @GetMapping("/query1")
    public List<Map<String, Object>> getQuery1() {
        return sqlExecutorService.runSelect("SELECT \n" +
                "    p.patientID,\n" +
                "    CONCAT(p.firstName, ' ', p.lastName) AS patientName,\n" +
                "    p.age,\n" +
                "    p.gender,\n" +
                "    p.bloodGroup,\n" +
                "    p.allergies,\n" +
                "    d.doctorID,\n" +
                "    CONCAT(d.firstName, ' ', d.lastName) AS doctorName,\n" +
                "    d.specialization,\n" +
                "    dep.name AS departmentName,\n" +
                "    a.dateTime AS appointmentDate,\n" +
                "    a.status AS appointmentStatus,\n" +
                "    r.prescription,\n" +
                "    lab.testName,\n" +
                "    lt.result AS labTestResult,\n" +
                "    t.transactionID,\n" +
                "    t.amount AS transactionAmount,\n" +
                "    t.paymentMode,\n" +
                "    t.discount\n" +
                "FROM Patient p\n" +
                "LEFT JOIN Appointment a ON p.patientID = a.patientID\n" +
                "LEFT JOIN Doctor d ON a.doctorID = d.doctorID\n" +
                "LEFT JOIN Department dep ON d.departmentID = dep.departmentID\n" +
                "LEFT JOIN Records r ON p.patientID = r.patientID\n" +
                "LEFT JOIN LabTest lt ON r.recordID = lt.recordID\n" +
                "LEFT JOIN Laboratory lab ON lt.labID = lab.labID -- Corrected connection to fetch testName\n" +
                "LEFT JOIN Transaction t ON r.recordID = t.recordID -- Corrected link to transactions\n" +
                "ORDER BY a.dateTime DESC;");
    }

    @GetMapping("/query2")
    public List<Map<String, Object>> getQuery2() {
        return sqlExecutorService.runSelect("SELECT \n" +
                "    d.doctorID,\n" +
                "    CONCAT(d.firstName, ' ', d.lastName) AS doctorName,\n" +
                "    d.specialization,\n" +
                "    COUNT(DISTINCT a.appointmentID) AS totalAppointments,\n" +
                "    SUM(CASE WHEN a.status = 'Completed' THEN 1 ELSE 0 END) AS completedAppointments,\n" +
                "    SUM(CASE WHEN a.status = 'Cancelled' THEN 1 ELSE 0 END) AS cancelledAppointments,\n" +
                "    COUNT(DISTINCT r.recordID) AS totalMedicalRecords,\n" +
                "    COUNT(DISTINCT lt.recordID) AS totalLabTests,\n" +
                "    AVG(t.amount) AS avgRevenue,\n" +
                "    SUM(t.amount) AS totalRevenue,\n" +
                "    MAX(t.amount) AS highestTransaction,\n" +
                "    MIN(t.amount) AS lowestTransaction\n" +
                "FROM Doctor d\n" +
                "LEFT JOIN Appointment a ON d.doctorID = a.doctorID\n" +
                "LEFT JOIN Records r ON d.doctorID = r.doctorID\n" +
                "LEFT JOIN LabTest lt ON r.recordID = lt.recordID\n" +
                "LEFT JOIN Transaction t ON r.recordID = t.recordID\n" +
                "GROUP BY d.doctorID, d.firstName, d.lastName, d.specialization\n" +
                "ORDER BY totalAppointments DESC;\n");
    }

    @GetMapping("/query3")
    public List<Map<String, Object>> getQuery3() {
        return sqlExecutorService.runSelect("SELECT \n" +
                "    CASE \n" +
                "        WHEN age BETWEEN 0 AND 12 THEN 'Child'\n" +
                "        WHEN age BETWEEN 13 AND 19 THEN 'Teenager'\n" +
                "        WHEN age BETWEEN 20 AND 35 THEN 'Young Adult'\n" +
                "        WHEN age BETWEEN 36 AND 60 THEN 'Middle-Aged Adult'\n" +
                "        ELSE 'Senior Citizen'\n" +
                "    END AS ageCategory,\n" +
                "    gender,\n" +
                "    bloodGroup,\n" +
                "    COUNT(*) AS patientCount\n" +
                "FROM Patient\n" +
                "GROUP BY ageCategory, gender, bloodGroup\n" +
                "ORDER BY patientCount DESC;");
    }

    @GetMapping("/query4")
    public List<Map<String, Object>> getQuery4() {
        return sqlExecutorService.runSelect("SELECT \n" +
                "    d.doctorID,\n" +
                "    CONCAT(d.firstName, ' ', d.lastName) AS doctorName,\n" +
                "    d.specialization,\n" +
                "    d.experience,\n" +
                "    totalRevenue\n" +
                "FROM Doctor d\n" +
                "JOIN (\n" +
                "    SELECT \n" +
                "        r.doctorID, \n" +
                "        SUM(t.amount) AS totalRevenue\n" +
                "    FROM Records r\n" +
                "    JOIN Transaction t ON r.recordID = t.recordID\n" +
                "    GROUP BY r.doctorID\n" +
                "    ORDER BY totalRevenue DESC\n" +
                "    LIMIT 0, 3  -- Retrieves top 3 highest-earning doctors\n" +
                ") AS revenueSubquery ON d.doctorID = revenueSubquery.doctorID;\n");
    }

    @GetMapping("/query5")
    public List<Map<String, Object>> getQuery5() {
        return sqlExecutorService.runSelect("SELECT \n" +
                "    dep.departmentID,\n" +
                "    dep.name AS departmentName,\n" +
                "    COUNT(DISTINCT a.appointmentID) AS totalAppointments,\n" +
                "    COUNT(DISTINCT r.recordID) AS totalMedicalRecords,\n" +
                "    (SELECT COUNT(*) FROM LabTest lt \n" +
                "     JOIN Records r2 ON lt.recordID = r2.recordID \n" +
                "     JOIN Doctor d2 ON r2.doctorID = d2.doctorID \n" +
                "     WHERE d2.departmentID = dep.departmentID) AS totalLabTests,\n" +
                "    (SELECT SUM(t.amount) FROM Transaction t \n" +
                "     JOIN Appointment a2 ON t.patientID = a2.patientID \n" +
                "     JOIN Doctor d3 ON a2.doctorID = d3.doctorID \n" +
                "     WHERE d3.departmentID = dep.departmentID) AS totalRevenue\n" +
                "FROM Department dep\n" +
                "LEFT JOIN Doctor d ON dep.departmentID = d.departmentID\n" +
                "LEFT JOIN Appointment a ON d.doctorID = a.doctorID\n" +
                "LEFT JOIN Records r ON d.doctorID = r.doctorID\n" +
                "GROUP BY dep.departmentID, dep.name\n" +
                "ORDER BY totalRevenue DESC;");
    }

    @GetMapping("/query6")
    public List<Map<String, Object>> getQuery6() {
        return sqlExecutorService.runSelect("SELECT \n" +
                "    p.patientID, \n" +
                "    CONCAT(p.firstName, ' ', p.lastName) AS PatientName, \n" +
                "    COUNT(lt.recordID) AS TotalLabTests\n" +
                "FROM Patient p\n" +
                "JOIN Records r ON p.patientID = r.patientID\n" +
                "JOIN LabTest lt ON r.recordID = lt.recordID\n" +
                "GROUP BY p.patientID, p.firstName, p.lastName\n" +
                "ORDER BY TotalLabTests DESC\n" +
                "LIMIT 5;\n");
    }

    @GetMapping("/query7")
    public List<Map<String, Object>> getQuery7() {
        return sqlExecutorService.runSelect("SELECT \n" +
                "    d.departmentID, \n" +
                "    d.name AS DepartmentName, \n" +
                "    COUNT(doc.doctorID) AS TotalDoctors\n" +
                "FROM Department d\n" +
                "JOIN Doctor doc ON d.departmentID = doc.departmentID\n" +
                "GROUP BY d.departmentID, d.name\n" +
                "ORDER BY TotalDoctors DESC;");
    }

    @GetMapping("/query8")
    public List<Map<String, Object>> getQuery8() {
        return sqlExecutorService.runSelect("SELECT \n" +
                "    b.bedNumber, \n" +
                "    b.bedType, \n" +
                "    b.wardNumber, \n" +
                "    p.patientID, \n" +
                "    CONCAT(p.firstName, ' ', p.lastName) AS PatientName\n" +
                "FROM Bed b\n" +
                "JOIN Patient p ON b.patientID = p.patientID\n" +
                "WHERE b.availabilityStatus = FALSE;");
    }

    @GetMapping("/query9")
    public List<Map<String, Object>> getQuery9() {
        return sqlExecutorService.runSelect("SELECT \n" +
                "    d.departmentID, \n" +
                "    d.name AS DepartmentName, \n" +
                "    ROUND(AVG(doc.experience), 2) AS AvgExperienceYears\n" +
                "FROM Department d\n" +
                "JOIN Doctor doc ON d.departmentID = doc.departmentID\n" +
                "GROUP BY d.departmentID, d.name\n" +
                "ORDER BY AvgExperienceYears DESC;");
    }

    @GetMapping("/query10")
    public List<Map<String, Object>> getQuery10() {
        return sqlExecutorService.runSelect("SELECT DISTINCT \n" +
                "    p.patientID, \n" +
                "    CONCAT(p.firstName, ' ', p.lastName) AS PatientName, \n" +
                "    p.insuranceNumber\n" +
                "FROM Patient p\n" +
                "JOIN Transaction t ON p.patientID = t.patientID\n" +
                "WHERE t.paymentMode = 'Insurance';");
    }

    @GetMapping("/query11")
    public List<Map<String, Object>> getQuery11() {
        return sqlExecutorService.runSelect("SELECT \n" +
                "    p.allergies, \n" +
                "    COUNT(*) AS TotalPatients\n" +
                "FROM Patient p\n" +
                "WHERE p.allergies IS NOT NULL AND p.allergies <> ''\n" +
                "GROUP BY p.allergies\n" +
                "ORDER BY TotalPatients DESC\n" +
                "LIMIT 5;");
    }

    /* Sample Commands */

    /*@PostMapping("/run-select")
    public List<Map<String, Object>> runSelect(@RequestBody String sql) {
        return sqlExecutorService.runSelect(sql);
    }

    @PostMapping("/run-update")
    public String runUpdate(@RequestBody String sql) {
        int rows = sqlExecutorService.runUpdate(sql);
        return rows + " row(s) affected";
    }*/
}

