package my.iris.task;

import my.iris.config.AppConfig;
import my.iris.util.LogUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * scheduled task for cleaning temporary files
 */
@Component
public class CleanTask {

    /**
     * Clean temporary files not modified in the last 2 days
     */
    @Scheduled(cron = "0 0 4 * * *") // 每天凌晨 4 点
    public void cleanTemporaryFiles() {
        long now = System.currentTimeMillis();
        long threshold = now - 2L * 24 * 60 * 60 * 1000; // 当前时间减去两天
        Path root = Paths.get(AppConfig.getAppDir(), "work", "Tomcat", "localhost", "ROOT");

        LogUtils.info(getClass(), "Start cleaning temporary files: " + root);

        AtomicInteger all = new AtomicInteger(0);
        AtomicInteger deleted = new AtomicInteger(0);

        try (Stream<Path> files = Files.walk(root, 1)) { // 只遍历当前目录（不递归）
            files.filter(Files::isRegularFile)
                    .forEach(path -> {
                        try {
                            long lastModified = Files.getLastModifiedTime(path).toMillis();
                            if (lastModified < threshold) {
                                all.incrementAndGet();
                                Files.delete(path);
                                deleted.incrementAndGet();
                            }
                        } catch (IOException e) {
                            LogUtils.error(getClass(), "Failed to delete file: " + path, e);
                        }
                    });
        } catch (IOException e) {
            LogUtils.error(getClass(), "Failed to walk through files", e);
        }

        LogUtils.info(getClass(), String.format("Temporary files cleaned, all: %d, deleted: %d", all.get(), deleted.get()));
    }
}
