package com.github.gavlyukovskiy.sample;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.HostInfoEnvironmentPostProcessor;
import org.springframework.cloud.sleuth.util.ExceptionUtils;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.slf4j.LoggerFactory.getLogger;

@SpringBootApplication(
        exclude = HostInfoEnvironmentPostProcessor.class
)
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
