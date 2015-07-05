package com.wmenjoy.utils.chain;

public class ConditionNode extends Node {

	private Node conditionNode;
	private Node firstNode;
	private Node secondNode;
	public ConditionNode(Node conditionNode, Node firstNode, Node secondNode) {
		this.firstNode = firstNode;
		this.secondNode = secondNode;
		this.conditionNode = conditionNode;
	}

	@Override
	protected int handle(final BaseContextParam contextParam) {
		
		int result = this.conditionNode.handle(contextParam);
		
		if(result == 0){
			return this.firstNode == null ? 0 : this.firstNode.handle(contextParam);
		} else {
			return this.secondNode == null ? 0 : this.secondNode.handle(contextParam);
		}
	}

	
}
