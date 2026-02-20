package pro.sky.manager.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Сервис для обработки обновлений от Telegram
 */

@Service
@RequiredArgsConstructor
public class TelegramUpdateHandler {

    private final TelegramBot telegramBot;

    /**
     * Обработка входящего обновления (Update) от Telegram
     */
    public void handleUpdate(Update update) {
        if (update.message() == null) {
            return; // Игнорируем обновления без сообщений
        }
        Message message = update.message();
        Long chatId = message.chat().id();

        String text = message.text();
        if (text == null) {
            return;
        }

        if (text.equals("/start")) {
            sendWelcomeMessage(chatId);
        } else if (text.startsWith("/recommend")) {
            // Пока заглушка — позже тут нужно доделать
            telegramBot.execute(new SendMessage(chatId, "Эта команда будет реализована позже."));
        } else {
            telegramBot.execute(new SendMessage(chatId, "Команда не распознана. Введите /start для начала."));
        }
    }

    /**
     * Отправка приветственного сообщения и справки
     */
    private void sendWelcomeMessage(Long chatId) {
        String welcomeText = "Привет! Я бот с рекомендациями.\n" +
                "Единственная команда:\n" +
                "/recommend username\n" +
                "— выведет рекомендации для пользователя по имени.";
        telegramBot.execute(new SendMessage(chatId, welcomeText));
    }
}
