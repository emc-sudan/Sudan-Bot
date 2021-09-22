package com.Sudan.SudanBot.commands.music;

import com.Sudan.SudanBot.Colours;
import com.Sudan.SudanBot.GuildMusicManager;
import com.Sudan.SudanBot.MusicCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class Stop extends MusicCommand {
    @Override
    public CommandData command() {
        return new CommandData("stop", "Stops the currently playing song and clears the queue");
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
    protected void afterCheck(SlashCommandEvent ctx, Guild guild, GuildVoiceState memberVoiceState, GuildVoiceState selfVoiceState, GuildMusicManager musicManager) {
        musicManager.scheduler.player.stopTrack();
        musicManager.scheduler.queue.clear();
        musicManager.scheduler.lowQueue.clear();
        MessageEmbed embed = new EmbedBuilder()
                .setColor(Colours.SUCCESS.colour)
                .setTitle("Stopped")
                .build();
        ctx.getHook().sendMessageEmbeds(embed).setEphemeral(true).queue();
    }
}
