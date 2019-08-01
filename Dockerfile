FROM maven:3.6.1-jdk-11-slim as builder
WORKDIR /building
ADD . .

RUN mvn -T 4 clean install
RUN mv target/$(ls ./target | grep \.jar | grep -v original | grep -v javadoc | grep -v sources) ./application.jar

# **********************************
# packing jar file to docker image #
# **********************************

FROM openjdk:11

EXPOSE 8080

WORKDIR /app

COPY --from=builder /building/application.jar .

ENTRYPOINT java $JVM_MIN_MEM $JVM_MAX_MEM -jar application.jar
