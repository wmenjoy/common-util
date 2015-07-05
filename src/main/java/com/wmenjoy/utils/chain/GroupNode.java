package com.wmenjoy.utils.chain;

public class GroupNode extends Node {

	Node subNode;
	public GroupNode(final Node nodeList) {
		this.subNode = nodeList;
	}

	@Override
	protected int handle(final BaseContextParam contextParam) {
		return this.subNode.handle(contextParam);

	}

}
