package discord.bot.command.commands;

import discord.bot.command.CommandContext;
import discord.bot.command.ICommand;
import net.dv8tion.jda.api.JDA;

public class PingCommand implements ICommand {
    @Override
    public void handle(CommandContext commandContext) {
        JDA jda = commandContext.getJDA();

        jda.getRestPing().queue(
                (ping) -> commandContext.getChannel()
                        .sendMessageFormat("Reset ping : %sms\nWS ping : %sms", ping, jda.getGatewayPing()).queue());
    }

    @Override
    public String getHelp() {
        return "Shows the current ping from the bot to the discord servers.";
    }

    @Override
    public String getName() {
        return "ping";
    }
}
