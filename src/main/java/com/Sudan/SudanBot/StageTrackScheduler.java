package com.Sudan.SudanBot;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.concurrent.ThreadLocalRandom;

public class StageTrackScheduler extends TrackScheduler {
    AudioTrack[] playlist;

    public StageTrackScheduler(AudioPlayer player, AudioPlaylist playlist) {
        super(player);
        this.playlist = playlist.getTracks().toArray(new AudioTrack[0]);
    }

    @Override
    public void nextTrack() {
        AudioTrack track = queue.poll();
        track = track != null ? track : lowQueue.poll();
        player.startTrack(track != null ? track : playlist[ThreadLocalRandom.current().nextInt(playlist.length)].makeClone(), false);
    }
}
