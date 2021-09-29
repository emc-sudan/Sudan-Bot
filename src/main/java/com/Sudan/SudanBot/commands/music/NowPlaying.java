package com.Sudan.SudanBot.commands.music;

import com.Sudan.SudanBot.Colours;
import com.Sudan.SudanBot.GuildMusicManager;
import com.Sudan.SudanBot.MusicCommand;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.concurrent.TimeUnit;

public class NowPlaying extends MusicCommand {
    @Override
    public CommandData command() {
        return new CommandData("nowplaying", "Shows the currently playing song");
    }

    @Override
    public boolean ephemeral() {
        return true;
    }

    @Override
    protected boolean allowDeaf() {
        return true;
    }

    @Override
    protected void afterCheck(SlashCommandEvent ctx, Guild guild, GuildVoiceState memberVoiceState, GuildVoiceState selfVoiceState, GuildMusicManager musicManager) {
        AudioTrack track = musicManager.audioPlayer.getPlayingTrack();
        if (track == null) {
            MessageEmbed embed = new EmbedBuilder()
                    .setColor(Colours.ERROR.colour)
                    .setTitle("There is currently nothing playing")
                    .build();
            ctx.getHook().sendMessageEmbeds(embed).queue();
            return;
        }
        AudioTrackInfo info = track.getInfo();
        long seconds;
        long minutes;
        long hours;
        long time;
        time = info.length;
        hours = TimeUnit.MILLISECONDS.toHours(time);
        minutes = TimeUnit.MILLISECONDS.toMinutes(time) - hours * 60;
        seconds = TimeUnit.MILLISECONDS.toSeconds(time) - hours * 60 * 60 - minutes * 60;
        boolean showMinutes = minutes > 0;
        boolean showHours = hours > 0;
        String duration;
        if (showHours) duration = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        else if (showMinutes) duration = String.format("%02d:%02d", minutes, seconds);
        else duration = String.format("%02d", seconds);
        time = track.getPosition();
        hours = TimeUnit.MILLISECONDS.toHours(time);
        minutes = TimeUnit.MILLISECONDS.toMinutes(time) - hours * 60;
        seconds = TimeUnit.MILLISECONDS.toSeconds(time) - hours * 60 * 60 - minutes * 60;
        String position;
        if (showHours) position = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        else if (showMinutes) position = String.format("%02d:%02d", minutes, seconds);
        else position = String.format("%02d", seconds);
        float percent = (float) track.getPosition() / info.length;
        MessageEmbed embed = new EmbedBuilder()
                .setColor(Colours.INFO.colour)
                .setTitle("Now playing")
                .setDescription(String.format("%s[`%s`](%s)", info.isStream ? "\uD83D\uDD34 " : "", info.title, info.uri))
                .addField("Time", String.format("`[%s/%s]`", position, duration), true)
                .addField("Progress", new StringBuilder(String.format("――――――――――――――――――― `(%.2f%%)`", percent * 100)).insert((int) (percent * 20), "**__▬__**").toString(), false)
                .build();
        ctx.getHook().sendMessageEmbeds(embed).queue();
    }
}
