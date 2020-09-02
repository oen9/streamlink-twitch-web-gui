ThisBuild / scalaVersion     := "2.13.3"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "oen9"
ThisBuild / organizationName := "oen9"

lazy val root = (project in file("."))
  .settings(
    name := "streamlink-twitch-web-gui",
    libraryDependencies ++= Seq()
  )
