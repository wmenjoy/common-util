package com.wmenjoy.utils.buffer;

/**
 * This is a small utility class, whose main functionality is to allow simple
 * reuse of raw byte/char buffers. It is usually used through
 * <code>ThreadLocal</code> member of the owning class pointing to instance of
 * this class through a <code>SoftReference</code>. The end result is a
 * low-overhead GC-cleanable recycling: hopefully ideal for use by stream
 * readers.
 */
public class BufferRecycler {
    public final static int DEFAULT_WRITE_CONCAT_BUFFER_LEN = 2000;

    public enum ByteBufferType {
        READ_IO_BUFFER(4000)
        /**
         * Buffer used for temporarily storing encoded content; used for example
         * by UTF-8 encoding writer
         */
        , WRITE_ENCODING_BUFFER(4000)

        /**
         * Buffer used for temporarily concatenating output; used for example
         * when requesting output as byte array.
         */
        , WRITE_CONCAT_BUFFER(2000);

        private final int size;

        ByteBufferType(final int size) {
            this.size = size;
        }
    }

    public enum CharBufferType {
        TOKEN_BUFFER(2000) // Tokenizable input
        , CONCAT_BUFFER(2000) // concatenated output
        , TEXT_BUFFER(200) // Text content from input
        , NAME_COPY_BUFFER(200) // Temporary buffer for getting name characters
        ;

        private final int size;

        CharBufferType(final int size) {
            this.size = size;
        }
    }

    final protected byte[][] _byteBuffers = new byte[ByteBufferType.values().length][];
    final protected char[][] _charBuffers = new char[CharBufferType.values().length][];

    public BufferRecycler() {
    }

    public final byte[] allocByteBuffer(final ByteBufferType type) {
        final int ix = type.ordinal();
        byte[] buffer = this._byteBuffers[ix];
        if (buffer == null) {
            buffer = this.balloc(type.size);
        } else {
            this._byteBuffers[ix] = null;
        }
        return buffer;
    }

    public final void releaseByteBuffer(final ByteBufferType type, final byte[] buffer) {
        this._byteBuffers[type.ordinal()] = buffer;
    }

    public final char[] allocCharBuffer(final CharBufferType type) {
        return this.allocCharBuffer(type, 0);
    }

    public final char[] allocCharBuffer(final CharBufferType type, int minSize) {
        if (type.size > minSize) {
            minSize = type.size;
        }
        final int ix = type.ordinal();
        char[] buffer = this._charBuffers[ix];
        if ((buffer == null) || (buffer.length < minSize)) {
            buffer = this.calloc(minSize);
        } else {
            this._charBuffers[ix] = null;
        }
        return buffer;
    }

    public final void releaseCharBuffer(final CharBufferType type, final char[] buffer) {
        this._charBuffers[type.ordinal()] = buffer;
    }

    /*
    /**********************************************************
    /* Actual allocations separated for easier debugging/profiling
    /**********************************************************
     */

    private final byte[] balloc(final int size) {
        return new byte[size];
    }

    private final char[] calloc(final int size) {
        return new char[size];
    }
}
