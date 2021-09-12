package com.Sudan.SudanBot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.MessageEmbed;
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
            MessageEmbed embed = new EmbedBuilder()
                    .setColor(Colours.ERROR.colour)
                    .setTitle("Could not retrieve server")
                    .build();
            ctx.getHook().sendMessageEmbeds(embed).setEphemeral(true).queue();
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
            MessageEmbed embed = new EmbedBuilder()
                    .setColor(Colours.ERROR.colour)
                    .setTitle("Do you really think i'm going to play music for someone who's not listening?")
                    .build();
            ctx.getHook().sendMessageEmbeds(embed).setEphemeral(true).queue();
            return;
        }
        afterCheck(ctx, guild, memberVoiceState, selfVoiceState, Music.getInstance().getMusicManager(guild));
    }
}
