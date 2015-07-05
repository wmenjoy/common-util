package com.wmenjoy.utils.chain;

import java.util.ArrayList;
import java.util.List;

/**
 * 处理同时处理多个Node
 *
 * @author jinliang.liu
 *
 */
public class OrNode extends Node {

	
	List<Node> nodeList;

	
	public OrNode(final Node node, final Node next) {
		this.nodeList = new ArrayList<Node>();
		this.nodeList.add(node);
		this.nodeList.add(next);
	}

	/***
	 * 使用线程池，处理
	 */
	@Override
	protected int handle(final BaseContextParam contextParam) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void appendNode(final Node next) {
		this.nodeList.add(next);
	}

}
