package common.utils.support

abstract class SystemProperties {

    static String hostName

    static String hostAddress

    static String machineID

    static String serviceName = 'UNKNOWN'

    static String serviceVersion = 'UNKNOWN'

    static {
        try {
            final InetAddress addr = InetAddress.getLocalHost()
            hostName = addr.getCanonicalHostName()
            hostAddress = addr.getHostAddress()
            machineID = hostName + '/' + hostAddress
        } catch (final UnknownHostException e) {
            throw new ExceptionInInitializerError(e)
        }
    }
}
