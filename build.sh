#!/bin/bash
echo "构建镜像 ..."
mvn clean package -X -U -Dmaven.test.skip=true

docker build -t registry.cn-beijing.aliyuncs.com/hummerrisk/hummerrisk:v0.1 --platform linux/x86_64 .
docker push registry.cn-beijing.aliyuncs.com/hummerrisk/hummerrisk:v0.1
docker images|grep hummerrisk|awk '{print "docker rmi -f "$3}'|sh
