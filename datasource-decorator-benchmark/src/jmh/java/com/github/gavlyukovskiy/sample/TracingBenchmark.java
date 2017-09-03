package com.github.gavlyukovskiy.sample;

import org.apache.commons.io.IOUtils;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Measurement(iterations = 30)
@Warmup(iterations = 5)
@Fork(value = 1)
public class TracingBenchmark {

    @Param({ "base", "sleuth", "p6spy", "sleuth_p6spy" })
    private String profile;
    private URL url;

    @Setup
    public void setup() throws IOException {
        this.url = new URL("http://localhost:" + loadPort(profile) + "/select/");
    }

    @Benchmark
    public void rest() throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
        }

        IOUtils.toString(conn.getInputStream());
    }

    private String loadPort(String port) throws IOException {
        return Files.lines(Paths.get("datasource-decorator-benchmark/" + port + ".port")).findFirst().orElseThrow(IllegalStateException::new);
    }

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(TracingBenchmark.class.getSimpleName())
                .result(BenchmarkResultSaver.getResultPath(TracingBenchmark.class))
                .resultFormat(ResultFormatType.TEXT)
                .build();

        new Runner(opt).run();
    }
}
