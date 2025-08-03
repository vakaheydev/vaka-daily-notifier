package com.vaka.daily_notifier.service.notification.resolver;

import com.vaka.daily_notifier.domain.Task;
import com.vaka.daily_notifier.domain.TaskNotification;
import org.springframework.stereotype.Component;

@Component
public class TaskNotificationResolver implements NotificationResolver {
    public boolean shouldNotify(Task task) {
        NotificationResolver resolver;

        if (isTaskType(task, "singular")) {
            resolver = new SingularTaskNotificationResolver();
        } else if (isTaskType(task, "repetitive")) {
            resolver = new RepetitiveTaskNotificationResolver();
        } else if (isTaskType(task, "regular")) {
            resolver = new RegularTaskNotificationResolver();
        } else {
            throw new IllegalArgumentException("Unknown task type: " + task.getTaskType());
        }

        return resolver.shouldNotify(task);
    }

    public boolean isTaskType(Task task, String taskTypeName) {
        return task.getTaskType().getName().equals(taskTypeName);
    }
}
