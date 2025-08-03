package com.vaka.daily_notifier.service.notification.resolver;

import com.vaka.daily_notifier.domain.Task;

public interface NotificationResolver {
    boolean shouldNotify(Task task);
}
