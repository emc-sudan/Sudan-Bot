package com.Sudan.SudanBot;

import com.Sudan.SudanBot.tables.Guild;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.sql.SQLException;

public class StageMusic extends ListenerAdapter {
    StageMusic(JDA jda) {
        for (net.dv8tion.jda.api.entities.Guild guild : jda.getGuilds()) {
            Guild.GuildData guildData;
            try {
                guildData = Guild.get(guild.getId());
            } catch (SQLException e) {
                e.printStackTrace();
                continue;
            }
            if (guildData.musicStage() == null) continue;
            Music.getInstance().join(guild, guild.getStageChannelById(guildData.musicStage()));
            if (guildData.stagePlaylist() != null) {
                try {
                    setStagePlaylist(guild, guildData.stagePlaylist());
                } catch (IllegalArgumentException e) {
                    try {
                        guildData.setStagePlaylist(null);
                    } catch (SQLException sqlException) {
                        sqlException.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
        if (!event.getMember().getId().equals(event.getJDA().getSelfUser().getId())) return;
        event.getVoiceState().inviteSpeaker().queue();
    }

    @Override
    public void onGuildVoiceMove(GuildVoiceMoveEvent event) {
        if (!event.getMember().getId().equals(event.getJDA().getSelfUser().getId())) return;
        event.getVoiceState().inviteSpeaker().queue();
    }

    public static void setStagePlaylist(net.dv8tion.jda.api.entities.Guild guild, String playlist) throws IllegalArgumentException {
        Music.getInstance().playerManager.loadItem(playlist, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                throw new IllegalArgumentException();
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                Music.getInstance().createStageMusicManager(guild, playlist);
            }

            @Override
            public void noMatches() {
                throw new IllegalArgumentException();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                throw new IllegalArgumentException();
            }
        });
    }
}
