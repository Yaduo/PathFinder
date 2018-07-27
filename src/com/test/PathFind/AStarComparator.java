package com.test.PathFind;

import java.util.*;

/*
 * AStarComparator���������� H
 * return�����ȼ�������ͬ�ĵ㵽target�����Ȩֵ
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
		// ��ȡ����
		int[] t1 = o1[1];
		int[] t2 = o2[1];
		int[] target = game.target;
		
		//direct distance ֱ���������
		/*
		 * ���ɶ���
		 * a: ��A��targetֱ�߾����ƽ�� 
		 * b: ��B��targetֱ�߾����ƽ��
		 */
		double a = Math.pow(t1[0]-target[0],2) + Math.pow(t1[1]-target[1],2);
		double b = Math.pow(t2[0]-target[0],2) + Math.pow(t2[1]-target[1],2);

		// Monte Carlo method ���ؿ��޾���
		/*
		 * int a=game.visited[o2[0][1]][o2[0][0]]+Math.abs(t1[0]-target[0])+Math.abs(t1[1]-target[1]);
		 * int b=game.visited[o2[0][1]][o2[0][0]]+Math.abs(t2[0]-target[0])+Math.abs(t2[1]-target[1]);
		 */

		// return���ȼ�
		return (int)(a-b);
	}
	
	public boolean equals(Object obj){
		return false;
	}
}