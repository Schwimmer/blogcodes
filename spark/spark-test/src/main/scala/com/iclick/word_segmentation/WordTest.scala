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

/**
 * @author David
 */
object WordTest {
  def main(args: Array[String]): Unit = {
    Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)
    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)

    //local
    val conf = new SparkConf().setAppName("wordSegname").setMaster("local[1]").
      set("spark.sql.shuffle.partitions", "1").set("spark.network.timeout", "30s")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)
    val path = "/Users/david/david/code/data/敏感词库/百度敏感词.txt"
    val maxLen = 5
    val pmi = 0
    val info = 0
    val save_path_result = "/User/david/david/code/result.txt"

    val sdf = new java.text.SimpleDateFormat("yyyy-MM-dd:HH:mm:ss")
    var start = sdf.format(System.currentTimeMillis())
    val word1 = sc.textFile(path).map { x =>
      val x_filter = x.replaceAll("\\p{Punct}", " ").replaceAll("\\pP", " ")
        .replaceAll("　", " ").replaceAll("[" + AtomsUitl.stopwords + "]", " ").replaceAll("\\p{Blank}", " ").replaceAll("\\p{Space}", " ").replaceAll("\\p{Cntrl}", " ")
      x_filter
    }
    
    println("word1")
    word1.foreach ( x=>println(x))

    val sum_document = word1.count()

    // 用空格和行号分隔成短句
    val word_document = word1.zipWithIndex.filter { x => !StringUtils.isBlank(x._1) }.flatMap { x =>
      val arr = ArrayBuffer[(String, Int)]()
      val line = x._1.split(" ")
      for (i <- line) {
        arr += ((i, x._2.toInt))
      }
      arr
    }.map { x => (x._1.trim, x._2) }.filter(x => !StringUtils.isBlank(x._1))

    //对每个分割的短句，从第一个词开始每次至多取5个，与index构成map
    val word_document_caculate = word_document.map { x => ("$" + x._1 + "$", x._2) }.flatMap {
      x =>
        var arr = ArrayBuffer[(String, Int)]()
        for (y <- 1 to AtomsUitl.len(x._1) - 2) {
          arr += ((AtomsUitl.substring(x._1, y, Math.min(maxLen + y, AtomsUitl.len(x._1))), x._2))
        }
        arr
    }
//    }.sortBy(x => x._1)

    println("word_document_calculate")
    word_document_caculate.foreach{ x => println(x)}

    //提取所有的词的组合，以及词频。但是会分出重复的词，不解
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
    
    println("\n\nword_document_result")
    word_document_result.foreach { x => println(x) }
    
    // 求的是所有词的总字数？
    val sum_word = word_document_result.map{x => AtomsUitl.len(x._1)}.sum()

    val word=word1.flatMap{x=>
      val  line=x.split(" ")
      line
    }.filter(x=> !StringUtils.isBlank(x))
    
    word.foreach { x => println(x)}

    //将每个句子倒序排列，提取每个子集
    val wordleft = word.map(x => AtomsUitl.reverse(x)).map { x => "$" + x + "$" }.flatMap {
      x =>
        var arr = ArrayBuffer[String]()
        for (y <- 1 to AtomsUitl.len(x) - 2) {
          //             arr+=x.substring(y, Math.min(maxLen + y,  x.length()))
          arr += AtomsUitl.substring(x, y, Math.min(maxLen + y, AtomsUitl.len(x)))
        }
        arr
    }.sortBy(x => x)
    
    println("wordleft")
    wordleft.foreach (x => println(x))
    
    //  //计算左信息熵做准备
    println("Calculate the left word information entropy and information entropy .....")

    val wordleft_caculate = wordleft.map {
      s =>
        val first = AtomsUitl.substring(s, 0, 1).toString
        (first, s)
    }.groupBy(f => f._1).map {
      x => x._2
    }.flatMap {
      x => 
        val stat = Maps.newHashMap[String, CounterMap]()
        var arrBuff = ArrayBuffer[(String, Double)]()
        for (curr <- x) {
          for (ii <- 1 to AtomsUitl.len(curr._2) - 1) {
            val w = AtomsUitl.substring(curr._2, 0, ii)
            val suffix = AtomsUitl.substring(AtomsUitl.substring(curr._2, ii), 0, 1)
//存储每个词和对应的左词的个数
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
          arrBuff += ((AtomsUitl.reverse(w), re))
        }
        arrBuff
    }.foreach{x => println(x)}

  }
}