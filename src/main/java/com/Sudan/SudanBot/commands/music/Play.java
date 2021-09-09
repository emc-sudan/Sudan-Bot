package com.Sudan.SudanBot.commands.music;

import com.Sudan.SudanBot.ICommand;
import com.Sudan.SudanBot.Music;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class Play implements ICommand {
    public CommandData command() {
        return new CommandData("play", "Adds a song to the queue")
                .addOption(OptionType.STRING, "song", "The URL for the song to play", true);
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
        if (selfVoiceState.getChannel() != memberVoiceState.getChannel() || selfVoiceState == null) {
            try {
                Music.join(ctx);
            } catch (IllegalStateException exception) {
                return;
            }
        }
        if (memberVoiceState.isDeafened()) {
            ctx.getHook().sendMessage("Do you really think i'm going to play music for someone who's not listening?").setEphemeral(true).queue();
            return;
        }
        Music.getInstance().queue(ctx, ctx.getOption("song").getAsString());
    }
}
