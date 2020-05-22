package net.tjdz.hqs.model;

import net.tjdz.hqs.model.wall.BrickWall;
import net.tjdz.hqs.model.wall.Wall;
import net.tjdz.hqs.util.MapIO;

import java.util.ArrayList;
import java.util.List;

/**
 * 地图类，单例模式
 */
public class Map {
	public static List<Wall> walls = new ArrayList<>();// 地图中所有墙块的集合

	/**
	 * 私有构造方法
	 */
	private Map() {

	}

	/**
	 * 获取地图对象
	 * 
	 * @param level
	 *              关卡数
	 * @return 指定关卡的地图对象
	 */
	public static Map getMap(String level) {
		walls.clear();// 墙块集合清空
		walls.addAll(MapIO.readMap(level));// 读取指定关卡的墙块集合
		// 基地砖墙
		for (int a = 340; a <= 400; a += 20) {// 循环基地砖墙的横坐标
			for (int b = 500; b <= 560; b += 20) {// 循环基地砖墙的纵坐标
				if (a >= 360 && a <= 380 && b >= 520) {// 如果墙块与基地发生重合
					continue;// 执行下一次循环
				} else {
					walls.add(new BrickWall(a, b));// 墙块集合中添加墙块
				}
			}
		}
		return new Map();// 返回新的地图对象
	}

	/**
	 * 获取地图对象
	 * 
	 * @param level
	 *              关卡数
	 * @return 指定关卡的地图对象
	 */
	public static Map getMap(int level) {
		return getMap(String.valueOf(level));// 调用重载方法
	}

	/**
	 * 获取地图对象中的所有墙块
	 * 
	 * @return 墙块集合
	 */
	public static List<Wall> getWalls() {
		return walls;
	}

}
