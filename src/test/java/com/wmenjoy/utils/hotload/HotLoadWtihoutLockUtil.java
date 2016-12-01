package com.wmenjoy.utils.hotload;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

import com.wmenjoy.utils.log.ExceptionLogger;


/**
 * hotLoadUtil
 * <p>Title: 文件热发工具类</p>
 * <p>Description: 检测文件是否更新等</p>
 * <p>Date: 2011-6-22</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: qunar.com</p>
 * @author shi.yan
 * @version 1.0
 */
public class HotLoadWtihoutLockUtil {
    public static ConcurrentHashMap<String, Long> PROP_MAP = new ConcurrentHashMap<String, Long>();
    // private static Logger log = Logger.getLogger(HotLoadWtihoutLockUtil.class);

    /**
     * @function: 判断文件是否改变，如果有多个线程同时访问，那么只有一个线程看到的文件是改变的：
     *
     * 1、利用类似于事务的原理，前提，value值，每次变化不一样
     * 具体操作如下：
     * 	1、如果当前改变量没有设置，设置完成后，返回看到改变：putIfAbsent可以保障只有一个线程设置成功
     * 	2、如果当前线程看到的没有改变，直接返回true
     * 	3、如果我看到改变了，此时可能也有别的线程那么在看到改变，那么，谁先赋值成功，就谁看到改变。
     * @param fileName
     * @return
     */

    public static boolean isFileChanged(final String fileName) {
        boolean isFileChanged = false;
        try {
            //获取文件的最新修改时间
            File f = new File(fileName);
            long lastModifyTime = f.lastModified();

            //作用:如果没有这个键值，那么设置为最新的文件修改时间，只有一个线程会返回这个
            Long lastTime = PROP_MAP.putIfAbsent(fileName, lastModifyTime);

            if(lastTime == null){
                return true;
            }
            //如果时间相等，说明这个线程没看到文件的变化，如果文件时间不想等，说明看到了这次的变化,然后执行replace函数
            //replace函数可以保障，如果我看到值和原来的值一样就设置，不一样，就不设置。
            if(lastTime != lastModifyTime){
                return PROP_MAP.replace(fileName, lastTime, lastModifyTime);
            } else {
                return false;
            }
            //设置初始值
        } catch (Exception e) {
            ExceptionLogger.error(e);
        }
        return isFileChanged;
    }

}
