# Spark HBase

A HBase datasource implementation for Spark and [MLSQL](http://www.mlsql.tech).
  
## Requirements

This library requires Spark 2.3+/2.4+ (tested).

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

## Build Shade Jar

```sql
mvn -Pshade -Pspark-2.4.x clean package
```

## Usage

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




