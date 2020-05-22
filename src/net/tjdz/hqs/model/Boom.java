package net.tjdz.hqs.model;

import net.tjdz.hqs.frame.GamePanel;
import net.tjdz.hqs.util.ImageUtil;

import java.awt.*;

/**
 * 展示爆炸效果（图片)
 */
public class Boom extends DisplayableImage {

	private int timer = 0;// 计时器
	private int fresh = GamePanel.FRESHTIME;// 刷新时间
	private boolean alive = true;// 是否存活

	/**
	 * 爆炸效果工作方法
	 * 
	 * @param x 爆炸图片横坐标
	 * @param y 爆炸图片纵坐标
	 */
	public Boom(int x, int y) {
		super(x, y, ImageUtil.BOOM_IMAGE_URL);// 调用父类构造方法，使用默认爆炸效果图片
	}

	/**
	 * 展示爆炸图片,此照片只显示0.3秒
	 * 
	 * @param g 绘图对象
	 */
	public void show(Graphics g) {
		if (timer >= 300) {// 当计时器记录已到0.3秒
			alive = false;// 爆炸效果失效
		} else {
			g.drawImage(getImage(), x, y, null);// 绘制爆炸效果
			timer += fresh;// 计时器按照刷新时间递增
		}
	}

	/**
	 * 爆炸图片是否有效
	 * 
	 * @return
	 */
	public boolean isAlive() {
		return alive;
	}
}
