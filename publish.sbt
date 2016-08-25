import com.amazonaws.auth.EnvironmentVariableCredentialsProvider
import com.amazonaws.auth.profile.ProfileCredentialsProvider

crossPaths := false

publishMavenStyle := true

publishArtifact in Compile := false

awsProfile := "default"

s3credentials :=
  new ProfileCredentialsProvider(awsProfile.value) |
    new EnvironmentVariableCredentialsProvider()

publishTo := Some(s3resolver.value("Ahlers Consulting", s3("artifacts.ahlers.consulting")))
