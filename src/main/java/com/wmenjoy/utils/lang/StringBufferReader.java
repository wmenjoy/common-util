package com.wmenjoy.utils.lang;

import java.io.IOException;
import java.io.Reader;

public class StringBufferReader extends Reader{

    private Reader in;

    private char cb[];
    private int nChars, nextChar;

    private static final int INVALIDATED = -2;
    private static final int UNMARKED = -1;
    private int markedChar = UNMARKED;
    private int readAheadLimit = 0; /* Valid only when markedChar > 0 */

    /** If the next character is a line feed, skip it */
    private boolean skipLF = false;

    /** The skipLF flag when the mark was set */
    private boolean markedSkipLF = false;

    private static int defaultCharBufferSize = 8192;
    private static int defaultExpectedLineLength = 80;

    /**
     * Creates a buffering character-input stream that uses an input buffer of
     * the specified size.
     *
     * @param  in   A Reader
     * @param  sz   Input-buffer size
     *
     * @exception  IllegalArgumentException  If sz is <= 0
     */
    public StringBufferReader(final Reader in, final int sz) {
        super(in);
        if (sz <= 0) {
            throw new IllegalArgumentException("Buffer size <= 0");
        }
        this.in = in;
        this.cb = new char[sz];
        this.nextChar = this.nChars = 0;
    }

    /**
     * Creates a buffering character-input stream that uses a default-sized
     * input buffer.
     *
     * @param  in   A Reader
     */
    public StringBufferReader(final Reader in) {
        this(in, defaultCharBufferSize);
    }

    /** Checks to make sure that the stream has not been closed */
    private void ensureOpen() throws IOException {
        if (this.in == null) {
            throw new IOException("Stream closed");
        }
    }

    /**
     * Fills the input buffer, taking the mark into account if it is valid.
     */
    private void fill() throws IOException {
        int dst;
        if (this.markedChar <= UNMARKED) {
            /* No mark */
            dst = 0;
        } else {
            /* Marked */
            final int delta = this.nextChar - this.markedChar;
            if (delta >= this.readAheadLimit) {
                /* Gone past read-ahead limit: Invalidate mark */
                this.markedChar = INVALIDATED;
                this.readAheadLimit = 0;
                dst = 0;
            } else {
                if (this.readAheadLimit <= this.cb.length) {
                    /* Shuffle in the current buffer */
                    System.arraycopy(this.cb, this.markedChar, this.cb, 0, delta);
                    this.markedChar = 0;
                    dst = delta;
                } else {
                    /* Reallocate buffer to accommodate read-ahead limit */
                    final char ncb[] = new char[this.readAheadLimit];
                    System.arraycopy(this.cb, this.markedChar, ncb, 0, delta);
                    this.cb = ncb;
                    this.markedChar = 0;
                    dst = delta;
                }
                this.nextChar = this.nChars = delta;
            }
        }

        int n;
        do {
            n = this.in.read(this.cb, dst, this.cb.length - dst);
        } while (n == 0);
        if (n > 0) {
            this.nChars = dst + n;
            this.nextChar = dst;
        }
    }

    /**
     * Reads a single character.
     *
     * @return The character read, as an integer in the range
     *         0 to 65535 (<tt>0x00-0xffff</tt>), or -1 if the
     *         end of the stream has been reached
     * @exception  IOException  If an I/O error occurs
     */
    @Override
    public int read() throws IOException {
        synchronized (this.lock) {
            this.ensureOpen();
            for (;;) {
                if (this.nextChar >= this.nChars) {
                    this.fill();
                    if (this.nextChar >= this.nChars) {
                        return -1;
                    }
                }
                if (this.skipLF) {
                    this.skipLF = false;
                    if (this.cb[this.nextChar] == '\n') {
                        this.nextChar++;
                        continue;
                    }
                }
                return this.cb[this.nextChar++];
            }
        }
    }

    /**
     * Reads characters into a portion of an array, reading from the underlying
     * stream if necessary.
     */
    private int read1(final char[] cbuf, final int off, final int len) throws IOException {
        if (this.nextChar >= this.nChars) {
            /* If the requested length is at least as large as the buffer, and
               if there is no mark/reset activity, and if line feeds are not
               being skipped, do not bother to copy the characters into the
               local buffer.  In this way buffered streams will cascade
               harmlessly. */
            if ((len >= this.cb.length) && (this.markedChar <= UNMARKED) && !this.skipLF) {
                return this.in.read(cbuf, off, len);
            }
            this.fill();
        }
        if (this.nextChar >= this.nChars) {
            return -1;
        }
        if (this.skipLF) {
            this.skipLF = false;
            if (this.cb[this.nextChar] == '\n') {
                this.nextChar++;
                if (this.nextChar >= this.nChars) {
                    this.fill();
                }
                if (this.nextChar >= this.nChars) {
                    return -1;
                }
            }
        }
        final int n = Math.min(len, this.nChars - this.nextChar);
        System.arraycopy(this.cb, this.nextChar, cbuf, off, n);
        this.nextChar += n;
        return n;
    }

    /**
     * Reads characters into a portion of an array.
     *
     * <p> This method implements the general contract of the corresponding
     * <code>{@link Reader#read(char[], int, int) read}</code> method of the
     * <code>{@link Reader}</code> class.  As an additional convenience, it
     * attempts to read as many characters as possible by repeatedly invoking
     * the <code>read</code> method of the underlying stream.  This iterated
     * <code>read</code> continues until one of the following conditions becomes
     * true: <ul>
     *
     *   <li> The specified number of characters have been read,
     *
     *   <li> The <code>read</code> method of the underlying stream returns
     *   <code>-1</code>, indicating end-of-file, or
     *
     *   <li> The <code>ready</code> method of the underlying stream
     *   returns <code>false</code>, indicating that further input requests
     *   would block.
     *
     * </ul> If the first <code>read</code> on the underlying stream returns
     * <code>-1</code> to indicate end-of-file then this method returns
     * <code>-1</code>.  Otherwise this method returns the number of characters
     * actually read.
     *
     * <p> Subclasses of this class are encouraged, but not required, to
     * attempt to read as many characters as possible in the same fashion.
     *
     * <p> Ordinarily this method takes characters from this stream's character
     * buffer, filling it from the underlying stream as necessary.  If,
     * however, the buffer is empty, the mark is not valid, and the requested
     * length is at least as large as the buffer, then this method will read
     * characters directly from the underlying stream into the given array.
     * Thus redundant <code>BufferedReader</code>s will not copy data
     * unnecessarily.
     *
     * @param      cbuf  Destination buffer
     * @param      off   Offset at which to start storing characters
     * @param      len   Maximum number of characters to read
     *
     * @return     The number of characters read, or -1 if the end of the
     *             stream has been reached
     *
     * @exception  IOException  If an I/O error occurs
     */
    @Override
    public int read(final char cbuf[], final int off, final int len) throws IOException {
        synchronized (this.lock) {
            this.ensureOpen();
            if ((off < 0) || (off > cbuf.length) || (len < 0) ||
                ((off + len) > cbuf.length) || ((off + len) < 0)) {
                throw new IndexOutOfBoundsException();
            } else if (len == 0) {
                return 0;
            }

            int n = this.read1(cbuf, off, len);
            if (n <= 0) {
                return n;
            }
            while ((n < len) && this.in.ready()) {
                final int n1 = this.read1(cbuf, off + n, len - n);
                if (n1 <= 0) {
                    break;
                }
                n += n1;
            }
            return n;
        }
    }

    /**
     * Reads a line of text.  A line is considered to be terminated by any one
     * of a line feed ('\n'), a carriage return ('\r'), or a carriage return
     * followed immediately by a linefeed.
     *
     * @param      ignoreLF  If true, the next '\n' will be skipped
     *
     * @return     A String containing the contents of the line, not including
     *             any line-termination characters, or null if the end of the
     *             stream has been reached
     *
     * @see        java.io.LineNumberReader#readLine()
     *
     * @exception  IOException  If an I/O error occurs
     */
    String readLine(final boolean ignoreLF) throws IOException {
        StringBuffer s = null;
        int startChar;

        synchronized (this.lock) {
            this.ensureOpen();
            boolean omitLF = ignoreLF || this.skipLF;

        bufferLoop:
            for (;;) {

                if (this.nextChar >= this.nChars) {
                    this.fill();
                }
                if (this.nextChar >= this.nChars) { /* EOF */
                    if ((s != null) && (s.length() > 0)) {
                        return s.toString();
                    } else {
                        return null;
                    }
                }
                boolean eol = false;
                char c = 0;
                int i;

                /* Skip a leftover '\n', if necessary */
                if (omitLF && (this.cb[this.nextChar] == '\n')) {
                    this.nextChar++;
                }
                this.skipLF = false;
                omitLF = false;

            charLoop:
                for (i = this.nextChar; i < this.nChars; i++) {
                    c = this.cb[i];
                    if ((c == '\n') || (c == '\r')) {
                        eol = true;
                        break charLoop;
                    }
                }

                startChar = this.nextChar;
                this.nextChar = i;

                if (eol) {
                    String str;
                    if (s == null) {
                        str = new String(this.cb, startChar, i - startChar);
                    } else {
                        s.append(this.cb, startChar, i - startChar);
                        str = s.toString();
                    }
                    this.nextChar++;
                    if (c == '\r') {
                        this.skipLF = true;
                    }
                    return str;
                }

                if (s == null) {
                    s = new StringBuffer(defaultExpectedLineLength);
                }
                s.append(this.cb, startChar, i - startChar);
            }
        }
    }

    /**
     * Reads a line of text.  A line is considered to be terminated by any one
     * of a line feed ('\n'), a carriage return ('\r'), or a carriage return
     * followed immediately by a linefeed.
     *
     * @return     A String containing the contents of the line, not including
     *             any line-termination characters, or null if the end of the
     *             stream has been reached
     *
     * @exception  IOException  If an I/O error occurs
     *
     * @see java.nio.file.Files#readAllLines
     */
    public String readLine() throws IOException {
        return this.readLine(false);
    }

    /**
     * Skips characters.
     *
     * @param  n  The number of characters to skip
     *
     * @return    The number of characters actually skipped
     *
     * @exception  IllegalArgumentException  If <code>n</code> is negative.
     * @exception  IOException  If an I/O error occurs
     */
    @Override
    public long skip(final long n) throws IOException {
        if (n < 0L) {
            throw new IllegalArgumentException("skip value is negative");
        }
        synchronized (this.lock) {
            this.ensureOpen();
            long r = n;
            while (r > 0) {
                if (this.nextChar >= this.nChars) {
                    this.fill();
                }
                if (this.nextChar >= this.nChars) {
                    break;
                }
                if (this.skipLF) {
                    this.skipLF = false;
                    if (this.cb[this.nextChar] == '\n') {
                        this.nextChar++;
                    }
                }
                final long d = this.nChars - this.nextChar;
                if (r <= d) {
                    this.nextChar += r;
                    r = 0;
                    break;
                }
                else {
                    r -= d;
                    this.nextChar = this.nChars;
                }
            }
            return n - r;
        }
    }

    /**
     * Tells whether this stream is ready to be read.  A buffered character
     * stream is ready if the buffer is not empty, or if the underlying
     * character stream is ready.
     *
     * @exception  IOException  If an I/O error occurs
     */
    @Override
    public boolean ready() throws IOException {
        synchronized (this.lock) {
            this.ensureOpen();

            /*
             * If newline needs to be skipped and the next char to be read
             * is a newline character, then just skip it right away.
             */
            if (this.skipLF) {
                /* Note that in.ready() will return true if and only if the next
                 * read on the stream will not block.
                 */
                if ((this.nextChar >= this.nChars) && this.in.ready()) {
                    this.fill();
                }
                if (this.nextChar < this.nChars) {
                    if (this.cb[this.nextChar] == '\n') {
                        this.nextChar++;
                    }
                    this.skipLF = false;
                }
            }
            return (this.nextChar < this.nChars) || this.in.ready();
        }
    }

    /**
     * Tells whether this stream supports the mark() operation, which it does.
     */
    @Override
    public boolean markSupported() {
        return true;
    }

    /**
     * Marks the present position in the stream.  Subsequent calls to reset()
     * will attempt to reposition the stream to this point.
     *
     * @param readAheadLimit   Limit on the number of characters that may be
     *                         read while still preserving the mark. An attempt
     *                         to reset the stream after reading characters
     *                         up to this limit or beyond may fail.
     *                         A limit value larger than the size of the input
     *                         buffer will cause a new buffer to be allocated
     *                         whose size is no smaller than limit.
     *                         Therefore large values should be used with care.
     *
     * @exception  IllegalArgumentException  If readAheadLimit is < 0
     * @exception  IOException  If an I/O error occurs
     */
    @Override
    public void mark(final int readAheadLimit) throws IOException {
        if (readAheadLimit < 0) {
            throw new IllegalArgumentException("Read-ahead limit < 0");
        }
        synchronized (this.lock) {
            this.ensureOpen();
            this.readAheadLimit = readAheadLimit;
            this.markedChar = this.nextChar;
            this.markedSkipLF = this.skipLF;
        }
    }

    /**
     * Resets the stream to the most recent mark.
     *
     * @exception  IOException  If the stream has never been marked,
     *                          or if the mark has been invalidated
     */
    @Override
    public void reset() throws IOException {
        synchronized (this.lock) {
            this.ensureOpen();
            if (this.markedChar < 0) {
                throw new IOException((this.markedChar == INVALIDATED)
                                      ? "Mark invalid"
                                      : "Stream not marked");
            }
            this.nextChar = this.markedChar;
            this.skipLF = this.markedSkipLF;
        }
    }

    @Override
    public void close() throws IOException {
        synchronized (this.lock) {
            if (this.in == null) {
                return;
            }
            this.in.close();
            this.in = null;
            this.cb = null;
        }
    }



}
