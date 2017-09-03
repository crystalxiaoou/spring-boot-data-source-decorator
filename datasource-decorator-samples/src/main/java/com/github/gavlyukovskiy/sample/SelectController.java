package com.github.gavlyukovskiy.sample;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/select")
public class SelectController {

    private final DataSource dataSource;
    private final List<String> columns;

    public SelectController(DataSource dataSource) {
        this.dataSource = dataSource;
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS")) {
            columns = new ArrayList<>();
            ResultSetMetaData metaData = resultSet.getMetaData();
            for (int i = 0; i < metaData.getColumnCount(); i++) {
                columns.add(metaData.getColumnName(i + 1));
            }
        }
        catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @RequestMapping("/")
    public List<Map<String, String>> run() {
        List<Map<String, String>> results = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS")) {
            Map<String, String> result = new HashMap<>();
            while (resultSet.next()) {
                for (String column : columns) {
                    result.put(column, resultSet.getString(column));
                }
            }
            results.add(result);
        }
        catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return results;
    }
}
