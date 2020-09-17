scalaVersion := "2.13.3"

val Ver = new {
  val circe = "0.13.0"

  val slinky = "0.6.6"

  val logback = "1.2.3"
  val zio     = "1.0.1"
  val http4s  = "0.21.7"
  val caliban = "0.9.2"
}

lazy val sharedSettings = Seq(
  scalaVersion := "2.13.3",
  version := "0.1.0-SNAPSHOT",
  organization := "com.github.oen9",
  organizationName := "oen9",
  name := "streamlink-twitch-web-gui",
  libraryDependencies ++= Seq(
    "org.typelevel" %% "cats-core"             % "2.2.0",
    "io.circe"      %%% "circe-parser"         % Ver.circe,
    "io.circe"      %%% "circe-generic-extras" % Ver.circe,
    "io.circe"      %%% "circe-generic"        % Ver.circe,
    "io.circe"      %%% "circe-literal"        % Ver.circe
  ),
  scalacOptions ++= Seq(
    "-Xlint",
    "-unchecked",
    "-deprecation",
    "-feature",
    "-language:higherKinds",
    "-Ymacro-annotations",
    "-Ywarn-unused:imports",
    "-Xlint:-byname-implicit" // github.com/scala/bug/issues/12072 TODO
  ),
  semanticdbEnabled := true,
  semanticdbVersion := scalafixSemanticdb.revision
)

lazy val jsSettings = Seq(
  libraryDependencies ++= Seq(
    "me.shadaj"             %%% "slinky-web"          % Ver.slinky,
    "me.shadaj"             %%% "slinky-react-router" % Ver.slinky,
    "io.suzaku"             %%% "diode"               % "1.1.13",
    "com.github.ghostdogpr" %%% "caliban-client"      % Ver.caliban
  ),
  npmDependencies in Compile ++= Seq(
    "react"            -> "16.13.1",
    "react-dom"        -> "16.13.1",
    "react-popper"     -> "1.3.7",
    "react-router-dom" -> "5.2.0",
    "bootstrap"        -> "4.5.2",
    "bootswatch"       -> "4.5.2",
    "jquery"           -> "3.5.1"
  ),
  scalaJSUseMainModuleInitializer := true,
  version.in(webpack) := "4.44.1",
  webpackBundlingMode := BundlingMode.Application,
  webpackBundlingMode.in(fastOptJS) := BundlingMode.LibraryOnly()
)

lazy val jvmSettings = Seq(
  libraryDependencies ++= Seq(
    "dev.zio"               %% "zio"                 % Ver.zio,
    "dev.zio"               %% "zio-interop-cats"    % "2.1.4.0",
    "dev.zio"               %% "zio-logging-slf4j"   % "0.4.0",
    "com.github.pureconfig" %% "pureconfig"          % "0.13.0",
    "ch.qos.logback"        % "logback-classic"      % Ver.logback,
    "org.http4s"            %% "http4s-blaze-server" % Ver.http4s,
    "org.http4s"            %% "http4s-circe"        % Ver.http4s,
    "org.http4s"            %% "http4s-dsl"          % Ver.http4s,
    "org.http4s"            %% "http4s-blaze-client" % Ver.http4s,
    "com.github.ghostdogpr" %% "caliban"             % Ver.caliban,
    "com.github.ghostdogpr" %% "caliban-http4s"      % Ver.caliban
  ),
  libraryDependencies ++= Seq(
    "dev.zio" %% "zio-test"     % Ver.zio % Test,
    "dev.zio" %% "zio-test-sbt" % Ver.zio % Test
  ),
  testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"),
  target := baseDirectory.value / ".." / "target",
  addCompilerPlugin(("org.typelevel" %% "kind-projector" % "0.11.0").cross(CrossVersion.full))
)

lazy val app =
  crossProject(JSPlatform, JVMPlatform)
    .crossType(CrossType.Full)
    .in(file("."))
    .settings(sharedSettings)
    .jsSettings(jsSettings)
    .jvmSettings(jvmSettings)

lazy val appJS = app.js
  .enablePlugins(ScalaJSBundlerPlugin)
  .disablePlugins(RevolverPlugin)

lazy val appJVM = app.jvm
  .enablePlugins(JavaAppPackaging)
  .settings(
    (unmanagedResourceDirectories in Compile) += (resourceDirectory in (appJS, Compile)).value,
    mappings.in(Universal) ++= webpack.in(Compile, fullOptJS).in(appJS, Compile).value.map { f =>
      f.data -> s"assets/${f.data.getName()}"
    },
    mappings.in(Universal) ++= Seq(
      (target in (appJS, Compile)).value / ("scala-" + scalaBinaryVersion.value) / "scalajs-bundler" / "main" / "node_modules" / "bootswatch" / "dist" / "pulse" / "bootstrap.min.css" -> "assets/bootstrap.min.css"
    ),
    bashScriptExtraDefines += """addJava "-Dassets=${app_home}/../assets""""
  )

disablePlugins(RevolverPlugin)
enablePlugins(CodegenPlugin)
