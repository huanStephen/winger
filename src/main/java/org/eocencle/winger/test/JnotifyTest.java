package org.eocencle.winger.test;

import net.contentobjects.jnotify.JNotify;
import net.contentobjects.jnotify.JNotifyListener;

public class JnotifyTest {
	public static void main(String[] args) {
		try {
			new JnotifyTest().test();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void test() throws Exception {
		//监控路径
		String path = "C:/Users/dell/Desktop/test";
		//可随意组合监听事件类型
		int mask = JNotify.FILE_CREATED | JNotify.FILE_DELETED
				| JNotify.FILE_MODIFIED | JNotify.FILE_RENAMED;
		//是否监控子目录
		boolean watchSubtree = false;
		//添加监听，返回监听唯一标示
		int watchID = JNotify.addWatch(path, mask, watchSubtree, new Listener());
		System.out.println(watchID);
		//为啥睡眠？如果不睡眠，程序运行结束，监听就被移除 (Common-io的FileAlterationMonitor会继续运行不休眠)
		Thread.sleep(1000000);
		//根据watchID手动移除监听
		boolean res = JNotify.removeWatch(watchID);
		if (!res) {
			//返回FALSE，监听标识无效
		}
	}
	//可以在下面的监控方法中添加自己的代码。比如在fileModified中添加重新加载配置文件的代码
	//可以结合rsync，实现实时同步文件
	class Listener implements JNotifyListener {
		public void fileRenamed(int wd, String rootPath, String oldName,
				String newName) {
			print("renamed " + rootPath + " : " + oldName + " -> " + newName);
		}
		public void fileModified(int wd, String rootPath, String name) {
			print("modified " + rootPath + " : " + name);
		}
		public void fileDeleted(int wd, String rootPath, String name) {
			print("deleted " + rootPath + " : " + name);
		}
		public void fileCreated(int wd, String rootPath, String name) {
			print("created " + rootPath + " : " + name);
		}
		void print(String msg) {
			System.err.println(msg);
		}
	}
}
