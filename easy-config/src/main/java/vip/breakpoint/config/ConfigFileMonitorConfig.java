package vip.breakpoint.config;

import org.apache.commons.io.monitor.FileAlterationMonitor;
import vip.breakpoint.enums.FileTypeEnum;
import vip.breakpoint.filter.MonitorConfigFilter;
import vip.breakpoint.listener.ConfigFileListener;
import vip.breakpoint.monitor.ConfigFileMonitor;

import java.io.File;
import java.util.*;

/**
 * 文件监听配置
 *
 * @author : breakpoint
 * create on 2022/10/19
 * 欢迎关注公众号 《代码废柴》
 */
public class ConfigFileMonitorConfig {

    /**
     * 监听器
     */
    private volatile ConfigFileMonitor monitor;
    /**
     * 文件事件通知监听
     */
    private final ConfigFileListener listener = new ConfigFileListener();
    /**
     * 监听文件变化时间间隔
     */
    private final long interval;

    /**
     * default is 0.5s
     */
    public static final long DEFAULT_INTERVAL = 500;

    private static final Set<String> undoFiles = new HashSet<>();

    static {
        undoFiles.add("pom.properties");
    }

    public ConfigFileMonitorConfig(long interval) {
        this.interval = interval;
    }

    /**
     * DCL 获取 配置文件坚挺者
     *
     * @return monitor
     */
    private ConfigFileMonitor getMonitor() {
        if (null == monitor) {
            synchronized (ConfigFileMonitorConfig.class) {
                if (null == monitor) {
                    monitor = new ConfigFileMonitor(new FileAlterationMonitor(interval));
                }
            }
        }
        return monitor;
    }

    /**
     * 增加监听文件
     *
     * @param filePathArr     文件路径数组
     * @param fileTypeEnumSet 文件类型
     */
    public void addMonitorFile(List<String> filePathArr, Set<FileTypeEnum> fileTypeEnumSet) {
        if (null == filePathArr) return;
        MonitorConfigFilter monitorConfigFilter = new MonitorConfigFilter(fileTypeEnumSet);

        for (String path : filePathArr) {
            File filePath = new File(path);
            List<File> monitorCandidateFiles = getAllFileFromDirector(filePath, fileTypeEnumSet);
            Map<String, File> parentPath2FileMap = new HashMap<>();
            for (File monitorCandidateFile : monitorCandidateFiles) {
                parentPath2FileMap.put(monitorCandidateFile.getParentFile().getAbsolutePath(),
                        monitorCandidateFile.getParentFile());
            }
            for (File monitorPath : parentPath2FileMap.values()) {
                getMonitor().monitor(monitorPath, monitorConfigFilter, listener);
            }
        }
        try {
            getMonitor().start();
        } catch (Exception e) {
            System.out.println("文件监听失败");
        }
        // 停止监听
        Runtime.getRuntime().addShutdownHook(new StopMonitorHook(getMonitor()));
    }

    private static final class StopMonitorHook extends Thread {

        private final ConfigFileMonitor monitor;

        public StopMonitorHook(ConfigFileMonitor monitor) {
            this.monitor = monitor;
        }

        @Override
        public void run() {
            try {
                monitor.stop();
            } catch (Exception e) {
                System.out.println("停止监听失败");
            }
        }
    }

    /**
     * 获取所有可以操作的文件
     *
     * @param file            文件
     * @param fileTypeEnumSet 文件类型
     * @return 符合文件的集合
     */
    private List<File> getAllFileFromDirector(File file, Set<FileTypeEnum> fileTypeEnumSet) {
        if (!file.exists()) {
            return new ArrayList<>();
        }
        List<File> ret = new ArrayList<>();
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (null != files) {
                for (File subFile : files) {
                    ret.addAll(getAllFileFromDirector(subFile, fileTypeEnumSet));
                }
            }
        } else if (file.isFile()) {
            if (!undoFiles.contains(file.getName()) && isCandidateFileType(file, fileTypeEnumSet)) {
                ret.add(file);
            }
        }
        return ret;
    }

    private boolean isCandidateFileType(File file, Set<FileTypeEnum> fileTypeEnumSet) {
        if (file.isFile()) {
            boolean ret = false;
            for (FileTypeEnum typeEnum : fileTypeEnumSet) {
                ret = file.getName().endsWith(typeEnum.getFileType());
                if (ret) {
                    return true;
                }
            }
        }
        return false;
    }
}
