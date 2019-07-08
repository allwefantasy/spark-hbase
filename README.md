# Spark HBase

A HBase datasource implementation for Spark and [MLSQL](http://www.mlsql.tech).
  
## Requirements

This library requires Spark 2.3+/2.4+, HBase 1.2+/2.0+ (tested).

## Liking 

You can link against this library in your program at the following coordinates:

### Scala 2.11

```sql
groupId: tech.mlsql
artifactId: spark-hbase_2.4.3_2.11
version: 0.1.0
```

* 2.4.3 -> Spark version. 2.4.3/2.3.2 available
* 2.11 -> Scala version.  2.11 available
* HBase version by default is 1.2.x; If you wanna support HBase 2.0.x,build manually 

## Build Shade Jar

```shell
-- hbase 1.2.0, spark 2.4.3
mvn clean package -Pshade -Phbase-1.2.x -Pspark-2.4.3 -pl spark-hbase_2.4.3_2.11 -am

-- hbase 1.2.0, spark 2.3.2
mvn clean package -Pshade -Phbase-1.2.x -Pspark-2.4.3 -pl spark-hbase_2.3.2_2.11 -am

-- hbase 2.0.x, spark 2.4.3
mvn clean package -Pshade -Phbase-2.0.x -Pspark-2.3.2 -pl spark-hbase_2.4.3_2.11 -am

-- hbase 2.0.x, spark 2.3.2
mvn clean package -Pshade -Phbase-2.0.x -Pspark-2.3.2 -pl spark-hbase_2.3.2_2.11 -am

```

## Test

```shell
-- hbase 1.2.0, spark 2.4.3
mvn clean test  -Phbase-1.2.x -Pspark-2.4.3 -Ptest-spark-2.4.3 -pl spark-hbase-tests -am

-- hbase 1.2.0, spark 2.3.2
mvn clean test  -Phbase-1.2.x -Pspark-2.4.3 -Ptest-spark-2.4.3 -pl spark-hbase-tests -am

-- hbase 2.0.x, spark 2.4.3
mvn clean test  -Phbase-2.0.x -Pspark-2.3.2 -Ptest-spark-2.3.2 -pl spark-hbase-tests -am

-- hbase 2.0.x, spark 2.3.2
mvn clean test  -Phbase-2.0.x -Pspark-2.3.2 -Ptest-spark-2.3.2 -pl spark-hbase-tests -am
```



## Limitation of 0.1.0

1. Do not support multi families(load/save).
2. Do not support PushDown filter.

## RoadMap

 

## Usage

Spark Code Example:

```scala
val data = (0 to 255).map { i =>
      HBaseRecord(i, "extra")
    }
    val tableName = "t1"
    val familyName = "c1"
    
    
    import spark.implicits._
    sc.parallelize(data).toDF.write
      .options(Map(
        "outputTableName" -> cat,
        "family" -> family
      ) ++ options)
      .format("org.apache.spark.sql.execution.datasources.hbase")
      .save()
      
    val df = spark.read.format("org.apache.spark.sql.execution.datasources.hbase").options(
      Map(
        "inputTableName" -> tableName,
        "family" -> familyName,
        "field.type.col1" -> "BooleanType",
        "field.type.col2" -> "DoubleType",
        "field.type.col3" -> "FloatType",
        "field.type.col4" -> "IntegerType",
        "field.type.col5" -> "LongType",
        "field.type.col6" -> "ShortType",
        "field.type.col7" -> "StringType",
        "field.type.col8" -> "ByteType"
      )
    ).load()    
```


MLSQL Example:

```sql
connect hbase where `zk`="127.0.0.1:2181"
and `family`="cf" as hbase1;

load hbase.`hbase1:mlsql_example`
as mlsql_example;

select * from mlsql_example as show_data;


select '2' as rowkey, 'insert test data' as name as insert_table;

save insert_table as hbase.`hbase1:mlsql_example`;
```

Or with connect statement:

```sql
connect hbase where `zk`="127.0.0.1:2181"
and `tsSuffix`="_ts"
and `family`="cf" as hbase_conn;

select 'a' as id, 1 as ck, 1552419324001 as ck_ts as test ;
save overwrite test
as hbase.`hbase_conn:mlsql_example`
options  rowkey="id";

load hbase.`hbase_conn:mlsql_example`
options `field.type.ck`="IntegerType"
as testhbase;
```

You should configure parameters like `zookeeper.znode.parent`,`hbase.rootdir` according by 
your HBase configuration.  

Parameters：

| Property Name  |  Meaning |
|---|---|
| tsSuffix |to overwrite hbase value's timestamp|
|namespace|hbase namespace|
| family |hbase family，family="" means load all existing families|
| field.type.ck | specify type for ck(field name),now supports:LongType、FloatType、DoubleType、IntegerType、BooleanType、BinaryType、TimestampType、DateType，default: StringType。|




