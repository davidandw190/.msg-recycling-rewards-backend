package io.rewardsapp.service.implementation;

import io.rewardsapp.dto.UserDTO;
import io.rewardsapp.service.EmailService;
import io.rewardsapp.service.ScheduledTasksService;
import io.rewardsapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@EnableScheduling
@RequiredArgsConstructor
public class ScheduledTasksServiceImpl implements ScheduledTasksService {

    private final UserService userService;

    private final EmailService emailService;


    @Override
    @Scheduled(cron = "${scheduled-tasks.inactive-users-cron}")  // to run every Monday at midnight
    public void sendEmailToInactiveUsers() {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        List<UserDTO> inactiveUsers = userService.findInactiveUsers(oneWeekAgo);

        inactiveUsers.forEach(this::sendInactiveUserEmail);
    }

    private void sendInactiveUserEmail(UserDTO user) {
        String subject = "Inactive Account Notification";
        String message = "Dear " + user.firstName() + ",\n\n"
                + "We noticed that you haven't logged into your RecyclingRewards account for a week. "
                + "Please log in to stay connected with our community.\n\n"
                + "Thank you,\nRecyclingRewards Team";

    }
}
