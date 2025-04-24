package com.nirviti.ai;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/patientdb")
public class PatientDashboardController {

    private final SqlExecutorService sqlExecutorService;

    public PatientDashboardController(SqlExecutorService service) {
        this.sqlExecutorService = service;
    }

    @GetMapping("/authenticate")
    public String authenticate(@RequestParam String patientId, @RequestParam String password) {
        return "ok";
    }

    @GetMapping("/getInfo/{id}")
    public List<Map<String, Object>> getInfo(@PathVariable("id") String id) {
        return sqlExecutorService.runSelect("SELECT * FROM patient WHERE patientID = '" + id + "';");
    }

    @GetMapping("/getHistory/{id}")
    public List<Map<String, Object>> getHistory(@PathVariable("id") String id) {
        return sqlExecutorService.runSelect("SELECT \n" +
                "    A.dateTime,\n" +
                "    CONCAT(D.firstName, ' ', D.lastName) AS doctorName,\n" +
                "    Dept.name AS departmentName,\n" +
                "    A.status\n" +
                "FROM \n" +
                "    Appointment A\n" +
                "JOIN \n" +
                "    Doctor D ON A.doctorID = D.doctorID\n" +
                "JOIN \n" +
                "    Department Dept ON D.departmentID = Dept.departmentID\n" +
                "WHERE \n" +
                "    A.patientID = '" + id + "';");
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

