def basicJavaOpts = Seq("-source", "1.6")

lazy val root = project.withId("netutil").in(file("."))
  .settings(
    name             := "NetUtil",
    version          := "1.1.0-SNAPSHOT",
    organization     := "de.sciss",
    description      := "A Java library for sending and receiving messages using the OpenSoundControl (OSC) protocol",
    homepage         := Some(url(s"https://git.iem.at/sciss/${name.value}")),
    licenses         := Seq("LGPL v2.1+" -> url("http://www.gnu.org/licenses/lgpl-2.1.txt")),
    javacOptions                   := basicJavaOpts ++ Seq("-encoding", "utf8", "-Xlint:unchecked", "-target", "1.6"),
    javacOptions in (Compile, doc) := basicJavaOpts,  // does not accept `-encoding` or `target`
    scalaVersion     := "2.12.7",  // this is just a Java only project
    crossPaths       := false,     // this is just a Java only project
    autoScalaLibrary := false,     // this is just a Java only project
    // we are using Scala for testing only
    libraryDependencies += "org.scala-lang" % "scala-library" % scalaVersion.value % Test,
    // ---- publishing ----
    publishMavenStyle := true,
    publishTo := {
      Some(if (isSnapshot.value)
        "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
      else
        "Sonatype Releases"  at "https://oss.sonatype.org/service/local/staging/deploy/maven2"
      )
    },
    publishArtifact in Test := false,
    pomIncludeRepository := { _ => false },
    pomExtra := { val n = name.value
<scm>
  <url>git@git.iem.at:sciss/{n}.git</url>
  <connection>scm:git:git@git.iem.at:sciss/{n}.git</connection>
</scm>
<developers>
  <developer>
    <id>sciss</id>
    <name>Hanns Holger Rutz</name>
    <url>http://www.sciss.de</url>
  </developer>
</developers>
    }
  )
