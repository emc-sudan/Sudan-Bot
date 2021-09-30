package com.Sudan.SudanBot.commands.music;

import com.Sudan.SudanBot.Colours;
import com.Sudan.SudanBot.GuildMusicManager;
import com.Sudan.SudanBot.Music;
import com.Sudan.SudanBot.MusicCommand;
import com.Sudan.SudanBot.tables.Guild;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.StageChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.sql.SQLException;

import static com.Sudan.SudanBot.StageMusic.setStagePlaylist;

public class Leave extends MusicCommand {
    @Override
    public CommandData command() {
        return new CommandData("leave", "Makes the bot leave your voice channel");
    }

    @Override
    public boolean ephemeral() {
        return true;
    }

    @Override
    protected boolean allowDeaf() {
        return false;
    }

    @Override
    protected boolean allowAudience() {
        return false;
    }

    @Override
    protected boolean allowAutoStage() {
        return false;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void afterCheck(SlashCommandEvent ctx, net.dv8tion.jda.api.entities.Guild guild, GuildVoiceState memberVoiceState, GuildVoiceState selfVoiceState, GuildMusicManager musicManager) {
        musicManager.scheduler.player.stopTrack();
        musicManager.scheduler.queue.clear();
        musicManager.scheduler.lowQueue.clear();
        Guild.GuildData guildData;
        try {
            guildData = Guild.get(guild.getId());
        } catch (SQLException e) {
            e.printStackTrace();
            MessageEmbed embed = new EmbedBuilder()
                    .setColor(Colours.ERROR.colour)
                    .setTitle("An unknown error has occurred")
                    .build();
            ctx.getHook().sendMessageEmbeds(embed).queue();
            return;
        }
        if (guildData.musicStage() != null) {
            if (guildData.stagePlaylist() != null) {
                try {
                    setStagePlaylist(guild, guildData.stagePlaylist());
                } catch (IllegalArgumentException e) {
                    try {
                        guildData.setStagePlaylist(null);
                    } catch (SQLException sqlException) {
                        sqlException.printStackTrace();
                        e.printStackTrace();
                        MessageEmbed embed = new EmbedBuilder()
                                .setColor(Colours.ERROR.colour)
                                .setTitle("An unknown error has occurred")
                                .build();
                        ctx.getHook().sendMessageEmbeds(embed).queue();
                        return;
                    }
                }
            } else {
                Music.getInstance().removeMusicManager(guild);
            }
            StageChannel stageChannel = guild.getStageChannelById(guildData.musicStage());
            if (stageChannel == null) {
                try {
                    guildData.setMusicStage(null);
                    afterCheck(ctx, guild, memberVoiceState, selfVoiceState, musicManager);
                    return;
                } catch (SQLException e) {
                    e.printStackTrace();
                    MessageEmbed embed = new EmbedBuilder()
                            .setColor(Colours.ERROR.colour)
                            .setTitle("An unknown error has occurred")
                            .build();
                    ctx.getHook().sendMessageEmbeds(embed).queue();
                    return;
                }
            }
            Music.getInstance().join(guild, stageChannel);
        } else {
            guild.getAudioManager().closeAudioConnection();
            Music.getInstance().removeMusicManager(guild);
        }
        MessageEmbed embed = new EmbedBuilder()
                .setColor(Colours.SUCCESS.colour)
                .setTitle(String.format("Left %s", memberVoiceState.getChannel().getName()))
                .build();
        ctx.getHook().sendMessageEmbeds(embed).queue();
    }
}
