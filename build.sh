#!/bin/bash
echo "构建镜像 ..."
mvn clean package -X -U -Dmaven.test.skip=true

docker build -t registry.cn-beijing.aliyuncs.com/hummercloud/hummerrisk:v1.0.0 .
docker push registry.cn-beijing.aliyuncs.com/hummercloud/hummerrisk:v1.0.0
docker images|grep hummerrisk|awk '{print "docker rmi -f "$3}'|sh
