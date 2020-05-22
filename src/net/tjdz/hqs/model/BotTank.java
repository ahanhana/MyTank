package net.tjdz.hqs.model;

import net.tjdz.hqs.enumtype.Direction;
import net.tjdz.hqs.enumtype.TankType;
import net.tjdz.hqs.frame.GamePanel;
import net.tjdz.hqs.util.ImageUtil;

import java.awt.*;
import java.util.List;
import java.util.Random;

/**
 * 电脑坦克类
 * 为了控制游戏难度，电脑坦克随机向上移动的概率应该控制为比其他方向更小
 * 并且电脑坦克最好每次移动的方向都不想同，向着某一个方向连续移动的时间也应该不相同
 */
public class BotTank extends Tank {
	{
		//初始化敌人坦克速度
		setSpeed(3);
	}
	private Random random = new Random();// 随机数对象
	private Direction dir;// 移动方向
	private int freshTime = GamePanel.FRESHTIME;// 刷新时间，采用游戏面板的刷新时间
	private int moveTimer = 0;// 移动计时器

	@Override
	public void setSpeed(int speed) {
		super.setSpeed(speed);
	}

	private boolean pause=false;//电脑坦克暂停状态
	/**
	 * 获取电脑坦克暂停状态
	 */
	public boolean isPause() {
		return pause;
	}
	/**
	 * 设置电脑坦克暂停状态
	 */
	public void setPause(boolean pause) {
		this.pause = pause;
	}

	/**
	 * 
	 * 电脑坦克构造方法
	 * 
	 * @param x
	 *             横坐标
	 * @param y
	 *             纵坐标
	 * @param gamePanel
	 *             游戏面板
	 * @param type
	 *             坦克类型
	 */

	public BotTank(int x, int y, GamePanel gamePanel, TankType type) {
		super(x, y, ImageUtil.BOT_DOWN_IMAGE_URL, gamePanel, type);// 调用父类构造方法，使用默认机器人坦克图片
		dir = Direction.DOWN;// 移动方向默认向下
		setAttackCoolDownTime(1000);// 设置攻击冷却时间
		// setSpeed(2);//设置电脑坦克移动速度
	}

	/**
	 * 电脑坦克展开行动的方法
	 */
	public void go(){
		if(isAttackCoolDown()){// 如果冷却结束，就可以攻击
			attack();// 攻击
		}
		if(moveTimer>random.nextInt(1000)+500){// 如果移动计时器超过随机的0.5~1.5秒，则随机一个方向
			dir=randomDirection();
			moveTimer=0;// 重置计时器
		}else{
			moveTimer+=freshTime;// 计时器按照刷新时间递增
		}
		switch (dir) {// 根据方向选择朝着哪个方向移动
		case UP:
			upWard();
			break;
		case DOWN:
			downWard();
			break;
		case LEFT:
			leftWard();
			break;
		case RIGHT:
			rightWard();
			break;
		}	
	}

	/**
	 * 获取随机方向
	 * 
	 * @return
	 */
	private Direction randomDirection() {
		Direction [] dirs=Direction.values();// 获取出方向的枚举值
		Direction oldDir=dir;// 保存原来的方向
		Direction newDir=dirs[random.nextInt(4)];
		if(oldDir==newDir||newDir==Direction.UP) {// 如果电脑坦克原来的方向和本次随机的方向相同，或者电脑坦克新的方向是向上，那么重新随机一次方向
			return dirs[random.nextInt(4)];
		}
		return newDir;
	}

	/**
	 * 重写移动到面板的边界事件
	 */
	protected void moveToBorder() {
		if (x < 0) {// 如果坦克横坐标小于0
			x = 0;// 让坦克横坐标等于0
			dir = randomDirection();// 随机调整移动方向
		} else if (x > gamePanel.getWidth() - width) {// 如果坦克横坐标超出了最大范围
			x = gamePanel.getWidth() - width;// 让坦克横坐标保持最大值
			dir = randomDirection();// 随机调整移动方向
		}
		if (y < 0) {// 如果坦克纵坐标小于0
			y = 0;// 让坦克纵坐标等于0
			dir = randomDirection();// 随机调整移动方向
		} else if (y > gamePanel.getHeight() - height) {// 如果坦克纵坐标超出了最大范围
			y = gamePanel.getHeight() - height;// 让坦克纵坐标保持最大值
			dir = randomDirection();// 随机调整移动方向
		}
	}

	/**
	 * 重写碰到坦克方法
	 */
	@Override
	boolean hitTank(int x, int y) {
		Rectangle next = new Rectangle(x, y, width, height);// 创建碰撞位置
		List<Tank> tanks = gamePanel.getTanks();// 获取所有坦克集合
		for (int i = 0, lengh = tanks.size(); i < lengh; i++) {// 遍历坦克集合
			Tank t = tanks.get(i);// 获取坦克对象
			if (!this.equals(t)) {// 如果此坦克对象与本对象不是同一个
				if (t.isAlive() && t.hit(next)) {// 如果对方说是存活的，并且与本对象发生碰撞
					if (t instanceof BotTank) {// 如果对方也是电脑
						dir = randomDirection();// 随机调整移动方向
					}
					return true;// 发生碰撞
				}
			}
		}
		return false;// 未发生碰撞
	}

	/**
	 * 重写攻击方法，每次攻击只有4%概率会触发父类攻击方法
	 */
	@Override
	public void attack() {
		int rnum = random.nextInt(100);// 创建随机数，范围在0-99
		if (rnum < 4) {// 如果随机数小于4
			super.attack();// 执行父类攻击方法
		}
	}
}
