package com.bamisu.gamelib.utils;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;


public class ServerLoop implements Runnable {
	public class LoopMethod {
		public String methodName;
		public Object target;
		public int maxTime;
		public int curTime;
		public int numTime;
		public Object[] params;
		public Class[] classArr;

		public LoopMethod(String fName, Object fTarget, int excTime, int nTime) {
			super();
			methodName = fName;
			target = fTarget;
			maxTime = excTime;
			curTime = excTime;
			numTime = nTime;
			params = new Object[0];
			classArr = null;
		}

		public LoopMethod(String fName, Object fTarget, int excTime, int nTime, Object... params) {
			super();
			methodName = fName;
			target = fTarget;
			maxTime = excTime;
			curTime = excTime;
			numTime = nTime;
			this.params = params;
			this.classArr = new Class[this.params.length];
			for (int i = 0; i < this.params.length; i++) {
				this.classArr[i] = this.params[i].getClass();
			}
		}

		public boolean onTimer() {
			curTime--;
			if (curTime <= 0) {
				try {
					Class c = target.getClass();
					Method f = c.getMethod(methodName, classArr);
					f.invoke(target, params);
					if (numTime > 0) {
						numTime--;
						if (numTime == 0) {
							return false;
						}
					}
					curTime = maxTime;
					return true;
				} catch (Exception e) {
					e.printStackTrace();
//					curTime++;
					return false;
				}
			}
			return true;
		}
	}


	private List<LoopMethod> fList = new LinkedList<LoopMethod>();

	public ServerLoop() {
		super();
	}

	public void register(String fName, Object fTarget, int excTime, int nTime, Object... params) {
		fList.add(new LoopMethod(fName, fTarget, excTime, nTime, params));
	}

	public void register(String fName, Object fTarget, int excTime, int nTime) {
		fList.add(new LoopMethod(fName, fTarget, excTime, nTime));
	}

	public void run() {
		//Logger.getLogger("packet").debug("go go Task Loop");
		for (int i = 0; i < fList.size(); i++) {
			if (!fList.get(i).onTimer()) {
				fList.remove(i);
				i--;
			}
		}
	}
}
