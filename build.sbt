enablePlugins(ScalaJSPlugin)

name := "LambdaInterpreter"
version := "0.1"
scalaVersion := "3.0.1"

// This is an application with a main method
scalaJSUseMainModuleInitializer := true

libraryDependencies ++= Seq(
  "org.scalatest" %%% "scalatest" % "3.2.9" % Test,
  ("org.scala-js" %%% "scalajs-dom" % "1.1.0").cross(CrossVersion.for3Use2_13)
)