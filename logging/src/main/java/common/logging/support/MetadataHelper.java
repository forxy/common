package common.logging.support;

import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.Advised;
import org.springframework.cglib.proxy.Proxy;

/**
 * Helper class for metadata extraction
 */
public class MetadataHelper {
    private static String localHost;
    private static String localHostAddress;

    private MetadataHelper() {
    }

    /**
     * Provides local host name
     *
     * @return local host name
     */
    public static String getLocalHost() {
        return localHost;
    }

    /**
     * Provides local host ip address
     *
     * @return local host ip address
     */
    public static String getLocalHostAddress() {
        return localHostAddress;
    }

    /**
     * Get short one-line description of exception
     *
     * @param th throwable
     * @return short error description
     */
    public static String getShortErrorDescription(final Throwable th) {
        final StringBuilder sb = new StringBuilder(60);
        if (th != null) {
            sb.append(th.getClass().getSimpleName());
            String msg = th.getMessage();
            msg = msg != null ? FormatHelper.compactFormat(msg.substring(0, Math.min(msg.length(), 50))) : null;
            if (msg != null) {
                sb.append(" ").append(msg);
            }
        }
        return sb.toString();
    }

    /**
     * Extracts real class name from advised (or not) beans.
     * Compatible with spring 2.0.2 +
     *
     * @param obj input object
     * @return class name of a real object if obj is proxied
     */
    public static String getRealClassName(final Object obj) {
        Class result = null;
        try {
            Object current = obj;
            result = obj.getClass();
            //dig through spring advised chain
            while (current instanceof Advised) {
                final TargetSource targetSource = ((Advised) current).getTargetSource();
                result = targetSource.getTargetClass();
                current = targetSource.getTarget();
            }
            if (Proxy.isProxyClass(result)) {
                //is jdk proxy
                final Class[] interfaces = result.getInterfaces();
                if (interfaces.length > 0) {
                    result = interfaces[0];
                }
            } else if (result.getName().contains("$$")) {
                //is cglib proxy
                result = result.getSuperclass();
            }
        } catch (Exception ignored) {
            result = obj.getClass();
        }
        return result.getSimpleName();
    }
}
