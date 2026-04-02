import org.gradle.api.artifacts.VersionCatalogsExtension

plugins {
  id("com.github.node-gradle.node")
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

node {
  version.set(libs.findVersion("node").get().requiredVersion)
  pnpmVersion.set(libs.findVersion("pnpm").get().requiredVersion)
  download.set(true)
}
