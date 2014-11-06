package common.deploy.task

import groovy.xml.XmlUtil

class RemoveServiceFromServerXmlTask extends UpdateServerXmlTask {

    void updateServerConfigFile() {
        configurePorts()
        def serverXml = new File("$tomcatHome/conf/server.xml")
        def server = new XmlSlurper().parse(serverXml);
        def oldServiceNode = server.Service.find {
            (it.@name == serviceName) || (it.Connector.@port == port)
        }

        if (oldServiceNode) {
            // replace old service node by null
            oldServiceNode.replaceNode {}
        }

        // write back into file
        serverXml.write(XmlUtil.serialize(server))
    }

    protected void configurePorts() {
        port = basePort + 80
        sslPort = basePort + 90
        ajpPort = basePort + 9
    }
}