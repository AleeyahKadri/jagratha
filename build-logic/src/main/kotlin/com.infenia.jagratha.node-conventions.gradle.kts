plugins {
  id("com.github.node-gradle.node")
}

node {
  version.set(libs.versions.node.get())
  pnpmVersion.set(libs.versions.pnpm.get())
  download.set(true)
}
