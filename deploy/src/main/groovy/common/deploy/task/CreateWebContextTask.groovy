package common.deploy.task

import groovy.xml.MarkupBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class CreateWebContextTask extends DefaultTask {

    def String tomcatHome = System.env.TOMCAT_HOME
    def String serviceName = 'Catalina'
    def String host = 'localhost'
    def String warFileName = project.name
    def Boolean reloadable = true

    @TaskAction
    void createWebContextFile() {
        def contextDir = new File("$tomcatHome/conf/${serviceName}/${host}")
        def contextXml = new File("$contextDir.path/${serviceName}.xml")
        contextDir.mkdirs()
        contextXml.createNewFile()
        def mb = new MarkupBuilder(contextXml.newPrintWriter())
        mb.Context(docBase: "${tomcatHome}/warfiles/${warFileName}", reloadable: reloadable)
    }
}