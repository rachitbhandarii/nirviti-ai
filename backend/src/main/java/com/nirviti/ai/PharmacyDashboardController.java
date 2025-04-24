package com.nirviti.ai;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/pharmacydb")
public class PharmacyDashboardController {

    private final SqlExecutorService sqlExecutorService;

    public PharmacyDashboardController(SqlExecutorService service) {
        this.sqlExecutorService = service;
    }

    @GetMapping("/getInfo/{id}")
    public List<Map<String, Object>> getInfo(@PathVariable("id") String id) {
        return sqlExecutorService.runSelect("SELECT * FROM pharmacy WHERE pharmacyID = '" + id + "'");
    }

    @GetMapping("/authenticate")
    public String authenticate(@RequestParam String pharmacyId, @RequestParam String password) {
        return "ok";
    }

    /* Testing */

    /*@GetMapping("/getDoctors")
    public List<Map<String, Object>> getDoctors() {
        return sqlExecutorService.runSelect("SELECT * FROM doctor");
    }*/

    @GetMapping("/getPharmacies")
    public List<Map<String, Object>> getPharmacies() {
        return sqlExecutorService.runSelect("SELECT * FROM item");
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

