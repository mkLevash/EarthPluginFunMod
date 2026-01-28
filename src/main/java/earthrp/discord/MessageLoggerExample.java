//package earthrp.discord;
//
//import net.dv8tion.jda.api.JDA;
//import net.dv8tion.jda.api.JDABuilder;
//import net.dv8tion.jda.api.Permission;
//import net.dv8tion.jda.api.entities.Activity;
//import net.dv8tion.jda.api.entities.Member;
//import net.dv8tion.jda.api.entities.Message;
//import net.dv8tion.jda.api.entities.User;
//import net.dv8tion.jda.api.entities.channel.ChannelType;
//import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
//import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
//import net.dv8tion.jda.api.entities.emoji.Emoji;
//import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
//import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
//import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
//import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
//import net.dv8tion.jda.api.hooks.ListenerAdapter;
//import net.dv8tion.jda.api.interactions.IntegrationType;
//import net.dv8tion.jda.api.interactions.InteractionContextType;
//import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
//import net.dv8tion.jda.api.interactions.commands.OptionMapping;
//import net.dv8tion.jda.api.interactions.commands.build.Commands;
//import net.dv8tion.jda.api.interactions.components.ActionRow;
//import net.dv8tion.jda.api.interactions.components.buttons.Button;
//import net.dv8tion.jda.api.requests.GatewayIntent;
//import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
//
//import javax.annotation.Nonnull;
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.EnumSet;
//
//import static net.dv8tion.jda.api.interactions.commands.OptionType.INTEGER;
//import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;
//
//
//public class MessageLoggerExample extends ListenerAdapter {
//    private final String admin = "329714147216326658";
//
//
//    // See https://emojipedia.org/red-heart/ and find the codepoints
//    public static final Emoji HEART = Emoji.fromUnicode("U+2764");
//
//    public static void main(String[] args) throws IOException
//    {
//        // Possible ways to provide the token:
//
//        // 1. From a file:
//
//        // This would just be some text file with only the token in it
//        // Use Files.readString in java 11+
//        String token = "MTA0MjExMjYwNzAxMzg0Mjk4NA.GpJMxc.0fYIOqBUh1T-BorOZfmPfiln492g3TbBQXFhgY";
//
//        // 2. Using environment variable:
//        // String token = System.getenv("TOKEN");
//
//        // 3. Using system property as -Dtoken=...
//        // This leaks the token in command line (task manager) and thread dumps to any other users on the same machine
//        // String token = System.getProperty("token");
//
//        // 4. From the command line directly
//        // This leaks the token in command line (task manager) to any other users on the same machine
//        // String token = args[0];
//
//
//        // Pick which intents we need to use in our code.
//        // To get the best performance, you want to make the most minimalistic list of intents, and have all others disabled.
//        // When an intent is enabled, you will receive events and cache updates related to that intent.
//        // For more information:
//        //
//        // - The documentation for GatewayIntent: https://docs.jda.wiki/net/dv8tion/jda/api/requests/GatewayIntent.html
//        // - The wiki page for intents and caching: https://jda.wiki/using-jda/gateway-intents-and-member-cache-policy/
//
//        EnumSet<GatewayIntent> intents = EnumSet.of(
//                // Enables MessageReceivedEvent for guild (also known as servers)
//                GatewayIntent.GUILD_MESSAGES,
//                // Enables the event for private channels (also known as direct messages)
//                GatewayIntent.DIRECT_MESSAGES,
//                // Enables access to message.getContentRaw()
//                GatewayIntent.MESSAGE_CONTENT,
//                // Enables MessageReactionAddEvent for guild
//                GatewayIntent.GUILD_MESSAGE_REACTIONS,
//                // Enables MessageReactionAddEvent for private channels
//                GatewayIntent.DIRECT_MESSAGE_REACTIONS
//        );
//
//        // To start the bot, you have to use the JDABuilder.
//
//        // You can choose one of the factory methods to build your bot:
//        // - createLight(...)
//        // - createDefault(...)
//        // - create(...)
//        // Each of these factory methods use different defaults, you can check the documentation for more details.
//
//        try
//        {
//            // By using createLight(token, intents), we use a minimalistic cache profile (lower ram usage)
//            // and only enable the provided set of intents. All other intents are disabled, so you won't receive events for those.
//            JDA jda = JDABuilder.createLight(token, intents)
//                    // On this builder, you are adding all your event listeners and session configuration
//                    .addEventListeners(new MessageLoggerExample())
//                    // You can do lots of configuration before starting, checkout all the setters on the JDABuilder class!
//
//                    // Once you're done configuring your jda instance, call build to start and login the bot.
//                    .build();
//
//            CommandListUpdateAction commands = jda.updateCommands();
//
//            // Simple reply commands
//            commands.addCommands(
//                    Commands.slash("penis", "Makes the bot say what you tell it to")
//                            .setContexts(InteractionContextType.ALL) // Allow the command to be used anywhere (Bot DMs, Guild, Friend DMs, Group DMs)
//                            .setIntegrationTypes(IntegrationType.ALL) // Allow the command to be installed anywhere (Guilds, Users)
//                            .addOption(STRING, "content", "What the bot should say", true) // you can add required options like this too
//            );
//
//            commands.addCommands(
//                    Commands.slash("prune", "Prune messages from this channel")
//                            .addOption(INTEGER, "amount", "How many messages to prune (Default 100)") // simple optional argument
//                            // The default integration types are GUILD_INSTALL.
//                            // Can't use this in DMs, and in guilds the bot isn't in.
//                            .setContexts(InteractionContextType.GUILD)
//                            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_MANAGE))
//            );
//
//
//            commands.queue();
//
//            // Here you can now start using the jda instance before its fully loaded,
//            // this can be useful for stuff like creating background services or similar.
//
//            // The queue(...) means that we are making a REST request to the discord API server!
//            // Usually, this is done asynchronously on another thread which handles scheduling and rate-limits.
//            // The (ping -> ...) is called a lambda expression, if you're unfamiliar with this syntax it is HIGHLY recommended to look it up!
//            jda.getRestPing().queue(ping ->
//                    // shows ping in milliseconds
//                    System.out.println("[Earth]Logged in with ping: " + ping)
//            );
//
//            // If you want to access the cache, you can use awaitReady() to block the main thread until the jda instance is fully loaded
//            jda.awaitReady();
//
//            // Now we can access the fully loaded cache and show some statistics or do other cache dependent things
//            System.out.println("[Earth]Guilds: " + jda.getGuildCache().size());
//        }
//        catch (InterruptedException e)
//        {
//            // Thrown if the awaitReady() call is interrupted
//            e.printStackTrace();
//        }
//    }
//
//    // This overrides the method called onMessageReceived in the ListenerAdapter class
//    // Your IDE (such as intellij or eclipse) can automatically generate this override for you, by simply typing "onMessage" and auto-completing it!
//    @Override
//    public void onMessageReceived(@Nonnull MessageReceivedEvent event)
//    {
//        // The user who sent the message
//        User author = event.getAuthor();
//        // This is a special class called a "union", which allows you to perform specialization to more concrete types such as TextChannel or NewsChannel
//        MessageChannelUnion channel = event.getChannel();
//        // The actual message sent by the user, this can also be a message the bot sent itself, since you *do* receive your own messages after all
//        Message message = event.getMessage();
//
//        // Check whether the message was sent in a guild / server
//        if (event.isFromGuild()&&!author.isBot())
//        {
//
//            // This is a message from a server
//            System.out.printf("[%s] [%#s] %#s: %s\n",
//                    event.getGuild().getName(), // The name of the server the user sent the message in, this is generally referred to as "guild" in the API
//                    channel, // The %#s makes use of the channel name and displays as something like #general
//                    author,  // The %#s makes use of User#getAsTag which results in something like minn or Minn#1337
//                    message.getContentDisplay() // This removes any unwanted mention syntax and converts it to a readable string
//            );
//
//
//        }
//        else
//        {
//            // This is a message from a private channel
//            System.out.printf("[direct] %#s: %s\n",
//                    author, // same as above
//                    message.getContentDisplay()
//            );
//        }
//        if (event.isFromGuild()&&author.getId().equals(admin)) {
//            //message.addReaction(Emoji.fromUnicode("U+1F480")).queue();
//        }
//
//        // Using specialization, you can check concrete types of the channel union
//
//        if (channel.getType() == ChannelType.TEXT)
//        {
//            System.out.println("[Earth]The channel topic is " + channel.asTextChannel().getTopic());
//        }
//
//        if (channel.getType().isThread())
//        {
//            System.out.println("[Earth]This thread is part of channel #" +
//                    channel.asThreadChannel()  // Cast the channel union to thread
//                            .getParentChannel() // Get the parent of that thread, which is the channel it was created in (like forum or text channel)
//                            .getName()          // And then print out the name of that channel
//            );
//        }
//    }
//
//    @Override
//    public void onMessageReactionAdd(@Nonnull MessageReactionAddEvent event)
//    {
//        if (event.getEmoji().equals(HEART))
//            System.out.println("[Earth]A user loved a message!");
//    }
//
//    @Override
//    public void onSlashCommandInteraction(SlashCommandInteractionEvent event)
//    {
//        // Only accept commands from guilds
//        if (event.getGuild() == null)
//            return;
//        switch (event.getName())
//        {
//
//            case "penis":
//                say(event, event.getOption("content").getAsString()); // content is required so no null-check here
//                break;
//            case "prune": // 2 stage command with a button prompt
//                prune(event);
//                break;
//            default:
//                event.reply("I can't handle that command right now :(").setEphemeral(true).queue();
//        }
//    }
//    public void say(SlashCommandInteractionEvent event, String content)
//    {
//
//        event.reply(content).queue(); // This requires no permissions!
//    }
//
//    public void prune(SlashCommandInteractionEvent event)
//    {
//        OptionMapping amountOption = event.getOption("amount"); // This is configured to be optional so check for null
//        int amount = amountOption == null
//                ? 100 // default 100
//                : (int) Math.min(200, Math.max(2, amountOption.getAsLong())); // enforcement: must be between 2-200
//        String userId = event.getUser().getId();
//        event.reply("This will delete " + amount + " messages.\nAre you sure?") // prompt the user with a button menu
//                .addComponents(
//                        ActionRow.of( // this means "<style>(<id>, <label>)", you can encode anything you want in the id (up to 100 characters)
//                                Button.secondary(userId + ":delete", "Nevermind!"),
//                                Button.danger(userId + ":prune:" + amount, "Yes!") // the first parameter is the component id we use in onButtonInteraction above
//                        )
//                )
//                .setEphemeral(true)
//                .queue();
//    }
//
//    @Override
//    public void onButtonInteraction(ButtonInteractionEvent event)
//    {
//        String[] id = event.getComponentId().split(":"); // this is the custom id we specified in our button
//        String authorId = id[0];
//        String type = id[1];
//        // Check that the button is for the user that clicked it, otherwise just ignore the event (let interaction fail)
//        if (!authorId.equals(event.getUser().getId()))
//            return;
//        event.deferEdit().queue(); // acknowledge the button was clicked, otherwise the interaction will fail
//
//        MessageChannel channel = event.getChannel();
//        switch (type)
//        {
//            case "prune":
//                int amount = Integer.parseInt(id[2]);
//                event.getChannel().getIterableHistory()
//                        .skipTo(event.getMessageIdLong())
//                        .takeAsync(amount)
//                        .thenAccept(channel::purgeMessages);
//                // fallthrough delete the prompt message with our buttons
//            case "delete":
//                event.getHook().deleteOriginal().queue();
//        }
//    }
//
//
//}
