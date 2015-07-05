package com.wmenjoy.utils.chain;

public class ForNode extends Node{

	private Node currentNode;
	private int count;
	
	public ForNode(Node node, int count) {
		this.currentNode = node;
		this.count = count;
	}

	@Override
	protected int handle(BaseContextParam contextParam) {
		
		for(int i = 0; i < count; i ++){
			this.currentNode.handle(contextParam);
		}
		return 0;
	}

}
