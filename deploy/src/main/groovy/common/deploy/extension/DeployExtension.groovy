package common.deploy.extension

class DeployExtension {
    def String tomcatHome = System.env.TOMCAT_HOME
    def String appconfigDir = "appconfig"
    def String env = "dev"
    def String host = 'localhost'
    def String serviceName = 'Catalina'
    def Boolean reloadableWar = true
    def Set<String> configIncludes = []
    def Set<String> configExcludes = []

    // --------- Generating Tomcat server.xml -------------------------
    def Boolean useSSL = false
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
}
