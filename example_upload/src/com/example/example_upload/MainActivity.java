package com.example.example_upload;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private TextView messageText;
    private Button uploadButton, btnselectpic;
    private ImageView imageview;
    private int serverResponseCode = 0;
    private ProgressDialog dialog = null;
       
    private String upLoadServerUri = null;
    private String imagepath=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		uploadButton = (Button)findViewById(R.id.uploadButton);
        btnselectpic = (Button)findViewById(R.id.button_selectpic);
        messageText  = (TextView)findViewById(R.id.messageText);
        imageview = (ImageView)findViewById(R.id.imageView_pic);
        
        btnselectpic.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		        //intent.setType("image/*");
		        
		        startActivityForResult(Intent.createChooser(intent, "Complete action using"), 1);
			}
		});
        uploadButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog = ProgressDialog.show(MainActivity.this, "", "Uploading file...", true);
	    		messageText.setText("uploading started.....");
	    		new Thread(new Runnable() {
	    			public void run() {
	    				new HttpAsyncTask().execute();
		            }
	    		}).start();
			}
		});
        upLoadServerUri = "http://192.168.0.111:3000/images/new_record";
        ImageView img= new ImageView(this);
	}
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	Toast.makeText(this, "" + requestCode, Toast.LENGTH_LONG).show();
    	Toast.makeText(this, "" + resultCode, Toast.LENGTH_LONG).show();
        if (requestCode == 1 && resultCode == RESULT_OK) {
        	try {
		        Uri selectedImage = data.getData();
		        imagepath = getPath(selectedImage);
		        
		        ParcelFileDescriptor parcelFD = getContentResolver().openFileDescriptor(selectedImage, "r");
	            FileDescriptor imageSource = parcelFD.getFileDescriptor();
	
	            BitmapFactory.Options o = new BitmapFactory.Options();
	            o.inJustDecodeBounds = true;
	            BitmapFactory.decodeFileDescriptor(imageSource, null, o);
	            
	        	BitmapFactory.Options o2 = new BitmapFactory.Options();
	            //o2.inSampleSize = scale;
	            Bitmap bitmap = BitmapFactory.decodeFileDescriptor(imageSource, null, o2);
	
	            imageview.setImageBitmap(bitmap);
	            messageText.setText("Uploading file path:" + imagepath);
        	} catch(FileNotFoundException a) {
        		a.printStackTrace();
        	}
        }
    }
    public String getPath(Uri uri) {
    	String wholeID = DocumentsContract.getDocumentId(uri);

    	// Split at colon, use second item in the array
    	String id = wholeID.split(":")[1];

    	String[] column = { MediaStore.Images.Media.DATA };     

    	// where id is equal to             
    	String sel = MediaStore.Images.Media._ID + "=?";

    	Cursor cursor = getContentResolver().
    	                          query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, 
    	                          column, sel, new String[]{ id }, null);

    	String filePath = "";
    	int columnIndex = cursor.getColumnIndex(column[0]);
    	if (cursor.moveToFirst()) {
    	    filePath = cursor.getString(columnIndex);
    	}
    	cursor.close();
    	
    	return filePath;
    }
    public void uploadFile() {
    	Bitmap bm = BitmapFactory.decodeFile(imagepath);
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();  
    	bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);  
    	byte[] bi = baos.toByteArray(); 
    	String imageStr = Base64.encodeToString(bi, Base64.NO_WRAP);
    	
    	// Create a new HttpClient and Post Header
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost(upLoadServerUri);
    	try {
	        // Add your data
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        nameValuePairs.add(new BasicNameValuePair("encoded_image", imageStr));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        dialog.dismiss();
    	} catch (ClientProtocolException a) {
    	} catch (IOException b) {
    	} catch (Exception c) {}
    }
    
    private class HttpAsyncTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void...voids) {
        	uploadFile();
            return "";
        }
    }
}
