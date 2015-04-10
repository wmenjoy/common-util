package com.wmenjoy.utils.archtechture.nio.reactor;

public interface Reactor {

    /**
     * 注册handler
     *
     * @param event
     * @param handler
     * @return
     */
    public boolean register(Event event, Handler handler);

    /**
     * 删除handler
     *
     * @param event
     * @return
     */
    public boolean remove(Event event);

    /**
     * 具体handler的处理流程
     */
    public void handle();
}
