#!/bin/sh
java -cp /usr/lib/h2/bin/h2*.jar org.h2.tools.Script -url "jdbc:h2:tcp://localhost/~/baza5" -user sa -script $(date '+/root/backups/backup_%Y_%m_%d_%H:%M:%S.sql')
