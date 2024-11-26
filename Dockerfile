# 자바 버전 선택
FROM openjdk:17-jdk-alpine

# 작업 디렉토리 설정
WORKDIR /app/travel-service

# gradle로 빌드된 jar파일을 현재 디렉토리에 복사
COPY build/libs/*.jar app.jar

# 포트 노출
EXPOSE 8004

# develop 프로필로 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=develop"]