FROM debian:jessie
RUN apt-get update && \
    apt-get install -y python python-pip cron && \
    apt-get install -y mysql-client && \
    apt-get install -y rsyslog && \
    apt-get install -y vim && \
    rm -rf /var/lib/apt/lists/*
RUN pip install s3cmd
RUN service rsyslog start

COPY mysqlbackup-tos3.sh /usr/local/bin/mysqlbackup-tos3.sh

COPY backup-cron /etc/cron.daily/backup-cron
RUN chmod 0644 /etc/cron.daily/backup-cron
RUN crontab /etc/cron.daily/backup-cron
CMD cron && printenv | sed 's/^\(.*\)$/export \1/g' > /root/project_env.sh && tail -f /var/log/syslog
