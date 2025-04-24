package com.nirviti.ai;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/update")
public class updateController {

    private final SqlExecutorService sqlExecutorService;

    public updateController(SqlExecutorService service) {
        this.sqlExecutorService = service;
    }

    @GetMapping("/info/patient/{id}")
    public List<Map<String, Object>> getPatientInfo(@PathVariable("id") String id) {
        return sqlExecutorService.runSelect("SELECT * FROM patient WHERE patientID = '" + id + "';");
    }

    @GetMapping("/info/doctor/{id}")
    public List<Map<String, Object>> getDoctorInfo(@PathVariable("id") String id) {
        return sqlExecutorService.runSelect("SELECT * FROM doctor WHERE doctorID = '" + id + "';");
    }

    @GetMapping("/info/staff/{id}")
    public List<Map<String, Object>> getStaffInfo(@PathVariable("id") String id) {
        return sqlExecutorService.runSelect("SELECT * FROM staff WHERE staffID = '" + id + "';");
    }

    @PostMapping("/info/patient/{id}")
    public String setPatientInfo(@PathVariable("id") String id, @RequestBody List<Map<String, Object>> struct) {
        // struct would have update & val
        String update = struct.get(0).get("update").toString();
        String val = struct.get(0).get("val").toString();
        sqlExecutorService.runUpdate("UPDATE Patient\n" +
                "SET " + update + " = '" + val + "'\n" +
                "WHERE PatientID = '" + id + "';");
        return "OK";
    }

    @PostMapping("/info/doctor/{id}")
    public String setDoctorInfo(@PathVariable("id") String id, @RequestBody List<Map<String, Object>> struct) {
        // struct would have update & val
        String update = struct.get(0).get("update").toString();
        String val = struct.get(0).get("val").toString();
        sqlExecutorService.runUpdate("UPDATE Doctor\n" +
                "SET " + update + " = '" + val + "'\n" +
                "WHERE DoctorID = '" + id + "';");
        return "OK";
    }

    @PostMapping("/info/staff/{id}")
    public String setStaffInfo(@PathVariable("id") String id, @RequestBody List<Map<String, Object>> struct) {
        // struct would have update & val
        String update = struct.get(0).get("update").toString();
        String val = struct.get(0).get("val").toString();
        sqlExecutorService.runUpdate("UPDATE Staff\n" +
                "SET " + update + " = '" + val + "'\n" +
                "WHERE StaffID = '" + id + "';");
        return "OK";
    }

    @PostMapping("/bed/allocate")
    public String allocateBed(@RequestBody List<Map<String, Object>> struct) {
        String patientID = struct.get(0).get("patientID").toString();
        String bedNumber = struct.get(0).get("bedNumber").toString();
        sqlExecutorService.runUpdate("update Bed set availabilityStatus = false, patientID = '" + patientID + "' where bedNumber = '" + bedNumber + "';");
        return "OK";
    }

    @PostMapping("/bed/deallocate")
    public String deallocateBed(@RequestBody List<Map<String, Object>> struct) {
        String bedNumber = struct.get(0).get("bedNumber").toString();
        sqlExecutorService.runUpdate("update Bed set availabilityStatus = true, patientID = NULL where bedNumber = '" + bedNumber + "';");
        return "OK";
    }

    @PostMapping("/appointment/book")
    public String appointment(@RequestBody List<Map<String, Object>> struct) {
        List<Map<String,Object>> newPk = sqlExecutorService.runSelect("SELECT COALESCE(MAX(appointmentID), 0) + 1 FROM Appointment");
        String pk = newPk.get(0).get("COALESCE(MAX(appointmentID), 0) + 1").toString();
        String patientID = struct.get(0).get("patientID").toString();
        String doctorID = struct.get(0).get("doctorID").toString();
        sqlExecutorService.runUpdate("INSERT INTO Appointment (appointmentID, patientID, doctorID, status)\n" +
                "VALUES (" + pk + " , " + patientID + " , " + doctorID + " , 'Scheduled' );");
        return "OK";
    }

    @PostMapping("/appointment/cancel")
    public String cancelApointment(@RequestBody List<Map<String, Object>> struct) {
        String appointmentID = struct.get(0).get("appointmentID").toString();
        sqlExecutorService.runUpdate("UPDATE Appointment\n" +
                "SET status = 'Cancelled'\n" +
                "WHERE appointmentID = '"+ appointmentID +"';\n");
        return "OK";
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

