name := "cmi"

version := "1.0"

scalaVersion := "2.12.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.0.0",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.0",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
  "com.typesafe" % "config" % "1.3.1",
  "ch.qos.logback" % "logback-classic" % "1.1.7",
  "org.typelevel" %% "cats" % "0.8.1",
  "com.couchbase.client" % "java-client" % "2.3.5",
  "io.reactivex" %% "rxscala" % "0.26.4",
  "com.github.julien-truffaut" %% "monocle-core" % "1.3.2",
  "com.typesafe.akka" %% "akka-http-xml" % "10.0.0"
)
