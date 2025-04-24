package com.nirviti.ai;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ai")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class AiController {
    private final AiService aiService;
    private final SqlExecutorService sqlExecutorService;

    @GetMapping("/patientSummary/{id}")
    public ResponseEntity<String> patientSummary(@PathVariable("id") String id) {
        List<Map<String,Object>> query = sqlExecutorService.runSelect("select prescription from Records where patientID = '" + id + "';");
        AiRequest request = new AiRequest();
        request.setContent(query.toString());
        request.setOperation("summarize prescription");
        String result = aiService.processContent(request);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/pharmacySummary/{id}")
    public ResponseEntity<String> pharmacySummary(@PathVariable("id") String id) {
        List<Map<String,Object>> query = sqlExecutorService.runSelect("SELECT name\n" +
                "FROM Item\n" +
                "WHERE pharmacyID = '" + id + "';");
        AiRequest request = new AiRequest();
        request.setContent(query.toString());
        request.setOperation("summarize items");
        String result = aiService.processContent(request);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/seasonSummary")
    public ResponseEntity<String> seasonSummary() {
        AiRequest request = new AiRequest();
        request.setContent("");
        request.setOperation("summarize season");
        String result = aiService.processContent(request);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/patientLoad")
    public ResponseEntity<String> patientLoad() {
        AiRequest request = new AiRequest();
        request.setContent("");
        request.setOperation("patient load");
        String result = aiService.processContent(request);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/bloodShortage")
    public ResponseEntity<String> bloodShortage() {
        List<Map<String,Object>> query = sqlExecutorService.runSelect("SELECT bloodGroup, COUNT(*) AS numberOfPatients\n" +
                "FROM Patient\n" +
                "GROUP BY bloodGroup\n" +
                "ORDER BY numberOfPatients DESC;");
        AiRequest request = new AiRequest();
        request.setContent(query.toString());
        request.setOperation("blood shortage");
        String result = aiService.processContent(request);
        return ResponseEntity.ok(result);
    }
}
