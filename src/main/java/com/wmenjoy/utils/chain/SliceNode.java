package com.wmenjoy.utils.chain;

public class SliceNode extends WorkNode {

	protected SliceNode(String handlerName) {
		super(handlerName);
	}

	@Override
	protected int process(final BaseContextParam contextParam) {
		return 0;
	}

}
