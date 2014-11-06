package common.deploy.task

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static org.junit.Assert.*

/**
 * Created by Tiger on 08.05.14.
 */
class CreateWebContextTest {
    def static final TOMCAT_HOME = this.getClass().getResource("/tomcatHome").path
    def static final SERVICE_NAME = 'Test'

    @Test
    public void testUpdateServerConfigFile() {
        println TOMCAT_HOME
        Project project = ProjectBuilder.builder().build()
        def CreateWebContextTask task = project.task('createWebContext', type: CreateWebContextTask) as CreateWebContextTask
        assertTrue(task instanceof CreateWebContextTask)

        // configure test task
        task.tomcatHome = TOMCAT_HOME
        task.serviceName = SERVICE_NAME
        task.createWebContextFile()

        def context = new XmlParser().parse("$TOMCAT_HOME/conf/Test/localhost/Test.xml");
        def docBase = context["@docBase"]

        assertNotNull(docBase)
        assertEquals("$TOMCAT_HOME/warfiles/$project.name".toString(), docBase)
    }
}
