
resolvers += MavenRepository("HMRC-open-artefacts-maven2", "https://open.artefacts.tax.service.gov.uk/maven2")
resolvers += Resolver.url("HMRC-open-artefacts-ivy2", url("https://open.artefacts.tax.service.gov.uk/ivy2"))(Resolver.ivyStylePatterns)

addSbtPlugin("uk.gov.hmrc" % "sbt-auto-build" % "3.0.0")
addSbtPlugin("uk.gov.hmrc" % "sbt-distributables" % "2.1.0")
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.8.7")
addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "1.0.0")
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.9.2")
addSbtPlugin("org.irundaia.sbt" % "sbt-sassify" % "1.4.11")
addSbtPlugin("net.ground5hark.sbt" % "sbt-concat" % "0.2.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-uglify" % "2.0.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-digest" % "1.1.4")
addSbtPlugin("com.iheart" % "sbt-play-swagger" % "0.10.2")
addSbtPlugin("com.lucidchart" % "sbt-scalafmt" % "1.16")
