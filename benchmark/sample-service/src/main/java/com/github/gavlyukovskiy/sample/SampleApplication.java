package com.github.gavlyukovskiy.sample;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.sleuth.Sampler;
import org.springframework.cloud.sleuth.SpanAdjuster;
import org.springframework.cloud.sleuth.SpanNamer;
import org.springframework.cloud.sleuth.SpanReporter;
import org.springframework.cloud.sleuth.TraceKeys;
import org.springframework.cloud.sleuth.log.SpanLogger;
import org.springframework.cloud.sleuth.trace.DefaultTracer;
import org.springframework.cloud.sleuth.util.ExceptionUtils;
import org.springframework.cloud.sleuth.zipkin.EndpointLocator;
import org.springframework.cloud.sleuth.zipkin.ZipkinSpanListener;
import org.springframework.cloud.sleuth.zipkin.ZipkinSpanReporter;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.slf4j.LoggerFactory.getLogger;

@SpringBootApplication
public class SampleApplication {

    private static final Logger log = getLogger(SampleApplication.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(SampleApplication.class, args);
        ExceptionUtils.setFail(true);
        writePort(context);
    }

    private static void writePort(ConfigurableApplicationContext context) {
        try {
            String[] profiles = context.getEnvironment().getActiveProfiles();
            File port = new File(Stream.of(profiles).collect(Collectors.joining("_")) + ".port");
            if (port.exists()) {
                FileUtils.forceDelete(port);
            }
            boolean created = port.createNewFile();
            if (!created) {
                throw new IllegalArgumentException("port wasn't created");
            }
            FileUtils.writeStringToFile(port, context.getEnvironment().getProperty("local.server.port"));
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    FileUtils.forceDelete(port);
                }
                catch (IOException ignored) {
                }
            }));
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
