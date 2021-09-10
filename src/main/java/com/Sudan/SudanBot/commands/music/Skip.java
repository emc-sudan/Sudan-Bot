package com.Sudan.SudanBot.commands.music;

import com.Sudan.SudanBot.GuildMusicManager;
import com.Sudan.SudanBot.MusicCommand;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class Skip extends MusicCommand {
    @Override
    public CommandData command() {
        return new CommandData("skip", "Skips the currently playing song");
    }

    @Override
    protected boolean allowDeaf() {
        return false;
    }

    @Override
    protected void afterCheck(SlashCommandEvent ctx, Guild guild, GuildVoiceState memberVoiceState, GuildVoiceState selfVoiceState, GuildMusicManager musicManager) {
        AudioTrack track = musicManager.audioPlayer.getPlayingTrack();
        if (track == null) {
            ctx.getHook().sendMessage("I can't skip nothing").setEphemeral(true).queue();
            return;
        }
        musicManager.scheduler.nextTrack();
        ctx.getHook().sendMessage(String.format("Skipped `%s`", track.getInfo().title)).setEphemeral(true).queue();
    }
}
