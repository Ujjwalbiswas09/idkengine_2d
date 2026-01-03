package engine.core;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

public class WaveFileParser {
    private AudioTrack track;
    private boolean isMono;
    public void parse(InputStream ins) throws Exception {
        DataInputStream dis = new DataInputStream(ins);
    }

    
}
