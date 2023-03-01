import play.sbt.routes.RoutesKeys
import uk.gov.hmrc.versioning.SbtGitVersioning.autoImport.majorVersion

lazy val appName: String = "ctc-departure-trader-details-frontend"

lazy val root = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin) //Required to prevent https://github.com/scalatest/scalatest/issues/1427
  .settings(majorVersion := 0)
  .settings(
    ThisBuild / useSuperShell := false,
  )
  .disablePlugins(JUnitXmlReportPlugin) //Required to prevent https://github.com/scalatest/scalatest/issues/1427
  .configs(A11yTest)
  .settings(inConfig(A11yTest)(org.scalafmt.sbt.ScalafmtPlugin.scalafmtConfigSettings): _*)
  .settings(inConfig(Test)(testSettings): _*)
  .settings(headerSettings(A11yTest): _*)
  .settings(automateHeaderSettings(A11yTest))
  .settings(
    majorVersion        := 0,
    scalaVersion        := "2.13.8",
    name := appName,
    RoutesKeys.routesImport ++= Seq(
      "models._"
    ),
    TwirlKeys.templateImports ++= Seq(
      "play.twirl.api.HtmlFormat",
      "play.twirl.api.HtmlFormat._",
      "uk.gov.hmrc.govukfrontend.views.html.components._",
      "uk.gov.hmrc.hmrcfrontend.views.html.components._",
      "uk.gov.hmrc.hmrcfrontend.views.html.helpers._",
      "models.Mode",
      "views.html.helper.CSPNonce",
      "viewModels.{InputSize, LabelSize, LegendSize}",
      "templates._",
      "views.utils.ViewUtils._"
    ),
    PlayKeys.playDefaultPort := 10130,
    libraryDependencies ++= AppDependencies(),
    // https://www.scala-lang.org/2021/01/12/configuring-and-suppressing-warnings.html
    // suppress warnings in generated routes files
    scalacOptions ++= Seq(
      "-feature",
      "-language:implicitConversions",
      "-Wconf:src=routes/.*:s",
      "-Wconf:cat=unused-imports&src=html/.*:s",
    ),
    ThisBuild / useSuperShell := false,
    ThisBuild / scalafmtOnCompile := true
  )
  .settings(resolvers += Resolver.jcenterRepo)
  .settings(CodeCoverageSettings.settings: _*)


lazy val testSettings: Seq[Def.Setting[_]] = Seq(
  fork := true,
  javaOptions ++= Seq(
    "-Dconfig.resource=test.application.conf"
  )
)