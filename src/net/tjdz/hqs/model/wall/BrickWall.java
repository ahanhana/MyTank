package net.tjdz.hqs.model.wall;

import net.tjdz.hqs.util.ImageUtil;

/**
 * 砖墙
 */
public class BrickWall extends Wall {
	/**
	 * 砖墙构造方法
	 * 
	 * @param x
	 *              初始化横坐标
	 * @param y
	 *              初始化纵坐标
	 */
	public BrickWall(int x, int y) {
		super(x, y, ImageUtil.BRICKWALL_IMAGE_URL);// 调用父类构造方法，使用默认砖墙图片
	}

}
