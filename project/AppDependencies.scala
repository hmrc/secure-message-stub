import sbt._

object AppDependencies {
  import play.core.PlayVersion

  private val bootstrapVersion = "9.18.0"
  private val frontendPlayVersion = "12.8.0"
  
  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"       %% "play-frontend-hmrc-play-30" % frontendPlayVersion,
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-30" % bootstrapVersion,
    "uk.gov.hmrc"       %% "play-partials-play-30"      % "10.1.0",
    "com.beachape"      %% "enumeratum"                 % "1.7.4",
    "commons-codec"     %  "commons-codec"              % "1.15"
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"            %% "bootstrap-test-play-30"  % bootstrapVersion,
    "org.scalatestplus.play" %% "scalatestplus-play"      % "7.0.0",
    "org.jsoup"              %  "jsoup"                   % "1.17.2",
    "org.playframework"      %% "play-test"               % PlayVersion.current,
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test
}
