//package earthrp.discord;
//
//import earthrp.Earth;
//import net.dv8tion.jda.api.JDABuilder;
//import net.dv8tion.jda.api.JDA;
//import net.dv8tion.jda.api.entities.Activity;
//import net.dv8tion.jda.api.entities.Message;
//import net.dv8tion.jda.api.entities.User;
//import net.dv8tion.jda.api.entities.channel.ChannelType;
//import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
//import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
//import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
//
//import javax.annotation.Nonnull;
//
//public class DiscordBot {
//
//
//    private final Earth plugin;
//    private JDA jda;
//    private static final String TOKEN = Earth.getInstance().getConfig().getString("token");
//
//    public static String getToken(){
//        return TOKEN;
//    }
//
//
//    public DiscordBot(Earth plugin) {
//        this.plugin = plugin;
//    }
//
//
//
//    public void startBot() {
//        try {
//            // ← Вставь свой токен сюда
//            jda = JDABuilder.createDefault(TOKEN)
//                    .setActivity(Activity.playing("Penis"))
//                    .build();
//
//            plugin.getLogger().info("Discord-бот запущен.");
//            MessageLoggerExample.main(null);
//        } catch (Exception e) {
//            plugin.getLogger().severe("Ошибка запуска Discord-бота: " + e.getMessage());
//        }
//
//
//
//    }
//
//
//        public void shutdown() {
//        if (jda != null) {
//            jda.shutdown();
//            plugin.getLogger().info("Discord-бот остановлен.");
//        }
//    }
//
//
//
//
//}
