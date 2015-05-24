name := "Colorextract"

version := "1.0"

scalaVersion := "2.11.4"

val akkaVersion = "2.3.6"
val json4sVersion = "3.2.10"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % akkaVersion

libraryDependencies += "com.typesafe.akka" %% "akka-slf4j" % akkaVersion

libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.2" % "test"

libraryDependencies += "org.scalamock" %% "scalamock-scalatest-support" % "3.1.2" % "test"

libraryDependencies += "org.mockito" % "mockito-core" % "1.9.5" % "test"

// -- Json --
libraryDependencies += "org.json4s" %% "json4s-native" % json4sVersion

libraryDependencies += "org.json4s" %% "json4s-ext" % json4sVersion

libraryDependencies += "commons-io" % "commons-io" % "2.4"

libraryDependencies += "io.spray" %% "spray-client" % "1.3.1"

libraryDependencies += "org.jsoup" % "jsoup" % "1.8.2"

libraryDependencies += "com.github.nscala-time" %% "nscala-time" % "2.0.0"
