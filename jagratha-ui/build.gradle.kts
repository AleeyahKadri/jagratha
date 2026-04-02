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
import com.github.gradle.node.pnpm.task.PnpmTask
import org.gradle.language.jvm.tasks.ProcessResources

plugins {
  id("com.infenia.jagratha.java-conventions")
  id("com.infenia.jagratha.quality-conventions")
  id("com.infenia.jagratha.jacoco-conventions")
  id("com.infenia.jagratha.node-conventions")
  alias(libs.plugins.spring.boot)
  alias(libs.plugins.spring.dependency.management)
  alias(libs.plugins.jte)
}

extra["jacocoMinimumCoverage"] = 0.05

dependencies {
  implementation(project(":jagratha-core"))
  implementation(libs.jte.starter)
  implementation(libs.jte.core)
  implementation(libs.htmx.spring.boot)
  implementation(libs.spring.boot.starter.webflux)

  compileOnly(libs.lombok)
  annotationProcessor(libs.lombok)

  testImplementation(libs.spring.boot.starter.webflux.test)
  testImplementation(libs.reactor.test)
}

jte {
  sourceDirectory.set(file("src/main/jte").toPath())
  targetDirectory.set(file("build/jte-classes").toPath())
  compilePath.setFrom(sourceSets.main.get().compileClasspath)
  generate()
  precompile()
  binaryStaticContent.set(false)
}

tasks.named("bootJar") {
  enabled = false
}

sourceSets.main {
  output.dir(file("build/jte-classes"), mapOf("builtBy" to "precompileJte"))
}

tasks.named("jar") {
  enabled = true
}

val tailwind by tasks.registering(PnpmTask::class) {
  dependsOn(tasks.named("pnpmInstall"))
  pnpmCommand.set(
    listOf(
      "exec",
      "tailwindcss",
      "-i",
      "./src/main/resources/static/css/input.css",
      "-o",
      "${layout.buildDirectory.get()}/tailwind/style.css",
      "--minify"
    )
  )
  inputs.file("./src/main/resources/static/css/input.css")
  outputs.file(layout.buildDirectory.file("tailwind/style.css"))
}

tasks.named<ProcessResources>("processResources") {
  dependsOn(tailwind)
  from(layout.buildDirectory.dir("tailwind")) {
    into("static/css")
  }
}

tasks.named("jacocoTestReport") {
  onlyIf {
    tasks.named("test").get().didWork || file("${buildDir}/jacoco/test.exec").exists()
  }
}
