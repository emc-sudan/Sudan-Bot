package com.Sudan.SudanBot.commands.music;

import com.Sudan.SudanBot.GuildMusicManager;
import com.Sudan.SudanBot.Music;
import com.Sudan.SudanBot.MusicCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class Play extends MusicCommand {
    @Override
    public CommandData command() {
        return new CommandData("play", "Adds a song to the queue")
                .addOption(OptionType.STRING, "song", "The URL or name of the song to play", true);
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
        return true;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void afterCheck(SlashCommandEvent ctx, Guild guild, GuildVoiceState memberVoiceState, GuildVoiceState selfVoiceState, GuildMusicManager musicManager) {
        String song = ctx.getOption("song").getAsString().strip();
        if (!song.startsWith("https://") && !song.startsWith("http://")) song = "ytsearch:" + song;
        Music.getInstance().queue(ctx, song);
    }
}
