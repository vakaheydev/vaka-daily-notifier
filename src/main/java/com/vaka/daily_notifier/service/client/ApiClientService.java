package com.vaka.daily_notifier.service.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.vaka.daily_notifier.domain.Task;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ApiClientService {
    private RestClient restClient;
    private ObjectMapper objectMapper;

    public ApiClientService(RestClient restClient, ObjectMapper objectMapper) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
    }

    public Optional<Long> getTelegramIdByScheduleId(int scheduleId) {
        String response = "";
        try {
            response = restClient.get()
                    .uri("/schedule/" + scheduleId)
                    .retrieve()
                    .body(String.class);
        } catch (Exception exception) {
            log.error("Exception while getting schedule: {}", exception.getClass().getSimpleName());
        }

        try {
            JsonNode node = objectMapper.readTree(response);
            int userId = node.get("userId").asInt();
            return getTelegramIdByUserId(userId);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<Long> getTelegramIdByUserId(int userId) {
        String response = "";
        try {
            response = restClient.get()
                    .uri("/user/" + userId)
                    .retrieve()
                    .body(String.class);
        } catch (Exception exception) {
            log.error("Exception while getting user: {}", exception.getClass().getSimpleName());
            return Optional.empty();
        }

        try {
            JsonNode node = objectMapper.readTree(response);
            return Optional.of(node.get("telegramId").asLong());
        } catch (JsonProcessingException e) {
            return Optional.empty();
        }

    }


    public List<Task> getTasks() {
        String response = "";
        try {
            response = restClient.get()
                    .uri("/task")
                    .retrieve()
                    .body(String.class);
        } catch (Exception exception) {
            log.error("Exception while getting tasks: {}", exception.getClass().getSimpleName());
        }

        try {
            return objectMapper.readValue(response,
                    TypeFactory.defaultInstance().constructCollectionType(List.class, Task.class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
