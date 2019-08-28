# ontaxi
1. Configure MySQL to support UTF-8

    Modify file /etc/mysql/mysql.conf.d/mysqld.cnf to add 
    ```
    collation-server = utf8_unicode_ci
    init-connect='SET NAMES utf8'
    character-set-server = utf8
    ```
    under [mysqld] fragment
2. Install poseidon-theme-2.0.0.jar to local maven repository
    mvn install:install-file -Dfile=lib\poseidon-theme-2.0.0.jar -DgroupId=org.primefaces \
        -DartifactId=poseidon-theme -Dversion=2.0.0
