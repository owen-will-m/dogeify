package com.suchapps.dogeify;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class DogeView extends TextView{
    public static final int MODE_TEXT = 1;//add text to the image
    public static final int MODE_CROP = 2;//crop a doge onto it
    public static final int WAS_DOGE = 420;
    public static final int WAS_TEXT = 123;
    
    int startUndoX;
    int startUndoY;//values pertaining to the undo button
    int endUndoX;
    int endUndoY;
    Stack<Integer> undoState;
    Stack<Integer> lastColor;
    private boolean rotate = false;
    private Bitmap undo;
    private Canvas dCanvas;
	private int mode;
	private Paint paint;
	private Context dContext;
	private float x=0;
	private InputStream bitmap;
	private float y=0;
	private String theText;
	private boolean drawIt;
	private ArrayList<text> texts;
	private boolean started;
	private int color;
	private float startx = 0;
	private float starty = 0;
	boolean drawLine = false;
	private Paint defaultPaint = new Paint();
	private boolean dogeHead = false;
	double lineLength = 0;
	private Bitmap newPic;
	int height;
	boolean customImage = false;
	Random random = new Random();
	int rotationCoefficient = 0;
	int wid;
	private ArrayList<DogeFace> heads = new ArrayList<DogeFace>();
	int hei;
	boolean showUndo = true;
	private Uri theImage;
	private Rect undoRect;
	int actionBarHeight;
	// Calculate ActionBar height
	Bitmap dogeDR;
	Bitmap dogeUR;
	Bitmap dogeDL;
	Bitmap dogeUL;
	Bitmap dogeface;
	
	Typeface comicSans; //add comic sans typeface
	
	public DogeView(Context context) {//constructor takes application context
		super(context);
		this.setDrawingCacheEnabled(true);
	    //this.setFocusable(true);
	   // this.setFocusableInTouchMode(true); IDK IF NECESSARY
	    
		
		TypedValue tv = new TypedValue();
		if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
		{
		    actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
		}
		
		//MUST DECLARE THE FOUR DOGE HEADS!
		
		undoState = new Stack<Integer>();//genius! essentially it holds the current state of the most recent movement i.e. whether the last was head or text
		lastColor = new Stack<Integer>();
		lastColor.push(Color.parseColor("#000000"));
		
	    WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
	    Display display = wm.getDefaultDisplay();
	    height = display.getHeight();

	    this.setHeight(height);//makes sure that the view wraps around the entire screen  
	    hei = height;
	    wid = display.getWidth();
	    //this.setBackgroundColor(Color.parseColor("#ff4444"));
	    //setting values for the view itself
	    //default paint values
	    defaultPaint.setColor(Color.BLACK);
		defaultPaint.setStyle(Style.FILL);
		defaultPaint.setStrokeWidth(3);
	    
		
	    //setting values for the paint
	    dCanvas = new Canvas();
		paint = new Paint();
		color = Color.BLACK;
		paint.setColor(color);
		paint.setStyle(Style.FILL);
		paint.setTextSize(30);
		dContext = context;
		comicSans = Typeface.createFromAsset(dContext.getAssets(), "LDFComicSans.ttf");

		paint.setTypeface(comicSans);
		try{
			InputStream inp = dContext.getAssets().open("dogeface.png");
			Bitmap bit = BitmapFactory.decodeStream(inp);
			dogeDL = bit;

			inp = dContext.getAssets().open("dogeDR.png");
			dogeDR = BitmapFactory.decodeStream(inp);
			
			
			inp = dContext.getAssets().open("dogeUR.png");
			dogeUR = BitmapFactory.decodeStream(inp);
			
			
			inp = dContext.getAssets().open("dogeUL.png");
			dogeUL = BitmapFactory.decodeStream(inp);
			
			inp = dContext.getAssets().open("undo.png");
			undo = BitmapFactory.decodeStream(inp);
			
			inp = dContext.getAssets().open("dogefull.png");
			dogeface =BitmapFactory.decodeStream(inp);
			
			
			
			}catch(Exception e){
				
			}
		
		drawIt=false;//if true then add text to arraylist
		
		started = false;//if false then we initialize a bunch of stuff down there
		
		this.draw(dCanvas);


		
		
		
	}


	
	
	
	
	
	
	
	@Override
	public void onDraw(Canvas canvas) {
	       drawDoge(canvas);//draws the doge image to the background or the selected image
	       
	       
	       if(dogeHead){//this means draw a bitmap of the doge head onto the canvas with the specified length and width!
	    	   for(DogeFace df: heads){//draw all the doges 
	    		   drawDogeHead(canvas, df);  
	    	   }
	       }
	       
	  
	       
	       
	       //this segment will add the text to the image dictated by the setText method
	       paint.setColor(color);
		if(!started){
			texts = new ArrayList<text>();
			text q = new text("",0,0,defaultPaint);
			texts.add(q);
			started = false;
		}
		
		

        if(drawIt){//called after it's pressed, will add a new text to arraylist	
        	int tempColor = randomColor();
        	if(tempColor==lastColor.peek()){
        	paint.setColor(randomColor(tempColor));//if it was the same color again, the try again (this lowers odds of repeating consecutive color 
        	}
            texts.add(new text(theText,x,y,newPaint())); 	
            undoState.push(WAS_TEXT);
        	drawIt =false;
        	lastColor.push(tempColor);
        }    
        
        
        
        
        //draw entire arraylist of the things
        for(text t:texts){
        	t.drawText(canvas);
        }
        
        
	       
	       
	       if(drawLine){//if you want me to draw a line, then do it!
	    	   canvas.drawLine(startx,starty,x,y, defaultPaint);
	    	   lineLength = Math.sqrt((startx-x)*(startx-x)+(starty-y)*(starty-y));//gets the distance of the line! MATHHH

	       }
        
        
        //because it is at the end of the method, the undo button must be drawn on top of everything else!
        startUndoX = this.getWidth()-(this.getWidth()/8);
        startUndoY = this.getHeight()-(this.getWidth()/8);
        endUndoX = this.getWidth();
        endUndoY = this.getHeight();
        if(showUndo){//public variable: allows activity to hide this button for save or whatever
        canvas.drawBitmap(undo,null,new Rect(startUndoX,startUndoY,endUndoX,endUndoY),defaultPaint);
        }
    }

	
	
	
	
	
	
	
	
	
	


	private int randomColor(int tempColor) {//this method returns a new color that is guaranteed to be different from the last
		int rando = tempColor;
		while(rando==tempColor){
			rando = randomColor();	
		}
		return rando;
	}









	private Paint newPaint() {
		Paint p;
		p = new Paint();
		p.setColor(randomColor());
		p.setStyle(Style.FILL);
		p.setTextSize(randomSize());
		p.setTypeface(comicSans);
		return p;
	}









	private float randomSize() {
	
		int h = random.nextInt(50)+(this.getHeight()/22);
		
		return (float)h;
	}









	public void addText(String string) {//called when the user presses the text button
		theText = string;
        drawIt = true;
        started = true;
        invalidate();
	}
	
	
	private int randomColor() {
      int rand = random.nextInt(12) + 1;//random integer between 1 and 10
      switch (rand){
      case 1: return Color.parseColor("#9933CC");
      case 2:return Color.parseColor("#ffffff");
      case 3:return Color.parseColor("#0099CC");
      case 4:return Color.parseColor("#33B5E5");
      case 5:return Color.parseColor("#FF8800");
      case 6: return Color.parseColor("#FFBB33");
      case 7:return Color.parseColor("#99CC00");
      case 8:return Color.parseColor("#669900");
      case 9:return Color.parseColor("#AA66CC");
      case 10:return Color.parseColor("#FF4444");
      case 11: return Color.parseColor("#CC0000");
      }
		return Color.parseColor("#ffffff");
	}









	public void drawDoge(Canvas c){//draws the doge photo onto the screen
		Bitmap originalImage;
		try{
			if(!customImage){
			 originalImage=dogeface;	 
			}else{
				originalImage = newPic;		    
			}
			//alright so it will set the image
			int trueW = originalImage.getWidth();
			int trueH = originalImage.getHeight();
			

			if(rotate){//ondraw method has to do all of this!!? not want
				
				 Matrix matrix = new Matrix();
				 matrix.postRotate(90*rotationCoefficient);

				 Bitmap rotated = Bitmap.createBitmap(originalImage, 0, 0, originalImage.getWidth(), originalImage.getHeight(),
				         matrix, true);

				 originalImage = rotated;
				 }
		
			RectF defaultRect = new RectF(0, 0, originalImage.getWidth(), originalImage.getHeight());
			RectF screenRect = new RectF(0, 0, this.getWidth(), this.getHeight());
			
			Matrix defToScreenMatrix = new Matrix();
			defToScreenMatrix.setRectToRect(defaultRect, screenRect, Matrix.ScaleToFit.CENTER);//
			//use width and height to calculate what the height is!
			if(rotationCoefficient%2!=0)
            this.setHeight((int)(
            		(trueW*wid) / trueH
            		
            		));
			else
		    this.setHeight(height);	
			invalidate();
		
			c.drawBitmap(originalImage, defToScreenMatrix, paint);
			
		}catch(Exception e){
			Toast.makeText(dContext, e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}
	


	public void drawDogeHead(Canvas c){//am i even using this one? no
		InputStream inp;
		try{
		inp = dContext.getAssets().open("dogeface.png");
		Bitmap bit = BitmapFactory.decodeStream(inp);
		c.drawBitmap(bit,null,new Rect((int)startx,(int)starty,(int)x,(int)y),defaultPaint);
		}catch(Exception e){
			Toast.makeText(dContext, e.getMessage(), Toast.LENGTH_SHORT).show();
		}
		
	}
	
	//here, on other devices, the method drawbitmap does not work when going up or left.
		//because of this, i must make an updoge, leftdoge , upleftdoge
		
		

	public void drawDogeHead(Canvas c, DogeFace df){
    float m = //the slope of the line
    		(df.y2-df.y1) / (df.x2-df.x1);
   // Toast.makeText(dContext,"Slope: "+ m,Toast.LENGTH_SHORT).show();
    m = Math.abs(m);//make sure it's positive
		if(m<5){//the slope of the line is not greater than a certain point.
		
		
		if(df.x1<df.x2&&df.y1<df.y2)//DR
			c.drawBitmap(dogeDR,null,new Rect(df.x1,df.y1,df.x2,df.y2),defaultPaint);
		if(df.x1>df.x2&&df.y1<df.y2)//DL
			c.drawBitmap(dogeDL,null,new Rect(df.x2,df.y1,df.x1,df.y2),defaultPaint);
		if(df.x2<df.x1&&df.y2<df.y1)//UL
			c.drawBitmap(dogeUL,null,new Rect(df.x2,df.y2,df.x1,df.y1),defaultPaint);
			
		if(df.x1<df.x2&&df.y1>df.y2)//UR	
		c.drawBitmap(dogeUR,null,new Rect(df.x1,df.y2,df.x2,df.y1),defaultPaint);
		
		}
	}
	
	
	
	
	









	public void setColor(int color) {//setter that changes the color to something
    this.color = color;	
	}

	public int getMode() {
		return mode;
	}
	
	public float getItsX(){
		return x;
	}
	public float getItsY(){
		return y;
	}
	public void setItsX(float x){
		this.x=x;
	}
	public void setItsY(float y){
		this.y=y;
	}

	public void startLine() {//this method intends to start drawing a line from startx and starty
	startx = x;
	starty = y;	
	drawLine = true;
	}



	public void setMode(int mode) {
		this.mode = mode;	
	}



	public void endLine() {//this method intends to stop drawing the line and paste an image of doge head with that specs
   drawLine=false;//stop drawing the line!
   dogeHead = true;
   heads.add(new DogeFace((int)startx,(int)x,(int)starty,(int)y));
   undoState.push(WAS_DOGE);
	}



	public void setImage(Uri theImage) {

		heads = new ArrayList<DogeFace>();
		texts = new ArrayList<text>();
		customImage = true;	
		try{
		newPic = MediaStore.Images.Media.getBitmap(dContext.getContentResolver(), theImage);
		}catch(Exception e){
			Toast.makeText(dContext, e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	    undoState = new Stack<Integer>();
		invalidate();
	}
	
	
	
	class text {//class that holds a string value, coords, and a paint value.
		float x;
		float y;
		String s;
		Paint p;
		
		public text(String s, float x, float y, Paint p){
			this.s=s;
			this.x=x;
			this.y=y;
			this.p=p;
		}
		public void drawText(Canvas canvas){
			canvas.drawText(s, x, y, p);		
		}
		
	}
	class DogeFace{
		int x1 ,y1 , x2, y2;
		public DogeFace(int x1,int x2,int y1,int y2){
			this.x1=x1;
			this.x2=x2;
			this.y1=y1;
			this.y2=y2;
		}
		 
		
		
	}
	public void setImage(Bitmap imageBitmap) {
		// TODO Auto-generated method stub
		heads = new ArrayList<DogeFace>();
		texts = new ArrayList<text>();
		undoState = new Stack<Integer>();
		customImage = true;	
		newPic = imageBitmap;
		invalidate();
	}









	public void clear() {
		texts = new ArrayList<text>();
		heads = new ArrayList<DogeFace>();
		undoState = new Stack<Integer>();
		invalidate();
		
	}









	public void renew() {
		customImage = false;
		clear();	
		undoState = new Stack<Integer>();
	}









	public void rotate() {
		
		rotationCoefficient++;
		rotate =true;
		invalidate();
		
	}

	public void undo() {
		if(!undoState.empty()){//if theres still something there
			int theState = undoState.pop();
			if(theState==WAS_DOGE){
				heads.remove(heads.size()-1);
			}else if(theState==WAS_TEXT){
				texts.remove(texts.size()-1);			
			}
		}
		invalidate();
	}

}
