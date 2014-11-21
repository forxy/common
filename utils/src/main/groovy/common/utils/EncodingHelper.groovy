package common.utils

import java.nio.charset.Charset

abstract class EncodingHelper {
    private static final Charset UTF8
    private static final Charset ISO88591

    private static final Map<String, Charset> CHARSET_CACHE = new HashMap<String, Charset>()

    static {
        for (final Charset charset : Charset.availableCharsets().values()) {
            CHARSET_CACHE.put(charset.name().toLowerCase(Locale.US), charset)
            for (final String alias : charset.aliases()) {
                CHARSET_CACHE.put(alias.toLowerCase(Locale.US), charset)
            }
        }
        UTF8 = CHARSET_CACHE.get('utf-8')
        ISO88591 = CHARSET_CACHE.get('iso-8859-1')
    }

    private EncodingHelper() {
    }

    /**
     * Return charset by alias. Returns default ISO-8859-1 if input is null or charset not found.
     *
     * @param alias - charset name or alias
     */
    static Charset getCharsetByAlias(final String alias) {
        Charset result = alias != null ? CHARSET_CACHE.get(alias.trim().toLowerCase(Locale.US)) : ISO88591
        result = result != null ? result : ISO88591
        return result
    }

    /**
     * Return utf8 byte array out of string
     *
     * @param input string
     * @return bytes
     */
    static byte[] toUTFBytes(final String input) {
        byte[] result = null
        try {
            result = input == null ? null : input.getBytes(UTF8.name()) //.name() is for 1.5 support
        } catch (UnsupportedEncodingException ignored) {
            //cannot happen
        }
        return result
    }

    /**
     * Return utf8 string out of bytes
     *
     * @param input bytes
     * @return string
     */
    static String toUTFString(final byte[] input) {
        String result = null
        try {
            result = input == null ? null : new String(input, UTF8.name()) //.name() is for 1.5 support
        } catch (UnsupportedEncodingException ignored) {
            //cannot happen
        }
        return result
    }

    /**
     * Return string in given charset out of bytes
     *
     * @param input bytes
     * @param charset charset
     * @return string
     */
    static String toCharsetString(final byte[] input, final Charset charset) {
        String result = null
        try {
            result = input == null ? null : new String(input, charset.name()) //.name() is for 1.5 support
        } catch (UnsupportedEncodingException ignored) {
            //cannot happen
        }
        return result
    }

    /**
     * URL Decode data using charset, return initial data on error.
     *
     * @param encodedData encoded data
     * @param charset charset to use
     * @return decoded data
     */
    static String safeURLDecode(final String encodedData, final Charset charset) {
        String result = null
        if (encodedData != null) {
            try {
                result = URLDecoder.decode(encodedData, charset.name())
            } catch (IllegalArgumentException ignore) {
                // encoding failed
                result = encodedData
            } catch (UnsupportedEncodingException ignore) {
                // should never happen as we resolve from existing cs
                result = encodedData
            }
        }
        return result
    }
}