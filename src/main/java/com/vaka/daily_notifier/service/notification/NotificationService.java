package com.vaka.daily_notifier.service.notification;

import com.vaka.daily_notifier.domain.Notification;
import com.vaka.daily_notifier.domain.Task;
import com.vaka.daily_notifier.service.client.ApiClientService;
import com.vaka.daily_notifier.service.notification.format.FormatNotificationService;
import com.vaka.daily_notifier.service.notification.resolver.NotificationResolver;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {
    private final ApiClientService api;
    private final KafkaNotificationService kafka;
    private final NotificationResolver notificationResolver;
    private final FormatNotificationService formatNotificationService;

    public NotificationService(ApiClientService api, KafkaNotificationService kafka,
                               NotificationResolver notificationResolver,
                               FormatNotificationService formatNotificationService) {
        this.api = api;
        this.kafka = kafka;
        this.notificationResolver = notificationResolver;
        this.formatNotificationService = formatNotificationService;
    }

    public void notifyUsers() {
        List<Task> tasks = api.getTasks();
        List<Notification> notifications = new ArrayList<>();

        for (Task task : tasks) {
            Notification notification = resolveNotification(task);
            if (notification != null) {
                notifications.add(notification);
            }
        }

        kafka.sendNotifications(notifications);
    }


    private Notification resolveNotification(Task task) {
        if (notificationResolver.shouldNotify(task)) {
            return resolveTelegramNotification(task);
        }

        return null;
    }

    private Notification resolveTelegramNotification(Task task) {
        int scheduleId = task.getScheduleId();
        Optional<Long> optTgId = api.getTelegramIdByScheduleId(scheduleId);

        if (optTgId.isEmpty()) {
            return null;
        }

        long telegramId = optTgId.get();

        return Notification.builder()
                .chanel("telegram")
                .chanelUserId(Long.toString(telegramId))
                .message(formatNotificationService.formatTaskForNotification(task))
                .timestamp(LocalDateTime.now())
                .build();
    }
}
