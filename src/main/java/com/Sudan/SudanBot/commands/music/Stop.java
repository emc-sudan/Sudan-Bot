package com.Sudan.SudanBot.commands.music;

import com.Sudan.SudanBot.ICommand;
import com.Sudan.SudanBot.Music;
import com.Sudan.SudanBot.TrackScheduler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.Objects;

public class Stop implements ICommand {
    public CommandData command() {
        return new CommandData("stop", "Stops the currently playing song and clears the queue");
    }

    @SuppressWarnings("ConstantConditions")
    public void handle(SlashCommandEvent ctx) {
        Guild guild = ctx.getGuild();
        if (guild == null) {
            ctx.getHook().sendMessage("Could not retrieve server").setEphemeral(true).queue();
            return;
        }
        GuildVoiceState selfVoiceState = guild.getMember(ctx.getJDA().getSelfUser()).getVoiceState();
        GuildVoiceState memberVoiceState = ctx.getMember().getVoiceState();
        if (!Objects.equals(selfVoiceState.getChannel(), memberVoiceState.getChannel()) || !selfVoiceState.inVoiceChannel()) {
            try {
                Music.join(ctx);
            } catch (IllegalStateException exception) {
                return;
            }
        }
        TrackScheduler scheduler = Music.getInstance().getMusicManager(guild).scheduler;
        scheduler.player.stopTrack();
        scheduler.queue.clear();
        scheduler.lowQueue.clear();
        ctx.getHook().sendMessage("Stopped").setEphemeral(true).queue();
    }
}
