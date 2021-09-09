package com.Sudan.SudanBot;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.HashMap;
import java.util.Map;

public class Music {
    private final AudioPlayerManager playerManager;
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
    public static void join(SlashCommandEvent ctx) throws IllegalStateException {
        Guild guild = ctx.getGuild();
        if (guild == null) {
            ctx.getHook().sendMessage("Could not retrieve server").setEphemeral(true).queue();
            throw new IllegalStateException("Could not retrieve server");
        }
        Member self = guild.getMember(ctx.getJDA().getSelfUser());
        assert self != null;
        GuildVoiceState selfVoiceState = self.getVoiceState();
        assert selfVoiceState != null;
        if (selfVoiceState.inVoiceChannel()) {
            ctx.getHook().sendMessage("Do you expect me to be in two places at once?").setEphemeral(true).queue();
            throw new IllegalStateException("Do you expect me to be in two places at once?");
        }
        Member author = ctx.getMember();
        assert author != null;
        GuildVoiceState voiceState = author.getVoiceState();
        assert voiceState != null;
        if (!voiceState.inVoiceChannel()) {
            ctx.getHook().sendMessage("I can't join your voice channel if you're not in a voice channel").setEphemeral(true).queue();
            throw new IllegalStateException("I can't join your voice channel if you're not in a voice channel");
        }
        getInstance().join(guild, voiceState.getChannel());
        ctx.getHook().sendMessage("Joined your voice channel").setEphemeral(true).queue();
    }
    public void queue(SlashCommandEvent ctx, String url) {
        Guild guild = ctx.getGuild();
        if (guild == null) {
            ctx.getHook().sendMessage("Could not retrieve guild").setEphemeral(true).queue();
            return;
        }
        final GuildMusicManager manager = getMusicManager(ctx.getGuild());
        playerManager.loadItemOrdered(manager, url, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                manager.scheduler.queue(track);
                ctx.getHook().sendMessage(String.format("Queued `%s`", track.getInfo().title)).setEphemeral(true).queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                //
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
    public GuildMusicManager getMusicManager(Guild guild) {
        return this.musicManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> {
            final GuildMusicManager guildMusicManager = new GuildMusicManager(playerManager);
            guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());
            return guildMusicManager;
        });
    }
    public static Music getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Music();
        }
        return INSTANCE;
    }
}
