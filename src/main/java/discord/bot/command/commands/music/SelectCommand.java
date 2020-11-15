package discord.bot.command.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import discord.bot.command.CommandContext;
import discord.bot.command.ICommand;
import discord.bot.lavaplayer.GuildMusicManager;
import discord.bot.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

public class SelectCommand implements ICommand {
    @Override
    public void handle(CommandContext commandContext) {
        final TextChannel channel = commandContext.getChannel();
        final Member selfMember = commandContext.getSelfMember();
        final GuildVoiceState selfVoiceState = selfMember.getVoiceState();

        if (commandContext.getArgs().isEmpty()) {
            channel.sendMessage("Correct usage is '!select <number>'");
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

        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(commandContext.getGuild());

        String args = String.join(" ", commandContext.getArgs());

        if (!isStringInt(args)) {
            channel.sendMessage("It's not number").queue();
            return;
        }

        int number = Integer.parseInt(args);

        List<AudioTrack> tracks = PlayerManager.getInstance().getTracks();

        if (tracks.isEmpty()) {
            return;
        }

        musicManager.scheduler.queue(tracks.get(number - 1));

        channel.sendMessage("You selected number " + (number)).queue();


    }

    @Override
    public String getHelp() {
        return "검색결과에서 선택";
    }

    @Override
    public String getName() {
        return "select";
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
