package com.nirviti.ai;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class SqlExecutorService {

    private final JdbcTemplate jdbcTemplate;

    public SqlExecutorService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // For SELECT
    public List<Map<String, Object>> runSelect(String sql) {
        return jdbcTemplate.queryForList(sql);
    }

    // For INSERT, UPDATE, DELETE, ALTER, etc.
    public int runUpdate(String sql) {
        return jdbcTemplate.update(sql); // Returns number of affected rows
    }
}

