package cn.colonq.admin.utils;

import java.util.LinkedList;

import cn.colonq.admin.config.ServiceException;

public class ThreadSafePool<T> {
	private final LinkedList<T> linked;

	public ThreadSafePool(final Class<T> type) {
		final int maxThread = Runtime.getRuntime().availableProcessors();
		linked = new LinkedList<>();
		for (int i = 0; i < maxThread; i++) {
			T t = null;
			try {
				t = type.getDeclaredConstructor().newInstance();
			} catch (Exception e) {
				throw new ServiceException(e.getMessage());
			}
			if (t != null) {
				linked.add(t);
			}
		}
	}

	public ThreadSafePool(final LinkedList<T> linked) {
		this.linked = linked;
	}

	public synchronized void putItem(final T item) {
		this.linked.addLast(item);
		notify();
	}

	public synchronized T getItem() {
		while (linked.size() == 0) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				throw new ServiceException(e.getMessage());
			}
		}
		return linked.removeFirst();
	}
}
