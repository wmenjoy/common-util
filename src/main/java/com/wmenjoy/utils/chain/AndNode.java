package com.wmenjoy.utils.chain;

import java.util.ArrayList;
import java.util.List;

public class AndNode extends Node {

	List<Node> nodeList;

	public AndNode(final Node node, final Node next) {
		this.nodeList = new ArrayList<Node>();
		this.nodeList.add(node);
		this.nodeList.add(next);
	}

	public void appendNode(final Node next) {
		this.nodeList.add(next);

	}

	/**
	 * 可以使用缓冲池同步执行
	 */
	@Override
	protected int handle(final BaseContextParam contextParam) {
		
		
		
		return 0;
	}

}
