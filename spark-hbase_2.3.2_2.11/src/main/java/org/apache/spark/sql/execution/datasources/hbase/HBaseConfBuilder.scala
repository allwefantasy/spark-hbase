package org.apache.spark.sql.execution.datasources.hbase

import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.spark.sql.SparkSession
import org.json4s.DefaultFormats
import scala.collection.JavaConversions._

/**
  * 2019-07-08 WilliamZhu(allwefantasy@gmail.com)
  */
object HBaseConfBuilder {
  def build(spark: SparkSession, parameters: Map[String, String]) = {
    val testConf = spark.sqlContext.sparkContext.conf.getBoolean(SparkHBaseConf.testConf, false)
    if (testConf) SparkHBaseConf.conf
    else {
      implicit val formats = DefaultFormats

      // task is already broadcast; since hConf is per HBaseRelation (currently), broadcast'ing
      // it again does not help - it actually hurts. When we add support for
      // caching hConf across HBaseRelation, we can revisit broadcast'ing it (with a caching
      // mechanism in place)
      val hc = HBaseConfiguration.create()
      
      if (parameters.containsKey("zk") || parameters.containsKey("hbase.zookeeper.quorum")) {
        hc.set("hbase.zookeeper.quorum", parameters.getOrElse("zk", parameters.getOrElse("hbase.zookeeper.quorum", "127.0.0.1:2181")))
      }

      if (parameters.containsKey("znode")) {
        hc.set("zookeeper.znode.parent", parameters.get("znode").get)
      }

      if (parameters.containsKey("rootdir")) {
        hc.set("hbase.rootdir", parameters.get("rootdir").get)
      }

      /**
        * when people confgiure the wrong zk address, by default the HBase client will
        * try infinitely. We should control this group parameters to limit the try times.
        */
      hc.set("hbase.client.pause", parameters.getOrElse("hbase.client.pause", "1000"))
      hc.set("zookeeper.recovery.retry", parameters.getOrElse("zookeeper.recovery.retry", "60"))
      hc.set("hbase.client.retries.number", parameters.getOrElse("hbase.client.retries.number", "60"))


      parameters.filter { f =>
        f._1.startsWith("hbase.") || f._1.startsWith("zookeeper.") || f._1.startsWith("phoenix.")
      }.foreach { f =>
        hc.set(f._1, f._2)
      }

      hc
    }

  }
}
