package com.wmenjoy.utils.lang;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;

/**
 * 借助于arrayList实现的一个Stack， 现成不安全
 * 
 * @author jinliang.liu
 * 
 * @param <E>
 */
public final class Stack<E> {
	private int mSize = 0;

	private List<E> mElements = new ArrayList<E>();

	public Stack() {
	}

	/**
	 * push.
	 * 
	 * @param ele
	 */
	public void push(final E ele) {
		if (this.mElements.size() > this.mSize) {
			this.mElements.set(this.mSize, ele);
		} else {
			this.mElements.add(ele);
		}
		this.mSize++;
	}

	/**
	 * pop.
	 * 
	 * @return the last element.
	 */
	public E pop() {
		if (this.mSize == 0) {
			throw new EmptyStackException();
		}
		return this.mElements.set(--this.mSize, null);
	}

	/**
	 * peek.
	 * 
	 * @return the last element.
	 */
	public E peek() {
		if (this.mSize == 0) {
			throw new EmptyStackException();
		}
		return this.mElements.get(this.mSize - 1);
	}

	/**
	 * get.
	 * 
	 * @param index
	 *            index.
	 * @return element.
	 */
	public E get(final int index) {
		if (index >= this.mSize) {
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: "
					+ this.mSize);
		}

		return index < 0 ? this.mElements.get(index + this.mSize)
				: this.mElements.get(index);
	}

	/**
	 * set.
	 * 
	 * @param index
	 *            index.
	 * @param value
	 *            element.
	 * @return old element.
	 */
	public E set(final int index, final E value) {
		if (index >= this.mSize) {
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: "
					+ this.mSize);
		}

		return this.mElements
				.set(index < 0 ? index + this.mSize : index, value);
	}

	/**
	 * remove.
	 * 
	 * @param index
	 * @return element
	 */

	public E remove(final int index) {
		if (index >= this.mSize) {
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: "
					+ this.mSize);
		}

		final E ret = this.mElements.remove(index < 0 ? index + this.mSize
				: index);
		this.mSize--;
		return ret;
	}

	/**
	 * get stack size.
	 * 
	 * @return size.
	 */

	public int size() {
		return this.mSize;
	}

	/**
	 * is empty.
	 * 
	 * @return empty or not.
	 */

	public boolean isEmpty() {
		return this.mSize == 0;
	}

	/**
	 * clear stack.
	 */

	public void clear() {
		this.mSize = 0;
		this.mElements.clear();
	}

}
