package org.apache.spark.sql

import org.apache.spark.{SparkConf, SparkContext}
import org.junit.{Before, Test}

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-04-18
  *  \* Time: 09:37
  *  \* Description: 
  *  \*/
class SparkHbaseTest {
  var ss:SparkSession = _
  var sc:SparkContext= _
  val tableName = "hhy:t1"
  val familyName = "c1"

  @Before
  def before(): Unit = {
    val conf = new SparkConf()
    conf.set("zk", "cdh217,cdh219,cdh218,cdh207,cdh131")
    conf.set("znode", "/hbase")
    conf.set("rootdir", "hdfs://tdhdfs/opt/hbase")

    ss = SparkSession.builder()
        .master("local")
        .appName("HBaseTest")
        .config(conf)
        .getOrCreate()
    sc = ss.sparkContext
  }

  @Test
  def writeData(): Unit = {
    val data = (0 to 255).map { i =>
      HBaseRecord(i, "extra")
    }
    val spark = ss
    import spark.implicits._
    sc.parallelize(data).toDF.write
        .options(Map(
          "outputTableName" -> tableName,
          "family" -> familyName
        ))
        .format("org.apache.spark.sql.execution.datasources.hbase")
        .save()
  }

  @Test
  def readData(): Unit = {
    val df = ss.read.format("org.apache.spark.sql.execution.datasources.hbase").options(
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
    df.show()
  }
}
