package com.wmenjoy.utils.lang;

public class IntAppender {

    private transient int[] buffer;

    public IntAppender(){
        this.buffer = new int[32];
    }


    public void append(final int ch, final int len) {
        if (len >= this.buffer.length) {
            final int[] tmp = new int[len+len];
            System.arraycopy(this.buffer, 0, tmp, 0, len);
            this.buffer = tmp;
        }
        this.buffer[len] = ch;
    }


    public int[] getBuffer() {
        return this.buffer;
    }



}
