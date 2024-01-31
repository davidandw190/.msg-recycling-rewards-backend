FROM maven:3.9.6 AS build
WORKDIR /app
ARG CONTAINER_PORT
COPY pom.xml /app
RUN mvn dependency:resolve
COPY . /app
RUN mvn clean
RUN mvn package -DskipTests -X

FROM openjdk:17
COPY --from=build /app/target/*.jar rewardsapp.jar
EXPOSE ${CONTAINER_PORT}
CMD ["java", "-jar", "rewardsapp.jar"]