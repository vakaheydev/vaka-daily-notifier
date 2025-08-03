package com.vaka.daily_notifier.service.notification;

import com.vaka.daily_notifier.domain.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class KafkaNotificationService {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaNotificationService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendNotifications(List<Notification> notifications) {
        for (Notification notification : notifications) {
            kafkaTemplate.send("telegram", notification.toJson());
        }
    }
}