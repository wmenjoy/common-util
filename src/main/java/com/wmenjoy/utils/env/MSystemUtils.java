package com.wmenjoy.utils.env;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.wmenjoy.utils.log.ExceptionLogger;

/**
 * @description: 与系统相关的操作方法
 * @author jinliang.liu
 *
 */
public abstract class MSystemUtils {

	/**
	 * 同一台机器的主机名，取一次就可以保存下来
	 */
	private static String HOSTNAME = null;
	static {
		HOSTNAME =  getHostNameFromSystem();
	}

	/**
	 * 获取系统主机名称
	 * @return
	 */
	private static String getHostNameFromSystem(){
		InetAddress a;
		try {
			a = InetAddress.getLocalHost();
			return a.getHostName();
		} catch (final UnknownHostException e) {
			ExceptionLogger.error(e);
		}
		return "";
	}

	/**
	 * 获取主机名称
	 * @return
	 */
	public static String getHostName() {

		return HOSTNAME;
	}
}
