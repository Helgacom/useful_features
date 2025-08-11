package com.helga.lib.scheduler;

import java.nio.file.Path;

public interface SchedulerService {

    void doWorkOnASchedule(Path file);
}

