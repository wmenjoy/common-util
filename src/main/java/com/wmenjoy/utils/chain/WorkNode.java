package com.wmenjoy.utils.chain;

public abstract class WorkNode extends Node{
	
	private String handlerName;
	
	private ChainHandler handler;

	public WorkNode(final String handlerName){
		this.handlerName = handlerName;
	}


	public String getHandlerName() {
		return handlerName;
	}


	public ChainHandler getHandler() {
		return handler;
	}

	/**
	 * 初始化
	 * @param handlerFactory
	 */
	protected void init(final ChainHandlerFactory handlerFactory){
		if(handlerFactory == null){
			throw new NullPointerException();
		}
		
		handler = handlerFactory.get(handlerName);
	}


	@Override
	protected int handle(BaseContextParam contextParam) {
		int result = this.process(contextParam);
		
		if(result == 0){
			contextParam.add(handler);
		}
		return result;
	}


	protected abstract int process(BaseContextParam contextParam);
	
	
	

}
