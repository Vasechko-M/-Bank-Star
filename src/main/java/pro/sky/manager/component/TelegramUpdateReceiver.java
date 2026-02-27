package pro.sky.manager.component;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.GetUpdates;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pro.sky.manager.service.TelegramUpdateHandler;

import java.util.List;

/**
 * Компонент для получения обновлений
 */

@Component
@RequiredArgsConstructor
public class TelegramUpdateReceiver {

    private static final Logger log = LoggerFactory.getLogger(TelegramUpdateReceiver.class);

    private final TelegramBot telegramBot;
    private final TelegramUpdateHandler updateHandler;

    private volatile int lastUpdateId = 0;

    @PostConstruct
    public void startPolling() {
        Thread thread = new Thread(this::pollingLoop);
        thread.setDaemon(true);
        thread.start();
    }

    private void pollingLoop() {
        while (true) {
            GetUpdates request = new GetUpdates().limit(100).offset(lastUpdateId + 1).timeout(10);
            List<Update> updates = telegramBot.execute(request).updates();
            for (Update update : updates) {
                try {
                    updateHandler.handleUpdate(update);
                } catch (Exception e) {
                    log.error("Ошибка при обработке обновления: ", e);
                }
                lastUpdateId = update.updateId();
            }
        }
    }
}
