package com.wmenjoy.utils.chain;

public abstract class Node<RequestT, ResponseT> {
    
    Node<RequestT, ResponseT> next;
    
    
    
    
    public Node<RequestT, ResponseT> getNext() {
        return next;
    }




    public void setNext(Node<RequestT, ResponseT> next) {
        this.next = next;
    }




    protected abstract int handle(RequestT reqParam, ResponseT result);
}
