#!/usr/bin/env bash
mvn clean package -Pshade -pl spark-hbase_2.4.3_2.11 -am
# mvn clean package 