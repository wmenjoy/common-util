package com.wmenjoy.utils.chain;

public abstract class Node<RequestT, ResponseT> {
   
    Node<?, ?> next;

    public Node<?, ?> getNext() {
        return this.next;
    }

    public void setNext(final Node<RequestT, ResponseT> next) {
        this.next = next;
    }

    protected abstract int handle(RequestT reqParam, ResponseT result);
}
