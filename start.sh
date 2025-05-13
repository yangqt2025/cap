#!/bin/bash

# 定义变量
JAR_PATH="/home/agent-back-end-0.0.1-SNAPSHOT.jar"
LOG_DIR="/var/log/agent-backend"  # 使用系统日志目录
LOG_FILE="$LOG_DIR/app.log"
PROFILE="prod"
PORT=8101
APP_NAME="agent-back-end"
MAX_RETRIES=3
RETRY_INTERVAL=5
DB_NAME="aiagent"  # 确保使用正确的数据库名
CONFIG_DIR="/home/config"  # 配置文件目录

# 检查是否为root用户
if [ "$(id -u)" != "0" ]; then
    echo "错误：此脚本需要root权限运行"
    exit 1
fi

# 创建日志目录并设置权限
mkdir -p $LOG_DIR
chown root:root $LOG_DIR
chmod 755 $LOG_DIR
touch $LOG_FILE
chown root:root $LOG_FILE
chmod 644 $LOG_FILE

# 创建配置目录
mkdir -p $CONFIG_DIR
chown root:root $CONFIG_DIR
chmod 755 $CONFIG_DIR

# 检查JAR文件是否存在
if [ ! -f "$JAR_PATH" ]; then
    echo "错误：找不到JAR文件 $JAR_PATH"
    exit 1
fi

# 检查数据库是否存在
check_database() {
    if ! mysql -h 139.159.163.234 -u root -pMian2002@lmn -e "USE $DB_NAME" 2>/dev/null; then
        echo "错误：数据库 $DB_NAME 不存在，正在创建..."
        mysql -h 139.159.163.234 -u root -pMian2002@lmn -e "CREATE DATABASE IF NOT EXISTS $DB_NAME"
        if [ $? -ne 0 ]; then
            echo "错误：无法创建数据库 $DB_NAME"
            exit 1
        fi
    fi
}

# 检查端口是否被占用
check_port() {
    if netstat -tln | grep -q ":$PORT "; then
        echo "端口 $PORT 已被占用，请检查是否有其他服务正在运行"
        return 1
    fi
    return 0
}

# 检查应用是否正常响应
check_health() {
    local retries=0
    while [ $retries -lt $MAX_RETRIES ]; do
        if curl -s -o /dev/null -w "%{http_code}" http://localhost:$PORT/api/health | grep -q "200"; then
            echo "应用健康检查通过！"
            return 0
        fi
        echo "等待应用启动... ($((retries + 1))/$MAX_RETRIES)"
        sleep $RETRY_INTERVAL
        ((retries++))
    done
    echo "应用健康检查失败！"
    return 1
}

# 停止现有进程
stop_existing_process() {
    local pid=$(ps -ef | grep java | grep $JAR_PATH | grep -v grep | awk '{print $2}')
    if [ -n "$pid" ]; then
        echo "发现正在运行的Java进程，PID: $pid"
        echo "正在停止进程..."
        kill -9 $pid
        sleep 2
    fi
}

# 启动应用
start_application() {
    echo "正在启动应用..."
    # 确保使用正确的配置文件
    nohup java -jar $JAR_PATH \
        --spring.profiles.active=$PROFILE \
        --spring.config.location=file:$CONFIG_DIR/ \
        > $LOG_FILE 2>&1 &
    local pid=$!
    echo "应用启动中，进程ID: $pid"
    return $pid
}

# 主流程
main() {
    # 检查数据库
    check_database

    # 停止现有进程
    stop_existing_process

    # 检查端口
    if ! check_port; then
        exit 1
    fi

    # 启动应用
    start_application
    local app_pid=$!

    # 等待应用启动并检查健康状态
    if check_health; then
        echo "应用启动成功！"
        echo "进程ID: $app_pid"
        echo "日志文件位置: $LOG_FILE"
        echo "可以使用以下命令查看日志: tail -f $LOG_FILE"
    else
        echo "应用启动失败，请检查日志文件: $LOG_FILE"
        echo "最后10行日志："
        tail -n 10 $LOG_FILE
        kill -9 $app_pid
        exit 1
    fi
}

# 执行主流程
main

# 添加定时任务（可选）
# echo "0 0 * * * /bin/bash $PWD/start.sh >> $LOG_FILE 2>&1" | crontab - 