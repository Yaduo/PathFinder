
package com.test.PathFind;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Stack;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;

// the framework of algorithm
public class Game {

	// Algorithms ID，"0": depth first as default 
	int algorithmId = 0;
	
	// ----------------------- Declare Map -----------------------------
	// Map ID, "0": the first map as default
	int mapId = 0;
	// class member "map[][][]", "source[]" and "target[][]" are static
	int[][] map = Map.map[mapId];
	// starting position
	int[] source = Map.source;
	// target position, 0 as default
	int[] target = Map.target[0];
	
	// ----------------- Declare view and Controls ---------------------
	// Declare GameView
	GameView gameView;
	// Declare Button
	Button goButton;
	Button cleanMapButton;
	Button cleanPathButton;
	// Declare TextView
	TextView StepsTextView;
	
	// ---------------- Declare Algorithm Components ---------------------
	/*
	 * hm.put(tempTarget[0]+":"+tempTarget[1],new int[][]{currentEdge[1],currentEdge[0]});
	 * key：String --  tempTarget[0]+":"+tempTarget[1]		例："2:2"
	 * value：int[][] -- {currentEdge[1],              		例：{[子节点X，子节点Y],          意义：父子间的path
	 *                    currentEdge[0]}               		 [父节点X，父节点Y]}
	 */
	// declare Search process 
	// store into an ArrayList
	ArrayList<int[][]> searchProcess = new ArrayList<int[][]>();
	// record the path result
	HashMap<String,int[][]> hm = new HashMap<String,int[][]>();

	// 8 Directions
	/*
	 * |(-1,-1)	(0,-1)	(1,-1)                         (0,0) |----------> x
	 * |                                                     |
	 * |(-1,0)    0		(1,0)                                | y
	 * |                                                     |
	 * |(-1,1)	(0,1)	(1,1)                                >
	 *  
	 */
	int[][] direction = {
		{0,1}, {0,-1},
		{-1,0}, {1,0},
		{-1,1}, {-1,-1},
		{1,-1}, {1,1}
	};
	
	// -------- closed list ----------
	// 1 visited, 0 not 
	int[][] visited = new int[Map.map[0].length][Map.map[0][0].length];
	
	// -------- open list ----------- 
	/*
	 * DFS, Depth First -- very slow!! 
	 * generally, we often use stack to store the DFS edge.
	 * because FIFO!!!
	*/	 
	Stack<int[][]> stack = new Stack<int[][]>();
	
	/*
	 * BFS, Breadth First -- very slow, but widely used!! 
	 * generally, we often use Queue(linkedlist) to store the DFS edge.
	 * because LIFO!!!
	*/	
	LinkedList<int[][]> queue = new LinkedList<int[][]>();
	
	/*
	 * A*, quick!!  
	 * generally, we often use PriorityQueue to store the A* edge.
	 * because PriorityQueue!!!
	 * PriorityQueue(int initialCapacity, Comparator<? super E> comparator)     
	 */	
	PriorityQueue<int[][]> astarQueue = new PriorityQueue<int[][]>(100,new AStarComparator(this));	//this == Game
	
	/*
	 * Dijkstra, quick!!  
	 * not yet implemented!
	*/
	//PriorityQueue<int[][]> astarQueue=new PriorityQueue<int[][]>(100,new AStarComparator(this));	

	
	// ------------------------- Game Logic (Algorithm) ---------------------------
	// run the algorithm
	public void runAlgorithm()
	{
		Log.d("yaduo", "Run Algorithm");
		clearState();
		switch(algorithmId)
		{		
			case 0: DFS(); break;
			case 1: BFS(); break;
			case 2: BFSAStar(); break;
			case 3: Dijkstra(); break;
		}		
	}
	
	// DFS: depth-first algorithm
	/*
	 * departure from a node (source), and going to another node (target)
	 * Keep going to the left until crash the wall
	 * then turn right, and keep right until crash the wall
	 * maybe someday, it will reach the target
	 * very very slow!!!!!
	 * Generally, we often use stack to store the DFS edge, Because FIFO!!!
	*/
	public void DFS(){
		new Thread(){
			public void run(){
				// step counter
				int count=0;
				
				// start list , put the source point into stack
				/* 
				 * starts[][] = [
				 * 		[2,2],
				 * 		[2,2],
				 * ]
				*/
				int[][] start = {
					{source[0], source[1]},
					{source[0], source[1]}
				};
				stack.push(start);			
				
				// loop to doing search
				while(true){
					// get edge from the top of the stack
					
					// currentEdge{(parent_x,parent_y),(current_x,current_y)}
					int[][] currentEdge = stack.pop();
					
					// tempTarget, next visit node，the second node (maybe turn left)
					int[] tempTarget = currentEdge[1];
					
					// -------------------- determine whether visited the point ------------------------- 
					/* 
					 * if this node visited, then continue to next while loop
					*/
					if(visited[tempTarget[1]][tempTarget[0]] == 1){
						continue;
					}
					
					/*
					 * else: tempTarget not yet visited, then visit this node
					 */
					count++; 
					
					// set the tempTarget as "visited"
					visited[tempTarget[1]][tempTarget[0]] = 1;
					// add the tempTarget into the "search process": ArrayList<int[][]>
					searchProcess.add(currentEdge);
					// record the father of the tempTarget
					hm.put(tempTarget[0]+":"+tempTarget[1],new int[][]{currentEdge[1],currentEdge[0]});
					
					// repaint the GameView
					gameView.postInvalidate();
					
					// thread sleep, control program speed here
					try {
						Thread.sleep(timeSpan);
					} catch(Exception e){
						e.printStackTrace();
					}
					
					// -------------------------- determine whether reach the target --------------------------- 
					/* 
					 * if：tempTarget is the target, then while loop end
					*/
					if(tempTarget[0]==target[0] && tempTarget[1]==target[1]){
						break; //jump out of the while loop
					}
					
					/*
					 * else: tempTarget is not the target, dealing with this node and move to the next loop
					 */
					// push all the possible edges into stack 
					int currCol = tempTarget[0];
					int currRow = tempTarget[1];
					// deal with 8 direction
					for(int[] rc:direction){
						// get the coordinate
						int i = rc[1]; // x
						int j = rc[0]; // y
						
						if(i==0 && j==0){
							// 应该不可能得到原点(0,0)坐标
							continue;
						}
						
						// 对标方向与目标的关系 
						/*
						 * 检测next node的可通过性(不能超过边间，不能为墙)
						 * (currRow+i, currCol+j)指下一个将要进行搜索的node
						 * MapList.map[mapId].length 指地图纵向的长度
						 * MapList.map[mapId][0].length 指地图横向的长度
						 * "map[currRow+i][currCol+j]!=1" 指下个node不能是墙
						 */
						if(currRow+i>=0 && currRow+i<Map.map[mapId].length &&
						currCol+j>=0 && currCol+j<Map.map[mapId][0].length && map[currRow+i][currCol+j]!=1){
							//push the tempEdge into stack
							int[][] tempEdge = {
								{tempTarget[0], tempTarget[1]},	//当前访问的node
								{currCol+j, currRow+i}			//下次访问的node
							};
							stack.push(tempEdge);
							/*
							 * 只要符合要求的node全部放入stack
							 * 然后按照FIFO的原则在while loop中逐个比较
							 */
						}
					}
					
					// 进入下次while loop
				}
				
				// ----------------------- 找到路径, 发送信息， 结束线程  ----------------------------------
				// find a path
				pathFlag = true;	
				
				// repaint GameView
				gameView.postInvalidate();

				// if the program find a path
				// then set the button to enable to use
				Message msg1 = myHandler.obtainMessage(1);
				myHandler.sendMessage(msg1);
				// and change the TextView
				Message msg2 = myHandler.obtainMessage(2, count);
				myHandler.sendMessage(msg2);
			}
		}.start();		
	}
	
	// BFS: breadth-first algorithm
	/* 
	 * departure from a node (source), and going to another node (target)
	 * search every node around the source node 
	 * if it didn't reach the target, then search every node around the next node.
	 * very slow, but widely used in games !!!!!
	*/
	// generally, we often use Queue to store the DFS edge.
	// because LIFO!!!
	public void BFS(){
		new Thread(){
			public void run()
			{
				int count=0;
				int[][] start = {
					{source[0],source[1]},
					{source[0],source[1]}
				};
				queue.offer(start);

				while(true)
				{					
					int[][] currentEdge=queue.poll();
					int[] tempTarget=currentEdge[1];
					
					if(visited[tempTarget[1]][tempTarget[0]]==1){
						continue;
					}
					count++;
					
					visited[tempTarget[1]][tempTarget[0]]=1;
					searchProcess.add(currentEdge);
					hm.put(tempTarget[0]+":"+tempTarget[1], new int[][]{currentEdge[1],currentEdge[0]});
					
					// repaint the GameView
					gameView.postInvalidate();

					try{
						Thread.sleep(timeSpan);
					} catch(Exception e) {
						e.printStackTrace();
					}

					if(tempTarget[0]==target[0]&&tempTarget[1]==target[1]) {
						break;
					}
					
					// add all the possible edges into the queue 
					int currCol=tempTarget[0];
					int currRow=tempTarget[1];
					for(int[] rc:direction){
						int i=rc[1];
						int j=rc[0];
						if(i==0&&j==0){continue;}
						if(currRow+i>=0&&currRow+i<Map.map[mapId].length
								&&currCol+j>=0&&currCol+j<Map.map[mapId][0].length&&
						map[currRow+i][currCol+j]!=1){
							int[][] tempEdge={
								{tempTarget[0],tempTarget[1]},
								{currCol+j,currRow+i}
							};
							queue.offer(tempEdge);
						}
					}
				}
				
				// find a path
				pathFlag=true;	
				gameView.postInvalidate();
				
				// if the program find a path
				// then set the button to enable to use
				Message msg1 = myHandler.obtainMessage(1);
				myHandler.sendMessage(msg1);
				// and change the TextView
				Message msg2 = myHandler.obtainMessage(2, count);
				myHandler.sendMessage(msg2);
			}
		}.start();				
	}

	// BFS base A*: breadth-first based A* algorithm
	/* 
	 * A* is a heuristic searching algorithm. 
	 *It use a heuristic function to evaluate the path cost, then decide which route to go. 
		formula: F = G + H
			F is the total cost
			G is the cost from current node move to the next node
			H is the heuristic function, return the direct distance from node A to node B
	
		like: from A to B
			------------------------------------------
			|g   h |g    h|g   h |     |     |     |
			|14  60|10  50|14  40|     |     |     |
			|----------------------------------------
			|g   h |      |g   h |     |     |     |
			|10  40|  A   |10  30|     |     |  B  |
			|----------------------------------------
			|g   h |g   h |g   h |     |     |     |
			|14  60|10  50|14  40|     |     |     |
			-----------------------------------------------
	*/
	// it's easy to modify base on BFS.
	// only need to change the Queue to a PriorityQueue.
	public void BFSAStar(){
		new Thread(){
			public void run(){
				int count = 0;
				int[][] start = {
					{source[0],source[1]},
					{source[0],source[1]}
				};
				astarQueue.offer(start); //PriorityQueue
				
				while(true){
					// ----------------------- 访问目前节点 -------------------------
					// get edge from the top of the stack
					/*
					 * currentEdge 就是正在进行访问的路径
					 * 包括父节点的坐标，以及当前访问节点的坐标
					 * currentEdge{(父x,父y),(当前x,当前y)}
					 */
					int[][] currentEdge = astarQueue.poll();
					
					// tempTarget, 目前要访问的node
					int[] tempTarget = currentEdge[1];
					/* 
					 * if：如果该点已访问，则continue进下一次while loop
					*/
					if(visited[tempTarget[1]][tempTarget[0]] == 1){
						continue;
					}
					
					/*
					 * else: 如果tempTarget未访问，则访问该点
					 */
					// 先记录步数
					count++; 
					
					// set the tempTarget as "visited"
					visited[tempTarget[1]][tempTarget[0]] = 1;
					// 记录搜索步骤
					searchProcess.add(currentEdge);
					// 记录tempTarget父节点
					// record the father of the tempTarget
					hm.put(tempTarget[0]+":"+tempTarget[1], new int[][]{currentEdge[1],currentEdge[0]});
					// repaint screen
					gameView.postInvalidate();
					// speed control
					try{
						Thread.sleep(timeSpan);
					} catch(Exception e) {
						e.printStackTrace();
					}
					
					// ----------------------- 处理目前节点 -------------------------
					// 如果找到目标，则while结束
					/* 
					 * if：如果tempTarget是最终目标，则while loop结束
					*/
					if(tempTarget[0]==target[0] && tempTarget[1]==target[1]){
						break;
					}
					
					/*
					 * else: 如果不是最终目标，则处理该点，准备下次loop
					 */
					int currCol = tempTarget[0];
					int currRow = tempTarget[1];
					for(int[] rc:direction){
						int i = rc[1];
						int j = rc[0];
						// 基本不可能有这种情况
						if(i==0 && j==0){
							continue;
						}
						/*
						 * 检测next node的可通过性(不能超过边间，不能为墙)
						 * (currRow+i, currCol+j)指下一个将要进行搜索的node
						 * MapList.map[mapId].length 指地图纵向的长度
						 * MapList.map[mapId][0].length 指地图横向的长度
						 * "map[currRow+i][currCol+j]!=1" 指下个node不能是墙
						 */
						if(currRow+i>=0 && currRow+i<Map.map[mapId].length && 
						currCol+j>=0 && currCol+j<Map.map[mapId][0].length && map[currRow+i][currCol+j]!=1){
							int[][] tempEdge = {
									{tempTarget[0],tempTarget[1]},
									{currCol+j,currRow+i}
							};
							astarQueue.offer(tempEdge);
							/*
							 * 只要符合要求的node全部放入astarQueue
							 * 然后按照权重，在while loop中逐个比较
							 * 权重法则，AStarComparator
							 */
						}						
					}
				}
				// 找到路径以后的处理
				pathFlag = true;	
				gameView.postInvalidate();
				Message msg1 = myHandler.obtainMessage(1);
				myHandler.sendMessage(msg1);
				Message msg2 = myHandler.obtainMessage(2, count);
				myHandler.sendMessage(msg2);
			}
		}.start();				
	}

	//Dijkstra
	public void Dijkstra()
	{		
		// not implement
	}	
	
	
	// -------------------------- Message Handler ----------------------------
		// true: Found a path
		boolean pathFlag=false;
		// Handler
		private Handler myHandler = new Handler(){
	        public void handleMessage(Message msg){
	        	if(msg.what == 1){
					// change the button state
	        		goButton.setEnabled(true);
	        		cleanMapButton.setEnabled(true);
	        		cleanPathButton.setEnabled(true);
	        	} else if(msg.what == 2) {
					// change the value of TextView
	        		//String strDijkstra = Context.getString(R.string.dijkstra);  
	        		StepsTextView.setText("Used Steps: " + (Integer)msg.obj);
	        	}
	        }
		};
		
		// ------------------------- Game State Control ---------------------------
		// Speed Control
		// Interval speed 线程等待速度，越大程序速度越慢
		int timeSpan = 30;
		// 程序减速
		public void slowDown(){
			if(timeSpan>=100 &&timeSpan<500)
			{
				timeSpan+=100;
			}
			else if(timeSpan>=30 &&timeSpan<100)
			{
				timeSpan+=30;
			}
			else if(timeSpan < 30)
			{
				timeSpan+=10;
			}
		};
		
		// 程序加速
		public void speedUp(){
			if(timeSpan>100)
			{
				timeSpan-=100;
			}
			if(timeSpan<=100 && timeSpan>30)
			{
				timeSpan-=30;
			}
			else if(timeSpan<=30 &&timeSpan>=6)
			{
				timeSpan-=6;
			}
		};
		
		// 程序速度还原
		public void speedNormal(){
			timeSpan = 30;
		};

		// State Control
		// Clear all of the states and lists
		public void clearState(){
			// 速度还原
			speedNormal(); 
			// pathFlag还原为未找到
			pathFlag = false;	
			// 清空左右array
			searchProcess.clear();
			stack.clear();
			queue.clear();
			astarQueue.clear();
			hm.clear();
			visited = new int[Map.map[mapId].length][Map.map[mapId][0].length];
			//hmPath.clear();
			// reprint the screen
			gameView.postInvalidate();
		}
		
		// Clear all of the states, lists and maps
		public void clearMap(){
			gameView.resetMap();
			gameView.postInvalidate();
		}
}
	