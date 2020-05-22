package net.tjdz.hqs.model;

import net.tjdz.hqs.util.MapIO;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * 关卡
 */
public class Level {
	private static int nextLevel = 1;// 下一关记录
	private static int previsousLevel = 1;// 上一关记录
	private static int count=0;// 关卡总数
	static{
		readLevel();
	}
	/**
	 * 读取关卡
	 */
	private static void readLevel() {
		try {
			File f = new File(MapIO.DATA_PATH);// 创建地图文件目录文件夹
			if (!f.exists()) {// 如果此文件夹不存在
				throw new FileNotFoundException("地图文件缺失！");// 抛出异常
			}
			File fs[] = f.listFiles();// 获取地图文件目录文件夹下的所有文件对象
			count = fs.length;// 将文件数量作为关卡总数
			if (count == 0) {// 如果目录下没有任何文件
				throw new FileNotFoundException("地图文件缺失！");// 抛出异常
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 下一关
	 * 
	 * @return 关卡号
	 */
	public static int nextLevel() {
		nextLevel++;// 下一关记录+1
		previsousLevel = nextLevel;// 记录下一关关卡号，作为下一关的上一关
		if (nextLevel > count) {// 如果关卡数大于关卡总数
			nextLevel = 1;// 从第一关开始
			previsousLevel=count;
		}
		return nextLevel;// 返回下一关的值
	}

	/**
	 * 上一关
	 * 
	 * @return 关卡号
	 */
	public static int previsousLevel() {
		return previsousLevel;// 返回上一关的值
	}
	/**
	 * 获取关卡总数
	 * @return 当前关卡总数
	 */
	public static int getCount() {
		readLevel();
		return count;
	}
}
