/*
 * Copyright 2007 the original author or authors.
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

package org.gradle.build.integtests

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author Hans Dockter
 */
class Tutorial {
    private static Logger logger = LoggerFactory.getLogger(Tutorial)
    static String NL = System.properties['line.separator']

    static void execute(String gradleHome, String samplesDirName, String userguideOutputDir) {
        File userguideDir = new File(samplesDirName, 'userguide')
        getScripts().each {GradleRun run ->
            logger.info("Test Id: $run.id")
            Map result
            if (run.groovyScript) {
                result = runGroovyScript(new File(userguideDir, "$run.subDir/$run.file"))
            } else {
                result = Executer.execute(gradleHome, new File(userguideDir, run.subDir).absolutePath, [run.execute], [], run.file,
                        run.debugLevel)
            }
//            println new File(userguideOutputDir, run.id + '.out').text
//            println result.output
            result.output = ">$result.command$NL" + result.output
            assert result.output == new File(userguideOutputDir, run.id + '.out').text
        }
    }

    static void main(String[] args) {
        execute(args[0], args[1], args[2])
    }

    static List getScripts() {
        [
                run('tutorial', 'configByDag', 'release'),
                new GradleRun(subDir: 'tutorial', id: 'scope', file: 'scope.groovy', groovyScript: true),
                runMp('firstExample/water', 'FirstExample', 'hello'),
                runMp('addKrill/water', 'AddKrill', 'hello'),
                runMp('useSubprojects/water', 'UseSubprojects', 'hello'),
                runMp('subprojectsAddFromTop/water', 'SubprojectsAddFromTop', 'hello'),
                runMp('spreadSpecifics/water', 'SpreadSpecifics', 'hello'),
                runMp('addTropical/water', 'AddTropical', 'hello'),
                runMp('tropicalWithProperties/water', 'TropicalWithProperties', 'hello'),
                runMp('tropicalWithProperties/water/bluewhale', 'SubBuild', 'hello'),
                runMp('partialTasks/water', 'PartialTasks', 'distanceToIceberg'),
//                runMp('partialTasks/water', 'PartialTasksNotQuiet', 'distanceToIceberg', Executer.INFO),
                runMp('dependencies/firstMessages/messages', 'FirstMessages', 'action'),
                runMp('dependencies/messagesHack/messages', 'MessagesHack', 'action'),
                runMp('dependencies/messagesHack/messages/consumer', 'MessagesHackBroken', 'action'),
                runMp('dependencies/messagesWithDependencies/messages', 'MessagesDependencies', 'action'),
                runMp('dependencies/messagesWithDependencies/messages/consumer', 'MessagesDependenciesSubBuild', 'action'),
                runMp('dependencies/messagesDifferentTaskNames/messages/consumer', 'MessagesDifferentTaskNames', 'consume'),
                runMp('dependencies/messagesTaskDependencies/messages/consumer', 'MessagesTaskDependencies', 'consume'),
                runMp('dependencies/messagesConfigDependenciesBroken/messages/consumer', 'MessagesConfigDependenciesBroken', 'consume'),
                runMp('dependencies/messagesConfigDependencies/messages/consumer', 'MessagesConfigDependencies', 'consume'),
                runMp('dependencies/messagesConfigDependenciesAltSolution/messages/consumer', 'MessagesConfigDependenciesAltSolution', 'consume'),
                run('tutorial', 'antChecksum', 'checksum'),
                run('tutorial', 'antChecksumWithMethod', 'checksum'),
                run('tutorial', 'autoskip', '-Dskip.autoskip autoskip'),
                run('tutorial', 'autoskipDepends', '-Dskip.autoskip depends'),
                run('tutorial', 'count', 'count'),
                run('tutorial', 'date', 'date'),
                run('tutorial', 'directoryTask', 'otherResources'),
                run('tutorial', 'disableTask', 'disableMe'),
                run('tutorial', 'dynamic', 'task_1'),
                run('tutorial', 'dynamicDepends', 'task_0'),
                run('tutorial', 'hello', 'hello'),
                run('tutorial', 'helloEnhanced', 'hello'),
                run('tutorial', 'helloWithShortCut', 'hello'),
                run('tutorial', 'intro', 'intro'),
                run('tutorial', 'lazyDependsOn', 'taskX'),
                run('tutorial', 'makeDirectory', 'compile'),
                run('tutorial', 'mkdirTrap', 'compile'),
                run('tutorial', 'pluginConfig', 'check'),
                run('tutorial', 'pluginConvention', 'check'),
                run('tutorial', 'pluginIntro', 'check'),
                run('tutorial', 'projectApi', 'check'),
                run('tutorial', 'replaceTask', 'resources'),
                run('tutorial', 'skipProperties', '-DmySkipProperty skipMe'),
                run('tutorial', 'stopExecutionException', 'myTask')

        ]
    }

    static GradleRun run(String subDir, String id, String execute, int debugLevel = Executer.QUIET) {
        new GradleRun(subDir: subDir, id: id, execute: execute, debugLevel: debugLevel, file: id + '.gradle')
    }

    static GradleRun runMp(String subDir, String id, String execute, int debugLevel = Executer.QUIET) {
        new GradleRun(subDir: "multiproject/" + subDir, id: 'multiproject' + id, execute: execute, debugLevel: debugLevel)
    }

    static Map runGroovyScript(File script) {
        StringWriter stringWriter = new StringWriter()
        PrintWriter printWriter = new PrintWriter(stringWriter)
        logger.info("Evaluating Groovy script: $script.absolutePath")
        new GroovyShell(new Binding(out: printWriter)).evaluate(script)
        [output: stringWriter, command: "groovy $script.name"]
    }

}

class GradleRun {
    String id
    String execute
    int debugLevel
    String file
    String subDir
    boolean groovyScript = false
}