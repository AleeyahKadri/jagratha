/*
 * Copyright 2026 Infenia Private Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
    target("*.gradle.kts", "src/**/*.gradle.kts", "build-logic/**/*.gradle.kts")
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
