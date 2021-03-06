/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package org.gradle.buildsetup.plugins

import org.gradle.api.Incubating
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.wrapper.Wrapper

@Incubating
class BuildSetupPlugin implements Plugin<Project> {
    public static final String SETUP_BUILD_TASK_NAME = "setupBuild"
    public static final String GROUP = 'Build Setup experimental'

    void apply(Project project) {
        Task setupBuild = project.getTasks().add(SETUP_BUILD_TASK_NAME);
        setupBuild.group = GROUP
        setupBuild.group = "[incubating] Lifecycle task of the Build-Setup plugin."

        if (project.file("pom.xml").exists()) {
            def maven2Gradle = project.task("maven2Gradle", type: ConvertMaven2Gradle) {
                group = GROUP
                description = '[incubating] Attempts to generate gradle builds from Maven project.'
            }
            setupBuild.dependsOn(maven2Gradle)
        } else if (!project.file("build.gradle").exists()) {
            // generate empty gradle build file
            GenerateBuildFile generateBuildFile = project.task("generateBuildFile", type: GenerateBuildFile) {
                group = GROUP
                description = '[incubating] Creates a Gradle build file.'
                buildFile = project.file("build.gradle")
            }
            // generate empty gradle settings file
            GenerateSettingsFile generateSettingsFile = project.task("generateSettingsFile", type: GenerateSettingsFile) {
                group = GROUP
                description = '[incubating] Creates a Gradle settings file.'
                settingsFile = project.file("settings.gradle")
            }
            setupBuild.dependsOn(generateSettingsFile)
            setupBuild.dependsOn(generateBuildFile)
        }
        Task wrapper = project.task("wrapper", type: Wrapper)
        setupBuild.dependsOn(wrapper)
    }
}