package discord.bot.command.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import discord.bot.command.CommandContext;
import discord.bot.command.ICommand;
import discord.bot.lavaplayer.GuildMusicManager;
import discord.bot.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class QueueCommand implements ICommand {
    @Override
    public void handle(CommandContext commandContext) {
        EmbedBuilder embedBuilder = new EmbedBuilder();

        final TextChannel channel = commandContext.getChannel();
        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(commandContext.getGuild());
        final BlockingQueue<AudioTrack> queue = musicManager.scheduler.queue;

        if (queue.isEmpty()) {
            channel.sendMessage("The queue is currently empty").queue();
            return;
        }

        final int trackCount = Math.min(queue.size(), 20);
        final List<AudioTrack> trackList = new ArrayList<>(queue);

        embedBuilder.setTitle("Queue",null);
        embedBuilder.setColor(Color.green);

        for (int i = 0; i < trackCount; i++) {
            final AudioTrack track = trackList.get(i);
            final AudioTrackInfo info = track.getInfo();

            embedBuilder.addField("#" + Integer.toString(i + 1),
                    "'" + info.title + " by " + info.author + "' ['" + formatTime(track.getDuration()) + "']",
                    true);
        }

        if (trackList.size() > trackCount) {
            embedBuilder.setFooter("And " + String.valueOf(trackList.size() - trackCount) + " more...");
        }

        channel.sendMessage(embedBuilder.build()).queue(message -> message.delete().queueAfter(1,TimeUnit.MINUTES));
    }

    private String formatTime(long timeInMillis) {
        final long hours = timeInMillis / TimeUnit.HOURS.toMillis(1);
        final long minutes = timeInMillis % TimeUnit.HOURS.toMillis(1) / TimeUnit.MINUTES.toMillis(1);
        final long seconds = timeInMillis % TimeUnit.MINUTES.toMillis(1) / TimeUnit.SECONDS.toMillis(1);

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    @Override
    public String getHelp() {
        return "Shows the queued up songs";
    }

    @Override
    public String getName() {
        return "queue";
    }


}
