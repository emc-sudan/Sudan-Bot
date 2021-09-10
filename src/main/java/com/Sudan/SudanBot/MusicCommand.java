package com.Sudan.SudanBot;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.util.Objects;

public abstract class MusicCommand implements ICommand {
    protected abstract boolean allowDeaf();

    protected abstract void afterCheck(SlashCommandEvent ctx, Guild guild, GuildVoiceState memberVoiceState, GuildVoiceState selfVoiceState, GuildMusicManager musicManager);

    @SuppressWarnings("ConstantConditions")
    @Override
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
        if (!allowDeaf() && memberVoiceState.isDeafened()) {
            ctx.getHook().sendMessage("Do you really think i'm going to play music for someone who's not listening?").setEphemeral(true).queue();
            return;
        }
        afterCheck(ctx, guild, memberVoiceState, selfVoiceState, Music.getInstance().getMusicManager(guild));
    }
}
