package org.apache.spark.sql.execution.datasources.hbase

import org.apache.hadoop.conf.Configuration

/**
  * 2019-07-08 WilliamZhu(allwefantasy@gmail.com)
  */
object SparkHBaseConf {
  val testConf = "spark.hbase.connector.test"
  val credentialsManagerEnabled = "spark.hbase.connector.security.credentials.enabled"
  val expireTimeFraction = "spark.hbase.connector.security.credentials.expireTimeFraction"
  val refreshTimeFraction = "spark.hbase.connector.security.credentials.refreshTimeFraction"
  val refreshDurationMins = "spark.hbase.connector.security.credentials.refreshDurationMins"
  val principal = "spark.hbase.connector.security.credentials"
  val keytab = "spark.hbase.connector.security.keytab"

  var conf: Configuration = _
  var BulkGetSize = "spark.hbase.connector.bulkGetSize"
  var defaultBulkGetSize = 100
  var CachingSize = "spark.hbase.connector.cacheSize"
  var defaultCachingSize = 100
  // in milliseconds
  val connectionCloseDelay = 10 * 60 * 1000
}
