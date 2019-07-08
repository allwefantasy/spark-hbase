/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * File modified by Hortonworks, Inc. Modifications are also licensed under
 * the Apache Software License, Version 2.0.
 */

package org.apache.spark.sql

import java.io.File

import com.google.common.io.Files
import org.apache.hadoop.hbase.client._
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.{HBaseTestingUtility, TableName}
import org.apache.spark.internal.Logging
import org.apache.spark.sql.execution.datasources.hbase.SparkHBaseConf
import org.apache.spark.{SparkConf, SparkContext}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, FunSuite}

class WithHBaseServerAndSpark extends FunSuite with BeforeAndAfterEach with BeforeAndAfterAll with Logging {
  private[spark] var htu = HBaseTestingUtility.createLocalHTU()

  private[spark] var columnFamily: Array[Byte] = Bytes.toBytes("cf0")
  private[spark] var columnFamilies: Array[Array[Byte]] =
    Array(Bytes.toBytes("cf0"), Bytes.toBytes("cf1"), Bytes.toBytes("cf2"), Bytes.toBytes("cf3"), Bytes.toBytes("cf4"))
  var table: Table = null
  var ss: SparkSession = null
  var sqlContext: SQLContext = null
  var sc: SparkContext = null
  // private[spark] var columnFamilyStr = Bytes.toString(columnFamily)

  val conf = new SparkConf
  conf.set("spark.hbase.connector.test", "true")

  override def beforeAll() {
    val tempDir: File = Files.createTempDir
    tempDir.deleteOnExit
    htu.cleanupTestDir
    htu.startMiniZKCluster
    htu.startMiniHBaseCluster(1, 4)
    logInfo(" - minicluster started")
    println(" - minicluster started")

    SparkHBaseConf.conf = htu.getConfiguration
    ss = SparkSession.builder()
      .master("local")
      .appName("HBaseTest")
      .config(conf)
      .getOrCreate()

    sqlContext = ss.sqlContext
    sc = ss.sparkContext
  }


  override def afterAll() {
    try {
      table.close()
      println("shutdown")
      logInfo("shuting down minicluster")
      htu.shutdownMiniHBaseCluster
      htu.shutdownMiniZKCluster
      logInfo(" - minicluster shut down")
      htu.cleanupTestDir
      ss.close()
    } catch {
      case _: Throwable => logError("teardown error")
    }
  }

  def deleteTable(tableName: Array[Byte]) {
    try {
      htu.deleteTable(TableName.valueOf(tableName))
    } catch {
      case _: Throwable =>
        logInfo(" - no table " + Bytes.toString(tableName) + " found")
    }

  }
}
