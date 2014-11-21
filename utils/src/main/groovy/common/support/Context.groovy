package common.support

import java.util.concurrent.LinkedBlockingDeque

/**
 * Thread-local storage class for logging context analogue and combination of log4j MDC and NDC classes.
 */
abstract class Context {
    static class ContextData {
        private final Map<String, Object> global
        private final LinkedBlockingDeque<Map<String, Object>> frame

        protected ContextData(final Map<String, Object> global, final LinkedBlockingDeque<Map<String, Object>> frame) {
            this.global = global
            this.frame = frame
        }

        Map<String, Object> getGlobal() {
            return global
        }

        Map<String, Object> getFrame() {
            return frame.peek()
        }

        int getFrameStackSize() {
            return frame.size()
        }
    }

    /**
     * Thread local holding ContextData
     */
    private static ThreadLocal<ContextData> contextStorage = new ThreadLocal<ContextData>() {
        @Override
        protected ContextData initialValue() {
            return new ContextData(new LinkedHashMap<String, Object>(), new LinkedBlockingDeque<Map<String, Object>>())
        }
    }

    private Context() {
    }

    /**
     * Push context frame stack deeper
     */
    static void push() {
        contextStorage.get().@frame.push(new LinkedHashMap<String, Object>())
    }

    /**
     * Obtain context data
     *
     * @return ContextData bean
     */
    static ContextData peek() {
        return contextStorage.get()
    }

    /**
     * Pop context frame out of stack, removes value from thread local if no frames left
     */
    static void pop() {
        final LinkedBlockingDeque frame = contextStorage.get().@frame
        frame.pop()
        if (frame.empty) {
            contextStorage.remove()
        }
    }

    /**
     * Add value to global frame
     *
     * @param key string key, typed object to support both enum & string values
     * @param value not null object value
     */
    static void addGlobal(final Object key, final Object value) {
        if (value != null) {
            contextStorage.get().global.put(key.toString(), value)
        }
    }

    /**
     * Add value to stack frame
     *
     * @param key string key, typed object to support both enum & string values
     * @param value not null object value
     */
    static void addFrame(final Object key, final Object value) {
        if (value != null) {
            final ContextData cd = contextStorage.get()
            if (cd.frameStackSize > 0) {
                cd.frame.put(key.toString(), value)
            }
        }
    }

    /**
     * Check if global or stack frame contains key
     *
     * @param obj string key, typed object to support both enum & string values
     * @return true if global or frame contains key
     */
    static boolean contains(final Object obj) {
        final String key = obj.toString()
        final ContextData cd = contextStorage.get()
        return cd.global.containsKey(key) || (cd.frameStackSize > 0 && cd.frame.containsKey(key))
    }
}