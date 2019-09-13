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
 */

package org.apache.spark.examples

import java.util.Random

import org.apache.spark.{SparkConf, SparkContext, SparkFiles}
import org.apache.spark.SparkContext._
import org.apache.spark.storage.StorageLevel

/**
  * Usage: GroupByTest [numMappers] [numKVPairs] [KeySize] [numReducers]
  */
object GroupByTest {
    def main(args: Array[String]) {
        val sparkConf = new SparkConf().setAppName("GroupBy Test").setMaster("local")

        var numMappers = if (args.length > 0) args(0).toInt else 2
        var numKVPairs = if (args.length > 1) args(1).toInt else 1000
        var valSize = if (args.length > 2) args(2).toInt else 1000
        var numReducers = if (args.length > 3) args(3).toInt else numMappers

        val sc = new SparkContext(sparkConf)


        SparkFiles
        //sc.killExecutor("")
        sc.version
        sc.getPersistentRDDs
        sc.taskScheduler
        sc.schedulerBackend


        val pairs1 = sc.parallelize(0 until numMappers, numMappers).flatMap { p =>
            val ranGen = new Random
            var arr1 = new Array[(Int, Array[Byte])](numKVPairs)
            for (i <- 0 until numKVPairs) {
                val byteArr = new Array[Byte](valSize)
                ranGen.nextBytes(byteArr)
                arr1(i) = (ranGen.nextInt(Int.MaxValue), byteArr)
            }
            arr1
        }.persist(StorageLevel.DISK_ONLY)
        // Enforce that everything has been calculated and in cache
        pairs1.count()

        println(pairs1.groupByKey(numReducers).count())

        sc.stop()
    }
}