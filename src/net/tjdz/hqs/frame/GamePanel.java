package net.tjdz.hqs.frame;

import net.tjdz.hqs.enumtype.GameType;
import net.tjdz.hqs.enumtype.TankType;
import net.tjdz.hqs.model.*;
import net.tjdz.hqs.model.wall.BaseWall;
import net.tjdz.hqs.model.wall.Wall;
import net.tjdz.hqs.util.AudioPlayer;
import net.tjdz.hqs.util.AudioUtil;
import net.tjdz.hqs.util.ImageUtil;

import javax.swing.*;
import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

/**
 * 游戏面板
 */
@SuppressWarnings({ "deprecation", "serial" })
public class GamePanel extends JPanel implements KeyListener {
	/**
	 * 游戏界面刷新时间间隔：20毫秒
	 * 也就是说游戏的刷新帧是500FPS
	 */
	public static final int FRESHTIME = 20;
	private BufferedImage image;// 在面板中显示的主图片
	private Graphics g;// 图片的绘图对象
	private MainFrame frame;// 主窗体
	private GameType gameType;// 游戏模式
	private Tank play1, play2;// 玩家1、玩家2
	private boolean j_key, s_key, w_key, a_key, d_key, up_key, down_key, left_key, right_key, num0_key;// 按键是否按下标志，左侧单词是按键名
	private int level= Level.previsousLevel();// 关卡值
	private List<Bullet> bullets;// 所有子弹集合
	private volatile List<Tank> allTanks;// 所有坦克集合
	private List<Tank> botTanks;// 电脑坦克集合
	private static final int botCount = 30;// 电脑坦克总数
	private int botReadyCount = botCount;// 准备出场的电脑坦克总数
	private int botSurplusCount = botCount;// 电脑坦克剩余量
	private int botMaxInMap = 6;// 场上最大电脑坦克数
	private int botX[] = { 10, 367, 754 };// 电脑坦克出生的3个横坐标位置
	private List<Tank> playerTanks;// 玩家坦克集合
	private volatile boolean finish = false;// 游戏是否结束
	private BaseWall base;// 基地
	private List<Wall> walls;// 所有墙块
	private List<Boom> boomImage;// 坦克阵亡后的爆炸效果集合
	private Random r = new Random();// 随机数对象
	private int createBotTimer = 0;// 创建电脑坦克计时器
	private Tank survivor;// （玩家）幸存者,用于绘制最后一个爆炸效果
	private List<AudioClip> audios= AudioUtil.getAudios();// 所有背景音效的集合
	private Tool tool=Tool.getToolInstance(r.nextInt(500), r.nextInt(500));
	private int toolTimer=0;// 道具出现的计时器
	private int pauseTimer=0;// 电脑坦克暂停计时器
	/**
	 * 游戏面板构造方法
	 * 
	 * @param frame 主窗体
	 * @param level 关卡
	 * @param gameType 游戏模式
	 */
	public GamePanel(MainFrame frame, int level, GameType gameType) {
		this.frame = frame;
		frame.setSize(775, 600);
		//this.setSize(775, 600);
		this.level = level;
		this.gameType = gameType;
		setBackground(Color.BLACK);// 面板使用黑色背景
		init();// 初始化组件
		Thread t = new FreshThead();// 创建游戏帧刷新线程，这一步很重要，不创建的话游戏会卡住
		t.start();// 启动线程
		new AudioPlayer(AudioUtil.START).new AudioThread().start();// 播放背景音效
		addListener();// 开启监听
	}
	/**
	 * 组件初始化
	 */
	private void init() {
		bullets = new ArrayList<>();// 实例化子弹集合
		allTanks = new ArrayList<>();// 实例化所有坦克集合
		walls = new ArrayList<>();// 实例化所有墙块集合
		boomImage = new ArrayList<>();// 实例化爆炸效果集合
		
		image = new BufferedImage(794, 572, BufferedImage.TYPE_INT_BGR);// 实例化主图片，采用面板实际大小
		g = image.getGraphics();// 获取主图片绘图对象

		playerTanks = new Vector<>();// 实例化玩家坦克集合
		play1 = new Tank(278, 537, ImageUtil.PLAYER1_UP_IMAGE_URL, this, TankType.PLAYER1);// 实例化玩家1
		if (gameType == GameType.TWO_PLAYER) {// 如果是双人模式
			play2 = new Tank(448, 537, ImageUtil.PLAYER2_UP_IMAGE_URL, this, TankType.PLAYER2);// 实例化玩家2
			playerTanks.add(play2);// 玩家坦克集合添加玩家2
		}
		playerTanks.add(play1);// 玩家坦克集合添加玩家1

		botTanks = new ArrayList<>();// 实例化电脑坦克集合
		botTanks.add(new BotTank(botX[0], 1, this, TankType.BOTTANK));// 在第一个位置添加电脑坦克
		botTanks.add(new BotTank(botX[1], 1, this, TankType.BOTTANK));// 在第二个位置添加电脑坦克
		botTanks.add(new BotTank(botX[2], 1, this, TankType.BOTTANK));// 在第三个位置添加电脑坦克
		botReadyCount -= 3;// 准备出场的坦克总数减去初始化数量
		allTanks.addAll(playerTanks);// 所有坦克集合添加玩家坦克集合
		allTanks.addAll(botTanks);// 所有坦克集合添加电脑坦克集合
		base = new BaseWall(360, 520);// 实例化基地
		initWalls();// 初始化地图中的墙块
	}

	/**
	 * 组件监听
	 */
	private void addListener() {
		frame.addKeyListener(this);// 主窗体载入键盘监听，本类已实现KeyListener接口
	}

	/**
	 * 初始化地图中的墙块
	 */
	@SuppressWarnings("static-access")
	public void initWalls() {
		Map map = Map.getMap(level);// 获取当前关卡的地图对象
		walls.addAll(map.getWalls());// 墙块集合添加当前地图中所有墙块
		walls.add(base);// 墙块集合添加基地
	}

	/**
	 * 重写绘制组件方法
	 */
	public void paint(Graphics g) {
		paintTankActoin();// 执行坦克动作
		createBotTank();// 循环创建电脑坦克
		paintImage();// 绘制主要的图片
		g.drawImage(image, 0, 0, this); // 将主图片绘制到面板上
		System.gc();
	}

	/**
	 * 绘制主图片
	 */
	private void paintImage() {
		g.setColor(Color.BLACK);// 使用黑色背景
		g.fillRect(0, 0, image.getWidth(), image.getHeight());// 填充一个覆盖整个图片的黑色矩形
		panitBoom();// 绘制爆炸效果
		paintBotCount();// 在屏幕顶部绘制剩余坦克数量
		panitBotTanks();// 绘制电脑坦克
		panitPlayerTanks();// 绘制玩家坦克
		allTanks.addAll(playerTanks);// 坦克集合添加玩家坦克集合
		allTanks.addAll(botTanks);// 坦克集合添加电脑坦克集合
		panitWalls();// 绘制墙块
		panitBullets();// 绘制子弹
		paintTool();// 绘制道具
		
		if (botSurplusCount == 0) {// 如果所有电脑都被消灭
			stopThread();// 结束游戏帧刷新线程
			paintBotCount();// 在屏幕顶部绘制剩余坦克数量
			g.setFont(new Font("楷体", Font.BOLD, 50));// 设置绘图字体
			g.setColor(Color.green);// 使用绿色
			g.drawString("胜   利 !", 250, 400);// 在指定坐标绘制文字
			gotoNextLevel();// 进入下一关卡
		}

		if (gameType == GameType.ONE_PLAYER) {// 如果是单人模式
			if (!play1.isAlive()&&play1.getLife()==0) {// 如果玩家1阵亡,并且玩家1的生命数等于0
				stopThread();// 结束游戏帧刷新线程
				boomImage.add(new Boom(play1.x, play1.y));// 添加玩家1爆炸效果
				panitBoom();// 绘制爆炸效果
				paintGameOver();// 在屏幕中央绘制game over
				gotoPrevisousLevel();// 重新进入本关卡
			}
		} else if(gameType == GameType.TWO_PLAYER){// 如果是双人模式
			if (play1.isAlive() && !play2.isAlive() && play2.getLife()==0) {// 如果玩家1是 幸存者
				survivor = play1;// 幸存者是玩家1
			} else if (!play1.isAlive() && play1.getLife()==0 && play2.isAlive()) {
				survivor = play2;// 幸存者是玩家2
			} else if (!(play1.isAlive() || play2.isAlive())) {// 如果两个玩家全部阵亡
				stopThread();// 结束游戏帧刷新线程
				boomImage.add(new Boom(survivor.x, survivor.y));// 添加幸存者爆炸效果
				panitBoom();// 绘制爆炸效果
				paintGameOver();// 在屏幕中央绘制game over
				gotoPrevisousLevel();// 重新进入本关卡
			}
		}

		if (!base.isAlive()) {// 如果基地被击中
			stopThread();// 结束游戏帧刷新线程
			paintGameOver();// 在屏幕中央绘制game over
			base.setImage(ImageUtil.BREAK_BASE_IMAGE_URL);// 基地使用阵亡图片
			gotoPrevisousLevel();// 重新进入本关卡
		}
		g.drawImage(base.getImage(), base.x, base.y, this);// 绘制基地
	}
	/**
	 * 绘制道具
	 */
	private void paintTool() {
		if(toolTimer>=4500) {
			toolTimer=0;// 重新计时
			tool.changeToolType();
		}else {
			toolTimer+=FRESHTIME;
		}
		if(tool.getAlive()) {
			tool.draw(g);
		}
	}
	/**
	 * 在屏幕顶部绘制剩余坦克数量
	 */
	private void paintBotCount() {
		g.setColor(Color.ORANGE);// 使用橙色
		g.drawString("敌方坦克剩余：" + botSurplusCount, 337, 15);// 在指定坐标绘制字符串
	}

	/**
	 * 在屏幕中央绘制game over
	 */
	private void paintGameOver() {
		g.setFont(new Font("楷体", Font.BOLD, 50));// 设置绘图字体
		g.setColor(Color.RED);// 设置绘图颜色
		g.drawString("Game Over !", 250, 400);// 在指定坐标绘制文字
		new AudioPlayer(AudioUtil.GAMEOVER).new AudioThread().start();//新建一个音效线程，用于播放音效
	}

	/**
	 * 绘制爆炸效果
	 */
	private void panitBoom() {
		for (int i = 0; i < boomImage.size(); i++) {// 循环遍历爆炸效果集合
			Boom boom = boomImage.get(i);// 获取爆炸对象
			if (boom.isAlive()) {// 如果爆炸效果有效
				AudioClip blast=audios.get(2);// 获取爆炸音效对象
				blast.play();// 播放爆炸音效
				boom.show(g);// 展示爆炸效果
			} else {// 如果爆炸效果无效
				boomImage.remove(i);// 在集合中刪除此爆炸对象
				i--;// 循环变量-1，保证下次循环i的值不会变成i+1，以便有效遍历集合，且防止下标越界
			}
		}
	}

	/**
	 * 绘制墙块
	 */
	private void panitWalls() {
		for (int i = 0; i < walls.size(); i++) {// 循环遍历墙块集合
			Wall w = walls.get(i);// 获取墙块对象
			if (w.isAlive()) {// 如果墙块有效
				g.drawImage(w.getImage(), w.x, w.y, this);// 绘制墙块
			} else {// 如果墙块无效
				walls.remove(i);// 在集合中刪除此墙块
				i--;// 循环变量-1，保证下次循环i的值不会变成i+1，以便有效遍历集合，且防止下标越界
			}
		}
	}

	/**
	 * 绘制子弹
	 */
	private void panitBullets() {
		for (int i = 0; i < bullets.size(); i++) {// 循环遍历子弹集合
			Bullet b = bullets.get(i);// 获取子弹对象
			if (b.isAlive()) {// 如果子弹有效
				b.move();// 子弹执行移动操作
				b.hitBase();// 子弹执行击中基地判断
				b.hitWall();// 子弹执行击中墙壁判断
				b.hitTank();// 子弹执行击中坦克判断
				//Fb.hitIronWall();
				b.hitBullet();// 子弹执行抵消判断
				g.drawImage(b.getImage(), b.x, b.y, this);// 绘制子弹
			} else {// 如果子弹无效
				bullets.remove(i);// 在集合中刪除此子弹
				i--;// 循环变量-1，保证下次循环i的值不会变成i+1，以便有效遍历集合，且防止下标越界
			}
		}
	}

	/**
	 * 绘制电脑坦克
	 * 
	 */
	private void panitBotTanks() {
		for (int i = 0; i < botTanks.size(); i++) {// 循环遍历电脑坦克集合
			BotTank t = (BotTank) botTanks.get(i);// 获取电脑坦克对象
			if (t.isAlive()) {// 如果坦克存活
				if(!t.isPause()) {//如果电脑坦克不处于暂停状态
					t.go();// 电脑坦克展开行动	
				} 
				if(t.isPause()) {// 电脑坦克处于暂停状态
					if(pauseTimer>2500) {// 如果暂停时间大于2.5秒
						t.setPause(false);// 解除暂停状态
						pauseTimer=0;// 下一次暂停状态重新计时
					}
					pauseTimer+=FRESHTIME;// 暂停时间开始累积
				}
				g.drawImage(t.getImage(), t.x, t.y, this);// 绘制坦克
			} else {// 如果坦克阵亡
				botTanks.remove(i);// 集合中删除此坦克
				i--;// 循环变量-1，保证下次循环i的值不会变成i+1，以便有效遍历集合，且防止下标越界
				boomImage.add(new Boom(t.x, t.y));// 在坦克位置创建爆炸效果
				decreaseBot();// 剩余坦克数量-1
			}
		}
	}

	/**
	 * 绘制玩家坦克
	 */
	private void panitPlayerTanks() {
		for (int i = 0; i < playerTanks.size(); i++) {// 循环遍历玩家坦克
			Tank t = playerTanks.get(i);// 获取玩家坦克对象
			if (t.isAlive()) {// 如果坦克存活
				t.hitTool();//判断是否碰撞到道具
				t.addStar();
				g.drawImage(t.getImage(), t.x,t.y, this);// 绘制坦克
			} else {// 如果坦克阵亡
				//TankType type=t.getTankType();
				//int life=t.getLife();
				playerTanks.remove(i);// 集合中删除此坦克
				boomImage.add(new Boom(t.x, t.y));// 在坦克位置创建爆炸效果
				AudioClip blast=audios.get(2);
				blast.play();
				t.setLife();
				if(t.getLife()>0) {
					if(t.getTankType()==TankType.PLAYER1) {
						play1 = new Tank(278, 537, ImageUtil.PLAYER1_UP_IMAGE_URL, this, TankType.PLAYER1);// 实例化玩家1
						playerTanks.add(play1);// 玩家坦克集合添加玩家1
					}
					if(t.getTankType()==TankType.PLAYER2) {
							play2 = new Tank(448, 537, ImageUtil.PLAYER2_UP_IMAGE_URL, this, TankType.PLAYER2);// 实例化玩家2
							playerTanks.add(play2);// 玩家坦克集合添加玩家2
					}
				}
				
				//i--;// 循环变量-1，保证下次循环i的值不会变成i+1，以便有效遍历集合，且防止下标越界
				
			}
		}
	}

	/**
	 * 结束游戏帧刷新
	 */
	private synchronized void stopThread() {
		frame.removeKeyListener(this);// 主窗体删除本类键盘事件监听对象
		finish = true;// 游戏停止标志为true
	}

	/**
	 * 游戏帧刷新线程内部类
	 */
	private class FreshThead extends Thread {
		public void run() {// 线程主方法
			while (!finish) {// 如果游戏未停止
				repaint();// 执行本类重绘方法
				System.gc();// 绘制一次会产生大量垃圾对象，回收内存
				try {
					Thread.sleep(FRESHTIME);// 指定时间后重新绘制界面
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 添加电脑坦克，如果场上坦克未到达最大值，每4秒钟之后在三个出生位置随机选择其一，创建电脑坦克。
	 */
	
	private void createBotTank() {
		int index = r.nextInt(3);
		createBotTimer += FRESHTIME;// 计时器按照刷新时间递增
		// “当场上电脑小于场上最大数时” 并且 “准备上场的坦克数量大于0” 并且 “计时器记录已过去1.5秒钟”
		if (botTanks.size() < botMaxInMap && botReadyCount > 0 && createBotTimer >= 1500) {
			
			Rectangle bornRect = new Rectangle(botX[index], 1, 35, 35);// 创建坦克随机出生区域
			for (int i = 0, lengh = allTanks.size(); i < lengh; i++) {// 循环遍历所有坦克集合
				Tank t = allTanks.get(i);// 获取坦克对象
				if (t.isAlive() && t.hit(bornRect)) {// 如果场上存在与随机位置重合并存活的坦克
					return;// 结束方法
				}
			}
			botTanks.add(new BotTank(botX[index], 1, GamePanel.this, TankType.BOTTANK));// 在随机位置创造电脑坦克
			new AudioPlayer(AudioUtil.ADD).new AudioThread().start();
			botReadyCount--;// 准备上场电脑数量-1
			createBotTimer = 0;// 产生电脑计时器重新计时
		}
	}
	
	/**
	 * 进入下一关卡
	 */
	private void gotoNextLevel() {
		Thread jump = new JumpPageThead(Level.nextLevel());// 创建跳转到下一关卡的线程
//		Thread jump = new JumpPageThead(Level.level.nextLevel());
		jump.start();// 启动线程
	}

	/**
	 * 重新进入本关卡
	 */
	private void gotoPrevisousLevel() {
		Thread jump = new JumpPageThead(Level.previsousLevel());// 创建重新进入本关卡的线程
//		Thread jump = new JumpPageThead(Level.level.previsousLevel());
		jump.start();// 启动线程
	}

	/**
	 * 剩余坦克数量减少1
	 */
	public void decreaseBot() {
		botSurplusCount--;// 电脑剩余数量-1
	}

	/**
	 * 按键按下时
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {// 判断按下的按键值
		case KeyEvent.VK_J:// 如果按下的是“J”
			j_key = true;// “J”按下标志为true
			break;
		case KeyEvent.VK_W:// 如果按下的是“W”
			w_key = true;// “W”按下标志为true
			a_key = false;// “A”按下标志为false
			s_key = false;// “S”按下标志为false
			d_key = false;// “D”按下标志为false
			break;
		case KeyEvent.VK_A:// 如果按下的是“A”
			w_key = false;// “W”按下标志为false
			a_key = true;// “A”按下标志为true
			s_key = false;// “S”按下标志为false
			d_key = false;// “D”按下标志为false
			break;
		case KeyEvent.VK_S:// 如果按下的是“S”
			w_key = false;// “W”按下标志为false
			a_key = false;// “A”按下标志为false
			s_key = true;// “S”按下标志为true
			d_key = false;// “D”按下标志为false
			break;
		case KeyEvent.VK_D:// 如果按下的是“D”
			w_key = false;// “W”按下标志为false
			a_key = false;// “A”按下标志为false
			s_key = false;// “S”按下标志为false
			d_key = true;// “D”按下标志为true
			break;
		case KeyEvent.VK_HOME:// 如果按下的是“HOME”，效果同下
		case KeyEvent.VK_NUMPAD0:// 如果按下的是小键盘数字0
			num0_key = true;// 小键盘数字0按下标志为true
			break;
		case KeyEvent.VK_UP:// 如果按下的是“↑”
			up_key = true;// “↑”按下标志为true
			down_key = false;// “↓”按下标志为false
			right_key = false;// “→”按下标志为false
			left_key = false;// “←”按下标志为false
			break;
		case KeyEvent.VK_DOWN:// 如果按下的是“↓”
			up_key = false;// “↑”按下标志为false
			down_key = true;// “↓”按下标志为true
			right_key = false;// “→”按下标志为false
			left_key = false;// “←”按下标志为false
			break;
		case KeyEvent.VK_LEFT:// 如果按下的是“←”
			up_key = false;// “↑”按下标志为false
			down_key = false;// “↓”按下标志为false
			right_key = false;// “→”按下标志为false
			left_key = true;// “←”按下标志为true
			break;
		case KeyEvent.VK_RIGHT:// 如果按下的是“→”
			up_key = false;// “↑”按下标志为false
			down_key = false;// “↓”按下标志为false
			right_key = true;// “→”按下标志为true
			left_key = false;// “←”按下标志为false
			break;
		}
	}

	/**
	 * 根据按键按下状态，让坦克执行相应动作
	 */
	private void paintTankActoin() {
		if (j_key) {// 如果“Y”键是按下状态
			play1.attack();// 玩家1坦克攻击
		}
		if (w_key) {// 如果“W”键是按下状态
			play1.upWard();// 玩家1坦克向上移动
		}
		if (d_key) {// 如果“D”键是按下状态
			play1.rightWard();// 玩家1坦克向右移动
		}
		if (a_key) {// 如果“A”键是按下状态
			play1.leftWard();// 玩家1坦克左移动
		}
		if (s_key) {// 如果“S”键是按下状态
			play1.downWard();// 玩家1坦克向下移动
		}
		if (gameType == GameType.TWO_PLAYER) {
			if (num0_key) {// 如果“M”键是按下状态
				play2.attack();// 玩家2坦克攻击
			}
			if (up_key) {// 如果“←”键是按下状态
				play2.upWard();// 玩家2坦克向上移动
			}
			if (right_key) {// 如果“→”键是按下状态
				play2.rightWard();// 玩家2坦克向右移动
			}
			if (left_key) {// 如果“↑”键是按下状态
				play2.leftWard();// 玩家2坦克左移动
			}
			if (down_key) {// 如果“↓”键是按下状态
				play2.downWard();// 玩家2坦克后移动
			}
		}
	}

	/**
	 * 按键抬起时
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_J:// 如果抬起的是“J”
			j_key = false;// “J”按下标志为false
			break;
		case KeyEvent.VK_W:// 如果抬起的是“W”
			w_key = false;// “W”按下标志为false
			break;
		case KeyEvent.VK_A:// 如果抬起的是“A”
			a_key = false;// “A”按下标志为false
			break;
		case KeyEvent.VK_S:// 如果抬起的是“S”
			s_key = false;// “S”按下标志为false
			break;
		case KeyEvent.VK_D:// 如果抬起的是“D”
			d_key = false;// “D”按下标志为false
			break;
		case KeyEvent.VK_HOME:// 如果抬起的是“HOME”，效果同下
		case KeyEvent.VK_NUMPAD0:// 如果抬起的是小键盘0
			num0_key = false;// 小键盘0按下标志为false
			break;
		case KeyEvent.VK_UP:// 如果抬起的是“↑”
			up_key = false;// “↑”按下标志为false
			break;
		case KeyEvent.VK_DOWN:// 如果抬起的是“↓”
			down_key = false;// “↓”按下标志为false
			break;
		case KeyEvent.VK_LEFT:// 如果抬起的是“←”
			left_key = false;// “←”按下标志为false
			break;
		case KeyEvent.VK_RIGHT:// 如果抬起的是“→”
			right_key = false;// “→”按下标志为false
			break;
		}
	}

	/**
	 * 向子弹集合中添加子弹
	 * 
	 * @param b
	 *              添加的子弹
	 */
	public void addBullet(Bullet b) {
		bullets.add(b);// 子弹集合中添加子弹
	}

	/**
	 * 获取所有墙块集合
	 * 
	 * @return 所有墙块
	 */
	public List<Wall> getWalls() {
		return walls;
	}

	/**
	 * 获取基地对象
	 * 
	 * @return 基地
	 */
	public BaseWall getBase() {
		return base;
	}

	/**
	 * 获取所有坦克集合
	 * 
	 * @return 所有坦克
	 */
	public List<Tank> getTanks() {
		return allTanks;
	}
	/**
	 * 获取游戏面板所有子弹
	 * @return
	 */
	public List<Bullet> getBullets(){
		return bullets;
	}
	/**
	 * 获取游戏面板中所有电脑坦克
	 * @return 面板中所有存在的电脑坦克
	 */
	public List<Tank> getBotTanks(){
		return botTanks;
	}
	/**
	 * 获取到游戏面板的道具对象
	 * @return
	 */
	public Tool getTool() {
		return tool;
	}
	/**
	 * 游戏结束跳转线程
	 */
	private class JumpPageThead extends Thread {
		int level;// 跳转的关卡

		/**
		 * 跳转线程构造方法
		 * 
		 * @param level
		 *            - 跳转的关卡
		 */
		public JumpPageThead(int level) {
			this.level = level;
		}

		/**
		 * 线程主方法
		 */
		public void run() {
			try {
				Thread.sleep(1000);// 1秒钟后
				frame.setPanel(new LevelPanel(level, frame, gameType));// 主窗体跳转到指定关卡
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 键入某按键事件
	 */
	public void keyTyped(KeyEvent e) {
		// 不实现此方法，但不可删除
	}
	/**
	 * 获取玩家坦克
	 * @return
	 */
	public List<Tank> getPlayerTanks() {
		// TODO Auto-generated method stub
		return playerTanks;
	}
}
