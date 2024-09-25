# 사용하고자 하는 기본 이미지 설정 (OpenJDK 17 사용)
FROM openjdk:17-slim

# 애플리케이션 jar 파일 위치 설정
ARG JAR_FILE=build/libs/*.jar

# 컨테이너에 jar 파일을 복사
COPY ${JAR_FILE} app.jar

# 애플리케이션 실행 명령어 설정
ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "/app.jar"]