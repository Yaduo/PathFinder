package com.test.PathFind;

import java.util.*;

/*
 * AStarComparator是启发函数 H
 * return：优先级，代表不同的点到target距离的权值
 * *It use a heuristic function to evaluate the path cost, then decide which route to go. 
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
public class AStarComparator implements Comparator<int[][]>
{	
	Game game;
	
	public AStarComparator(Game game){
		this.game = game;
	}
	
	@Override
	public int compare(int[][] o1,int[][] o2){
		// 获取引用
		int[] t1 = o1[1];
		int[] t2 = o2[1];
		int[] target = game.target;
		
		//direct distance 直线物理距离
		/*
		 * 勾股定理
		 * a: 点A到target直线距离的平方 
		 * b: 点B到target直线距离的平方
		 */
		double a = Math.pow(t1[0]-target[0],2) + Math.pow(t1[1]-target[1],2);
		double b = Math.pow(t2[0]-target[0],2) + Math.pow(t2[1]-target[1],2);

		// Monte Carlo method 门特卡罗距离
		/*
		 * int a=game.visited[o2[0][1]][o2[0][0]]+Math.abs(t1[0]-target[0])+Math.abs(t1[1]-target[1]);
		 * int b=game.visited[o2[0][1]][o2[0][0]]+Math.abs(t2[0]-target[0])+Math.abs(t2[1]-target[1]);
		 */

		// return优先级
		return (int)(a-b);
	}
	
	public boolean equals(Object obj){
		return false;
	}
}