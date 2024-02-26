import sbt._

object AppDependencies {
  import play.core.PlayVersion

  val bootstrapVersion = "7.23.0"
  val compile = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"       %% "play-frontend-hmrc-play-28" % "8.5.0",
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-28" % bootstrapVersion,
    "uk.gov.hmrc"       %% "play-partials-play-28"      % "9.1.0",
    "com.beachape"      %% "enumeratum"                 % "1.6.0",
    "com.typesafe.play" %% "play-json-joda"             % "2.9.2",
    "commons-codec"     % "commons-codec"               % "1.15"
  )

  val test = Seq(
    "uk.gov.hmrc"            %% "bootstrap-test-play-28" % bootstrapVersion,
    "org.scalatestplus.play" %% "scalatestplus-play"     % "5.1.0",
    "org.scalatestplus"      %% "mockito-3-4"            % "3.2.1.0",
    "org.pegdown"            % "pegdown"                 % "1.6.0",
    "org.jsoup"              % "jsoup"                   % "1.10.3",
    "com.typesafe.play"      %% "play-test"              % PlayVersion.current,
    "org.mockito"            % "mockito-all"             % "1.10.19",
    "com.vladsch.flexmark"   % "flexmark-all"            % "0.35.10"
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test

  val akkaVersion = "2.6.7"
  val akkaHttpVersion = "10.1.12"

  val overrides = Seq(
    "com.typesafe.akka" %% "akka-stream_2.12"    % akkaVersion,
    "com.typesafe.akka" %% "akka-protobuf_2.12"  % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j_2.12"     % akkaVersion,
    "com.typesafe.akka" %% "akka-actor_2.12"     % akkaVersion,
    "com.typesafe.akka" %% "akka-http-core_2.12" % akkaHttpVersion
  )
}
