# Chores-Splitter

[![Build Status](https://travis-ci.org/JakubDziworski/Chores-Splitter.svg?branch=master)](https://travis-ci.org/JakubDziworski/Chores-Splitter)

This app let's you split boring house chores between roommates in a fair way.

## Backend
### tests
`cd backend && sbt test`

### running
a) With embedded h2 database
* `cd backend && sbt run`

b) With h2 running as a server
* Change jdbc url in application.conf to `jdbc:h2:tcp://localhost/~/baza2;DB_CLOSE_DELAY=-1;MODE=PostgreSQL`
* Start h2 server (`java -cp h2*.jar org.h2.tools.Server`)
* `cd backend && sbt run`

## Android
### running
* gradle - `android/gradlew assembleDebug` - after that look for apk in `android/app/build/outputs/apk/app-debug.apk`
* ...or just use android studio
