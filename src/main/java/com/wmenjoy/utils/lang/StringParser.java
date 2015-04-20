package com.wmenjoy.utils.lang;

public class StringParser {
    /**
     * Temporary null terminated code point array used by pattern compiling.
     */
    private transient int[] temp;

    public static final int UNIX_LINES = 0x01;

    public static final int CASE_INSENSITIVE = 0x02;

    public static final int COMMENTS = 0x04;

    public static final int MULTILINE = 0x08;

    /**
     * Index into the pattern string that keeps track of how much has been
     * parsed.
     */
    private transient int cursor;

    /**
     * Holds the length of the pattern string.
     */
    private transient int strLength;

    public StringParser(final String str) {
        this.strLength = str.length();

        this.temp = new int[this.strLength + 2];

        int c, count = 0;
        // Convert all chars into code points
        for (int x = 0; x < this.strLength; x += Character.charCount(c)) {
            c = str.codePointAt(x);
            if (isSupplementary(c)) {
            }
            this.temp[count++] = c;
        }

        this.strLength = count;   // patternLength now in code points
    }

    /***
     * 设置
     *
     * @param flag
     * @return
     */
    private boolean has(final int flag) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * 测试是否匹配预定的字符
     */
    public boolean accept(final int ch, final String s) {
        int testChar = this.temp[this.cursor++];
        if (this.has(COMMENTS)) {
            testChar = this.parsePastWhitespace(testChar);
        }
        if (ch != testChar) {
        }
        return true;
    }

    /**
     * Mark the end of pattern with a specific character.
     */
    public void mark(final int c) {
        this.temp[this.strLength] = c;
    }

    /**
     * Peek the next character, and do not advance the cursor.
     */
    public int peek() {
        int ch = this.temp[this.cursor];
        if (this.has(COMMENTS)) {
            ch = this.peekPastWhitespace(ch);
        }
        return ch;
    }

    /**
     * Read the next character, and advance the cursor by one.
     */
    public int read() {
        int ch = this.temp[this.cursor++];
        if (this.has(COMMENTS)) {
            ch = this.parsePastWhitespace(ch);
        }
        return ch;
    }

    /**
     * Read the next character, and advance the cursor by one, ignoring the
     * COMMENTS setting
     */
    public int readEscaped() {
        final int ch = this.temp[this.cursor++];
        return ch;
    }

    /**
     * Advance the cursor by one, and peek the next character.
     */
    public int next() {
        int ch = this.temp[++this.cursor];
        if (this.has(COMMENTS)) {
            ch = this.peekPastWhitespace(ch);
        }
        return ch;
    }

    /**
     * Advance the cursor by one, and peek the next character, ignoring the
     * COMMENTS setting
     */
    public int nextEscaped() {
        final int ch = this.temp[++this.cursor];
        return ch;
    }

    /**
     * If in xmode peek past whitespace and comments.
     */
    private int peekPastWhitespace(int ch) {
        while (ASCII.isSpace(ch) || (ch == '#')) {
            while (ASCII.isSpace(ch)) {
                ch = this.temp[++this.cursor];
            }
            if (ch == '#') {
                ch = this.peekPastLine();
            }
        }
        return ch;
    }

    /**
     * If in xmode parse past whitespace and comments.
     */
    private int parsePastWhitespace(int ch) {
        while (ASCII.isSpace(ch) || (ch == '#')) {
            while (ASCII.isSpace(ch)) {
                ch = this.temp[this.cursor++];
            }
            if (ch == '#') {
                ch = this.parsePastLine();
            }
        }
        return ch;
    }

    /**
     * xmode parse past comment to end of line.
     */
    private int parsePastLine() {
        int ch = this.temp[this.cursor++];
        while ((ch != 0) && !this.isLineSeparator(ch)) {
            ch = this.temp[this.cursor++];
        }
        return ch;
    }

    /**
     * xmode peek past comment to end of line.
     */
    private int peekPastLine() {
        int ch = this.temp[++this.cursor];
        while ((ch != 0) && !this.isLineSeparator(ch)) {
            ch = this.temp[++this.cursor];
        }
        return ch;
    }

    /**
     * Determines if character is a line separator in the current mode
     */
    private boolean isLineSeparator(final int ch) {
        if (this.has(UNIX_LINES)) {
            return ch == '\n';
        } else {
            return ((ch == '\n') || (ch == '\r') || ((ch | 1) == '\u2029') || (ch == '\u0085'));
        }
    }

    /**
     * Read the character after the next one, and advance the cursor by two.
     */
    public int skip() {
        final int i = this.cursor;
        final int ch = this.temp[i + 1];
        this.cursor = i + 2;
        return ch;
    }

    /**
     * Unread one next character, and retreat cursor by one.
     */
    public void unread() {
        this.cursor--;
    }

    /**
     * Determines if there is any supplementary character or unpaired surrogate
     * in the specified range.
     */
    public boolean findSupplementary(final int start, final int end) {
        for (int i = start; i < end; i++) {
            if (isSupplementary(this.temp[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the specified code point is a supplementary character or
     * unpaired surrogate.
     */
    public static final boolean isSupplementary(final int ch) {
        return (ch >= Character.MIN_SUPPLEMENTARY_CODE_POINT) || Character.isSurrogate((char)ch);
    }

    public int getCursor() {
        return this.cursor;
    }

    public void setCursor(final int cursor) {
        this.cursor = cursor;
    }

    public boolean readFinished() {

        return this.cursor >= this.strLength;
    }
    
    public String readStr(final Set<Character> endCharSet) {

        if (endCharSet == null) {
            throw new NullPointerException("endCharSet 不能为空");
        }

        final StringBuilder sb = new StringBuilder(16);
        for (; (this.cursor < this.temp.length) && (this.temp[this.cursor] != 0);) {
            final char ch = (char)this.temp[this.cursor];
            if (!endCharSet.contains(ch)) {
                sb.append(ch);
                this.cursor++;
            } else {
                break;
            }
        }

        return sb.toString();

    }

    /**
     * 读取一个整形值 cursor指向下一个字符
     * 
     * @param endCharSet
     * @return
     */
    public int readInt(final Set<Character> endCharSet) {

        if (endCharSet == null) {
            throw new NullPointerException("endCharSet 不能为空");
        }

        int number = 0;

        while (!this.readFinished() && (this.peek() != 0) && ASCII.isSpace(this.peek())) {
            this.next();
        }

        if (!ASCII.isDigit(this.peek())) {
            throw new NumberFormatException("不是个数字");
        }

        for (; !this.readFinished() && (this.peek() != 0) && !ASCII.isSpace(this.peek());) {
            final char ch = (char)this.temp[this.cursor];
            if (endCharSet.contains(ch)) {
                break;
            }
            number = (number * 10) + (ch - '0');
            this.cursor++;
        }

        while (!this.readFinished() && (this.peek() != 0) && ASCII.isSpace(this.peek())) {
            this.next();
        }

        return number;

    }

}
