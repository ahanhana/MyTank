package net.tjdz.hqs.model.wall;

import net.tjdz.hqs.util.ImageUtil;

/**
 * 铁墙
 */
public class IronWall extends Wall {
	/**
	 * 
	 * 铁墙构造方法
	 * 
	 * @param x
	 *              初始化横坐标
	 * @param y
	 *              初始化纵坐标
	 */
	public IronWall(int x, int y) {
		super(x, y, ImageUtil.IRONWALL_IMAGE_URL);// 调用父类构造方法，使用默认铁墙图片
	}

}
