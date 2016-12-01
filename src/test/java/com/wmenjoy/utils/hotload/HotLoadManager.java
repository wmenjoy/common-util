package com.wmenjoy.utils.hotload;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.wmenjoy.utils.log.ExceptionLogger;

/**
 * 热部署线程管理
 * @author Thinkpad
 *
 */
public class HotLoadManager{
    private Map<String, IFileConfig> configMap = new HashMap<String, IFileConfig>();
    private Map<String, Long> modifyTimeMap = new HashMap<String, Long>();// 时间戳


    // private Logger logger = Logger.getLogger(HotLoadManager.class);

    private boolean result = true;

    private HotLoadManager() {
        startMainMonitor();
    }

    private static HotLoadManager instance= new HotLoadManager();
    public static HotLoadManager getInstance(){
        return instance;
    }

    /**
     * 启动监听线程
     */
    public void startMainMonitor() {
        this.result = true;
        new Thread(new MainMonitor()).start();
    }

    /**
     * 注册文件配置方法
     *
     * @param fileName
     * @param fileConfig
     */
    public void register(final String fileName, final IFileConfig fileConfig) {
        if (fileName == null || fileConfig == null) {
            throw new NullPointerException();
        }

        File file = new File(fileName);
        this.configMap.put(fileName, fileConfig);

        long lastModifyTime  = 0;
        if(!file.exists()){
            // ExceptionLogger.error("file "+fileName+" not exists");
        } else {
            lastModifyTime = file.lastModified();
        }

        this.modifyTimeMap.put(fileName, lastModifyTime);
    }

    /**
     * 暂停主监控线程
     */
    public void stopMainMonitor(){
        this.result = false;
    }

    /**
     * 删除文件配置
     *
     * @param fileName
     */
    public void remove(final String fileName) {
        if (fileName == null) {
            throw new NullPointerException();
        }
        this.configMap.remove(fileName);
        this.modifyTimeMap.remove(fileName);
    }

    /**
     *  子工作线程
     * @author Thinkpad
     *
     */
    class SubWorker extends Thread{

        IFileConfig fileConfig = null;
        public SubWorker(final IFileConfig fileConfig){
            this.fileConfig = fileConfig;
        }
        @Override
        public void run() {
            this.fileConfig.config();
        }

    }
    /**
     * 主工作线程
     *
     * @author Thinkpad
     *
     */
    class MainMonitor implements Runnable {

        @Override
        public void run() {
            while (HotLoadManager.this.result) {
                Set<String> fileNameSet = HotLoadManager.this.configMap.keySet();

                if (fileNameSet != null) {
                    for (String fileName : fileNameSet) {
                        if (isFileChange(fileName)) {
                            // HotLoadManager.this.logger.info("文件" + fileName + "发生变更");
                            IFileConfig fileConfig = HotLoadManager.this.configMap.get(fileName);
                            (new SubWorker(fileConfig)).start();
                        }
                    }
                }

                try {
                    Thread.sleep(50000);
                } catch (Exception e) {
                    ExceptionLogger.error(e);
                }
            }

        }
    }

    /**
     * 简单的文件是否修改的方法，单线程，不需要考虑并发
     *
     * @param fileName
     * @return
     */
    private boolean isFileChange(final String fileName) {
        File file = new File(fileName);
        long lastModifyTime = file.lastModified();
        Long lastTime = this.modifyTimeMap.get(fileName);
        if(lastTime == null){
            return false;
        }
        if (lastModifyTime == lastTime) {
            return false;
        }
        this.modifyTimeMap.put(fileName, lastModifyTime);
        return true;
    }
}
