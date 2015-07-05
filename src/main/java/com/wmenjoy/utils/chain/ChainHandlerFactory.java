package com.wmenjoy.utils.chain;

public interface ChainHandlerFactory {

	/**
	 * 构造handler
	 * @param handlerName
	 * @return
	 */
	ChainHandler get(String handlerName);

}
