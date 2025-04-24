package com.nirviti.ai;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/doctordb")
public class DoctorDashboardController {

    private final SqlExecutorService sqlExecutorService;

    public DoctorDashboardController(SqlExecutorService service) {
        this.sqlExecutorService = service;
    }

    @GetMapping("/authenticate")
    public String authenticate(@RequestParam String username, @RequestParam String password) {
        if (Objects.equals(username, "admin") && Objects.equals(password, "admin"))
            return "ok";
        return "error";
    }

    @GetMapping("/beds")
    public List<Map<String, Object>> beds() {
        return sqlExecutorService.runSelect("SELECT \n" +
                "    COUNT(*) AS totalBeds,\n" +
                "    SUM(CASE WHEN availabilityStatus = TRUE THEN 1 ELSE 0 END) AS availableBeds\n" +
                "FROM \n" +
                "    Bed;");
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

