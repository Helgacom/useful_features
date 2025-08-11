package com.helga.lib.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class SchedulerServiceImpl implements SchedulerService {

    private final ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(3);

    @Override
    public void doWorkOnASchedule(Path file) {
        if (file != null && file.toFile().exists()) {
            scheduler.schedule(() -> {
                        if (file.toFile().delete()) {
                            log.info("файл с наименованием [{}] был удален по истечении 30 минут", file.getFileName());
                        }
                    }, 30, TimeUnit.MINUTES
            );
        }
    }
}

