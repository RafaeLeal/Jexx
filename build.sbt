name := "jexx"

organization := "com.ploomes"

version := "1.1-SNAPSHOT"

scalaVersion := "2.12.2"

crossScalaVersions := Seq("2.12.1" ,"2.11.7")

resolvers ++= Seq[Resolver](
  s3resolver.value("Releases resolver", s3("releases.mvn-repo.ploomes.com")).withIvyPatterns,
  s3resolver.value("Snapshots resolver", s3("snapshots.mvn-repo.ploomes.com")).withIvyPatterns
)

libraryDependencies ++= Seq(
  "com.ploomes" %% "scala-sift" % "1.1-SNAPSHOT",
  "org.json4s" %% "json4s-native" % "3.5.0",
  "org.jdom" % "jdom" % "2.0.2",
  "jaxen" % "jaxen" % "1.1.6",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)

publishMavenStyle := false

s3overwrite := true

publishTo := {
  val prefix = if (isSnapshot.value) "snapshots" else "releases"
  Some(s3resolver.value("Ploomes "+prefix+" S3 bucket", s3(prefix+".mvn-repo.ploomes.com")) withIvyPatterns)
}