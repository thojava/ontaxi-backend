#!/bin/bash

PATH=/usr/local/sbin:/usr/local/bin:/sbin:/bin:/usr/sbin:/usr/bin

mysqldump -h mysql -u root -padm1n ontaxi > ontaxi.sql
export AWS_ACCESS_KEY_ID=AKIAQFNFYMQHBMSXWIWK
export AWS_SECRET_ACCESS_KEY=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
s3cmd put ontaxi.sql* s3://taxi-mysql-backup/ontaxi.sql

logger "database backup, upload to s3 and cleanup of old backups completed"
