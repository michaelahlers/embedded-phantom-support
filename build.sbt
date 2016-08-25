import java.io.File
import java.util.jar.Manifest

import ahlers.phantom.embedded.{PhantomDownloadConfigBuilder, PhantomPackageResolver, PhantomVersion}
import de.flapdoodle.embed.process.distribution.BitSize._
import de.flapdoodle.embed.process.distribution.Platform._
import de.flapdoodle.embed.process.distribution.{BitSize, Distribution, Platform}
import de.flapdoodle.embed.process.store.Downloader
import sbt.Def.Initialize

def artifactFor(log: Logger, version: String, platform: Platform, bitsizes: BitSize*): File = {

  val release =
    PhantomVersion
      .values
      .find(version.split('-').head == _.asInDownloadPath)
      .getOrElse(throw new IllegalStateException(s"""Can't find Phantom version for "$version"."""))

  val entries: Seq[(File, String)] = {
    import PhantomPackageResolver._

    val downloadConfig = new PhantomDownloadConfigBuilder().defaults().build()

    bitsizes.map(new Distribution(release, platform, _)) map { distribution =>
      val archive = File.createTempFile(archiveFilenameFor(distribution), archiveExtensionFor(distribution))
      val source = new URL(new Downloader().getDownloadUrl(downloadConfig, distribution))

      log.info(s"""Downloading "$source" to "$archive"...""")

      source #> archive !!

      log.info(s"""Downloaded "$source" (${archive.length} bytes).""")

      val destination = s"consulting/ahlers/embedded/phantom/${archivePathFor(distribution)}"
      archive -> destination
    }
  }

  val artifact = File.createTempFile(s"embedded-phantom-support-$platform", "jar")
  Package.makeJar(entries, artifact, new Manifest, log)
  artifact
}

val generateLinuxArtifact = taskKey[File]("Fetch and package an artifact for Linux.")
val generateMacOSArtifact = taskKey[File]("Fetch and package an artifact for macOS.")
val generateWindowsArtifact = taskKey[File]("Fetch and package an artifact for Windows.")

generateLinuxArtifact := artifactFor(streams.value.log, version.value, Linux, B32, B64)
generateMacOSArtifact := artifactFor(streams.value.log, version.value, OS_X, B64)
generateWindowsArtifact := artifactFor(streams.value.log, version.value, Windows, B32)

def supportArtifactDefinition(classifier: String): Initialize[Artifact] =
  Def.setting(name.value).apply(Artifact(_, "jar", "jar", Some(classifier), Nil, None))

addArtifact(supportArtifactDefinition("linux"), generateLinuxArtifact)
addArtifact(supportArtifactDefinition("macos"), generateMacOSArtifact)
addArtifact(supportArtifactDefinition("windows"), generateWindowsArtifact)
