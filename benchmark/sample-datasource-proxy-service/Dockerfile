FROM openjdk:10-jdk
VOLUME /tmp
ADD build/libs/*.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar", "$JAVA_OPTS"]