package pro.sky.manager.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Сервис для обработки обновлений от Telegram
 */
@Service
@RequiredArgsConstructor
public class TelegramUpdateHandler {

    private final TelegramBot telegramBot;
    private final UserRecommendationService userRecommendationService;

    /**
     * Обработка входящего обновления (Update) от Telegram
     */
    public void handleUpdate(Update update) {
        if (update.message() == null) {
            return;
        }
        Message message = update.message();
        Long chatId = message.chat().id();

        String text = message.text();
        if (text == null) {
            return;
        }

        if (text.equals("/start")) {
            sendWelcomeMessage(chatId);
            return;
        }

        try {
            java.util.UUID uuid = java.util.UUID.fromString(text.trim());
            String fullName = getFullNameByUUID(uuid.toString());
            if (fullName == null || fullName.isEmpty()) {
                telegramBot.execute(new SendMessage(chatId, "UUID не найден или некорректен. Попробуйте еще раз, введя ваш UUID."));
            } else {
                String recommendation = getRecommendationByUserId(uuid);
                String messageText = String.format(
                        "Здравствуйте, %s! Вы успешно зарегистрированы. Ознакомьтесь с рекомендациями для вас:\n%s",
                        fullName,
                        recommendation
                );
                telegramBot.execute(new SendMessage(chatId, messageText));
            }
        } catch (IllegalArgumentException e) {
            telegramBot.execute(new SendMessage(chatId, "Пожалуйста, введите корректный UUID в формате: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"));
        }
    }

    /**
     * Отправка приветственного сообщения и инструкции
     */
    private void sendWelcomeMessage(Long chatId) {
        String welcomeText = "Здравствуйте! Вас приветствует банк \"Стар\". Пожалуйста, введите ваш UUID.";
        telegramBot.execute(new SendMessage(chatId, welcomeText));
    }

    /**
     * Метод для получения имени по UUID
     */
    private String getFullNameByUUID(String uuid) {
        return userRecommendationService.getFullNameById(uuid);
    }

    /**
     * Метод для получения рекомендаций по UUID
     */
    private String getRecommendationByUserId(UUID userId) {
        return ("не советую брать кредит, если не имеешь стабильный заработок");
    }
}