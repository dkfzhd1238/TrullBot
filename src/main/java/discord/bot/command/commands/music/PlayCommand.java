package discord.bot.command.commands.music;

import discord.bot.command.CommandContext;
import discord.bot.command.ICommand;
import discord.bot.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class PlayCommand implements ICommand {
    @SuppressWarnings("ConstantConditions")
    @Override
    public void handle(CommandContext commandContext) {
        final TextChannel channel = commandContext.getChannel();
        final Member selfMember = commandContext.getSelfMember();
        final GuildVoiceState selfVoiceState = selfMember.getVoiceState();

        if (commandContext.getArgs().isEmpty()) {
            channel.sendMessage("Correct usage is '!play <search word>'");
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

        String search = String.join(" ", commandContext.getArgs());

        search = "ytsearch: " + search;

        PlayerManager.getInstance()
                .loadAndPlay(channel, search);
        
    }

    @Override
    public String getHelp() {
        return "Plays a song\n" +
                "Usage: '!play <search word>'";
    }

    @Override
    public String getName() {
        return "play";
    }

}
