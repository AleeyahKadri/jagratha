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
