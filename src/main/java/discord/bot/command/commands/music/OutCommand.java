package discord.bot.command.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import discord.bot.command.CommandContext;
import discord.bot.command.ICommand;
import discord.bot.lavaplayer.GuildMusicManager;
import discord.bot.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.managers.AudioManager;

public class OutCommand implements ICommand {
    @Override
    public void handle(CommandContext commandContext) {
        final TextChannel channel = commandContext.getChannel();
        final Member self = commandContext.getSelfMember();
        final GuildVoiceState selfVoiceState = self.getVoiceState();

        if (!selfVoiceState.inVoiceChannel()) {
            channel.sendMessage("I need to be in a voice channel for this to work").queue();
            return;
        }

        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(commandContext.getGuild());
        final AudioPlayer audioPlayer = musicManager.audioPlayer;

        if (!audioPlayer.isPaused()) {
            channel.sendMessage("It is playing now!").queue();
            return;
        }

        final AudioManager audioManager = commandContext.getGuild().getAudioManager();

        audioManager.closeAudioConnection();
        channel.sendMessageFormat("Leaving to ' %s '", selfVoiceState.getChannel().getName()).queue();
    }

    @Override
    public String getHelp() {
        return "Makes the bot leave your voice channel";
    }

    @Override
    public String getName() {
        return "out";
    }
}
