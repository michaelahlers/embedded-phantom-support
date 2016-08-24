organization := "consulting.ahlers"

name := "embedded-phantom-binary"

description := "Distribution of PhantomJS binaries as artifacts."

homepage := Some(url("http://github.com/michaelahlers/embedded-phantom-binary"))

startYear := Some(2016)

developers :=
  Developer("michaelahlers", "Michael Ahlers", "michael@ahlers.consulting", url("http://github.com/michaelahlers")) ::
    Nil

scmInfo :=
  Some(ScmInfo(
    browseUrl = url("http://github.com/michaelahlers/embedded-phantom-binary"),
    connection = "scm:git:https://github.com:michaelahlers/embedded-phantom-binary.git",
    devConnection = Some("scm:git:git@github.com:michaelahlers/embedded-phantom-binary.git")
  ))

licenses :=
  "BSD" -> url("https://github.com/ariya/phantomjs/blob/master/LICENSE.BSD") ::
    Nil
