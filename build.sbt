import com.typesafe.config.{ Config, ConfigFactory }
import scala.collection.JavaConverters._

name := """micro-posts"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.6"

//libraryDependencies += guice
//libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test

libraryDependencies ++= Seq(
  guice,
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test,
  "org.scalikejdbc"        %% "scalikejdbc"                  % "2.5.2",
  "org.scalikejdbc"        %% "scalikejdbc-config"           % "2.5.2",
  "org.scalikejdbc"        %% "scalikejdbc-jsr310"           % "2.5.2",
  "org.scalikejdbc"        %% "scalikejdbc-test"             % "2.5.2" % Test,
  "org.scalikejdbc"        %% "scalikejdbc-play-initializer" % "2.6.+",
  "org.skinny-framework"   %% "skinny-orm"                   % "2.3.7",
  "ch.qos.logback"         % "logback-classic"               % "1.2.3",
  "com.adrianhurt"         %% "play-bootstrap"               % "1.2-P26-B3",
  "mysql"                  % "mysql-connector-java"          % "6.0.6",
  "org.flywaydb"           %% "flyway-play"                  % "4.0.0",
  "com.github.t3hnar"      %% "scala-bcrypt"                 % "3.1",
  "jp.t2v"                 %% "play2-auth"                   % "0.16.0-SNAPSHOT",
  "jp.t2v"                 %% "play2-auth-test"              % "0.16.0-SNAPSHOT" % Test,
  "org.postgresql"         % "postgresql"                    % "42.0.0"
)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"
TwirlKeys.templateImports ++= Seq("forms._")

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"

lazy val envConfig = settingKey[Config]("env-config")

envConfig := {
  val env = sys.props.getOrElse("env", "dev")
  ConfigFactory.parseFile(file("env") / (env + ".conf"))
}

flywayLocations := envConfig.value.getStringList("flywayLocations").asScala
flywayDriver := envConfig.value.getString("jdbcDriver")
flywayUrl := envConfig.value.getString("jdbcUrl")
flywayUser := envConfig.value.getString("jdbcUserName")
flywayPassword := envConfig.value.getString("jdbcPassword")

herokuJdkVersion in Compile := "1.8"

herokuAppName in Compile := "koro2koro-micro-posts" // ご自身のアプリケーション名を指定してください

// prod/application.confであることを確認してください
herokuProcessTypes in Compile := Map(
  "web" -> s"target/universal/stage/bin/${normalizedName.value} -Dhttp.port=$$PORT -Dconfig.resource=prod/application.conf -Ddb.default.migration.auto=true"
)

herokuConfigVars in Compile := Map(
  "JAVA_OPTS" -> "-Xmx512m -Xms512m"
)
