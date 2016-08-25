resolvers ++=
  ("Ahlers Consulting" at "http://artifacts.ahlers.consulting") ::
    ("Era7 (releases)" at "https://s3-eu-west-1.amazonaws.com/releases.era7.com") ::
    Nil

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.0.1")

addSbtPlugin("ohnosequences" % "sbt-s3-resolver" % "0.14.0")

libraryDependencies ++=
  "consulting.ahlers" % "embedded-phantom" % "1.0.1-SNAPSHOT" ::
    Nil
