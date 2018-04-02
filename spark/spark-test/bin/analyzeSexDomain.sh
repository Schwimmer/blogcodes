#!/bin/sh
if [ ! $1 ]; then
    date=`date -d "0 day ago" +"%y-%m-%d"`
else
    date=$1
fi

P_HOME="/opt/spark-buzzads"
cd ${P_HOME}
CLASSPATH=${P_HOME}/spark-buzzads-1.0.jar
for f in ${P_HOME}/lib/*.jar; do
  CLASSPATH=${CLASSPATH},$f
done
/usr/lib/spark/bin/spark-submit --master spark://srv-buzz-bosk1:7077 --files conf/config.properties --files brandsafe.txt --jars $CLASSPATH --executor-memory 50g --dri
ver-memory 50g --conf spark.akka.frameSize=2000 --class com.iclick.spark.buzzads.stats.ExtractBrandsafe spark-buzzads-1.0.jar 30 $date