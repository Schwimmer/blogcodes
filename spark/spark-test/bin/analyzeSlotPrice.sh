1 #!/bin/sh
  2 P_HOME="/opt/spark-buzzads"
  3 cd ${P_HOME}
  4 curdate=`date -d "-1 days ago" +%y-%m-%d`
  5 CLASSPATH=${P_HOME}/spark-buzzads-1.0.jar
  6 for f in ${P_HOME}/lib/*.jar; do
  7   CLASSPATH=${CLASSPATH},$f
  8 done
  9 nohup /usr/lib/spark/bin/spark-submit --master spark://srv-buzz-bosk1:7077 --files conf/config.properties --jars $CLASSPATH --executor-memory 50g --driver-memory 50g --conf spark.akka.frameSize=2000 --class com.iclick.spark.buzzads.stats.AnalyzeSlotPrice spark-buzzads-1.0.jar 2 $curdate >> logs/analyzeSlotPrice.log &