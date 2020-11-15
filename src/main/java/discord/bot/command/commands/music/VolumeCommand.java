package discord.bot.command.commands.music;

import discord.bot.command.CommandContext;
import discord.bot.command.ICommand;
import discord.bot.lavaplayer.GuildMusicManager;
import discord.bot.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class VolumeCommand implements ICommand {
    @Override
    public void handle(CommandContext commandContext) {
        final TextChannel channel = commandContext.getChannel();
        final Member selfMember = commandContext.getSelfMember();
        final GuildVoiceState selfVoiceState = selfMember.getVoiceState();

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

        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(commandContext.getGuild());

        String args = String.join(" ", commandContext.getArgs());

        int volume = musicManager.audioPlayer.getVolume();

        if (commandContext.getArgs().isEmpty()) {
            channel.sendMessage("Current Volume : " + volume).queue();
            return;
        }

        if (!isStringInt(args)) {
            channel.sendMessage("It's not number").queue();
            return;
        }

        int number = Integer.parseInt(args);

        musicManager.audioPlayer.setVolume(number);
        channel.sendMessage("Now Volume : " + number).queue();


    }

    @Override
    public String getHelp() {
        return "Sets volume or Shows current volume";
    }

    @Override
    public String getName() {
        return "volume";
    }

    public static boolean isStringInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
