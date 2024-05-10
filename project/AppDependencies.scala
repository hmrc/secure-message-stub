import sbt._

object AppDependencies {
  import play.core.PlayVersion

  private val bootstrapVersion = "8.4.0"
  private val frontendPlayVersion = "8.5.0"
  
  val compile = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"       %% "play-frontend-hmrc-play-30" % "8.5.0",
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-30" % frontendPlayVersion,
    "uk.gov.hmrc"       %% "play-partials-play-30"      % "9.1.0",
    "com.beachape"      %% "enumeratum"                 % "1.6.0",
    "commons-codec"     %  "commons-codec"              % "1.15"
  )

  val test = Seq(
    "uk.gov.hmrc"            %% "bootstrap-test-play-30"  % bootstrapVersion,
    "org.scalatestplus.play" %% "scalatestplus-play"      % "7.0.0",
    "org.jsoup"              %  "jsoup"                   % "1.10.3",
    "org.playframework"      %% "play-test"               % PlayVersion.current,
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test
}
