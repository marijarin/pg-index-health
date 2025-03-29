/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

import com.github.spotbugs.snom.Confidence
import com.github.spotbugs.snom.Effort
import com.github.spotbugs.snom.SpotBugsTask
import net.ltgt.gradle.errorprone.errorprone
import org.sonarqube.gradle.SonarTask

plugins {
    id("java")
    id("org.sonarqube")
    id("checkstyle")
    id("pmd")
    id("com.github.spotbugs")
    id("jacoco")
    id("java-test-fixtures")
    id("net.ltgt.errorprone")
    id("org.gradle.test-retry")
}

dependencies {
    errorprone("com.google.errorprone:error_prone_core:2.36.0")
    errorprone("jp.skypencil.errorprone.slf4j:errorprone-slf4j:0.1.28")

    spotbugsPlugins("jp.skypencil.findbugs.slf4j:bug-pattern:1.5.0")
    spotbugsPlugins("com.h3xstream.findsecbugs:findsecbugs-plugin:1.13.0")
    spotbugsPlugins("com.mebigfatguy.sb-contrib:sb-contrib:7.6.9")
}

tasks.withType<JavaCompile>().configureEach {
    options.errorprone {
        disableWarningsInGeneratedCode.set(true)
        disable("StringSplitter", "ImmutableEnumChecker", "FutureReturnValueIgnored", "EqualsIncompatibleType", "TruthSelfEquals", "Slf4jLoggerShouldBeNonStatic", "Slf4jSignOnlyFormat")
    }
}

tasks {
    test {
        dependsOn(checkstyleMain, checkstyleTest, checkstyleTestFixtures, pmdMain, pmdTest, pmdTestFixtures, spotbugsMain, spotbugsTest, spotbugsTestFixtures)
    }

    withType<Test>().configureEach {
        retry {
            maxRetries.set(2)
            maxFailures.set(10)
            failOnPassedAfterRetry.set(false)
        }
    }

    jar {
        manifest {
            attributes["Implementation-Title"] = project.name
            attributes["Implementation-Version"] = project.version
        }
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }

    javadoc {
        val groupId = project.group
        val projectVersion = project.version
        val doclet = options as StandardJavadocDocletOptions
        doclet.addBooleanOption("html5", true)
        configurations.findByName("api")?.let { cfg ->
            cfg.dependencies
                .filterIsInstance<ProjectDependency>()
                .map {
                    val link = "https://javadoc.io/doc/$groupId/${it.name}/$projectVersion/"
                    val dependencyProject = rootProject.project(it.path)
                    val javadocTask = dependencyProject.tasks.named<Javadoc>("javadoc")
                    val javadocOutputDir = javadocTask.get().destinationDir
                    doclet.linksOffline(link, javadocOutputDir?.absolutePath)
                    dependsOn(javadocTask)
                }
        }
    }

    withType<SpotBugsTask>().configureEach {
        reports {
            create("xml") { enabled = true }
            create("html") { enabled = true }
        }
    }

    withType<SonarTask>().configureEach {
        dependsOn(test, jacocoTestReport)
    }
}

checkstyle {
    toolVersion = "10.20.1"
    configFile = file("${rootDir}/config/checkstyle/checkstyle.xml")
    isIgnoreFailures = false
    maxWarnings = 0
    maxErrors = 0
}

pmd {
    toolVersion = "7.7.0"
    isConsoleOutput = true
    ruleSetFiles = files("${rootDir}/config/pmd/pmd.xml")
    ruleSets = listOf()
}

spotbugs {
    showProgress.set(true)
    effort.set(Effort.MAX)
    reportLevel.set(Confidence.LOW)
    excludeFilter.set(file("${rootDir}/config/spotbugs/exclude.xml"))
}
