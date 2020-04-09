# Liquibase testing framework

Getting started with Liquibase testing framework

## Assumptions/Pre-requisites

### Hardware
Laptop with at least 8 Gb memory (recommended 16 Gb, ideally 32 Gb)

### Software

1. The Web-based Hello World case study
* Instructions to install here: https://github.com/acapozucca/helloworld/blob/master/product.helloworld/README.md
* You need to complete (at least) until the step 3 (included) of the section 
"Check local testing environment setup", and
(at least) until step 3 (included) of the section
"Run the application"



2. Install Liquibase on your local computer
* Download from https://github.com/liquibase/liquibase/releases/
* Follow instructions from here https://www.liquibase.org/documentation/installation-linux-unix-mac.html

**Notes**

* Choose the right version, for the right platform. This tutorial was made using version 3.8.7 on Mac.

* The platform is your local computer, but the DB is the one installed into the testing environment (created using Vagrant)


## Set local working environment



### Create DB user 

1- Clone this repo

2- Get to the directory

```
cd ~/<git_root_folder>/helloworld/product.helloworld
```

**Note**: This directory must exist as it is required to have installed already the Web-based Hello World case study.


3- Jump into the virtual environment (refered to also as *guest*) : 

```
 vagrant ssh
```

4- Connect to DB
```
mysql -u root -p 
```

**Note**:  

root's password is “12345678”


5- Create new DB user named 'ac'
```
CREATE USER ‘ac’@‘%' IDENTIFIED BY ‘ac';
```

6- Grant access to user 'ac'
```
grant all privilegies on hellodb.* TO 'ac'@'%’;
```

**Note**:

The user can be removed doing
```
DROP USER 'ac'@'%';
```



### Open access to mysql 

It is required to make possible to reach mysql (running into the guest) from the host via port 3306

1- Edit this file:
```
/etc/mysql/mysql.conf.d/mysqld.cnf
```

2- Find the following line:
```
bind-address = 127.0.0.1
```

3- Change the bind-address to 0.0.0.0:
```
bind-address = 0.0.0.0
```

4- Save this file and then run the following command as root:
```
sudo service mysql restart
```


5- Test the connection works
Use Java program named 'CheckConnectivity' to check access to the database from the host.

Run the Java program from the console using maven.

To run it using maven you have to execute the following commands:
```
cd ~/<git_root_folder>/liquibase/product.helloworld.testing.liquibase
mvn clean
mvn test
mvn exec:java  -Dexec.mainClass=database.CheckConnectivity
```


The expected outcome is:
```
Start connectivity test ...
Connection set starts ...
Connection set done!
```



## Use Liquibase to run the updates


### Checking the initial state of the DB

1- Connect to DB with the new created user
```
mysql -u ac -p 
```

**Note**:  

Password is “ac”

2- See the current tables into the DB "hellodb"


```
mysql> use hellodb;
mysql> show tables;

```

3- The expected outcome is:

```
+-------------------+
| Tables_in_hellodb |
+-------------------+
| TEST              |
+-------------------+
1 row in set (0.00 sec)
```

This is the state of the DB in terms of tables before updating with Liquibase.



### Running with native Liquibase command


1- Navigate to directory containing the changeLog (.xml) files 
```
cd ~/<git_root_folder>/liquibase/product.helloworld.testing.liquibase/src/main/resources
```
**Note**:

This directory must contain the following files
```
 db-changelog-1.1.xml
 db-changelog-1.2.xml
 db-changelog-1.3.xml
 db-changelog-1.4.xml
 db-changelog-1.5.xml
 liquibase.properties
 master_1.0.xml
```


2- Run liquibase from command line using either native Liquibase commands or maven

Native command
```
liquibase  update
```

3- The expected outcome is:

```
Liquibase: Update has been successful.
```


**Note**:

Verbose mode
```
liquibase --logLevel debug update
```





### Running with Maven

1- Navigate to the directory that contains the .pom file
```
cd ~/<git_root_folder>/liquibase/product.helloworld.testing.liquibase
```

2- Execute the following command:
```
mvn liquibase:update
```


3- The expected outcome has to contain (almost) at the end:

```
[INFO] BUILD SUCCESS
```



### Checking the state of the DB after the update

1- See the current tables into the DB "hellodb"


```
mysql> show tables;

```

2- The expected outcome is:

```
+-----------------------+
| Tables_in_hellodb     |
+-----------------------+
| DATABASECHANGELOG     |
| DATABASECHANGELOGLOCK |
| LIQ_TEST1             |
| LIQ_TEST2             |
| TEST                  |
+-----------------------+
5 rows in set (0.00 sec)
```

Now, 4 new tables having added: 2 tables are used by Liquibase to keep track of the
 changes made, whereas "LIQ_TEST1" and "LIQ_TEST2" are new tables created during the 
 update.
 
The exact changes that were run by the update are specified in the file
```
master_1.0.xml
```

Up to now, 3 changes have been executed. These changes are included into the files:

```
 db-changelog-1.1.xml
 db-changelog-1.2.xml
 db-changelog-1.3.xml
```

The first file creates the tables "LIQ_TEST1" and "LIQ_TEST2", the second one inserts 
a row in table "LIQ_TEST2", and the last one does it in "LIQ_TEST1". 


```
mysql> show tables;
+-----------------------+
| Tables_in_hellodb     |
+-----------------------+
| DATABASECHANGELOG     |
| DATABASECHANGELOGLOCK |
| LIQ_TEST1             |
| LIQ_TEST2             |
| TEST                  |
+-----------------------+
5 rows in set (0.00 sec)


mysql> select * from LIQ_TEST2;
+----------+------------+----------------+
| test2_id | test2_name | test2_location |
+----------+------------+----------------+
|        1 | sales      | Luxembourg     |
+----------+------------+----------------+
1 row in set (0.00 sec)

mysql> select * from LIQ_TEST1;
+----------+------------+----------------+
| test1_id | test1_name | test1_location |
+----------+------------+----------------+
|        1 | hhrr       | France         |
+----------+------------+----------------+
1 row in set (0.00 sec)

```



### Running more updates


1- Edit file:
```
master_1.0.xml
```

2- Find the following lines
```
 <!--    <include file="./db-changelog-1.4.xml"/> -->
 <!--   <include file="./db-changelog-1.5.xml"/> -->
```

3- Change them to:
```
 <include file="./db-changelog-1.4.xml"/>
 <include file="./db-changelog-1.5.xml"/>
```

4- Save the file and then run again Liquibase using the native command or maven.


The change log 1.4 alters table "LIQ_TEST1" adding a column named "test1_company", and 
then updates the information already available on such a table. This results in:


```
mysql> describe LIQ_TEST1;
+----------------+-------------+------+-----+---------+----------------+
| Field          | Type        | Null | Key | Default | Extra          |
+----------------+-------------+------+-----+---------+----------------+
| test1_id       | int(11)     | NO   | PRI | NULL    | auto_increment |
| test1_name     | varchar(50) | YES  |     | NULL    |                |
| test1_location | varchar(50) | NO   |     | NULL    |                |
| test1_company  | varchar(15) | YES  |     | NULL    |                |
+----------------+-------------+------+-----+---------+----------------+
4 rows in set (0.00 sec)


mysql> select * from LIQ_TEST1;
+----------+------------+----------------+---------------+
| test1_id | test1_name | test1_location | test1_company |
+----------+------------+----------------+---------------+
|        1 | hhrr       | France         | ArcelorMittal |
+----------+------------+----------------+---------------+
1 row in set (0.00 sec)
```

The change log 1.5 inserts more information in table "LIQ_TEST1"

```
mysql> select * from LIQ_TEST1;
+----------+----------------+----------------+---------------+
| test1_id | test1_name     | test1_location | test1_company |
+----------+----------------+----------------+---------------+
|        1 | hhrr           | France         | ArcelorMittal |
|        2 | logistic       | Germany        | BMW           |
|        3 | board          | Russia         | Yandex        |
|        4 | administration | Argentina      | Globant       |
+----------+----------------+----------------+---------------+
4 rows in set (0.00 sec)

```


**Notes**:
There is an extra way to check the modifications made by change log 1.5.

* In file CheckConnectivity.java, uncomment code between lines 18-39 
* Run

```
mvn exec:java  -Dexec.mainClass=database.CheckConnectivity
```


The expected outcome is:

```
Start connectivity test ...
Connection set starts ...
Connection set done!
For row 1, the name=hhrr, the location=France, and the company=ArcelorMittal
For row 2, the name=logistic, the location=Germany, and the company=BMW
For row 3, the name=board, the location=Russia, and the company=Yandex
For row 4, the name=administration, the location=Argentina, and the company=Globant
```





### Starting over with Liquibase changes
You need to restore the database to its original state as before using Liquibase. 
This means you need to remove everything that was added by Liquibase to the database ‘hellodb’

You have two alternatives to do so:

1- Drop every modification added by Liquibase.

```
mysql> drop table LIQ_TEST1;
mysql> drop table LIQ_TEST2;
mysql> drop table DATABASECHANGELOGLOCK;
mysql> drop table DATABASECHANGELOG;
```


2- Restore this database to its original state. 

```
mysql -u root -p  < /vagrant_scripts/db-helloworld.sql
```

**Note**
root's password: 12345678




## Final remarks

These guidelines explain how to use Liquibase:

- to make modifications over a database, and
- to control how the modifications are made, and
- to track these modifications.




