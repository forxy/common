package common.deploy.task

import groovy.xml.MarkupBuilder
import groovy.xml.XmlUtil
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class UpdateServerXmlTask extends DefaultTask {

    def Integer port
    def Integer sslPort
    def Integer ajpPort

    def Boolean useSSL = false

    def String tomcatHome = System.env.TOMCAT_HOME
    def String serviceName = 'Catalina'
    def Integer basePort = 10000
    def String protocol = 'HTTP/1.1'
    def Integer connectionTimeout = 20000
    def Integer maxHttpHeaderSize = 8192
    def Integer maxThreads = 150
    def Integer minSpareThreads = 25
    def Integer maxSpareThreads = 200
    def Boolean enableLookups = false
    def Boolean disableUploadTimeout = true
    def Integer acceptCount = 100
    def String ajpProtocol = 'AJP/1.3'
    def Boolean secure = true
    def Boolean clientAuth = false
    def String defaultHost = 'localhost'
    def String appBase = 'webapps'
    def Boolean unpackWARs = true
    def Boolean autoDeploy = true
    def String logsDir = 'logs'
    def String accessLogPrefix = 'localhost_access_log.'
    def String accessLogSuffix = '.txt'
    def String accessLogPattern = '%h %l %u %t &quot;%r&quot; %s %b'
    def String sslProtocol = 'TLS'
    def String keyAlias = 'alias'
    def String keystoreFile = '/conf/keystore.jks'
    def String keystorePass = 'changeit'
    def String keystoreType = 'JKS'
    def String truststoreFile = '/conf/truststore.jks'
    def String truststorePass = 'changeit'
    def String truststoreType = 'JKS'

    @TaskAction
    void updateServerConfigFile() {
        configurePorts()
        def serverXml = new File("$tomcatHome/conf/server.xml")
        def server = new XmlSlurper().parse(serverXml);
        def oldServiceNode = server.Service.find {
            (it.@name == serviceName) || (it.Connector.@port == port)
        }
        def String serviceNode = buildServiceNode()
        def newServiceNode = new XmlSlurper().parseText(serviceNode)

        if (oldServiceNode) {
            // replace old service node by null
            oldServiceNode.replaceNode{}
        }

        // add new service node
        server << newServiceNode

        // write back into file
        serverXml.write(XmlUtil.serialize(server))
    }

    private void configurePorts() {
        port = basePort + 80
        sslPort = basePort + 90
        ajpPort = basePort + 9
    }

    private String buildServiceNode() {
        def sw = new StringWriter()
        def mb = new MarkupBuilder(sw)
        mb.Service(name: serviceName) {
            Connector(port: port, protocol: protocol, connectionTimeout: connectionTimeout, redirectPort: sslPort)
            if (useSSL) {
                Connector(port: sslPort, SSLEnabled: true, maxHttpHeaderSize: maxHttpHeaderSize,
                        maxThreads: maxThreads, minSpareThreads: minSpareThreads, maxSpareThreads: maxSpareThreads,
                        enableLookups: enableLookups, disableUploadTimeout: disableUploadTimeout,
                        acceptCount: acceptCount, scheme: 'https', secure: secure, clientAuth: clientAuth,
                        sslProtocol: sslProtocol, keystoreFile: keystoreFile, keystorePass: keystorePass,
                        keystoreType: keystoreType, keyAlias: keyAlias, truststoreFile: truststoreFile,
                        truststorePass: truststorePass, truststoreType: truststoreType)
            }
            Connector(port: ajpPort, protocol: ajpProtocol, redirectPort: sslPort)
            Engine(name: serviceName, defaultHost: defaultHost) {
                Realm(className: 'org.apache.catalina.realm.LockOutRealm') {
                    Realm(className: 'org.apache.catalina.realm.UserDatabaseRealm', resourceName: 'UserDatabase')
                }
                Host(name: defaultHost, appBase: appBase, unpackWARs: unpackWARs, autoDeploy: autoDeploy) {
                    Valve(className: 'org.apache.catalina.valves.AccessLogValve', directory: logsDir,
                            prefix: accessLogPrefix, suffix: accessLogSuffix, pattern: accessLogPattern)
                }
            }
        }
        sw.toString()
    }
}