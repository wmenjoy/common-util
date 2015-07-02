package com.wmenjoy.utils.chain;

public interface ChainHandler {

	public int handle(BaseContextParam contextParam);

	public boolean needRollback();

	public boolean onException(BaseContextParam contextParam, Exception e);
}
