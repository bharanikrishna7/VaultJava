lazy val root = (project in file("."))
  .settings(
    name := "VaultJava",
    version := "0.1",
    scalaVersion := "2.13.3",
    libraryDependencies ++= Seq(
      google_http_client,
      json4s_native,
      log4j_scala_api,
      log4j_core,
      scalatic,
      scalatest,
    ),
    resolvers ++= Seq(artima_resolver),
    logBuffered in Test := false,
    // logBuffered in Runtime := false,
    logLevel := util.Level.Info
  )

// resolvers
val artima_resolver: MavenRepository                  = "Artima Maven Repository" at "http://repo.artima.com/releases"


// libraries
lazy val google_http_client: ModuleID                 = "com.google.http-client" % "google-http-client" % google_http_client_version
lazy val json4s_native: ModuleID                      = "org.json4s" %% "json4s-native" % json4s_native_version
lazy val log4j_scala_api: ModuleID                    = "org.apache.logging.log4j" %% "log4j-api-scala" % log4j_scala_version
lazy val log4j_core: ModuleID                         = "org.apache.logging.log4j" % "log4j-core" % log4j_core_version % Runtime
lazy val scalatic: ModuleID                           = "org.scalactic" %% "scalactic" % scalatest_version
lazy val scalatest: ModuleID                          = "org.scalatest" %% "scalatest" % scalatest_version % "test"

// library versions
val google_http_client_version: String                = "1.36.0"
val json4s_native_version: String                     = "3.7.0-M6"
val log4j_scala_version: String                       = "12.0"
val log4j_core_version: String                        = "2.13.0"
val scalatest_version: String                         = "3.2.2"
