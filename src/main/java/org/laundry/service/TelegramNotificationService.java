package org.laundry.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
@Slf4j
public class TelegramNotificationService {

    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Value("${telegram.bot.token:}")
    private String botToken;

    public TelegramNotificationService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.httpClient = new OkHttpClient();
    }

    @PostConstruct
    void logConfiguration() {
        if (botToken == null || botToken.isBlank()) {
            log.warn("Telegram: TELEGRAM_BOT_TOKEN не задан — уведомления отключены");
        } else {
            log.info("Telegram: бот настроен");
        }
    }

    public void sendToOwner(String message, String ownerTelegramChatId) {
        if (ownerTelegramChatId == null || ownerTelegramChatId.isBlank()) {
            log.debug("Telegram: у владельца не указан chat_id, уведомление не отправлено");
            return;
        }
        sendNotification(message, ownerTelegramChatId.trim());
    }

    public void sendNotification(String message, String chatId) {
        if (chatId == null || chatId.isBlank()) {
            return;
        }
        if (botToken == null || botToken.isBlank()) {
            log.warn("Telegram: токен бота не задан");
            return;
        }

        try {
            Object chatIdValue = parseChatId(chatId);
            Map<String, Object> payload = Map.of(
                    "chat_id", chatIdValue != null ? chatIdValue : chatId,
                    "text", message
            );
            String jsonPayload = objectMapper.writeValueAsString(payload);

            String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";
            RequestBody body = RequestBody.create(jsonPayload, MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder().url(url).post(body).build();

            try (Response response = httpClient.newCall(request).execute()) {
                String responseBody = response.body() != null ? response.body().string() : "";
                if (!response.isSuccessful()) {
                    log.error("Telegram API error {} for chat_id={}: {}", response.code(), chatId, responseBody);
                } else {
                    log.info("Telegram: уведомление отправлено владельцу chat_id={}", chatId);
                }
            }
        } catch (IOException e) {
            log.error("Telegram: ошибка сети для chat_id={}", chatId, e);
        }
    }

    private static Long parseChatId(String chatId) {
        try {
            return Long.parseLong(chatId.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
