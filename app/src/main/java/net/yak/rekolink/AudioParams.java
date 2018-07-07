package net.yak.rekolink;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;

public abstract class AudioParams {

    public static final int SAMPLE_RATE = 8000; // 8000;
    public static final int FRAME_SIZE = SAMPLE_RATE / 25;

    public static final int ENCODING_PCM_NUM_BITS = AudioFormat.ENCODING_PCM_16BIT;

    public static final int STREAM_TYPE = AudioManager.STREAM_MUSIC;

    public static final int RECORD_BUFFER_SIZE = Math.max(
            SAMPLE_RATE,
            ceil(AudioRecord.getMinBufferSize(
                    SAMPLE_RATE,
                    AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    ENCODING_PCM_NUM_BITS)));
    public static final int TRACK_BUFFER_SIZE = Math.max(
            FRAME_SIZE,
            ceil(AudioTrack.getMinBufferSize(
                    SAMPLE_RATE,
                    AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    ENCODING_PCM_NUM_BITS)));

    private static int ceil(int size) {
        return (int) Math.ceil(((double) size / FRAME_SIZE)) * FRAME_SIZE;
    }
}
