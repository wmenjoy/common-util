package com.wmenjoy.utils.chain;

import com.wmenjoy.utils.lang.Stack;

/**
 * 基本ContextParam
 * 
 * @author jinliang.liu
 *
 */
public abstract class BaseContextParam {
	Stack<ChainHandler> path;
	
	public BaseContextParam() {
		path = new Stack<ChainHandler>();
	}

	/**
	 * 异常处理handler 入栈操作
	 * @param handler
	 */
	protected void add(final ChainHandler handler){
		if(handler == null || !handler.needRollback()){
			return;
		}
		
		this.path.push(handler);
	}

	public Stack<ChainHandler> getRockbackPath() {
		return path;
	}	
}
