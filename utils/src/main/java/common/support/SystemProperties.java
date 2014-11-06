package common.support;

import java.net.InetAddress;
import java.net.UnknownHostException;

public abstract class SystemProperties {
    private static String s_hostName;

    private static String s_hostAddress;

    private static String s_machineID;

    private static String s_serviceName = "UNKNOWN";

    private static String s_serviceVersion = "UNKNOWN";

    static {
        try {
            final InetAddress addr = InetAddress.getLocalHost();
            s_hostName = addr.getCanonicalHostName();
            s_hostAddress = addr.getHostAddress();
            s_machineID = s_hostName + '/' + s_hostAddress;
        } catch (final UnknownHostException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static String getHostName() {
        return s_hostName;
    }

    public static String getHostAddress() {
        return s_hostAddress;
    }

    public static String getMachineID() {
        return s_machineID;
    }

    public static String getServiceName() {
        return s_serviceName;
    }

    public static void setServiceName(final String serviceName) {
        s_serviceName = serviceName;
    }

    public static String getServiceVersion() {
        return s_serviceVersion;
    }

    public static void setServiceVersion(final String serviceVersion) {
        s_serviceVersion = serviceVersion;
    }
}
