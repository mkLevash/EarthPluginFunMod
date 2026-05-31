package earthrp.bot;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import earthrp.Earth;
import org.bukkit.Bukkit;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class GeminiManager {


    private final Earth plugin;
    private final HttpClient httpClient;
    private final String apiKey = "AIzaSyAJATddXVqzoG-CDo5tLaMr_WFzsLybRAQ";

    public GeminiManager(Earth plugin) {
        this.plugin = plugin;
        // Создаем один общий клиент для плагина (лучше не создавать его на каждый чих)
        this.httpClient = HttpClient.newHttpClient();
    }

    public void askRulerAI(String prompt) {
        // Эндпоинт для модели Gemini 1.5 Flash (она быстрая и дешевая)
        String url = "https://generativelanguage.googleapis.com/v1/models/gemini-3.5-flash:generateContent?key=" + apiKey;

        // Формируем простую структуру JSON, которую требует Google API
        // В идеале для сборки JSON использовать библиотеки вроде Gson (она вшита в Spigot/Paper)
        String jsonPayload = "{"
                + "\"contents\": [{"
                + "  \"parts\":[{\"text\": \"" + escapeJson(prompt) + "\"}]"
                + "}]"
                + "}";

        // Строим сам HTTP запрос
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload, StandardCharsets.UTF_8))
                .build();

        // Отправляем АСИНХРОННО (сервер не будет лагать)
        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(responseBody -> {
                    // Перенаправляем выполнение в основной поток сервера
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        try {
                            // Разбираем строку в JSON-объект
                            JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();

                            // Проверяем, есть ли в ответе кандидаты (бывает пусто, если сработал фильтр мата/безопасности)
                            if (json.has("candidates")) {
                                JsonArray candidates = json.getAsJsonArray("candidates");
                                if (candidates.size() > 0) {

                                    // Зарываемся вглубь структуры Google API
                                    JsonObject firstCandidate = candidates.get(0).getAsJsonObject();
                                    JsonObject content = firstCandidate.getAsJsonObject("content");
                                    JsonArray parts = content.getAsJsonArray("parts");

                                    if (parts != null && parts.size() > 0) {
                                        // Вот он — чистый текст ответа от твоего бота-правителя!
                                        String rawBotResponse = parts.get(0).getAsJsonObject().get("text").getAsString();

                                        // Отправляем сообщение в чат (или обрабатываем дальше)
                                        // Например, разослать всем игрокам на сервере:
                                        Bukkit.broadcastMessage("§6[Правитель] §f" + rawBotResponse);
                                        return;
                                    }
                                }
                            }

                            // Если что-то пошло не так, но ошибки сети не было (например, пустой ответ)
                            plugin.getLogger().warning("Gemini вернул пустой ответ или сработал фильтр контента. Сырой ответ: " + responseBody);

                        } catch (JsonSyntaxException e) {
                            plugin.getLogger().severe("Не удалось распарсить JSON от Gemini. Возможно, изменился формат API: " + e.getMessage());
                        } catch (Exception e) {
                            plugin.getLogger().severe("Произошла непредвиденная ошибка при обработке ответа: " + e.getMessage());
                        }
                    });
                })
                .exceptionally(ex -> {
                    plugin.getLogger().severe("Ошибка сети при запросе к Gemini: " + ex.getMessage());
                    return null;
                });
    }




    // Экранирование кавычек, чтобы JSON не ломался, если игрок введет " или \
    private String escapeJson(String text) {
        return text.replace("\\", "\\\\").replace("\"", "\\\"");
    }

}
