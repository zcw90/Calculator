package com.zcw.calculator.operator;

import java.util.ArrayList;
import java.util.List;

/**
 * 栈
 * @author ASUS
 *
 * @param <T>
 */
public class Stack<T> {
	
	private List<T> stack;
	
	public Stack() {
		stack = new ArrayList<T>();
	}

	/**
	 * 入栈
	 * @param item
	 */
	public void push(T item) {
		stack.add(item);
	}
	
	/**
	 * 出栈
	 * <br>如果无内容可出栈，则返回null。
	 * @return
	 */
	public T pop() {
		if(stack.size() <= 0)
			return null;
		
		T item = stack.get(stack.size() - 1);
		stack.remove(stack.size() - 1);
		
		return item;
	}
	
	/**
	 * 返回栈顶元素
	 * @return
	 */
	public T getTop() {
		if(stack.size() <= 0)
			return null;
		
		return stack.get(stack.size() - 1);
	}
	
	/**
	 * 返回栈中数据量
	 * @return
	 */
	public int size() {
		return stack.size();
	}
	
	/**
	 * 清空栈
	 */
	public void clean() {
		stack.clear();
	}
}
