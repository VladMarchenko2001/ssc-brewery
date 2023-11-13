ARG COMPILE_IMAGE="maven:3.8.4-openjdk-11-slim"

FROM ${COMPILE_IMAGE} as maven_build

WORKDIR /repo

COPY . /repo

RUN mvn clean install -DskipTests

EXPOSE 8080

CMD ["java", "-jar", "target/scc-brewery-1.0-SNAPSHOT.jar"]
