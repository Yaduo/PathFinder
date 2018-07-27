
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

	// Algorithms ID��"0": depth first as default 
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
	 * key��String --  tempTarget[0]+":"+tempTarget[1]		����"2:2"
	 * value��int[][] -- {currentEdge[1],              		����{[�ӽڵ�X���ӽڵ�Y],          ���壺���Ӽ��path
	 *                    currentEdge[0]}               		 [���ڵ�X�����ڵ�Y]}
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
					
					// tempTarget, next visit node��the second node (maybe turn left)
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
					 * if��tempTarget is the target, then while loop end
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
							// Ӧ�ò����ܵõ�ԭ��(0,0)����
							continue;
						}
						
						// �Ա귽����Ŀ��Ĺ�ϵ 
						/*
						 * ���next node�Ŀ�ͨ����(���ܳ����߼䣬����Ϊǽ)
						 * (currRow+i, currCol+j)ָ��һ����Ҫ����������node
						 * MapList.map[mapId].length ָ��ͼ����ĳ���
						 * MapList.map[mapId][0].length ָ��ͼ����ĳ���
						 * "map[currRow+i][currCol+j]!=1" ָ�¸�node������ǽ
						 */
						if(currRow+i>=0 && currRow+i<Map.map[mapId].length &&
						currCol+j>=0 && currCol+j<Map.map[mapId][0].length && map[currRow+i][currCol+j]!=1){
							//push the tempEdge into stack
							int[][] tempEdge = {
								{tempTarget[0], tempTarget[1]},	//��ǰ���ʵ�node
								{currCol+j, currRow+i}			//�´η��ʵ�node
							};
							stack.push(tempEdge);
							/*
							 * ֻҪ����Ҫ���nodeȫ������stack
							 * Ȼ����FIFO��ԭ����while loop������Ƚ�
							 */
						}
					}
					
					// �����´�while loop
				}
				
				// ----------------------- �ҵ�·��, ������Ϣ�� �����߳�  ----------------------------------
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
					// ----------------------- ����Ŀǰ�ڵ� -------------------------
					// get edge from the top of the stack
					/*
					 * currentEdge �������ڽ��з��ʵ�·��
					 * �������ڵ�����꣬�Լ���ǰ���ʽڵ������
					 * currentEdge{(��x,��y),(��ǰx,��ǰy)}
					 */
					int[][] currentEdge = astarQueue.poll();
					
					// tempTarget, ĿǰҪ���ʵ�node
					int[] tempTarget = currentEdge[1];
					/* 
					 * if������õ��ѷ��ʣ���continue����һ��while loop
					*/
					if(visited[tempTarget[1]][tempTarget[0]] == 1){
						continue;
					}
					
					/*
					 * else: ���tempTargetδ���ʣ�����ʸõ�
					 */
					// �ȼ�¼����
					count++; 
					
					// set the tempTarget as "visited"
					visited[tempTarget[1]][tempTarget[0]] = 1;
					// ��¼��������
					searchProcess.add(currentEdge);
					// ��¼tempTarget���ڵ�
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
					
					// ----------------------- ����Ŀǰ�ڵ� -------------------------
					// ����ҵ�Ŀ�꣬��while����
					/* 
					 * if�����tempTarget������Ŀ�꣬��while loop����
					*/
					if(tempTarget[0]==target[0] && tempTarget[1]==target[1]){
						break;
					}
					
					/*
					 * else: �����������Ŀ�꣬����õ㣬׼���´�loop
					 */
					int currCol = tempTarget[0];
					int currRow = tempTarget[1];
					for(int[] rc:direction){
						int i = rc[1];
						int j = rc[0];
						// �������������������
						if(i==0 && j==0){
							continue;
						}
						/*
						 * ���next node�Ŀ�ͨ����(���ܳ����߼䣬����Ϊǽ)
						 * (currRow+i, currCol+j)ָ��һ����Ҫ����������node
						 * MapList.map[mapId].length ָ��ͼ����ĳ���
						 * MapList.map[mapId][0].length ָ��ͼ����ĳ���
						 * "map[currRow+i][currCol+j]!=1" ָ�¸�node������ǽ
						 */
						if(currRow+i>=0 && currRow+i<Map.map[mapId].length && 
						currCol+j>=0 && currCol+j<Map.map[mapId][0].length && map[currRow+i][currCol+j]!=1){
							int[][] tempEdge = {
									{tempTarget[0],tempTarget[1]},
									{currCol+j,currRow+i}
							};
							astarQueue.offer(tempEdge);
							/*
							 * ֻҪ����Ҫ���nodeȫ������astarQueue
							 * Ȼ����Ȩ�أ���while loop������Ƚ�
							 * Ȩ�ط���AStarComparator
							 */
						}						
					}
				}
				// �ҵ�·���Ժ�Ĵ���
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
		// Interval speed �̵߳ȴ��ٶȣ�Խ������ٶ�Խ��
		int timeSpan = 30;
		// �������
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
		
		// �������
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
		
		// �����ٶȻ�ԭ
		public void speedNormal(){
			timeSpan = 30;
		};

		// State Control
		// Clear all of the states and lists
		public void clearState(){
			// �ٶȻ�ԭ
			speedNormal(); 
			// pathFlag��ԭΪδ�ҵ�
			pathFlag = false;	
			// �������array
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
	