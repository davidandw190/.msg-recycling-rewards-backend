package io.rewardsapp.service;

import org.springframework.scheduling.annotation.Scheduled;

public interface ScheduledTasksService {
    void sendEmailToInactiveUsers();
}
