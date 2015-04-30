package common.utils.support

import java.util.concurrent.LinkedBlockingDeque

class ContextData {
    private final Map<String, Object> global
    private final LinkedBlockingDeque<Map<String, Object>> frames

    protected ContextData(final Map<String, Object> global, final LinkedBlockingDeque<Map<String, Object>> frames) {
        this.global = global
        this.frames = frames
    }

    Map<String, Object> getGlobal() {
        return global
    }

    Map<String, Object> getFrame() {
        return frames.peek()
    }

    LinkedBlockingDeque<Map<String, Object>> getFrames() {
        return frames
    }

    int getFrameStackSize() {
        return frames.size()
    }
}
