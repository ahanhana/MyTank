package net.tjdz.hqs.model.wall;

import net.tjdz.hqs.util.ImageUtil;

/**
 * 河流
 */
public class RiverWall extends Wall {
	/**
	 * 
	 * 河流构造方法
	 * 
	 * @param x
	 *              初始化横坐标
	 * @param y
	 *              初始化纵坐标
	 */
	public RiverWall(int x, int y) {
		super(x, y, ImageUtil.RIVERWALL_IMAGE_URL);// 调用父类构造方法，使用默认河流图片
	}

}
