package com.github.gavlyukovskiy.sample;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
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
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Measurement(iterations = 5)
@Warmup(iterations = 3)
@Fork(value = 1)
@Threads(Threads.MAX)
public class TracingBenchmark {

    @Param({ /*"base", "sleuth", "decorator", "sleuth_decorator",*/ "sleuth_decorator_zipkin" })
    private String profile;
    private String url;
    private OkHttpClient client;

    @Setup
    public void setup() throws IOException {
        client = new OkHttpClient();
        url = "http://localhost:" + loadPort(profile);
    }

/*
    TracingBenchmark.noop                base  avgt   10  0.214 ± 0.002  ms/op
    TracingBenchmark.noop              sleuth  avgt   10  0.245 ± 0.004  ms/op
    TracingBenchmark.noop               p6spy  avgt   10  0.216 ± 0.004  ms/op
    TracingBenchmark.noop        sleuth_p6spy  avgt   10  0.239 ± 0.006  ms/op

    @Benchmark
    public String noop() throws IOException {
        return rest(blackhole, "/noop");
    }
*/

    @Benchmark
    public String jdbc() throws IOException {
        return rest("/jdbc");
    }

    @Benchmark
    public String hibernate() throws IOException {
        return rest("/hibernate");
    }

    private String rest(String endpoint) throws IOException {
        Request request = new Request.Builder().url(url + endpoint).build();
        Response response = client.newCall(request).execute();
        if (response.code() != 200) {
            throw new IllegalStateException("Received " + response.code() + " status");
        }
        return response.body().string();
    }

    private int loadPort(String profile) throws IOException {
        return Files.lines(Paths.get("benchmark/" + profile + ".port")).findFirst().map(Integer::valueOf).orElseThrow(IllegalStateException::new);
    }

    public static void main(String[] args) throws Exception {
        OkHttpClient client = new OkHttpClient();
        for (int i = 0; i < 10; i++) {
            long start = System.currentTimeMillis();
            Request request = new Request.Builder().url("http://localhost:51108/jdbc").build();
            Response response = client.newCall(request).execute();
            if (response.code() != 200) {
                throw new IllegalStateException("Received " + response.code() + " status");
            }
            System.out.println(response.body().string());
            System.out.println((System.currentTimeMillis() - start) + "ms");
        }

        /*Options opt = new OptionsBuilder()
                .include(TracingBenchmark.class.getSimpleName())
                .result(BenchmarkResultSaver.getResultPath(TracingBenchmark.class))
                .resultFormat(ResultFormatType.TEXT)
                .build();

        new Runner(opt).run();*/
    }
}
