package com.wmenjoy.utils.chain;

import java.util.Map;

public class MultiConditionNode extends Node {
	private Node conditionNode;
	private Map<Integer, Node> multiConditionNodeMap;
	public MultiConditionNode(final Node conditionNode, final Map<Integer, Node> multiConditionNodeMap
			, Node defaultNode) {
		if(multiConditionNodeMap == null || multiConditionNodeMap.size() == 0){
			throw new IllegalArgumentException("multiConditionNodeMap 不能为空");
		}
		
		this.multiConditionNodeMap = multiConditionNodeMap;
	}

	@Override
	protected int handle(final BaseContextParam contextParam) {
		if(multiConditionNodeMap == null){
			
		}
		
		int result = this.conditionNode.handle(contextParam);
		Node node = this.multiConditionNodeMap.get(result);
		
		if(node == null){
			throw new IllegalArgumentException("unkown result");
		}
		
		return node.handle(contextParam);
	}

}
