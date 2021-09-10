package com.Sudan.SudanBot.commands.music;

import com.Sudan.SudanBot.Music;
import com.Sudan.SudanBot.MusicCommand;
import com.Sudan.SudanBot.TrackScheduler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class Stop extends MusicCommand {
    @Override
    public CommandData command() {
        return new CommandData("stop", "Stops the currently playing song and clears the queue");
    }

    @Override
    protected boolean allowDeaf() {
        return true;
    }

    @Override
    protected void afterCheck(SlashCommandEvent ctx, Guild guild, GuildVoiceState memberVoiceState, GuildVoiceState selfVoiceState) {
        TrackScheduler scheduler = Music.getInstance().getMusicManager(guild).scheduler;
        scheduler.player.stopTrack();
        scheduler.queue.clear();
        scheduler.lowQueue.clear();
        ctx.getHook().sendMessage("Stopped").setEphemeral(true).queue();
    }
}
