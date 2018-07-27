package com.test.PathFind;

import java.util.ArrayList;
import java.util.HashMap;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import android.util.Log;

public class GameView extends View {
	
	private static final int VIEW_WIDTH = 320;//width of the view
	private static final int VIEW_HEIGHT = 340;//height of the view
	
	// touch position
    int mPosX = 0;
    int mPosY = 0;
	
    // init
	Game game;
	Spinner searchMethod;
	TextView pathLengthTextView;
	int space = 13;
	
	int[][] map;
	int row;
	int col;
	
	Bitmap source = BitmapFactory.decodeResource(getResources(), R.drawable.source);
	Bitmap target = BitmapFactory.decodeResource(getResources(), R.drawable.target);
	
	Paint gamePaint = new Paint();
	
	//update the component of UI thread
	private Handler myHandler = new Handler(){
        public void handleMessage(Message msg) {
		//change the length of TextView
        	if(msg.what == 0) { 
        		pathLengthTextView.setText("Length of Path: " + (Integer)msg.obj);
        	}
        }
	};	
	
	// constructor
	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	// override onDraw
	protected void onDraw(Canvas canvas) {
		try {
			// call onMyDraw
			onMyDraw(canvas);
		}
		catch(Exception e){}
	}
	
	// Draw
	protected void onMyDraw(Canvas canvas) {
		//Log.d("yaduo","onMyDraw");
		//super.draw(canvas);
		canvas.drawColor(Color.GRAY); // background colour: Gray		
		gamePaint.setColor(Color.BLACK); // set the colour
		gamePaint.setStyle(Style.STROKE); // set the style
		// paint a rectangle as the frame
		// canvas.drawRect(left, top, right, bottom, paint) 
		canvas.drawRect(5, 5, 313, 327, gamePaint);
		
		// -------------------------- 绘制地图  --------------------------
		map = game.map;
		row = map.length;
		col = map[0].length;
		// paint the map blocks
		for(int i=0; i<row; i++){
			for(int j=0; j<col; j++){
				// road
				if(map[i][j] == 0){
					// Color: white
					gamePaint.setColor(Color.WHITE);
					// set the paint style as fill up
					gamePaint.setStyle(Style.FILL);
					//Log.d("yaduo", "map("+String.valueOf(i)+","+String.valueOf(j)+")");
					//Log.d("yaduo", "pos1("+String.valueOf(6+j*(space+1))+","+String.valueOf(6+i*(space+1))+")");
					//Log.d("yaduo", "pos2("+String.valueOf(6+j*(space+1)+space)+","+String.valueOf(6+i*(space+1)+space)+")");
					canvas.drawRect(6+j*(space+1), 6+i*(space+1), 6+j*(space+1)+space, 6+i*(space+1)+space, gamePaint);
				}
				// wall
				else if(map[i][j] == 1){
					// black
					gamePaint.setColor(Color.BLACK);
					gamePaint.setStyle(Style.FILL);
					canvas.drawRect(6+j*(space+1), 6+i*(space+1), 6+j*(space+1)+space, 6+i*(space+1)+space, gamePaint);					
				}
			}
		}
		
		// -------------------------- process of painting line --------------------------
		// paint the procedure of path finding
		// paint a line 
		ArrayList<int[][]> searchProcess = game.searchProcess;

		for(int k=0; k<searchProcess.size(); k++)
		{
			int[][] edge = searchProcess.get(k);
			// set colour: black
			gamePaint.setColor(Color.BLUE);
			gamePaint.setStrokeWidth(1);
			// draw line
			canvas.drawLine (
				edge[0][0]*(space+1)+space/2+6,edge[0][1]*(space+1)+space/2+6,
				edge[1][0]*(space+1)+space/2+6,edge[1][1]*(space+1)+space/2+6,
				gamePaint
			);
		}
		
		// ------------------------- painting the shortest path ------------------------------ 
		// paint the result of BFS, DFS, A*
		if(searchMethod.getSelectedItemId()==0 || searchMethod.getSelectedItemId()==1|| searchMethod.getSelectedItemId()==2){
			// for "Depth-first","breadth-first","A*" 
			if(game.pathFlag){
				// reference the HashMap with game.hm
				HashMap<String,int[][]> hm = game.hm;	
				// init starting point
				// reference the coordinate
				int[] startPoint = game.target;
				// init counter for path length
				int pathCount = 0;
				// while from end to start
				while(true){
					// ------------------------ draw the shortest path in red -------------------------
					/*
					 * tempA: the path of tempSource to parent node
					 * return： {[startPointX startPointY], [parentX parentY]} 
					 */
					int[][] tempA = hm.get(startPoint[0]+":"+startPoint[1]);
					// set line property
					gamePaint.setColor(Color.RED);
					gamePaint.setStyle(Style.STROKE);	// Bold
					gamePaint.setStrokeWidth(3);		//Set the brush thickness to 2px
					// 画线
					canvas.drawLine(	
						tempA[0][0]*(space+1)+space/2+6, tempA[0][1]*(space+1)+space/2+6,
						tempA[1][0]*(space+1)+space/2+6, tempA[1][1]*(space+1)+space/2+6, 
						gamePaint
					);
					pathCount++;
					
					// -------------------Determine whether to starting point---------------
					/*
					 * if: draw to the starting point, if the parent point of startPoint is origenal point, while loop end
					 * tempA[1][0] : parent x
					 * tempA[1][1] : parent y
					 */
					if(tempA[1][0]==game.source[0] && tempA[1][1]==game.source[1]){
						break;
					}
					
					/*
					 * else：not origenal point ，while loop 
					 * set the next startPoint
					 */
					startPoint = tempA[1];			
				}
				//change the TextView
				Message updateMsg = myHandler.obtainMessage(0, pathCount);
				myHandler.sendMessage(updateMsg);
			}			
		}
		// painting Dijkstra
		else if( searchMethod.getSelectedItemId()==3 || searchMethod.getSelectedItemId()==4){
			// not implement yet !!
			// not implement yet !!
			// not implement yet !!
			// not implement yet !!
		}
		
		// --------------------------------- draw the shortest path --------------------------------
		// 绘制起点和终点
		canvas.drawBitmap(source, 6+game.source[0]*(space+1), 6+game.source[1]*(space+1), gamePaint);
		canvas.drawBitmap(target, 6+game.target[0]*(space+1), 6+game.target[1]*(space+1), gamePaint);
	}
	
	// return the size of the view
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(VIEW_WIDTH,VIEW_HEIGHT);
    }

	@Override  
	public boolean onTouchEvent(MotionEvent event) {  
		int action = event.getAction();
        mPosX = (int) event.getX();
        mPosY = (int) event.getY();
        switch (action) {
        // touch down
        case MotionEvent.ACTION_DOWN:
            Log.d("yaduo", "ACTION_DOWN");
            drawMap(mPosX,mPosY);
            break;
         // touch move
        case MotionEvent.ACTION_MOVE:
            Log.d("yaduo", "ACTION_MOVE");
            drawMap(mPosX,mPosY);
            break;
         // touch up
        /*
        case MotionEvent.ACTION_UP:
           Log.d("yaduo", "ACTION_UP");
           resetMap();
           break;
        */
        }
        
        //refresh UI
        postInvalidate();
        
        return true; 
	}  
	
	public boolean drawMap(int posX, int posY)
	{
		//Log.d("yaduo", "touch at pos("+String.valueOf(posX)+","+String.valueOf(posY)+")");
		int leftTopX, leftTopY, rightBottomX, rightBottomY;
		for(int i=0; i<row; i++) {
			for(int j=0; j<col; j++) {
				leftTopX = 6+j*(space+1);
				leftTopY = 6+i*(space+1);
				rightBottomX = 6+j*(space+1)+space;
				rightBottomY = 6+i*(space+1)+space;		
				if((rightBottomX>=posX && posX>leftTopX) && (rightBottomY>=posY && posY>leftTopY))
				{
					Log.d("yaduo", "map("+String.valueOf(i)+","+String.valueOf(j)+")");
					//Log.d("yaduo", "leftTop("+String.valueOf(leftTopX)+","+String.valueOf(leftTopY)+")");
					//Log.d("yaduo", "rightBottom("+String.valueOf(rightBottomX)+","+String.valueOf(rightBottomY)+")");
					map[i][j] = 1;
				}
			}
		}
		return true;
	}
	
	public void resetMap()
	{
		for(int i=0; i<row; i++){
			for(int j=0; j<col; j++){
				map[i][j] = 0;
			}
		}
	};
}