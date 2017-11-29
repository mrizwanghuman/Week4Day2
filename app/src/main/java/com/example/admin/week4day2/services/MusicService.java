package com.example.admin.week4day2.services;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;

import com.example.admin.week4day2.Song;

import java.io.IOException;
import java.util.ArrayList;

public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {
    private final IBinder musicBind = new MusicBinder();
    //media player
    private MediaPlayer player;
    //song list
    private ArrayList<Song> songs;
    //current position
    private int songPosn;

    public MusicService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        songPosn =0;
        player= new MediaPlayer();
        initMusicPlayer();
    }
public void initMusicPlayer(){
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
}
public  void setList(ArrayList<Song> theSongs){
    songs = theSongs;
}
public class MusicBinder extends Binder{
    public MusicService getService(){
        return MusicService.this;
    }
}
public  void playSong(){
    player.reset();
    Song playSong = songs.get(songPosn);
    long currSong = playSong.getId();
    Uri trackuri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currSong);
    try {
        player.setDataSource(getApplicationContext(),trackuri);
    } catch (IOException e) {
        e.printStackTrace();
    }
    player.prepareAsync();
}
    @Override
    public IBinder onBind(Intent intent) {
    return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
    player.stop();
    player.release();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
mediaPlayer.start();
    }
    public void setSong(int sondIndex){
    songPosn = sondIndex;
    }
}
