package common.logging.wrapper

class TeePrintWriter extends PrintWriter {
    final PrintWriter secondaryWriter

    /**
     * Create a new tee writer with the specified primary and secondary
     * writers.
     *
     * @param primary The primary writer
     * @param secondary The secondary writer
     */
    TeePrintWriter(final PrintWriter primary, final PrintWriter secondary) {
        super(primary)
        secondaryWriter = secondary
    }

    @Override
    void write(final int c) {
        super.write(c)
        secondaryWriter.write(c)
        secondaryWriter.flush()
    }

    @Override
    void write(final char[] cbuf, final int off, final int len) {
        super.write(cbuf, off, len)
        secondaryWriter.write(cbuf, off, len)
        secondaryWriter.flush()
    }

    @Override
    void write(final String str, final int off, final int len) {
        super.write(str, off, len)
        secondaryWriter.write(str, off, len)
        secondaryWriter.flush()
    }

    @Override
    void flush() {
        super.flush()
        secondaryWriter.flush()
    }

    @Override
    void close() {
        super.close()
        secondaryWriter.close()
    }
}