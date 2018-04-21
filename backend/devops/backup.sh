#!/bin/sh
java -cp /usr/lib/h2/bin/h2*.jar org.h2.tools.Script -url "jdbc:h2:tcp://localhost:9093/~/chores-splitter" -user sa -script $(date '+/root/chores-splitter/backups/backup_%Y_%m_%d_%H:%M:%S.sql')
