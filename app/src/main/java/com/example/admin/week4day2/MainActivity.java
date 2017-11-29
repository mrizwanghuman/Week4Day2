package com.example.admin.week4day2;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.admin.week4day2.services.MusicService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {
    private MusicService musicSrv;
    private Intent playIntent;
    private boolean musicBound=false;
    private AssetFileDescriptor afd;
    private MediaPlayer player;
    private ArrayList<Song> songList;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.song_list);
        songList = new ArrayList<>();
        Collections.sort(songList, new Comparator<Song>(){
            public int compare(Song a, Song b){
                return a.getTitle().compareTo(b.getTitle());
            }
        });

        SongAdapter songAdt = new SongAdapter(this, songList);
        listView.setAdapter(songAdt);
getSongList();

}
public  void getSongList(){
    ContentResolver musicResolver = getContentResolver();
    Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
    if(musicCursor!=null && musicCursor.moveToFirst()){
        //get columns
        int titleColumn = musicCursor.getColumnIndex
                (android.provider.MediaStore.Audio.Media.TITLE);
        int idColumn = musicCursor.getColumnIndex
                (android.provider.MediaStore.Audio.Media._ID);
        int artistColumn = musicCursor.getColumnIndex
                (android.provider.MediaStore.Audio.Media.ARTIST);
        //add songs to list
        do {
            long thisId = musicCursor.getLong(idColumn);
            String thisTitle = musicCursor.getString(titleColumn);
            String thisArtist = musicCursor.getString(artistColumn);
            songList.add(new Song(thisId, thisTitle, thisArtist));
        }
        while (musicCursor.moveToNext());
    }
    }
private ServiceConnection musicServiceCon = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        MusicService.MusicBinder binder= (MusicService.MusicBinder) iBinder;
        musicSrv = binder.getService();
        musicSrv.setList((ArrayList<Song>) songList);
        musicBound= true;
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
musicBound= false;
    }
};

    @Override
    protected void onStart() {
        super.onStart();
        if(playIntent==null){
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicServiceCon, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }
    public void songPicked(View view){
        musicSrv.setSong(Integer.parseInt(view.getTag().toString()));
        musicSrv.playSong();
    }
}