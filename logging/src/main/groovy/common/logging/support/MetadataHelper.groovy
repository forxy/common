package common.logging.support

import org.springframework.aop.TargetSource
import org.springframework.aop.framework.Advised
import org.springframework.cglib.proxy.Proxy

/**
 * Helper class for metadata extraction
 */
class MetadataHelper {
    static String localHost;
    static String localHostAddress;

    /**
     * Get short one-line description of exception
     *
     * @param th throwable
     * @return short error description
     */
    static String getShortErrorDescription(final Throwable th) {
        final StringBuilder sb = new StringBuilder(60);
        if (th != null) {
            sb.append(th.class.simpleName);
            String msg = th.message;
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
    static String getRealClassName(final Object obj) {
        Class result = null;
        try {
            Object current = obj;
            result = obj.class;
            //dig through spring advised chain
            while (current instanceof Advised) {
                final TargetSource targetSource = ((Advised) current).targetSource;
                result = targetSource.targetClass;
                current = targetSource.target;
            }
            if (Proxy.isProxyClass(result)) {
                //is jdk proxy
                final Class[] interfaces = result.interfaces;
                if (interfaces.length > 0) {
                    result = interfaces[0];
                }
            } else if (result.name.contains('$$')) {
                //is cglib proxy
                result = result.superclass;
            }
        } catch (Exception ignored) {
            result = obj.class;
        }
        return result.simpleName;
    }
}
