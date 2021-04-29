import org.jetbrains.sbtidea.Keys._

lazy val powerMode =
  project.in(file("."))
    .enablePlugins(SbtIdeaPlugin)
    .settings(
      version := "0.0.1-SNAPSHOT",
      scalaVersion := "2.13.2",
      ThisBuild / intellijPluginName := "power-mode",
      ThisBuild / intellijBuild := "211.6693.111",
      ThisBuild / intellijPlatform := IntelliJPlatform.IdeaCommunity,
      Global / intellijAttachSources := true,
      Compile / javacOptions ++= "--release" :: "11" :: Nil,
      intellijPlugins += "com.intellij.properties".toPlugin,
      libraryDependencies += "com.eclipsesource.minimal-json" % "minimal-json" % "0.9.5" withSources(),
      unmanagedJars in Compile += baseDirectory.value / "lib",
      unmanagedResourceDirectories in Compile += baseDirectory.value / "resources",
      resourceDirectories in Runtime += baseDirectory.value / "resources",
      unmanagedResourceDirectories in Test += baseDirectory.value / "testResources",
    )
    
