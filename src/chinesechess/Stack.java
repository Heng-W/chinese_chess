/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chinesechess;

/**
 *
 * @author HW
 */
public class Stack {

    private Node top;
    private int depth;

    public void push(int[][] map) {
        Node p = new Node();
        //对二维数组进行深拷贝
        int[][] array = new int[map.length][map[0].length];
        for (int i = 0; i < map.length; i++) {
            System.arraycopy(map[i], 0, array[i], 0, map[i].length);
        }
        p.map = array;
        p.next = top;
        top = p;
        depth++;
    }

    public void pop() {
        top = top.next;
        depth--;
    }

    //清空堆栈
    public void clear() {
        top = null;
        depth = 0;
    }

    public boolean isEmpty() {
        return depth == 0;
    }

    public int[][] getmap() {
        return top.map;
    }

    public int getDepth() {
        return depth;
    }

    private class Node {

        int[][] map;
        Node next;
    }

}
