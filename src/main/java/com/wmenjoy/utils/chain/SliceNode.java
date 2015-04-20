package com.wmenjoy.utils.chain;

public class SliceNode extends Node{

        private String name;

    public SliceNode(final String sliceName) {
        this.name = sliceName;
    }

    @Override
    protected int handle(final Object reqParam, final Object result) {
        // TODO Auto-generated method stub
        return 0;
    }

    public String getName() {
        return this.name;
    }


}
