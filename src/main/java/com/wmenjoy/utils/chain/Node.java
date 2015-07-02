package com.wmenjoy.utils.chain;

public abstract class Node {

	Node next;

	public Node getNext() {
		return this.next;
	}

	public void setNext(final Node next) {
		this.next = next;
	}

	protected abstract int handle(BaseContextParam contextParam);

}
