package com.Sudan.SudanBot;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;

public class GuildMusicManager {
    public final AudioPlayer audioPlayer;
    public final TrackScheduler scheduler;
    private final AudioPlayerSendHandler sendHandler;

    public GuildMusicManager(AudioPlayerManager manager) {
        audioPlayer = manager.createPlayer();
        scheduler = new TrackScheduler(audioPlayer);
        audioPlayer.addListener(scheduler);
        sendHandler = new AudioPlayerSendHandler(audioPlayer);
    }

    public GuildMusicManager(AudioPlayerManager manager, AudioPlaylist playlist) {
        audioPlayer = manager.createPlayer();
        scheduler = new StageTrackScheduler(audioPlayer, playlist);
        audioPlayer.addListener(scheduler);
        sendHandler = new AudioPlayerSendHandler(audioPlayer);
    }

    public AudioPlayerSendHandler getSendHandler() {
        return sendHandler;
    }
}
