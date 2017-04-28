
name := "chores-splitter"

version := "1.0"

scalaVersion := "2.11.8"

val akkaV = "2.4.11"
val logbackV = "1.1.7"
val scalaLoggingV = "3.5.0"
val scalaTestV = "3.0.0"
val slickV = "3.2.0"

val dbDeps = Seq (
  "com.typesafe.slick" %% "slick" % slickV,
  "com.typesafe.slick" %% "slick-codegen" % slickV,
  "com.h2database" % "h2" % "1.4.191",
  "org.flywaydb" % "flyway-core" % "3.2.1"
)

val akkaDeps = Seq(
  "com.typesafe.akka" %% "akka-http-core" % akkaV,
  "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaV
)

val testDeps = Seq(
  "org.scalatest" %% "scalatest" % scalaTestV % "test",
  "com.typesafe.akka" %% "akka-stream-testkit" % akkaV % "test",
  "com.typesafe.akka" %% "akka-http-testkit" % akkaV % "test"
)

val loggingDeps = Seq(
  "ch.qos.logback" % "logback-classic" % logbackV,
  "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingV
)

val variousDeps = Seq(
  "org.typelevel" % "cats_2.11" % "0.7.2",
  "joda-time" % "joda-time" % "2.9.5",
  "org.joda" % "joda-convert" % "1.8.1"
)

libraryDependencies ++= akkaDeps ++ loggingDeps ++ testDeps ++ variousDeps ++ dbDeps
slick <<= slickCodeGenTask
sourceGenerators in Compile  <+= slickCodeGenTask


lazy val slick = TaskKey[Seq[File]]("gen-tables")
lazy val slickCodeGenTask = (sourceManaged, dependencyClasspath in Compile, runner in Compile, streams) map { (dir, cp, r, s) =>
  val outputDir = dir.getPath // place generated files in sbt's managed sources folder
  val url = "jdbc:h2:mem:test2;INIT=runscript from 'src/main/resources/db/migration/V1__Chores_Splitter.sql'"
  val jdbcDriver = "org.h2.Driver"
  val slickDriver = "slick.jdbc.H2Profile"
  val pkg = "com.kuba.dziworski.chords.splitter.slick"
  toError(r.run("slick.codegen.SourceCodeGenerator", cp.files, Array(slickDriver, jdbcDriver, url, outputDir, pkg), s.log))
  val fname = outputDir + "/com/kuba/dziworski/chords/splitter/slick/Tables.scala"
  Seq(file(fname))
}