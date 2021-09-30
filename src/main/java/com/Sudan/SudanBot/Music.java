package com.Sudan.SudanBot;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.HashMap;
import java.util.Map;

public class Music {
    public final AudioPlayerManager playerManager;
    private static Music INSTANCE;
    private final Map<Long, GuildMusicManager> musicManagers;
    public Music() {
        musicManagers = new HashMap<>();
        playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
    }
    public void join(Guild guild, VoiceChannel channel) {
        AudioManager manager = guild.getAudioManager();
        manager.setSendingHandler(new AudioPlayerSendHandler(playerManager.createPlayer()));
        manager.openAudioConnection(channel);
    }
    @SuppressWarnings("ConstantConditions")
    public static void join(SlashCommandEvent ctx) throws IllegalStateException {
        Guild guild = ctx.getGuild();
        if (guild == null) {
            MessageEmbed embed = new EmbedBuilder()
                    .setColor(Colours.ERROR.colour)
                    .setTitle("Could not retrieve server")
                    .build();
            ctx.getHook().sendMessageEmbeds(embed).queue();
            throw new IllegalStateException();
        }
        GuildVoiceState voiceState = ctx.getMember().getVoiceState();
        if (getInstance().getMusicManager(guild).scheduler instanceof StageTrackScheduler) {
            if (guild.getSelfMember().getVoiceState().getChannel().getMembers().stream().anyMatch((member) -> !member.getUser().isBot())) {
                MessageEmbed embed = new EmbedBuilder()
                        .setColor(Colours.ERROR.colour)
                        .setTitle("Other people are busy enjoying music, don't be selfish")
                        .build();
                ctx.getHook().sendMessageEmbeds(embed).queue();
                throw new IllegalStateException();
            }
            getInstance().removeMusicManager(guild);
        } else if (voiceState.inVoiceChannel()) {
            MessageEmbed embed = new EmbedBuilder()
                    .setColor(Colours.ERROR.colour)
                    .setTitle("Do you expect me to be in two places at once?")
                    .build();
            ctx.getHook().sendMessageEmbeds(embed).queue();
            throw new IllegalStateException();
        }
        if (!voiceState.inVoiceChannel()) {
            MessageEmbed embed = new EmbedBuilder()
                    .setColor(Colours.ERROR.colour)
                    .setTitle("I can't join your voice channel if you're not in a voice channel")
                    .build();
            ctx.getHook().sendMessageEmbeds(embed).queue();
            throw new IllegalStateException();
        }
        if (voiceState.getChannel().getType() == ChannelType.STAGE && voiceState.isSuppressed()) {
            MessageEmbed embed = new EmbedBuilder()
                    .setColor(Colours.ERROR.colour)
                    .setTitle("You need to be a speaker to do that")
                    .build();
            ctx.getHook().sendMessageEmbeds(embed).queue();
            throw new IllegalStateException();
        }
        getInstance().join(guild, voiceState.getChannel());
        MessageEmbed embed = new EmbedBuilder()
                .setColor(Colours.SUCCESS.colour)
                .setTitle(String.format("Joined `%s`", voiceState.getChannel().getName()))
                .build();
        ctx.getHook().sendMessageEmbeds(embed).queue();
    }
    public void queue(SlashCommandEvent ctx, String url) {
        Guild guild = ctx.getGuild();
        if (guild == null) {
            MessageEmbed embed = new EmbedBuilder()
                    .setColor(Colours.ERROR.colour)
                    .setTitle("Could not retrieve server")
                    .build();
            ctx.getHook().sendMessageEmbeds(embed).queue();
            return;
        }
        final GuildMusicManager manager = getMusicManager(ctx.getGuild());
        playerManager.loadItemOrdered(manager, url, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                manager.scheduler.queue(track);
                MessageEmbed embed = new EmbedBuilder()
                        .setColor(Colours.SUCCESS.colour)
                        .setTitle(String.format("Queued `%s`", track.getInfo().title))
                        .build();
                ctx.getHook().sendMessageEmbeds(embed).queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                if (playlist.isSearchResult()) {
                    AudioTrack track = playlist.getTracks().get(0);
                    manager.scheduler.queue(track);
                    MessageEmbed embed = new EmbedBuilder()
                            .setColor(Colours.SUCCESS.colour)
                            .setTitle(String.format("Queued `%s`", track.getInfo().title))
                            .build();
                    ctx.getHook().sendMessageEmbeds(embed).queue();
                    return;
                }
                for (AudioTrack track : playlist.getTracks()) {
                    manager.scheduler.lowQueue(track);
                }
                MessageEmbed embed = new EmbedBuilder()
                        .setColor(Colours.SUCCESS.colour)
                        .setTitle(String.format("Queued `%d` tracks from `%s`", playlist.getTracks().size(), playlist.getName()))
                        .build();
                ctx.getHook().sendMessageEmbeds(embed).queue();
            }

            @Override
            public void noMatches() {
                MessageEmbed embed = new EmbedBuilder()
                        .setColor(Colours.ERROR.colour)
                        .setTitle("Song not found")
                        .build();
                ctx.getHook().sendMessageEmbeds(embed).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                MessageEmbed embed = new EmbedBuilder()
                        .setColor(Colours.ERROR.colour)
                        .setTitle("Could not load song")
                        .setDescription(exception.getMessage())
                        .build();
                ctx.getHook().sendMessageEmbeds(embed).queue();
            }
        });
    }

    public GuildMusicManager getMusicManager(Guild guild) {
        return musicManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> {
            final GuildMusicManager guildMusicManager = new GuildMusicManager(playerManager);
            guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());
            return guildMusicManager;
        });
    }

    public void createStageMusicManager(Guild guild, AudioPlaylist playlist) {
        final GuildMusicManager guildMusicManager = new GuildMusicManager(playerManager, playlist);
        guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());
        musicManagers.put(guild.getIdLong(), guildMusicManager);
        guildMusicManager.scheduler.nextTrack();
    }

    public void removeMusicManager(Guild guild) {
        musicManagers.remove(guild.getIdLong());
    }

    public static Music getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Music();
        }
        return INSTANCE;
    }
}
