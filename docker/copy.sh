#!/bin/sh

# 复制项目的文件到对应docker路径，便于一键生成镜像。
usage() {
	echo "Usage: sh copy.sh"
	exit 1
}


# copy sql
echo "begin copy sql "
cp ../sql/ry_20220814.sql ./mysql/db
cp ../sql/ry_config_20220510.sql ./mysql/db

# copy html
echo "begin copy html "
cp -r ../hummer-ui/dist/** ./nginx/html/dist


# copy jar
echo "begin copy hummer-gateway "
cp ../hummer-gateway/target/hummer-gateway.jar ./hummer/gateway/jar

echo "begin copy hummer-auth "
cp ../hummer-auth/target/hummer-auth.jar ./hummer/auth/jar

echo "begin copy hummer-visual "
cp ../hummer-visual/hummer-monitor/target/hummer-visual-monitor.jar  ./hummer/visual/monitor/jar

echo "begin copy hummer-modules-system "
cp ../hummer-modules/hummer-system/target/hummer-modules-system.jar ./hummer/modules/system/jar

echo "begin copy hummer-modules-file "
cp ../hummer-modules/hummer-file/target/hummer-modules-file.jar ./hummer/modules/file/jar

echo "begin copy hummer-modules-job "
cp ../hummer-modules/hummer-job/target/hummer-modules-job.jar ./hummer/modules/job/jar

echo "begin copy hummer-modules-gen "
cp ../hummer-modules/hummer-gen/target/hummer-modules-gen.jar ./hummer/modules/gen/jar

