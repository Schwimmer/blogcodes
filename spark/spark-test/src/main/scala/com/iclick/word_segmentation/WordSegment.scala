package com.iclick.word_segmentation

import org.apache.log4j.{ Level, Logger }
import org.apache.spark.{ SparkConf, SparkContext }
import com.iclick.spark.wordSegment.util.CounterMap
import scala.collection.mutable.ArrayBuffer
import com.google.common.collect.Maps
import java.text.SimpleDateFormat
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import scala.collection.mutable.Map
import com.iclick.spark.wordSegment.util.AtomsUitl
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.functions._
import org.apache.spark.sql.SaveMode
import com.iclick.spark.wordSegment.util.ConterHashSet
import org.apache.commons.lang.StringUtils
//import com.mysql.jdbc.Driver

///tmp/yuming/webtable/ds=16-04-17   hadoop数据目录
object WordSegment {
  def main(args: Array[String]): Unit = {
    //关闭一些不必要的日志
    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)

    //master
/*    if (args.length < 5) {
      System.err.println("Usage: path ,maxLen ,pmi, info,shuffle_count")
      System.exit(1)
    }

    val path = args(0).toString
    val maxLen = args(1).toInt
    val pmi = args(2).toDouble
    val info = args(3).toDouble
    val shuffle_count = args(4).toInt
    val save_path_result = if (args.length >= 6) { args(5).toString } else "/tmp/wilson/"

    val conf = new SparkConf().set("spark.driver.maxResultSize", "10g").
      set("spark.sql.shuffle.partitions", s"${shuffle_count}").set("spark.network.timeout", "850s").
      set("spark.shuffle.compress", "true").set("spark.shuffle.spill.compress", "true").set("spark.shuffle.manager", "sort")
    if (System.getProperty("local") != null) {
      conf.setMaster("local").setAppName("wordSegname")
    }
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)*/

    //local
    val conf = new SparkConf().setAppName("wordSegname").setMaster("local[4]").
    set("spark.sql.shuffle.partitions","10").set("spark.network.timeout","30s")
    .set("spark.shuffle.compress","true").set("spark.shuffle.spill.compress","true")
    .set("spark.shuffle.manager","sort")
    val sc = new SparkContext(conf)
    val  sqlContext=new SQLContext(sc)
    val path="/home/david/key_word.txt"
    val maxLen=6
    val pmi=0
    val  info=0
    val save_path_result="D:\\code\\Data\\word"

    //    val word=scala.io.Source.fromFile("D:\\wilson.zhou\\Downloads\\红楼梦.txt").getLines().mkString("")

    val sdf = new java.text.SimpleDateFormat("yyyy-MM-dd:HH:mm:ss")
    var start = sdf.format(System.currentTimeMillis())
    val word1 = sc.textFile(path).map { x =>
      //      replaceAll("[" + AtomsUitl.stopwords + "]", " ")
      val x_filter = x.replaceAll("\\p{Punct}", " ").replaceAll("\\pP", " ").replaceAll("[" + AtomsUitl.stopwords + "]", " ")
        .replaceAll("　", " ").replaceAll("\\p{Blank}", " ").replaceAll("\\p{Space}", " ").replaceAll("\\p{Cntrl}", " ")

      x_filter
    }

    val sum_document = word1.count()

    val word_document = word1.zipWithIndex.filter { x => !StringUtils.isBlank(x._1) }.flatMap { x =>
      val arr = ArrayBuffer[(String, Int)]()
      val line = x._1.split(" ")
      for (i <- line) {
        arr += ((i, x._2.toInt))
      }
      arr
    }.map { x => (x._1.trim, x._2) }.filter(x => !StringUtils.isBlank(x._1))

    println("Calculate the iterms  documnt")

    val word_document_caculate = word_document.map { x => ("$" + x._1 + "$", x._2) }.flatMap {
      x =>
        var arr = ArrayBuffer[(String, Int)]()
        for (y <- 1 to AtomsUitl.len(x._1) - 2) {
          arr += ((AtomsUitl.substring(x._1, y, Math.min(maxLen + y, AtomsUitl.len(x._1))), x._2))
        }
        arr
    }.sortBy(x => x._1)

    println("documnet   caculate  will  start")

    val word_document_result = word_document_caculate.map {
      x =>
        val first = AtomsUitl.substring(x._1, 0, 1)
        (first, x._1, x._2)
    }.groupBy((f: (String, String, Int)) => f._1).map {
      x => x._2
    }.flatMap {

      x =>

        val documnet = Maps.newHashMap[String, ConterHashSet]
        var arrBuff = ArrayBuffer[(String, Int)]()

        for (curr <- x) {
          for (ii <- 1 to AtomsUitl.len(curr._2) - 1) {
            val w1 = AtomsUitl.substring(curr._2, 0, ii)
            if (documnet.containsKey(w1)) {
              documnet.get(w1).addelment(curr._3.asInstanceOf[java.lang.Integer])
            } else {
              val cm = new ConterHashSet();
              cm.addelment(curr._3.asInstanceOf[java.lang.Integer])
              documnet.put(w1, cm)
            }
          }
        }
        val documnet_iter = documnet.keySet.iterator
        while (documnet_iter.hasNext()) {
          val w = documnet_iter.next()
          val freq = documnet.get(w).getsize()
          arrBuff += ((w, freq))
        }
        arrBuff
    }

    //    word_document_result.take(20).foreach(println)
    //    println("word_document_result's count："+word_document_result.count())

    println("information entropy and information")
    val word = word1.flatMap { x =>
      val line = x.split(" ")
      line
    }.filter(x => !StringUtils.isBlank(x))

    val sum_word = word.map(x =>
      AtomsUitl.len(x)).sum()
    println("sum_word的长度是：" + sum_word)

    //  //计算左信息熵做准备
    println("Calculate the left word information entropy and information entropy .....")

    val wordleft = word.map(x => AtomsUitl.reverse(x)).map { x => "$" + x + "$" }.flatMap {
      x =>
        var arr = ArrayBuffer[String]()
        for (y <- 1 to AtomsUitl.len(x) - 2) {
          //             arr+=x.substring(y, Math.min(maxLen + y,  x.length()))
          arr += AtomsUitl.substring(x, y, Math.min(maxLen + y, AtomsUitl.len(x)))
        }
        arr
    }.sortBy(x => x)

    val wordleft_caculate = wordleft.map {
      s =>
        //            val first=s.substring(0, 1).toString()
        val first = AtomsUitl.substring(s, 0, 1).toString

        (first, s)
    }.groupBy((f: (String, String)) => f._1).map {
      x => x._2
    }.flatMap {
      x =>
        val stat = Maps.newHashMap[String, CounterMap]()
        var arrBuff = ArrayBuffer[(String, Double)]()
        for (curr <- x) {
          for (ii <- 1 to AtomsUitl.len(curr._2) - 1) {

            //                  val w = curr._2.substring(0,ii)
            val w = AtomsUitl.substring(curr._2, 0, ii)
            //                  val suffix = curr._2.substring(ii).substring(0, 1)

            val suffix = AtomsUitl.substring(AtomsUitl.substring(curr._2, ii), 0, 1)

            if (stat.containsKey(w)) {
              stat.get(w).incr(suffix)
            } else {
              val cm = new CounterMap()
              cm.incr(suffix)
              stat.put(w, cm)
            }
          }
        }
        var iterator_stat = stat.keySet().iterator()
        while (iterator_stat.hasNext()) {
          var w = iterator_stat.next()
          var cm = stat.get(w);
          var freq = 0
          var re = 0.0

          var cm_iter = cm.countAll().keySet().iterator()
          while (cm_iter.hasNext()) {
            freq += cm.get(cm_iter.next())
          }
          var cm_iter1 = cm.countAll().keySet().iterator()
          while (cm_iter1.hasNext()) {
            var p = cm.get(cm_iter1.next()) * 1.0 / freq
            re += -1 * Math.log(p) * p
          }
          //                  print("freq的值是："+freq+"    ")
          //                  println("re的值是："+re)

          arrBuff += ((AtomsUitl.reverse(w), re))
        }
        arrBuff
    }

    //      wordleft_caculate.take(20).foreach(println)
    //      println("左邻信息个个数是："+wordleft_caculate.count())
    //      println(wordleft_caculate.map(x=>x._1).distinct().count())

    //     println("wordleft'coutn----->"+wordleft.count)

    //计算右信息熵做准备
    println("Calculate the  right word information entropy and information entropy .....")
    val wordright = word.map { x => "$" + x + "$" }.flatMap {
      x =>
        var arr = ArrayBuffer[String]()
        //         AtomsUitl.len(x)-2
        for (y <- 1 to AtomsUitl.len(x) - 2) {
          //             arr+=x.substring(y, java.lang.Math.min(maxLen + y,  x.length()))
          arr += (AtomsUitl.substring(x, y, Math.min(maxLen + y, AtomsUitl.len(x))))
        }
        arr
    }.sortBy(x => x)

    //计算右邻字信息熵
    val wordright_caculate = wordright.map {
      s =>
        //            val first=s.substring(0, 1).toString()

        val first = AtomsUitl.substring(s, 0, 1).toString()
        (first, s)
    }.groupBy((f: (String, String)) => f._1).map {
      x => x._2
    }.flatMap {
      x =>
        var stat = Maps.newHashMap[String, CounterMap]()
        var arrBuff = ArrayBuffer[(String, Int, Double)]()
        for (curr <- x) {
          for (i <- 1 to AtomsUitl.len(curr._2) - 1) {
            //                  val w = curr._2.substring(0, i)
            val w = AtomsUitl.substring(curr._2, 0, i)

            //                  val suffix = curr._2.substring(i).substring(0, 1)
            val suffix = AtomsUitl.substring(AtomsUitl.substring(curr._2, i), 0, 1).toString
            if (stat.containsKey(w)) {
              stat.get(w).incr(suffix);
            } else {
              val cm = new CounterMap();
              cm.incr(suffix);
              stat.put(w, cm);
            }
          }
        }

        var iterator_stat = stat.keySet().iterator()
        while (iterator_stat.hasNext()) {
          var w = iterator_stat.next()
          var cm = stat.get(w);
          var freq = 0
          var re = 0.0

          var cm_iter = cm.countAll().keySet().iterator()
          while (cm_iter.hasNext()) {
            freq += cm.get(cm_iter.next())
          }
          var cm_iter1 = cm.countAll().keySet().iterator()
          while (cm_iter1.hasNext()) {
            var p = cm.get(cm_iter1.next()) * 1.0 / freq
            re += -1 * Math.log(p) * p
          }

          //              print("w的值是："+w+" ")
          //              print("freq的值是："+freq+" ")
          //              println("re的值是"+re)

          arrBuff += ((w, freq, re))
        }
        arrBuff
    }
    //    println("计算右邻信息前20条")
    //    wordright_caculate.take(20).foreach(println)
    //    println("右信息表的总共个数："+wordright_caculate.count())

    //    wordright_caculate.
    //左右合并开始
    println(" Merge  will begin to  calculated..............")
    import sqlContext.implicits._
    /*  val  word_caculate_total1=wordright_caculate.union(wordleft_caculate).sortBy(x=>x).groupBy((f:(String,Int,Double))=>f._1,20).map(x=>x._2)
   val  word_caculate_total= word_caculate_total1.map{
          x=>
         val  hashtable=new java.util.Hashtable[String,String]()
         hashtable.put("name","null")
         hashtable.put("freq","0")
         hashtable.put("e",java.lang.Double.MAX_VALUE.toString())
      for(str<-x){

         hashtable.put("name",str._1)

         if(str._2!= -20){
          hashtable.put("freq",String.valueOf(str._2))
         }

         if(str._3<java.lang.Double.parseDouble(hashtable.get("e"))){
           hashtable.put("e",String.valueOf(str._3))
         }

       }

      (hashtable.get("name") ,hashtable.get("freq").toInt,hashtable.get("e").toDouble)
       }.filter(x=> !StringUtils.isBlank(x._1) && x._1.length>1)*/

    val wordright_caculate_todf = wordright_caculate.toDF("right_name", "freq", "right_info")
    val wordleft_caculate_todf = wordleft_caculate.toDF("left_name", "left_info")
    val udf_get_min: ((Double, Double) => Double) = (arg1: Double, arg2: Double) => Math.min(arg1, arg2)
    val sqlfunctin = udf(udf_get_min)
    val word_caculate_total = wordright_caculate_todf.join(wordleft_caculate_todf, wordright_caculate_todf("right_name") === wordleft_caculate_todf("left_name"), "left").
      withColumn("info", sqlfunctin(col("right_info"), col("left_info"))).drop("right_info").
      drop("left_name").drop("left_info").filter(length(wordright_caculate_todf("right_name")) > 1).rdd

    //  wordright_caculate.union(wordleft_caculate).groupBy((f:(String,Int,Double))=>f._1).map(x=>x._2).take(20).foreach(println)

    //  println("计算凝固度")
    //  val  size_pmi=wordright_caculate.count()
    //  println("最后步骤中的size的总数是："+size_pmi)
    println("map_total has down")
    //计算凝固度
    val last = word_caculate_total.flatMap {
      x =>
        var w = x.apply(0).toString
        var f = x.apply(1).toString.toInt
        var e = x.apply(2).toString.toDouble

        //       var w=x._1
        //      var f=x._2
        //      var  e=x._3
        var arr = ArrayBuffer[(String, Int, Double, String, String)]()
        for (s <- 1 to AtomsUitl.len(w) - 1) {
          //        var  lw=w.substring(0,s)
          try {
            var lw = AtomsUitl.substring(w, 0, s)
            //        var  rw=w.substring(s)

            var rw = AtomsUitl.substring(w, s)
            arr += ((w, f, e, lw, rw))
          } catch {
            case e: Exception => arr += (("", 0, 0.0, "", ""))

          }

        }

        arr
    }.filter(f => !StringUtils.isBlank(f._4) && !StringUtils.isBlank(f._5))

    println("dataframe merge  will begin to  calculated..............")
    //        last.take(30).foreach(println)

    val df = last.toDF("w_total", "f", "e", "lw", "rw")
    val df1 = wordright_caculate.toDF("w", "freq", "re")

    val df2_drop = df.join(df1, df("lw") === df1("w"), "left").drop("re").drop("w").withColumnRenamed("freq", "lw_freq")
    //       val df2_drop=df2.drop("re").drop("w").withColumnRenamed("freq", "lw_freq")
    val df3_drop = df2_drop.join(df1, df2_drop("rw") === df1("w"), "left").drop("re").drop("w").withColumnRenamed("freq", "rw_freq")
    //       val df3_drop=df3.drop("re").drop("w").withColumnRenamed("freq", "rw_freq")

    //       948014
    //凝固度計算
    /*val result=df3_drop.rdd.groupBy{f=>f(0)}.map{
           x=>
        val map=new java.util.HashMap[String,String]()
        map.put("max","1")
        for(i<-x._2){
          map.put("w_total",i.apply(0).toString)
          map.put("f",i.apply(1).toString)
          map.put("e",i.apply(2).toString)

          var  ff:java.lang.Long=try{
            i.apply(5).toString.toLong*i.apply(6).toString.toLong
          }catch{
            case e:Exception=>1l
          }
          if(ff>map.get("max").toLong){
              map.put("max",ff.toString)
          }
         }
           var   pf=map.get("f").toLong*size_pmi*1.0/map.get("max").toLong
                var pmi=Math.log(pf)

          var  w_total= map.get("w_total")
          var f=map.get("f").toInt
          var e=map.get("e").toDouble
          map.clear()
                   (w_total,f,pmi,e,0)
//        ( map.get("w_total"),map.get("f").toInt ,pmi,map.get("e").toDouble,0)
        }.filter(f=>f._3>pmi&& f._4>info&& !StringUtils.isBlank(f._1))


       val  resultToDf=  result.toDF("name","freq","pmi","info","zero")
       */

    println("dataframe join has down")

    //计算凝聚度 改用DataFrame的形式
    val udf_get_pmi = (arg1: Int, arg2: Int, arg3: Int) => Math.log((arg1.toLong * sum_word.toLong * 1.0) / (arg2.toLong * arg3.toLong))
    val udf_get_pmi_udf = udf(udf_get_pmi)

    val resultToDf = df3_drop.withColumn("pmi", udf_get_pmi_udf(col("f"), col("rw_freq"), col("lw_freq"))).withColumn("zero", col("f") * 0).
      drop("rw_freq").drop("lw_freq").drop("lw").drop("rw").sort($"w_total", $"pmi").dropDuplicates(Array("w_total")).
      filter($"pmi" > pmi && $"e" > info).withColumnRenamed("w_total", "name").withColumnRenamed("f", "freq").withColumnRenamed("e", "info")

    println("The final result will be caculated")
    val word_document_resultToDf = word_document_result.toDF("name1", "document")
    val resultToDf2 = resultToDf.join(word_document_resultToDf, word_document_resultToDf("name1") === resultToDf("name"), "left").
      withColumn("documentcount", col("zero") + sum_document).drop("zero").drop("name1")
    //  val resultToDf2 =resultToDf1.withColumn("documentcount",col("zero")+sum_document).drop("zero").drop("name1")
    //       resultToDf2.show(20)
    //       互信息    凝聚度pmi
    //      左右熵  e

    //把结果存入到hdfs中
    println("Results will stored into  HDFS.")
    val sdf1 = new SimpleDateFormat("yy-MM-dd")
    val save_path = save_path_result + sdf1.format(System.currentTimeMillis())
    try {
      resultToDf2.rdd.map {
        x =>
          var name = x.apply(0).toString
          var freq = x.apply(1).toString
          var entropy = x.apply(2).toString
          var info = x.apply(3).toString
          var document = x.apply(4).toString
          var documenttotal = x.apply(5).toString
          s"${name},${freq},${info},${entropy},${document},${documenttotal}"
      }.saveAsTextFile(save_path)
      println("....................sucess.............")
      //      resultToDf2.rdd.repartition(1).saveAsTextFile(save_path)
    } catch {
      case e: Exception => println("some errors  happend  when sava  the last datas")
    }

    //把结果插入到mysql数据库中
    /*  val  driver="com.mysql.jdbc.Driver"
   Class.forName(driver)
         val url ="jdbc:mysql://10.1.1.28:3306/spark"
     val pro=new java.util.Properties
     pro.setProperty("user","usr_dba")
     pro.setProperty("password","4rfv%TGB^YHN")
     pro.setProperty("use_unicode", "true")
     pro.setProperty("characterEncoding", "utf8")
     resultToDf2.write.mode(SaveMode.Overwrite).jdbc(url, "wordsegment",pro)
     */

    println(start)
    println(sdf.format(System.currentTimeMillis()))
    sc.stop()

  }
}