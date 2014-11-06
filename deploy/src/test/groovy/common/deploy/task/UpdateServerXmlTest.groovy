package common.deploy.task

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test
import static org.junit.Assert.*

/**
 * Created by Tiger on 08.05.14.
 */
class UpdateServerXmlTest {
    def static final TOMCAT_HOME = this.getClass().getResource("/tomcatHome").path
    def static final String SERVICE_NAME_1 = 'Test1'
    def static final String SERVICE_NAME_2 = 'Test2'

    @Test
    public void testUpdateServerConfigFile() {
        Project project = ProjectBuilder.builder().build()
        def UpdateServerXmlTask taskAdd = project.task('updateServerXml', type: UpdateServerXmlTask) as UpdateServerXmlTask
        assertTrue(taskAdd instanceof UpdateServerXmlTask)

        // configure test add service task
        configureUpdateTask(taskAdd, 8000, SERVICE_NAME_1)
        taskAdd.updateServerConfigFile()

        def server = new XmlParser().parse("$TOMCAT_HOME/conf/server.xml");
        def serviceNode = server.Service.find{it.@name == SERVICE_NAME_1}
        assertNotNull(serviceNode)
        assertEquals(SERVICE_NAME_1, serviceNode.@name)
        assertEquals('8080', serviceNode.Connector[0].@port)

        configureUpdateTask(taskAdd, 9000, SERVICE_NAME_2)
        taskAdd.updateServerConfigFile()

        server = new XmlParser().parse("$TOMCAT_HOME/conf/server.xml");
        serviceNode = server.Service.find{it.@name == SERVICE_NAME_2}
        assertNotNull(serviceNode)
        assertEquals(SERVICE_NAME_2, serviceNode.@name)
        assertEquals('9080', serviceNode.Connector[0].@port)


        // now remove service from server.xml
        def UpdateServerXmlTask taskRemove = project.task('removeServiceFromServerXml', type: RemoveServiceFromServerXmlTask) as RemoveServiceFromServerXmlTask
        assertTrue(taskRemove instanceof RemoveServiceFromServerXmlTask)
        // configure test remove service task
        configureUpdateTask(taskRemove, 8000, SERVICE_NAME_1)
        taskRemove.updateServerConfigFile()

        server = new XmlParser().parse("$TOMCAT_HOME/conf/server.xml");
        serviceNode = server.Service.find{it.@name == SERVICE_NAME_1}
        assertNull(serviceNode)

        // configure test remove second service task
        configureUpdateTask(taskRemove, 9000, SERVICE_NAME_2)
        taskRemove.updateServerConfigFile()

        server = new XmlParser().parse("$TOMCAT_HOME/conf/server.xml");
        serviceNode = server.Service.find{it.@name == SERVICE_NAME_2}
        assertNull(serviceNode)
    }

    private static void configureUpdateTask(final UpdateServerXmlTask task, final int port, final String name) {
        task.tomcatHome = TOMCAT_HOME
        task.useSSL = true
        task.basePort = port
        task.serviceName = name
    }
}
