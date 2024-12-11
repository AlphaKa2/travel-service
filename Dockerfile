# 자바 버전 선택
FROM openjdk:17-jdk-alpine

# root 권한으로 실행
USER root

# 작업 디렉토리 설정
WORKDIR /app/blog-service

# gradle로 빌드된 jar파일을 현재 디렉토리에 복사
COPY build/libs/*.jar app.jar

# Elastic APM Agent 다운로드 및 복사
RUN apk add --no-cache wget \
    && wget -O elastic-apm-agent.jar https://search.maven.org/remotecontent?filepath=co/elastic/apm/elastic-apm-agent/1.50.0/elastic-apm-agent-1.50.0.jar

# CA 인증서 복사 (Jenkins workspace에서 docker build 시 build context에 CA 파일 포함)
COPY elastic-stack-ca.pem /tmp/elastic-stack-ca.pem

# CA 인증서를 truststore에 추가
RUN keytool -importcert -noprompt \
    -alias elastic-ca \
    -keystore /etc/ssl/certs/java/cacerts \
    -storepass changeit \
    -file /tmp/elastic-stack-ca.pem

# 포트 노출
EXPOSE 8004

# APM Agent와 함께 애플리케이션 실행
ENTRYPOINT java -javaagent:/app/travel-service/elastic-apm-agent.jar \
            -Delastic.apm.server_urls=$ELASTIC_APM_SERVER_URLS \
            -Delastic.apm.service_name=travel-service \
            -Delastic.apm.environment=develop \
            -Delastic.apm.secret_token=$ELASTIC_APM_SECRET_TOKEN \
            -Delastic.apm.application_packages=com.alphaka.travelservice \
            -jar app.jar --spring.profiles.active=develop
