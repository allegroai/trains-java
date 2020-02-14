ThisBuild / scalaVersion := "2.13.1"
/*ThisBuild / organization := ""*/

lazy val root = ( project in file ("."))
  .settings(
    name := "trains_sdk",
    version := "0.0.1",
    resolvers += Resolver.mavenLocal,
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.1.0" % Test,
      "org.json4s" %% "json4s-native" % "3.6.7",
      "org.json4s" %% "json4s-jackson" % "3.6.7",
      "com.softwaremill.sttp.client" %% "core" % "2.0.0-M6",
    )
  )


