package discord.bot.command.commands.music;

import discord.bot.command.CommandContext;
import discord.bot.command.ICommand;
import discord.bot.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.net.URI;
import java.net.URISyntaxException;

public class PlayURLCommand implements ICommand {
    @SuppressWarnings("ConstantConditions")
    @Override
    public void handle(CommandContext commandContext) {
        final TextChannel channel = commandContext.getChannel();
        final Member selfMember = commandContext.getSelfMember();
        final GuildVoiceState selfVoiceState = selfMember.getVoiceState();

        if (commandContext.getArgs().isEmpty()) {
            channel.sendMessage("Correct usage is '!playurl <youtube link>'");
            return;
        }

        if (!selfVoiceState.inVoiceChannel()) {
            channel.sendMessage("I need to be in a voice channel for this to work").queue();
            return;
        }

        final Member member = commandContext.getMember();
        final GuildVoiceState memberVoiceState = member.getVoiceState();

        if (!memberVoiceState.inVoiceChannel()) {
            channel.sendMessage("You need to be in a voice channel for this command to work").queue();
            return;
        }

        if (!memberVoiceState.getChannel().equals(selfVoiceState.getChannel())) {
            channel.sendMessage("You need to be in the same voice channel as me for this to work").queue();
            return;
        }

        String link = String.join(" ", commandContext.getArgs());

        if (!isUrl(link)) {
            return;
        }

        PlayerManager.getInstance()
                .loadAndPlay(channel, link);

    }

    @Override
    public String getHelp() {
        return "Plays a song\n" +
                "Usage: '!playurl <youtube link>'";
    }

    @Override
    public String getName() {
        return "playurl";
    }

    private boolean isUrl(String url) {
        try {
            new URI(url);
            return true;
        } catch (URISyntaxException exception) {
            return false;
        }
    }


}
