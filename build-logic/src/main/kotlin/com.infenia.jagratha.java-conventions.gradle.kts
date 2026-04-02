import org.gradle.api.artifacts.VersionCatalogsExtension

plugins {
  java
  id("io.freefair.lombok")
}

group = "com.infenia.jagratha"
version = "0.0.1-SNAPSHOT"

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(21)
  }
  sourceCompatibility = JavaVersion.VERSION_21
  targetCompatibility = JavaVersion.VERSION_21
}

repositories {
  mavenCentral()
  maven("https://repo.spring.io/milestone")
}

configurations {
  named("compileOnly") {
    extendsFrom(configurations.named("annotationProcessor").get())
  }
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies {
  testImplementation(libs.findLibrary("spring-boot-starter-test").get())
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<JavaCompile>().configureEach {
  options.compilerArgs.add("-parameters")
}

tasks.withType<Test>().configureEach {
  useJUnitPlatform()
}
