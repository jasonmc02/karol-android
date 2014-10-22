package com.example.example_form;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.os.Build;

public class MainActivity extends Activity {
	
	TextView tv;
	Button bt;
	EditText et;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		bt = (Button)findViewById(R.id.button1);
		tv = (TextView)findViewById(R.id.textView1);
		et = (EditText)findViewById(R.id.editText1);

		bt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String message = et.getText().toString();
				tv.setText(message);
			}
		});
	}
}
