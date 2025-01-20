package com.sn.blackdianqi.bean;

public class AudioEvent {

    public boolean audioState;

    public AudioEvent(boolean audioState) {
        this.audioState = audioState;
    }

    public boolean isAudioState() {
        return audioState;
    }

    public void setAudioState(boolean audioState) {
        this.audioState = audioState;
    }
}
