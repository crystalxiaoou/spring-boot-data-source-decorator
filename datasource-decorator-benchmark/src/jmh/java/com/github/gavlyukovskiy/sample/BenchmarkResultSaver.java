package com.github.gavlyukovskiy.sample;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class BenchmarkResultSaver {

    private BenchmarkResultSaver() {
    }

    public static String getResultPath(Class<?> benchmarkClass) throws IOException {
        Path benchmarkDirectory = Paths.get("benchmark");
        if (Files.notExists(benchmarkDirectory)) {
            Files.createDirectory(benchmarkDirectory);
        }
        return "benchmark/" +
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + "-" +
                LocalTime.now().toSecondOfDay() + "-" +
                benchmarkClass.getSimpleName() + ".txt";
    }
}
