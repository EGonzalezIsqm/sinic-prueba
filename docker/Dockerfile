FROM osgeo/gdal:alpine-normal-3.1.2

FROM quay.io/devfile/maven:3.8.1-openjdk-11-slim
WORKDIR /build

# Build dependency offline to streamline build
COPY pom.xml .
COPY settings.xml .
COPY src src
RUN mvn -s settings.xml dependency:go-offline package -Dmaven.test.skip=true


COPY --from=0 /build/target/xtf-to-json-1.0.jar /app/target/xtf-to-json-1.0.jar

EXPOSE 8081
ENTRYPOINT [ "java", "-jar", "/app/target/xtf-to-json-1.0.jar", "--server.port=8081" ]
