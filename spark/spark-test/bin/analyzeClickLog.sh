#!/bin/sh

fn=`date "+%Y%m%d"`
PIG_HOME="/opt/spark-buzzads/"
PIG_EXT_LOG="${PIG_HOME}/logs/analyze_click_log_${fn}.log"
Yesterday=`date -d -1days "+%Y-%m-%d"`
Today=`date "+%y-%m-%d"`

CLASSPATH=${PIG_HOME}/spark-buzzads-1.0.jar
for f in ${PIG_HOME}/lib/*.jar; do
  CLASSPATH=${CLASSPATH},$f
done
nohup /usr/lib/spark/bin/spark-submit --master spark://srv-buzz-bosk1:7077 --files conf/config.properties --jars $CLASSPATH --executor-memory 100g --driver-memory 100g --conf spark.akka.frameSize=2000 --class com.iclick.spark.buzzads.stats.AnalyzeClickLog spark-buzzads-1.0.jar /shortdata/ad_click_log 7 ${Today} /shortdata/ad_click_stat >>${PIG_EXT_LOG} 2>&1 &
#nohup /usr/lib/spark/bin/spark-submit --master local --files ${PIG_HOME}/conf/config.properties --jars $CLASSPATH --executor-memory 100g --driver-memory 100g --conf spark.akka.frameSize=2000 --class com.iclick.spark.buzzads.stats.AnalyzeClickLog ${PIG_HOME}/spark-buzzads-1.0.jar /shortdata/ad_click_log/ 1 ${Today} /tmp/yuming/shortdata/ad_click_stat >${PIG_EXT_LOG} 2>&1 &

