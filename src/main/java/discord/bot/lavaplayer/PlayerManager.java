package discord.bot.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PlayerManager {



    private static PlayerManager INSTANCE;

    private List<AudioTrack> tracks;

    private final Map<Long, GuildMusicManager> musicManagers;
    private final AudioPlayerManager audioPlayerManager;


    public PlayerManager() {
        this.musicManagers = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();

        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
    }

    public GuildMusicManager getMusicManager(Guild guild) {
        return this.musicManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> {
           final GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager);

           guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());

           return guildMusicManager;
        });


    }

    public void loadAndPlay(TextChannel channel, String trackUrl) {
        final GuildMusicManager musicManager = this.getMusicManager(channel.getGuild());

        this.audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                musicManager.scheduler.queue(track);

                channel.sendMessage("Adding to queue: '")
                        .append(track.getInfo().title)
                        .append("' by '")
                        .append(track.getInfo().author)
                        .append("'")
                        .queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                final List<AudioTrack> tracks = playlist.getTracks();

                channel.sendMessage("Adding to queue: '")
                        .append(String.valueOf(tracks.size()))
                        .append("' tracks from playlist '")
                        .append(playlist.getName())
                        .append("'")
                        .queue();

                for (final AudioTrack track : tracks) {
                    musicManager.scheduler.queue(track);
                }
            }

            @Override
            public void noMatches() {
                //
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                //
            }
        });
    }

    public void load(TextChannel channel, String trackUrl) {
        final GuildMusicManager musicManager = this.getMusicManager(channel.getGuild());

        this.audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {

                channel.sendMessage("Adding to queue: '")
                        .append(track.getInfo().title)
                        .append("' by '")
                        .append(track.getInfo().author)
                        .append("'")
                        .queue();



            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                tracks = playlist.getTracks();

                EmbedBuilder embedBuilder = new EmbedBuilder();

                if (tracks.isEmpty()) {
                    channel.sendMessage("The queue is currently empty").queue();
                    return;
                }

                final int trackCount = Math.min(tracks.size(), 20);
                final List<AudioTrack> trackList = new ArrayList<>(tracks);

                embedBuilder.setTitle("Search List",null);
                embedBuilder.setDescription(playlist.getName());
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


                channel.sendMessage(embedBuilder.build()).queue((message -> message.delete().queueAfter(1,TimeUnit.MINUTES)));



            }

            @Override
            public void noMatches() {
                //
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                //
            }
        });
    }

    public static PlayerManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PlayerManager();
        }

        return INSTANCE;
    }

    public List<AudioTrack> getTracks() {
        return this.tracks;
    }

    private String formatTime(long timeInMillis) {
        final long hours = timeInMillis / TimeUnit.HOURS.toMillis(1);
        final long minutes = timeInMillis % TimeUnit.HOURS.toMillis(1) / TimeUnit.MINUTES.toMillis(1);
        final long seconds = timeInMillis % TimeUnit.MINUTES.toMillis(1) / TimeUnit.SECONDS.toMillis(1);

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

}
