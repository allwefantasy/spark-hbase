package org.apache.spark.sql

import org.apache.spark.SparkContext
import org.junit.{Before, Test}

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-04-18
  *  \* Time: 09:37
  *  \* Description: 
  *  \*/
class SparkHbaseTest {
  var ss: SparkSession = _
  var sc: SparkContext = _
  val tableName = "hhy:t3"
  val familyName = "c1"

  val multiTableName = "hhy:m1"
  val multiFamilyName = "c1"
  val defaultM = Map(
    "hbase.zookeeper.quorum" -> "cdh217,cdh219,cdh218,cdh207,cdh131",
    "zookeeper.znode.parent" -> "/hbase",
    "hbase.rootdir" -> "hdfs://tdhdfs/opt/hbase"
  )

  @Before
  def before(): Unit = {
    ss = SparkSession.builder()
        .master("local")
        .appName("HBaseTest")
        .getOrCreate()
    sc = ss.sparkContext
  }

  @Test
  def writeData(): Unit = {
    val data = (256 to 300).map { i =>
      HBaseRecord(i, "extra")
    }
    val spark = ss
    import spark.implicits._
    sc.parallelize(data).toDF.write
        .options(Map(
          "outputTableName" -> tableName,
          "family" -> familyName
        ) ++ defaultM)
        .format("org.apache.spark.sql.execution.datasources.hbase")
        .save()
  }

  @Test
  def readData(): Unit = {
    val df = ss.read.format("org.apache.spark.sql.execution.datasources.hbase").options(
      Map(
        "inputTableName" -> tableName,
        //        "family" -> familyName,
        "field.type.col1" -> "BooleanType",
        "field.type.col2" -> "DoubleType",
        "field.type.col3" -> "FloatType",
        "field.type.col4" -> "IntegerType",
        "field.type.col5" -> "LongType",
        "field.type.col6" -> "ShortType",
        "field.type.col7" -> "StringType",
        "field.type.col8" -> "ByteType"
      ) ++ defaultM
    ).load()
    df.show(1000, false)
  }


  @Test
  def writeDataMultiFamily(): Unit = {
    val data = (0 to 200).map { i =>
      HBaseMultiFamilyRecord(i, "extra")
    }
    val spark = ss
    import spark.implicits._
    sc.parallelize(data).toDF.write
        .options(Map(
          "outputTableName" -> multiTableName,
          "family" -> multiFamilyName
        ) ++ defaultM)
        .format("org.apache.spark.sql.execution.datasources.hbase")
        .save()
  }

  @Test
  def readDataMultiFamily(): Unit = {
    val df = ss.read.format("org.apache.spark.sql.execution.datasources.hbase").options(
      Map(
        "inputTableName" -> multiTableName,
        //        "family" -> familyName,
        "field.type.c1:col0" -> "StringType",
        "field.type.c1:col1" -> "StringType",
        "field.type.c2:col0" -> "StringType",
        "field.type.c2:col1" -> "StringType",
        "field.type.c3:col0" -> "StringType",
        "field.type.c4:col1" -> "StringType"
      ) ++ defaultM
    ).load()
    df.show(1000, false)
  }
}
