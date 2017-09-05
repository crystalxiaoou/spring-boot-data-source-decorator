Before running benchmarks launch Spring Boot applications with profiles:

Benchmarking P6Spy:
```
java -jar .\build\service-p6spy.jar --spring.profiles.active=base
java -jar .\build\service-p6spy.jar --spring.profiles.active=sleuth
java -jar .\build\service-p6spy.jar --spring.profiles.active=decorator
java -jar .\build\service-p6spy.jar --spring.profiles.active=sleuth,decorator
java -jar .\build\service-p6spy.jar --spring.profiles.active=sleuth,decorator,zipkin
```

Benchmarking DataSource Proxy:
```
java -jar .\build\service-datasource-proxy.jar --spring.profiles.active=base
java -jar .\build\service-datasource-proxy.jar --spring.profiles.active=sleuth
java -jar .\build\service-datasource-proxy.jar --spring.profiles.active=decorator
java -jar .\build\service-datasource-proxy.jar --spring.profiles.active=sleuth,decorator
java -jar .\build\service-datasource-proxy.jar --spring.profiles.active=sleuth,decorator,zipkin
```
