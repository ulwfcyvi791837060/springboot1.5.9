package com.yyx.aio.common;

import java.util.*;
import java.util.concurrent.*;

/*
 * @fun 线程池
 * @author yaofeng
 *
 */
public class TaskPool {

	public static final String DefaultPool = "__default_taskpool__";

	private static Map<String, TaskPool> namedPools = Collections
			.synchronizedMap(new HashMap<String, TaskPool>());

	private String name = null;

	private ExecutorService _pool = null;

	private LinkedHashMap<String, ExecutorService> _poollist = new LinkedHashMap<String, ExecutorService>();

	private TaskPool(String name) {
		this.name = name;
	}

	public static TaskPool named(String poolName) {
		if (namedPools.containsKey(poolName))
			return namedPools.get(poolName);
		synchronized (namedPools) {
			namedPools.put(poolName, new TaskPool(poolName));
		}
		return namedPools.get(poolName);
	}

	static public synchronized void setPool(int poolSize) {
		named(DefaultPool).setPoolSize(poolSize);
	}

	static public void submitTask(Runnable task) {
		named(DefaultPool).getPool().submit(task);
	}

	static public void completeTask(final List<Runnable> tasks) {

		named(DefaultPool).complete(tasks);

	}

	public TaskPool submit(Runnable task) {
		this.getPool().submit(task);
		return this;
	}

	private ExecutorService getPool() {
		synchronized (this) {
				setPoolSize(50);
				if (_pool == null) {
				return _pool;
			} else {
				// select a pool according current thread
				Thread current = Thread.currentThread();
				String id = current.toString();
				int ind = id.indexOf("-");
				String poolname = (ind > 0) ? id.substring(0, ind) : "";
				if ("".equals(poolname) || !_poollist.containsKey(poolname)) {
					return _pool;
				} else {
					int i = 0; // find next pool index
					boolean found = false;
					ExecutorService target = null;
					for (String es : _poollist.keySet()) {
						if (found) {
							target = _poollist.get(es);
							break;
						} else {
							found = es.equals(poolname);
							i++;
						}
					}
					if (target == null) {
						final int poolindex = i;
						ThreadFactory factory = new ThreadFactory() {
							public Thread newThread(Runnable paramRunnable) {
								// TODO Auto-generated method stub

								return new Thread(paramRunnable,
										TaskPool.this.name + poolindex + "-") {
									public String toString() {
										return TaskPool.this.name + poolindex
												+ "-" + super.toString();
									}
								};
							}

						};
						target = new ThreadPoolExecutor(0, Math.max(0,
								((ThreadPoolExecutor) _pool)
										.getMaximumPoolSize()), 5000L,
								TimeUnit.MILLISECONDS,
								new LinkedBlockingQueue<Runnable>(), factory,
								new ThreadPoolExecutor.AbortPolicy());
						_poollist.put(TaskPool.this.name + poolindex, target);
					}
					return target;

				}
			}
		}

	}

	public TaskPool setPoolSize(int poolSize) {
		if (poolSize < 1)
			return this;
		synchronized (this) {
			if (_pool == null) {
				int workers = poolSize;
				ThreadFactory factory = new ThreadFactory() {
					public Thread newThread(Runnable paramRunnable) {
						// TODO Auto-generated method stub

						return new Thread(paramRunnable, TaskPool.this.name
								+ "0-") {
							public String toString() {
								return TaskPool.this.name + "0-"
										+ super.toString();
							}
						};
					}
				};
				_pool = new ThreadPoolExecutor(Math.max(1, poolSize), Math.max(
						4, poolSize), 5000L, TimeUnit.MILLISECONDS,
						new LinkedBlockingQueue<Runnable>(), factory,
						new ThreadPoolExecutor.AbortPolicy());
				_poollist.put(TaskPool.this.name + "0", _pool);

				System.out.println("TaskPool max: " + workers + "   started.");
			}
		}
		return this;

	}

	public TaskPool complete(final List<Runnable> tasks) {
		ArrayList<Future<?>> calls = new ArrayList<Future<?>>();
		for (Runnable r : tasks) {
			calls.add(this.getPool().submit(r));
		}
		try {
			for (Future<?> f : calls) {
				f.get();
			}
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("TaskPool completeTask Error", e);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("TaskPool completeTask Error", e);
		}
		return this;
	}
	
	public static void main(String[] args) {
		System.out.println(UUID.randomUUID().toString());
	}
}
