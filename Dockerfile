FROM eclipse-temurin:17-jdk

LABEL maintainer="yesul"
LABEL version="0.0.1"

ARG JAR_FILE_PATH=build/libs/*.jar
COPY ${JAR_FILE_PATH} app.jar

# JVM 타임존 설정 추가
ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "app.jar"]
