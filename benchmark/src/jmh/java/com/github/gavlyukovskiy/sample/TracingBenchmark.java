package com.github.gavlyukovskiy.sample;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
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
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Measurement(iterations = 5)
@Warmup(iterations = 3)
@Fork(value = 2)
public class TracingBenchmark {

    public static final int HTTP_OK = 200;

    @Param({ "base", "sleuth", "decorator", "sleuth,decorator", "sleuth,decorator,zipkin" })
    private String profile;
    @Param({ "sample-p6spy-service", "sample-datasource-proxy-service" })
    private String service;

    private String containerId;
    private String url;
    private OkHttpClient client;

    @Setup
    public void setup() throws IOException {
        client = new OkHttpClient();
        url = "http://localhost:12001";
        runContainer();
    }

    @TearDown
    public void tearDown() throws IOException {
        stopContainer();
    }

    @Benchmark
    public String noop() throws IOException {
        return request("/noop");
    }

    @Benchmark
    public String jdbc() throws IOException {
        return request("/jdbc");
    }

    @Benchmark
    public String hibernate() throws IOException {
        return request("/hibernate");
    }

    private String request(String endpoint) throws IOException {
        Request request = new Request.Builder().url(url + endpoint).build();
        Response response = client.newCall(request).execute();
        if (response.code() != HTTP_OK) {
            throw new IllegalStateException("Received " + response.code() + " status");
        }
        return response.body() != null ? response.body().string() : null;
    }

    private void runContainer() throws IOException {
        System.out.println("Running container...");

        Process process = new ProcessBuilder(
                "docker run" +
                        " -d" +
                        " --name " + service +
                        " -p 12001:12001" +
                        " -e \"SPRING.PROFILES.ACTIVE=" + profile + "\"" +
                        " datasource-decorator/benchmark/" + service)
                .start();

        if (process.exitValue() != -1) {
            throw new IllegalStateException("Error code while starting container: " + process.exitValue());
        }

        containerId = IOUtils.toString(process.getInputStream());

        System.out.println("Container " + containerId + " has been started");
    }

    private void stopContainer() throws IOException {
        System.out.println("Stopping container...");

        Process process = new ProcessBuilder(
                "docker stop " + containerId,
                "docker rm " + containerId)
                .start();

        if (process.exitValue() != -1) {
            throw new IllegalStateException("Error code while stopping container: " + process.exitValue());
        }

        System.out.println("Container " + containerId + " has been stopped");
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
