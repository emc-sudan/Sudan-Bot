package com.Sudan.SudanBot.commands.music;

import com.Sudan.SudanBot.Music;
import com.Sudan.SudanBot.MusicCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class Play extends MusicCommand {
    public CommandData command() {
        return new CommandData("play", "Adds a song to the queue")
                .addOption(OptionType.STRING, "song", "The URL for the song to play", true);
    }

    protected boolean allowDeaf() {
        return false;
    }

    @SuppressWarnings("ConstantConditions")
    public void afterCheck(SlashCommandEvent ctx, Guild guild, GuildVoiceState memberVoiceState, GuildVoiceState selfVoiceState) {
        String song = ctx.getOption("song").getAsString().strip();
        if (!song.startsWith("https://") && !song.startsWith("http://")) song = "ytsearch:" + song;
        Music.getInstance().queue(ctx, song);
    }
}
