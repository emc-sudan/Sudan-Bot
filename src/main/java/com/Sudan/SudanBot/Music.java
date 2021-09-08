package com.Sudan.SudanBot;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

public class Music {
    public static AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
    public static void join(Guild guild, VoiceChannel channel) {
        AudioManager manager = guild.getAudioManager();
        manager.setSendingHandler(new AudioPlayerSendHandler(playerManager.createPlayer()));
        manager.openAudioConnection(channel);
    }
}
