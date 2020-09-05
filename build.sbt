scalaVersion := "2.13.3"

val Ver = new {
  val slinky = "0.6.5"

  val logback = "1.2.3"
  val zio     = "1.0.1"
  val http4s  = "0.21.7"
}

lazy val sharedSettings = Seq(
  scalaVersion := "2.13.3",
  version := "0.1.0-SNAPSHOT",
  organization := "com.github.oen9",
  organizationName := "oen9",
  name := "streamlink-twitch-web-gui",
  libraryDependencies ++= Seq(
    "org.typelevel" %% "cats-core" % "2.2.0" // 2.1.1?
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
    "me.shadaj" %%% "slinky-web" % Ver.slinky
  ),
  npmDependencies in Compile ++= Seq(
    ),
  scalaJSUseMainModuleInitializer := true,
  version.in(webpack) := "4.44.1",
  webpackBundlingMode := BundlingMode.Application,
  webpackBundlingMode.in(fastOptJS) := BundlingMode.LibraryOnly()
)

lazy val jvmSettings = Seq(
  libraryDependencies ++= Seq(
    "dev.zio"        %% "zio"                 % Ver.zio,
    "dev.zio"        %% "zio-interop-cats"    % "2.1.4.0",
    "dev.zio"        %% "zio-logging-slf4j"   % "0.4.0",
    "ch.qos.logback" % "logback-classic"      % Ver.logback,
    "org.http4s"     %% "http4s-blaze-server" % Ver.http4s,
    "org.http4s"     %% "http4s-circe"        % Ver.http4s,
    "org.http4s"     %% "http4s-dsl"          % Ver.http4s,
    "org.http4s"     %% "http4s-blaze-client" % Ver.http4s
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
    bashScriptExtraDefines += """addJava "-Dassets=${app_home}/../assets""""
  )

disablePlugins(RevolverPlugin)
