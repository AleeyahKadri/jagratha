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
        minimum = if (project.hasProperty("jacocoMinimumCoverage")) {
          project.property("jacocoMinimumCoverage").toString().toBigDecimal()
        } else {
          "0.80".toBigDecimal()
        }
      }
    }
  }
}

tasks.named("check") {
  dependsOn(tasks.withType<JacocoCoverageVerification>())
}
