package common.logging.support

import common.utils.EncodingHelper
import common.utils.support.ContextData

/**
 * Log helper to convert logging data into string
 */
abstract class LogHelper {

    static final Set<String> KEYS_PAYLOAD = new HashSet<String>()
    static final Set<String> KEYS_HTTP = new HashSet<String>()

    static {
        KEYS_PAYLOAD.add(Fields.RequestPayload.name())
        KEYS_PAYLOAD.add(Fields.ResponsePayload.name())
        KEYS_HTTP.add(Fields.RequestURL.name())
        KEYS_HTTP.add(Fields.RequestHeaders.name())
        KEYS_HTTP.add(Fields.ResponseURL.name())
        KEYS_HTTP.add(Fields.ResponseHeaders.name())
    }

    static String contextDataToLogString(final ContextData data) {
        final StringBuilder sb = new StringBuilder(1024)
        final StringBuilder ht = new StringBuilder(1024)
        final StringBuilder pl = new StringBuilder(4096)
        for (final Map.Entry<String, Object> kv : data.getGlobal().entrySet()) {
            sb.append(kv.getKey()).append('=').append(convertSingle(kv.getValue())).append(' ')
        }
        if (data.getFrameStackSize() > 0) {
            for (final Map.Entry<String, Object> kv : data.getFrame().entrySet()) {
                final String key = kv.getKey()
                if (KEYS_HTTP.contains(key)) {
                    ht.append('\n').append('@').append(key).append('=').append(convertSingle(kv.getValue()))
                } else if (KEYS_PAYLOAD.contains(key)) {
                    pl.append('\n').append('@').append(key).append('=').append(convertSingle(kv.getValue()))
                } else {
                    sb.append(key).append('=').append(convertSingle(kv.getValue())).append(' ')
                }
            }
        }
        return sb.append(ht).append(pl).toString()
    }

    static String convertSingle(final Object value) {
        final String result
        if (value != null) {
            if (value instanceof byte[]) {
                result = convertBytes((byte[]) value)
            } else {
                result = value.toString()
            }
        } else {
            result = ''
        }
        return result
    }

    static String convertBytes(final byte[] bytes) {
        String result = EncodingHelper.toUTFString(bytes)
        return FormatHelper.compactFormat(result)
        //return FormatHelper.prettyBreak(FormatHelper.compactFormat(result), 9900, '\n')
    }
}
