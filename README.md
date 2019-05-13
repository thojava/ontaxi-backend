# ontaxi
1. Configure MySQL to support UTF-8

    Modify file /etc/mysql/mysql.conf.d/mysqld.cnf to add 
    ```
    collation-server = utf8_unicode_ci
    init-connect='SET NAMES utf8'
    character-set-server = utf8
    ```
    under [mysqld] fragment