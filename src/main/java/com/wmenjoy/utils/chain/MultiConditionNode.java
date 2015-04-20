package com.wmenjoy.utils.chain;

import java.util.Map;

public class MultiConditionNode<RequestT, ResponseT> extends Node<RequestT, ResponseT> {

    public MultiConditionNode(final Map<Integer, Node<?, ?>> multiConditionNodeMap) {
        // TODO Auto-generated constructor stub
    }

    @Override
    protected int handle(final RequestT reqParam, final ResponseT result) {
        // TODO Auto-generated method stub
        return 0;
    }

}
