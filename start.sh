#!/bin/bash

JAR_PATH="/home/agent-back-end-0.0.1-SNAPSHOT.jar"
PROFILE="prod"
LOG_FILE="/home/app.log"

echo "🔍 Checking for running Java process..."
PID=$(ps -ef | grep "$JAR_PATH" | grep -v grep | awk '{print $2}')

if [ -n "$PID" ]; then
  echo "🛑 Killing existing process: $PID"
  kill -9 $PID
  sleep 1
else
  echo "✅ No existing process found."
fi

echo "🚀 Starting service from $JAR_PATH ..."
nohup java \
    -Dspring.profiles.active="$PROFILE" \
    -Dspring.datasource.url="jdbc:mysql://139.159.163.234:3306/aiagent?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8&createDatabaseIfNotExist=true" \
    -Dspring.datasource.username=root \
    -Dspring.datasource.password=Mian2002@lmn \
    -Dspring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver \
    -Dspring.jpa.hibernate.ddl-auto=update \
    -Dspring.jpa.show-sql=true \
    -Dspring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL5InnoDBDialect \
    -Dspring.datasource.hikari.connection-timeout=300000 \
    -Dspring.datasource.hikari.maximum-pool-size=10 \
    -Dspring.datasource.hikari.minimum-idle=5 \
    -Dspring.datasource.hikari.idle-timeout=300000 \
    -Dspring.datasource.hikari.max-lifetime=600000 \
    -Dspring.datasource.hikari.validation-timeout=3000 \
    -Dspring.datasource.hikari.connection-test-query="SELECT 1" \
    -Dspring.datasource.hikari.initialization-fail-timeout=0 \
    -Dspring.datasource.hikari.register-mbeans=true \
    -Dspring.datasource.hikari.pool-name=HikariPool-aiagent \
    -Dspring.datasource.hikari.leak-detection-threshold=60000 \
    -Dspring.datasource.hikari.metrics-registry-name=aiagent \
    -Dlogging.level.com.zaxxer.hikari=DEBUG \
    -Dlogging.level.org.hibernate.SQL=DEBUG \
    -Dlogging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE \
    -Dlogging.level.com.yupi.springbootinit=DEBUG \
    -jar "$JAR_PATH" > "$LOG_FILE" 2>&1 &

echo "✅ Service started. Check logs at $LOG_FILE" 