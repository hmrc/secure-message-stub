import sbt._

object AppDependencies {
  import play.core.PlayVersion

  val compile = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc" %% "logback-json-logger" % "4.6.0",
    "uk.gov.hmrc" %% "play-health" % "3.16.0-play-27",
    "uk.gov.hmrc" %% "play-frontend-govuk" % "0.56.0-play-27",
    "uk.gov.hmrc" %% "play-frontend-hmrc" % "0.27.0-play-27",
    "uk.gov.hmrc" %% "play-ui" % "8.19.0-play-27",
    "uk.gov.hmrc" %% "bootstrap-frontend-play-27" % "3.2.0",
    "uk.gov.hmrc" %% "http-verbs-play-27" % "12.2.0",
    "com.beachape" %% "enumeratum" % "1.6.0",
    "uk.gov.hmrc" %% "time" % "3.19.0",
    "com.typesafe.play" %% "play-json-joda" % "2.9.2",
    "org.webjars" % "swagger-ui" % "3.38.0",
    "commons-codec" % "commons-codec" % "1.15"
  )

  val test = Seq(
    "org.scalatest" %% "scalatest" % "3.0.7",
    "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2",
    "org.pegdown" % "pegdown" % "1.6.0",
    "org.jsoup" % "jsoup" % "1.10.3",
    "com.typesafe.play" %% "play-test" % PlayVersion.current,
    "org.mockito" % "mockito-all" % "1.10.19"
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test

  val akkaVersion = "2.6.7"
  val akkaHttpVersion = "10.1.12"

  val overrides = Seq(
    "com.typesafe.akka" %% "akka-stream_2.12" % akkaVersion,
    "com.typesafe.akka" %% "akka-protobuf_2.12" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j_2.12" % akkaVersion,
    "com.typesafe.akka" %% "akka-actor_2.12" % akkaVersion,
    "com.typesafe.akka" %% "akka-http-core_2.12" % akkaHttpVersion
  )
}
