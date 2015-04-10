package com.wmenjoy.utils.config.parser.rule;

public abstract class BaseFieldRule<T> implements FieldRule<T> {

    protected boolean nullable = true;

    public BaseFieldRule(boolean nullable) {
        super();
        this.nullable = nullable;
    }

    public BaseFieldRule() {
        super();
    }

    @Override
    public boolean nullable() {
        return nullable;
    }

}
