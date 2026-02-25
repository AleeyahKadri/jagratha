import org.gradle.api.plugins.quality.Checkstyle
import org.gradle.api.plugins.quality.Pmd

plugins {
    id("com.diffplug.spotless")
    checkstyle
    pmd
    id("com.github.spotbugs")
}

private val catalog = versionCatalogs.named("libs")

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
        licenseHeaderFile(rootProject.file("config/license/header.txt"), "(import|plugins|buildscript|rootProject|include)")
    }

    format("xml") {
        target("**/*.xml")
        targetExclude("**/build/**", "**/bin/**", "**/out/**", "**/.gradle/**")
        licenseHeaderFile(rootProject.file("config/license/header-xml.txt"), "(<[^!?])")
    }
}

checkstyle {
    toolVersion = catalog.findVersion("checkstyle").get().requiredVersion
    configFile = rootProject.file("config/checkstyle/checkstyle.xml")
    isIgnoreFailures = false
    isShowViolations = true
}

pmd {
    toolVersion = catalog.findVersion("pmd").get().requiredVersion
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

// Disable quality tasks for everything except main
tasks.configureEach {
    if ((name.contains("Aot") || name.contains("Test")) &&
        (this is Checkstyle || this is Pmd || this::class.java.name.contains("SpotBugs"))) {
        enabled = false
    }
}
