import org.gradle.api.plugins.quality.Checkstyle
import org.gradle.api.plugins.quality.Pmd
import org.gradle.api.artifacts.VersionCatalogsExtension

plugins {
  id("com.diffplug.spotless")
  checkstyle
  pmd
  id("com.github.spotbugs")
}

spotless {
  java {
    licenseHeaderFile(rootProject.file("config/license/header.txt"))
    importOrder()
    removeUnusedImports()
    cleanthat()
    googleJavaFormat().reflowLongStrings()
    leadingTabsToSpaces(4)
    trimTrailingWhitespace()
    endWithNewline()
    targetExclude("bin/**", "build/**", "out/**", "**/.gradle/**")
  }

  kotlinGradle {
    target("*.gradle.kts", "build-logic/**/*.gradle.kts")
    licenseHeaderFile(rootProject.file("config/license/header.txt"), "(plugins|id|import|apply)")
  }

  format("xml") {
    target("**/*.xml")
    targetExclude("**/build/**", "**/bin/**", "**/out/**", "**/.gradle/**")
    licenseHeaderFile(rootProject.file("config/license/header-xml.txt"), "(<[^!?])")
  }
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

checkstyle {
  toolVersion = libs.findVersion("checkstyle").get().requiredVersion
  configFile = rootProject.file("config/checkstyle/checkstyle.xml")
  isIgnoreFailures = false
  isShowViolations = true
}

pmd {
  toolVersion = libs.findVersion("pmd").get().requiredVersion
  ruleSets = listOf(rootProject.file("config/pmd/ruleset.xml").absolutePath)
  isIgnoreFailures = false
  isConsoleOutput = true
}

tasks.withType<Checkstyle>().configureEach {
  reports {
    xml.required.set(true)
    html.required.set(true)
  }
}

tasks.configureEach {
  if ((name.contains("Aot") || name.contains("Test")) &&
    (this is Checkstyle || this is Pmd || javaClass.name.contains("SpotBugs"))
  ) {
    enabled = false
  }
}
