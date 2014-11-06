package common.deploy

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Delete
import common.deploy.extension.DeployExtension
import common.deploy.task.CreateWebContextTask
import common.deploy.task.RemoveServiceFromServerXmlTask
import common.deploy.task.UpdateServerXmlTask

class DeployPlugin implements Plugin<Project> {

    void apply(Project project) {

        project.extensions.create("deploy", DeployExtension)
        project.apply plugin: 'war'

        if (project.hasProperty('env')) {
            project.deploy.env = project.getProperty("env")
        }

        project.task('copyEnvConfigApp', type: Copy) {
            from { project.deploy.appconfigDir }
            into { "$project.deploy.tomcatHome/conf/$project.deploy.serviceName/$project.deploy.appconfigDir" }
            include '**/*.properties'
            include '**/*.xml'
            exclude '**/*.bat'
            exclude '**/*.sh'
            include { project.deploy.configIncludes }
            exclude { project.deploy.configExcludes }
        }

        project.task('copyEnvConfigTomcat', type: Copy) {
            from { "$project.deploy.appconfigDir/env/$project.deploy.env" }
            into { "$project.deploy.tomcatHome/bin" }
            include '**/*.bat'
            include '**/*.sh'
        }

        project.task('copyEnvConfigCertificates', type: Copy) {
            from { "$project.deploy.appconfigDir/env/$project.deploy.env" }
            into { "$project.deploy.tomcatHome/conf/$project.deploy.serviceName/$project.deploy.appconfigDir" }
            include '**/*.jks'
        }

        project.task('updateServerXml', type: UpdateServerXmlTask) {
            doFirst {
                tomcatHome project.deploy.tomcatHome
                serviceName project.deploy.serviceName
                useSSL project.deploy.useSSL
                basePort project.deploy.basePort
                protocol project.deploy.protocol
                connectionTimeout project.deploy.connectionTimeout
                maxHttpHeaderSize project.deploy.maxHttpHeaderSize
                maxThreads project.deploy.maxThreads
                minSpareThreads project.deploy.minSpareThreads
                maxSpareThreads project.deploy.maxSpareThreads
                enableLookups project.deploy.enableLookups
                disableUploadTimeout project.deploy.disableUploadTimeout
                acceptCount project.deploy.acceptCount
                ajpProtocol project.deploy.ajpProtocol
                secure project.deploy.secure
                clientAuth project.deploy.clientAuth
                defaultHost project.deploy.defaultHost
                appBase project.deploy.appBase
                unpackWARs project.deploy.unpackWARs
                autoDeploy project.deploy.autoDeploy
                logsDir project.deploy.logsDir
                accessLogPrefix project.deploy.accessLogPrefix
                accessLogSuffix project.deploy.accessLogSuffix
                accessLogPattern project.deploy.accessLogPattern
                sslProtocol project.deploy.sslProtocol
                keyAlias project.deploy.keyAlias
                keystoreFile project.deploy.keystoreFile
                keystorePass project.deploy.keystorePass
                keystoreType project.deploy.keystoreType
                truststoreFile project.deploy.truststoreFile
                truststorePass project.deploy.truststorePass
                truststoreType project.deploy.truststoreType
            }
        }

        project.task('removeServiceFromServerXml', type: RemoveServiceFromServerXmlTask) {
            doFirst {
                tomcatHome project.deploy.tomcatHome
                serviceName project.deploy.serviceName
                basePort project.deploy.basePort
            }
        }

        project.war {
            manifest {
                attributes(
                        'Product': project.rootProject.name,
                        'Version': project.version,
                        'Built-On': new Date().format('yyyy-mm-dd HH:MM:ss')
                )
            }
        }

        project.task('createWebContext', type: CreateWebContextTask, dependsOn: project.tasks.war) {
            doFirst {
                tomcatHome project.deploy.tomcatHome
                serviceName project.deploy.serviceName
                host project.deploy.host
                warFileName project.war.archiveName
                reloadable project.deploy.reloadableWar
            }
        }

        project.task('deploy', type: Copy, dependsOn:
                [project.tasks.war,
                 project.tasks.copyEnvConfigApp,
                 project.tasks.copyEnvConfigTomcat,
                 project.tasks.copyEnvConfigCertificates,
                 project.tasks.createWebContext,
                 project.tasks.updateServerXml]) {
            from { project.tasks.war }
            into { "$project.deploy.tomcatHome/warfiles" }
        }

        project.task('cleanDeploy', type: Delete, dependsOn: project.tasks.removeServiceFromServerXml) {
            delete {
                println "deleting $project.deploy.tomcatHome/conf/$project.deploy.serviceName"
                project.fileTree("$project.deploy.tomcatHome") {
                    include "conf/$project.deploy.serviceName/**/*.xml",
                            "conf/$project.deploy.serviceName/**/*.properties",
                            "warfiles/$project.rootProject.name*.war"
                }
            }
            doLast {
                new File("$project.deploy.tomcatHome/conf/$project.deploy.serviceName").deleteDir()
            }
        }
    }
}