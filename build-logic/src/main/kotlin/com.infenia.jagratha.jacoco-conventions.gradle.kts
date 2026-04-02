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
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification
import org.gradle.testing.jacoco.tasks.JacocoReport

plugins {
  jacoco
}

jacoco {
  toolVersion = "0.8.12"
}

tasks.withType<Test>().configureEach {
  finalizedBy(tasks.withType<JacocoReport>())

  extensions.configure(JacocoTaskExtension::class.java) {
    excludes = listOf(
      "java.*",
      "javax.*",
      "sun.*",
      "jdk.*",
      "com.sun.*",
      "org.w3c.*",
      "org.xml.*"
    )
  }
}

tasks.withType<JacocoReport>().configureEach {
  dependsOn(tasks.withType<Test>())

  classDirectories.setFrom(
    classDirectories.files.map { dir ->
      fileTree(dir) {
        exclude("gg/jte/generated/**")
      }
    }
  )

  onlyIf {
    executionData.files.any { it.exists() }
  }

  reports {
    xml.required.set(true)
    html.required.set(true)
  }
}

tasks.withType<JacocoCoverageVerification>().configureEach {
  dependsOn(tasks.withType<JacocoReport>())

  classDirectories.setFrom(
    classDirectories.files.map { dir ->
      fileTree(dir) {
        exclude("gg/jte/generated/**")
      }
    }
  )

  onlyIf {
    executionData.files.any { it.exists() }
  }

  violationRules {
    rule {
      limit {
        minimum =
          (project.findProperty("jacocoMinimumCoverage") as? String)
            ?.toBigDecimal()
            ?: 0.80.toBigDecimal()
      }
    }
  }
}

tasks.named("check") {
  dependsOn(tasks.withType<JacocoCoverageVerification>())
}
