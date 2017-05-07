#/bin/sh
java -cp /usr/lib/h2/bin/h2*.jar org.h2.tools.Server >> db.log &
sbt run >> app.log &