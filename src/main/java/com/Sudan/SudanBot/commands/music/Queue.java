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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class Queue extends MusicCommand {
    @Override
    public CommandData command() {
        return new CommandData("queue", "Shows the queue");
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
    protected boolean allowAudience() {
        return true;
    }

    @Override
    protected boolean allowAutoStage() {
        return true;
    }

    @Override
    protected void afterCheck(SlashCommandEvent ctx, Guild guild, GuildVoiceState memberVoiceState, GuildVoiceState selfVoiceState, GuildMusicManager musicManager) {
        BlockingQueue<AudioTrack> queue = musicManager.scheduler.queue;
        BlockingQueue<AudioTrack> lowQueue = musicManager.scheduler.lowQueue;
        if (queue.isEmpty() && lowQueue.isEmpty()) {
            MessageEmbed embed = new EmbedBuilder()
                    .setColor(Colours.ERROR.colour)
                    .setTitle("There is currently nothing in the queue")
                    .setDescription("Use `/play <song>` to add a song to the queue")
                    .build();
            ctx.getHook().sendMessageEmbeds(embed).queue();
            return;
        }
        EmbedBuilder eb = new EmbedBuilder()
                .setColor(Colours.INFO.colour)
                .setTitle("Queue");
        StringBuilder list = new StringBuilder();
        List<AudioTrack> tracks;
        addStuff:
        {
            String next;
            AudioTrackInfo info;
            tracks = new ArrayList<>(queue);
            for (short i = 0; i < tracks.size(); i++) {
                info = tracks.get(i).getInfo();
                next = String.format("\n`%d` %s[`%s`](%s)", i + 1, info.isStream ? "\uD83D\uDD34 " : "", info.title, info.uri);
                if (list.length() + next.length() > 2048) {
                    eb.setFooter(String.format("%d items hidden", tracks.size() - i + lowQueue.size()));
                    break addStuff;
                }
                list.append(next);
            }
            tracks = new ArrayList<>(lowQueue);
            for (short i = 0; i < tracks.size(); i++) {
                info = tracks.get(i).getInfo();
                next = String.format("\n`L%d` %s[`%s`](%s)", i + 1, info.isStream ? "\uD83D\uDD34 " : "", info.title, info.uri);
                if (list.length() + next.length() > 2048) {
                    eb.setFooter(String.format("%d items hidden", tracks.size() - i));
                    break addStuff;
                }
                list.append(next);
            }
        }
        eb.setDescription(list.toString());
        ctx.getHook().sendMessageEmbeds(eb.build()).queue();
    }
}
