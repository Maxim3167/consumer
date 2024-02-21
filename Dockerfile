FROM openjdk:latest
LABEL authors="Maxim Kabanov"
COPY build/libs/consumer_kafka-1.0-SNAPSHOT.jar /
RUN chmod +x consumer_kafka-1.0-SNAPSHOT.jar
CMD ["java","-jar","consumer_kafka-1.0-SNAPSHOT.jar"]