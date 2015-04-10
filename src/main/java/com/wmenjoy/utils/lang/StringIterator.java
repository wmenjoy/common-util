package com.wmenjoy.utils.lang;

import java.util.Iterator;

/***
 * 借助于split实现的一个StringIterator
 * @author jinliang.liu
 *
 */
public class StringIterator implements Iterator<String>{

    String lines[];
    int i;
    int currentIndex;
    boolean skipWhiteLine = true;
    boolean skipComment = true;

    /**
     * 采用默认的字符串解析方式
     *
     * @param msg
     */
    public StringIterator(final String msg) {
        this.lines = StringUtils.split(msg, "\n");
        this.i = -1;
        this.currentIndex = this.i;
    }

    public StringIterator(final String msg, final String sep) {
        this.lines = StringUtils.split(msg, sep);
        this.i = -1;
        this.currentIndex = this.i;
    }

    /**
     * 能够指定字符串的解析方式
     *
     * @param msg
     * @param skipWhiteLine
     * @param skipComment
     */
    public StringIterator(final String msg, final boolean skipWhiteLine,
            final boolean skipComment) {
        this.lines = StringUtils.split(msg, "\n");
        this.i = -1;
        this.currentIndex = this.i;
        this.skipComment = skipComment;
        this.skipWhiteLine = skipWhiteLine;
    }

    @Override
    public boolean hasNext() {
        if ((this.lines == null) || (this.lines.length == 0)) {
            return false;
        }

        this.currentIndex = this.i + 1;

        while ((this.currentIndex < this.lines.length)
                && this.shouldSkip(this.lines[this.currentIndex])) {
            this.currentIndex++;
        }

        return this.lines.length > this.currentIndex;
    }

    private boolean shouldSkip(final String line) {
        if (this.skipWhiteLine && StringUtils.isBlank(line)) {
            return true;
        }

        if (this.skipComment && StringUtils.startsWith(line, "#")) {
            return true;
        }

        return false;
    }

    @Override
    public String next() {
        this.i = this.currentIndex;
        if ((this.i >= 0) && (this.i < this.lines.length)) {
            return this.lines[this.i];
        }
        return null;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException(
                "StringIterator remove operation is unsupported");
    }


}
