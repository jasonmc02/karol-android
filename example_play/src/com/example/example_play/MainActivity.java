package com.example.example_play;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
	Button button1, button2;
	MediaPlayer mp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		button1 = (Button)findViewById(R.id.button1);
		button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSound();
            }
        });
		
		button2 = (Button)findViewById(R.id.button2);
		button2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				stopSound();
			}
		});
		
		// mas referencia y 
		// para ver como usar uris locales del dispositivo o con streaming desde http
		// http://developer.android.com/guide/topics/media/mediaplayer.html
	}

	public void playSound() {
        stopSound();
        mp = MediaPlayer.create(this, R.raw.icon);
        mp.start();
	}
	
	public void stopSound() {
		if (mp != null) {
            //mp.reset();
            mp.release();
        }
	}

}
