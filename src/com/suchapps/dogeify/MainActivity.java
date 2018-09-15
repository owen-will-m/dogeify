package com.suchapps.dogeify;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.ImageColumns;
import android.provider.MediaStore.Images.Media;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ClipData.Item;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;

public class MainActivity extends Activity {
	private static final int REQUEST_IMAGE_CAPTURE = 1;
	private static final int MEDIA_TYPE_IMAGE = 2;
	public static final int CAPTURE_IMAGE_FULLSIZE_ACTIVITY_REQUEST_CODE = 1777; 


DogeView dv;
AlertDialog ad;
AlertDialog.Builder adb;
RelativeLayout ll;
EditText input;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//link all dem to the ids 
		setContentView(R.layout.activity_main);
	    adb = new AlertDialog.Builder(this);
	    
	    
		//make action bar reddish
		ActionBar ab = getActionBar();
		ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#cc0000")));
        
		//link all of the widget
	    ll = (RelativeLayout) findViewById(R.id.linearlayout1);
		
		
		//declare a new dogeview and then add it
		dv = new DogeView(this);
		
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		//lp.setMargins(0,dv.actionBarHeight,0,0);

		ll.addView(dv,lp);
		
        dv.setOnTouchListener(new OnTouchListener(){//all of the touching goes to the activity, not the view  	
			@Override
			public boolean onTouch(View arg0, MotionEvent me) {
				dv.setItsX(me.getX());//update the x and y values!
            	dv.setItsY(me.getY());
            	
            	//if it's up AND IT'S ON THE UNDO BUTTON!!
            	if(MotionEvent.ACTION_UP==me.getAction()&&me.getX()>dv.startUndoX && me.getY()>dv.startUndoY){
         dv.drawLine=false;

            		dv.undo();
       		
            	}else{//if its not in the undo button then dont even do anything else.. like srsly
            	
            if(dv.getMode()==DogeView.MODE_TEXT&&me.getAction()==MotionEvent.ACTION_UP || me.getAction()==MotionEvent.ACTION_UP&&dv.getMode()==DogeView.MODE_CROP&&dv.lineLength<=25){//if i'm supposed to draw anything && end of an action
            	ad.show(); 
            	input.requestFocus();
            	dv.drawLine=false;
            }
            if(me.getAction()==MotionEvent.ACTION_DOWN){//the user has pressed down, so start drawing the crop line
            	dv.startLine();//this will start drawing the line
            }
            if(me.getAction()==MotionEvent.ACTION_MOVE){//this means that it should start drawing the line
            	dv.setMode(DogeView.MODE_CROP);//MAKE SURE THIS TURNS OFF AFTER IT HAS BEEN CROPPED!!!
            }
            if(me.getAction()==MotionEvent.ACTION_UP&&dv.getMode()==DogeView.MODE_CROP&&dv.lineLength>25){//if action ends and the doge is cropped, then
            	dv.endLine();
            	dv.setMode(DogeView.MODE_TEXT);
            }
            	}
		  
				dv.invalidate();//makes sure to redraw it
				return true;
			}
            	
        });
		
		dv.setMode(DogeView.MODE_TEXT);//which means that the user inputs text WILL BE CHANGED LATER
		dv.requestFocus(View.FOCUS_DOWN);//takes focus awway from edittext

		
		
		//create the alertdialogbuilder yessss
		input = new EditText(this);
		input.setOnFocusChangeListener(new View.OnFocusChangeListener() {//this basically means when it's focused open keyboard
		    @Override
		    public void onFocusChange(View v, boolean hasFocus) {
		        if (hasFocus) {
		            ad.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		        }
		    }
		});
		input.setFocusableInTouchMode(true);
		input.setFocusable(true);
		input.setHint("such character");
	    adb.setView(input);
	    adb.setPositiveButton("much enter",new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {//if you selected yes, then add the text!
				dv.addText(input.getText().toString());
				input.setText("");
			}
		});
	    adb.setNegativeButton("noep",new DialogInterface.OnClickListener() {		
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				input.setText("");
            arg0.cancel();
			}
		});
	    adb.setTitle("very text");
		ad = adb.create();
		//all specifications met for ALERT DIALOG BUILDER!!! AND ITS CREATED TOO
		
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);	
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {DAMMIT
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	    
	    case R.id.rotate://rotates the image dv
	    
	    dv.rotate();
	    
	    break;
	    
	    case R.id.save:
        //first save to external cache, then with that temp file, do media scan

	    	
	    	
	    	
	    	dv.showUndo=false;
	    	dv.invalidate();//make sure there's no undo button visible there
        Bitmap image = dv.getDrawingCache();
	    	dv.showUndo=true;
	    	dv.invalidate();//then put it back!
	    	//removeSideColor(Color.parseColor("ff4444"),image);
	    	
	    	File text = new File(Environment.getExternalStorageDirectory()+"/doges");
	    	if(text.mkdirs()){
	    		Toast.makeText(getApplicationContext(),"new folder created",Toast.LENGTH_LONG).show();
	    	}
	    	
        try{	//this saves the file
        	int i = 0;
    		File f = new File(Environment.getExternalStorageDirectory()+"/doges"+"/doge0.png");

        	while(f.exists()){
        		f = new File(Environment.getExternalStorageDirectory()+"/doges"+"/doge"+i+".png");
        		i++;
        	}
			FileOutputStream fos = new FileOutputStream(f);
			image.compress(Bitmap.CompressFormat.PNG, 100, fos);

			Toast.makeText(getApplicationContext(),"Saved as: "+f.getPath(),Toast.LENGTH_LONG).show();
		}catch(Exception e){
			Log.d("NO WORK", "NOT SAVE"+e.getMessage());
		}
	    	  	
        
        
        
        
        
        
	    break;
	    case R.id.share:
//sort of completely jacked this kinda  	

	    	
	    	
	    	dv.showUndo=false;
	    	dv.invalidate();//make sure there's no undo button visible there
        Bitmap icon = dv.getDrawingCache();
	    	dv.showUndo=true;
	    	dv.invalidate();//then put it back!
	    	
	    	
	    	Intent share = new Intent(Intent.ACTION_SEND);//ok this intent will share
	    	share.setType("image/png");//it's an image btw.

	    	ContentValues values = new ContentValues();
	    	values.put(Images.Media.TITLE, "dogeified");//aight so its named  dat
	    	values.put(Images.Media.MIME_TYPE, "image/png");//and its an image!
	    	
	    	
	    	Uri uri = getContentResolver().insert(Media.EXTERNAL_CONTENT_URI,//not sure, but like this makes a uri
	    	        values);


	    	OutputStream outstream;//ok
	    	try {
	    	    outstream = getContentResolver().openOutputStream(uri);
	    	    icon.compress(Bitmap.CompressFormat.PNG, 100, outstream);
	    	    outstream.close();
	    	} catch (Exception e) {
	    	    Log.d("io prob",e.getMessage());
	    	}

	    	share.putExtra(Intent.EXTRA_STREAM, uri);//kk then share it
	    	startActivity(Intent.createChooser(share, "Share Image"));
	    	
	    	
	    	
	    	
	    	
	    break;
	    case R.id.select:	    	Intent dIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
	    	startActivityForResult(dIntent, 420);
	    	break;
	    	
	    case R.id.takepic://take a picture!
	    	Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
	    	File file = new File(Environment.getExternalStorageDirectory()+File.separator + "image.jpg");
	    	intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
	    	startActivityForResult(intent, CAPTURE_IMAGE_FULLSIZE_ACTIVITY_REQUEST_CODE);

	        break;  
	    case R.id.newdoge :
	    	dv.renew();
	    	
	    	
	    	break;
	    case R.id.clear:
	    	dv.clear();
	    	
	    	break;
	    }
	    return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onActivityResult(int request, int result, Intent intent ){
		super.onActivityResult(request, result, intent);
		if (request == CAPTURE_IMAGE_FULLSIZE_ACTIVITY_REQUEST_CODE && result == RESULT_OK) {
			File file = new File(Environment.getExternalStorageDirectory()+File.separator + "image.jpg");
		       Bitmap bitmap = decodeSampledBitmapFromFile(file.getAbsolutePath(), dv.getWidth(), dv.getHeight());
		       dv.setImage(bitmap);
	    }
		if(result==Activity.RESULT_OK&&request==420){
			Uri theImage = intent.getData();
			dv.setImage(theImage);
		}
		
	}
	public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight) 
	{ // BEST QUALITY MATCH
	     
	    //First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(path, options);
	 
	    // Calculate inSampleSize, Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    options.inPreferredConfig = Bitmap.Config.RGB_565;
	    int inSampleSize = 1;
	 
	    if (height > reqHeight) 
	    {
	        inSampleSize = Math.round((float)height / (float)reqHeight);
	    }
	    int expectedWidth = width / inSampleSize;
	 
	    if (expectedWidth > reqWidth) 
	    {
	        //if(Math.round((float)width / (float)reqWidth) > inSampleSize) // If bigger SampSize..
	        inSampleSize = Math.round((float)width / (float)reqWidth);
	    }
	 
	    options.inSampleSize = inSampleSize;
	 
	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	 
	    return BitmapFactory.decodeFile(path, options);
	}
	
	
	
	private Bitmap removeSideColor(int color, Bitmap bit)//MUST FIX DIS YO
	{//this method removes a certain color from the sides of a bitmap
      
		int topMargin=0;
		int bottomMargin=bit.getHeight();
		int leftMargin=0;
		int rightMargin=bit.getWidth();
		
		//ok make 4 starting points (top bottom left right)
		int topX = bit.getWidth()/2;//this is also bottomX
		int botY = bit.getHeight();
		int rightX = bit.getWidth();
		int leftX = 0;
		int leftY = bit.getHeight()/2;//this is also leftY
		
		// 4 for loops that find the margins 
		//top loop
		int y = 0;
		boolean isColor=false;
		do{
			
			if(bit.getPixel(topX, y)==color){
				isColor = true;
			}else{
				isColor = false;
				topMargin = y;
			}	
			y++;
		}while(isColor);
		//bottom loop
		y=botY;
        do{
			
			if(bit.getPixel(topX, y)==color){
				isColor = true;
			}else{
				isColor = false;
				bottomMargin = y;
			}	
			y--;
		}while(isColor);
		//right loop
        int x = rightX;
        do{
			
			if(bit.getPixel(x, leftY)==color){
				isColor = true;
			}else{
				isColor = false;
				rightMargin = x;
			}	
			x--;
		}while(isColor);
        //left loop
        x=0;
        do{
			
			if(bit.getPixel(x, leftY)==color){
				isColor = true;
			}else{
				isColor = false;
				leftMargin = x;
			}	
			x++;
		}while(isColor);
        
        //so now i have the distances in x and y values from 
		/*
	    Bitmap bmOverlay = Bitmap.createBitmap(700, 1000, Bitmap.Config.ARGB_8888);

	    Paint p = new Paint();
	    p.setXfermode(new PorterDuffXfermode(Mode.CLEAR));              
	    Canvas c = new Canvas(bmOverlay); 
	    c.drawBitmap(bit, 0, 0, null); 
	    c.drawRect(leftMargin, topMargin, rightMargin, bottomMargin, p);

	    return bmOverlay;
	    */
        Toast.makeText(getApplicationContext(),"Top:" + topMargin, Toast.LENGTH_SHORT).show();
        return null;
	}
	
	
	
	
	
	
	
	

}
