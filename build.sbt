import java.io.File.createTempFile
import java.util.jar.Manifest

import ahlers.phantom.embedded.PhantomPackageResolver._
import ahlers.phantom.embedded.PhantomVersion.{byLabel => versionByLabel}
import ahlers.phantom.embedded.{PhantomDownloadConfigBuilder, PhantomPackageResolver, PhantomVersion}
import de.flapdoodle.embed.process.distribution.BitSize._
import de.flapdoodle.embed.process.distribution.Platform._
import de.flapdoodle.embed.process.distribution.{BitSize, Distribution, Platform}
import de.flapdoodle.embed.process.store.Downloader
import sbt.Def.Initialize

val locationFor: Distribution => String = {
  val config = new PhantomDownloadConfigBuilder().defaults().build()
  new Downloader().getDownloadUrl(config, _)
}

def artifactFor(platform: Platform, architectures: BitSize*): Initialize[Task[File]] =
  Def.task((name.value, version.value, streams.value.log)) map {
    case (project, ver, log) =>

      /* If this will fail, fail fast before downloading anything. */
      val distributions = architectures.map(new Distribution(versionByLabel(ver.split('-').head), platform, _))

      val entries: Seq[(File, String)] =
        distributions map { distribution =>
          val archive = createTempFile(archiveFilenameFor(distribution), archiveExtensionFor(distribution))
          val source: URL = new URL(locationFor(distribution))

          log.info(s"""Downloading "$source" to "$archive"...""")
          source #> archive !! log
          log.info(s"""Downloaded "$source" (${archive.length} bytes).""")

          val destination = s"consulting/ahlers/embedded/phantom/${archivePathFor(distribution)}"
          archive -> destination
        }

      val artifact = createTempFile(s"$project-$platform", "jar")
      Package.makeJar(entries, artifact, new Manifest, log)
      artifact

  }

val generateLinuxArtifact = taskKey[File]("Fetch and package an artifact for Linux.")
val generateMacOSArtifact = taskKey[File]("Fetch and package an artifact for macOS.")
val generateWindowsArtifact = taskKey[File]("Fetch and package an artifact for Windows.")

generateLinuxArtifact <<= artifactFor(Linux, B32, B64)
generateMacOSArtifact <<= artifactFor(OS_X, B64)
generateWindowsArtifact <<= artifactFor(Windows, B32)

def definitionFor(classifier: String): Initialize[Artifact] =
  Def.setting(name.value).apply(Artifact(_, "jar", "jar", Some(classifier), Nil, None))

addArtifact(definitionFor("linux"), generateLinuxArtifact)
addArtifact(definitionFor("macos"), generateMacOSArtifact)
addArtifact(definitionFor("windows"), generateWindowsArtifact)
