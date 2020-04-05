#!/bin/bash

PATH=/usr/local/sbin:/usr/local/bin:/sbin:/bin:/usr/sbin:/usr/bin
source /root/project_env.sh
mysqldump -h mysql -u "$MYSQL_USER" -p"${MYSQL_PASSWORD}" "${MYSQL_DATABASE}" > "${MYSQL_DATABASE}".sql
s3cmd put "${MYSQL_DATABASE}".sql s3://taxi-mysql-backup/"${MYSQL_DATABASE}".sql

logger "database backup, upload to s3 and cleanup of old backups completed"
