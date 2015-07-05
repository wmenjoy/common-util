package com.wmenjoy.utils.chain;

public class NoNode extends Node {

	Node cuurentNode;
	public NoNode(final Node node) {
		this.cuurentNode = node;
	}

	@Override
	protected int handle(final BaseContextParam contextParam) {
		return this.cuurentNode.handle(contextParam) == 0 ? 1: 0;
	}
}
