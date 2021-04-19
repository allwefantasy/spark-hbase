package org.apache.spark.sql

import org.apache.hadoop.hbase.util.Bytes

/**
  * 2019-07-08 WilliamZhu(allwefantasy@gmail.com)
  */
class ReadWriteTestSuite extends WithHBaseServerAndSpark {

  test("save table") {
    val data = (0 to 255).map { i =>
      HBaseRecord(i, "extra")
    }
    val tableName = "t1"
    val familyName = "c1"
    deleteTable(Bytes.toBytes(tableName))
    persistDataInHBase(tableName, familyName, data)
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
    assert(df.count() == 256)
  }


  def persistDataInHBase(cat: String, family: String, data: Seq[HBaseRecord], options: Map[String, String] = Map.empty): Unit = {
    val spark = ss
    import spark.implicits._
    sc.parallelize(data).toDF.write
      .options(Map(
        "outputTableName" -> cat,
        "family" -> family
      ) ++ options)
      .format("org.apache.spark.sql.execution.datasources.hbase")
      .save()
  }
}

case class HBaseRecord(
                        rowkey: String,
                        col0: String,
                        col1: Boolean,
                        col2: Double,
                        col3: Float,
                        col4: Int,
                        col5: Long,
                        col6: Short,
                        col7: String,
                        col8: Byte)

object HBaseRecord {
  def apply(i: Int, t: String): HBaseRecord = {
    val s = s"""row${"%03d".format(i)}"""
    HBaseRecord(s, s,
      i % 2 == 0,
      i.toDouble,
      i.toFloat,
      i,
      i.toLong,
      i.toShort,
      s"String$i: $t",
      i.toByte)
  }

  def unpadded(i: Int, t: String): HBaseRecord = {
    val s = s"""row${i}"""
    HBaseRecord(s, s,
      i % 2 == 0,
      i.toDouble,
      i.toFloat,
      i,
      i.toLong,
      i.toShort,
      s"String$i: $t",
      i.toByte)
  }
}

case class HBaseMultiFamilyRecord(
                          rowkey: String,
                          `c1:col0`: String,
                          `c1:col1`: String,
                          `c2:col0`: String,
                          `c2:col1`: String,
                          `c3:col0`: String,
                          `c3:col1`: String,
                          `c4:col0`: String,
                          `c4:col1`: String)

object HBaseMultiFamilyRecord {
  def apply(i: Int, t: String): HBaseMultiFamilyRecord = {
    val s = s"""row${"%03d".format(i)}"""
    HBaseMultiFamilyRecord(s,
      s,
      s"String$i: $t",
      s,
      s"String$i: $t",
      s,
      s"String$i: $t",
      s,
      s"String$i: $t")
  }
}