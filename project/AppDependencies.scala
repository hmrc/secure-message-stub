import sbt._

object AppDependencies {
  import play.core.PlayVersion

  private val bootstrapVersion = "10.4.0"
  private val frontendPlayVersion = "12.23.0"
  
  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"       %% "play-frontend-hmrc-play-30" % frontendPlayVersion,
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-30" % bootstrapVersion,
    "uk.gov.hmrc"       %% "play-partials-play-30"      % "10.2.0",
    "commons-codec"     %  "commons-codec"              % "1.20.0"
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"            %% "bootstrap-test-play-30"  % bootstrapVersion,
    "org.jsoup"              %  "jsoup"                   % "1.21.2",
    "org.playframework"      %% "play-test"               % PlayVersion.current,
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test
}
