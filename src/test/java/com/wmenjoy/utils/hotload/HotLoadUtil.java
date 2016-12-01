package com.wmenjoy.utils.hotload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import com.wmenjoy.utils.lang.StringUtils;
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
public class HotLoadUtil {
    // private static Logger log = Logger.getLogger(HotLoadUtil.class);

    public static Map<String,Long> PROP_MAP = new ConcurrentHashMap<String,Long>();

    /**
     * 返回配置文件对象,如果为null，那么不需要重新加载
     *
     * @param propFileName
     * @return
     */
    public static Properties getProperty(final String propFileName) {
        Properties props = null;
        try {
            // 只有初始化配置文件或者配置文件发生更改的情况下才会重新加载配置文件
            if (HotLoadUtil.isFileChanged(propFileName)) {
                props = new Properties();
                InputStream stream = new FileInputStream(propFileName);
                if (stream != null) {
                    props.load(stream);
                    stream.close();
                }
            }
        } catch (FileNotFoundException e) {
            ExceptionLogger.error(e);
            props = null;
        } catch (IOException e) {
            ExceptionLogger.error(e);
            props = null;
        }
        return props;
    }

    public static boolean isFileChanged(final String fileName) {
        boolean isFileChanged = false;
        try {
            // 添加文件的最后更新时间的记录
            Long formerTime = PROP_MAP.get(fileName);
            // 首次加载，需要读取配置文件
            if (formerTime == null) {
                formerTime = new File(fileName).lastModified();
                PROP_MAP.put(fileName, formerTime);
                isFileChanged = true;
                return isFileChanged;
            }
            File f = new File(fileName);
            long lastModifyTime = f.lastModified();
            // 配置文件发生改变的情况下，也需要重新加载
            if (formerTime.longValue() != lastModifyTime) {
                isFileChanged = true;
                //把最新的配置文件修改时间放入MAP
                PROP_MAP.put(fileName, lastModifyTime);
            }
        } catch (Exception e) {
            ExceptionLogger.error(e);
        }
        return isFileChanged;
    }

    public static String getFilePath() {
        String cacheRoot = System.getProperty("qunar.cache");
        if (StringUtils.isEmpty(cacheRoot)) {
            return StringUtils.EMPTY;
        }
        return cacheRoot + File.separatorChar;
    }

    public static String getFileFullPathName(final String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            return StringUtils.EMPTY;
        }
        String basePath = getFilePath();
        if (StringUtils.isBlank(basePath)) {
            // ExceptionLogger.error("HotFileUtil getFileFullPathName failed. basePath is Blank." +
            // fileName);
            return StringUtils.EMPTY;
        }
        return basePath + fileName;
    }

    public static List<String> read(final String fileName) {
        List<String> contents = new ArrayList<String>(0);

        String fullName = getFileFullPathName(fileName);
        if (StringUtils.isEmpty(fullName)) {
            // ExceptionLogger.error("HotFileUtil read failed. File fullName is Empty." + fullName);
            return contents;
        }
        File file = new File(fullName);
        if (!file.exists()) {
            // ExceptionLogger.error("HotFileUtil read failed. File not exists." + fullName);
            return contents;
        }
        if (!file.isFile()) {
            // ExceptionLogger.error("HotFileUtil read failed. File is not File." + fullName);
            return contents;
        }
        // try {
        // contents = FileUtils.readLines(file);
        // } catch (IOException e) {
        // ExceptionLogger.error("HotFileUtil read failed. ReadLines exception." + fullName, e);
        // }
        return contents;
    }

    public static List<String> readFiltered (final String fileName) {
        List<String> contents = read(fileName);
        removeEmptyAndRemark(contents);
        return contents;
    }

    /**
     * 删除文件里的注释 ：以#开头
     * @param contents
     */
    public static void removeEmptyAndRemark(final List<String> contents) {
        if (contents == null) {
            return;
        }
        for (Iterator<String> iterator = contents.iterator(); iterator.hasNext();) {
            String string = iterator.next();
            if (StringUtils.isEmpty(string) || StringUtils.startsWith(string, "#")) {
                iterator.remove();
            }
        }
    }

}
