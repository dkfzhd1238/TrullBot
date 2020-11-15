package discord.bot.command.commands;

import discord.bot.CommandManager;
import discord.bot.Config;
import discord.bot.command.CommandContext;
import discord.bot.command.ICommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class HelpCommand implements ICommand {

    private final CommandManager manager;

    public HelpCommand(CommandManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(CommandContext commandContext) {
        List<String> args = commandContext.getArgs();
        TextChannel channel = commandContext.getChannel();

        if (args.isEmpty()) {
            EmbedBuilder embedBuilder = new EmbedBuilder();

            embedBuilder.setTitle("List of Commands");
            manager.getCommands().stream().map(ICommand::getName).forEach(
                    (it) -> embedBuilder.appendDescription(Config.get("prefix") + it + "\n"));

            channel.sendMessage(embedBuilder.build()).queue(message -> message.delete().queueAfter(1, TimeUnit.MINUTES));
            return;

        }

        String search = args.get(0);
        ICommand command = manager.getCommand(search);

        if (command == null) {
            channel.sendMessage("Nothing found for " + search).queue();
            return;
        }

        channel.sendMessage(command.getHelp()).queue();
    }

    @Override
    public String getHelp() {
        return "Shows the list with commands in the bot\n" +
                "Usage: `!help [command]`";
    }

    @Override
    public String getName() {
        return "help";
    }
}
