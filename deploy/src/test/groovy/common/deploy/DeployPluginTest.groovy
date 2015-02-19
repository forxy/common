package common.deploy

import common.deploy.task.CreateWebContextTask
import common.deploy.task.UpdateServerXmlTask
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Delete
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertTrue

/**
 * Gradle deploy plugin test
 */
class DeployPluginTest {
    def static final TOMCAT_HOME = this.getClass().getResource("/tomcatHome").path
    def static final SERVICE_NAME = 'Test'

    @Test
    public void deployPluginAddsItsTaskToProject() {
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'war'
        project.apply plugin: 'deploy'
        project.deploy.serviceName = 'Test'

        assertNotNull(project.deploy.serviceName)
        assertTrue(project.tasks.copyEnvConfigApp instanceof Copy)
        assertTrue(project.tasks.copyEnvConfigTomcat instanceof Copy)
        assertTrue(project.tasks.createWebContext instanceof CreateWebContextTask)
        assertTrue(project.tasks.updateServerXml instanceof UpdateServerXmlTask)
        assertTrue(project.tasks.deploy instanceof Copy)
        assertTrue(project.tasks.cleanDeploy instanceof Delete)
    }
}
