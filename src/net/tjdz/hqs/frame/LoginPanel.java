package net.tjdz.hqs.frame;
import net.tjdz.hqs.enumtype.GameType;
import net.tjdz.hqs.util.ImageUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;

/**
 * 登陆面板（选择游戏模式）
 */
public class LoginPanel extends JPanel implements KeyListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GameType type;// 游戏模式
	private Image backgroud;// 背景图片
	private Image tank;// 坦克图标
	private int y1 = 270, y2 = 330, y3=390, y4=450;// 坦克图标可选择的四个Y坐标
	private int tankY = 270;// 坦克图标Y坐标
	private MainFrame frame;
	
	/**
	 * 登陆面板构造方法
	 * 
	 * @param frame 主窗体
	 */
	public LoginPanel(MainFrame frame) {
		this.frame=frame;
		addListener();
		try {
			backgroud = ImageIO.read(new File(ImageUtil.LOGIN_BACKGROUD_IMAGE_URL));// 读取背景图片
			tank = ImageIO.read(new File(ImageUtil.PLAYER1_RIGHT_IMAGE_URL));// 读取坦克图标
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 重写绘图方法
	 */
	@Override
	public void paint(Graphics g) {
		g.drawImage(backgroud, 0, 0, getWidth(), getHeight(), this);// 绘制背景图片，填满整个面板
		Font font = new Font("黑体", Font.BOLD, 35);// 创建体字
		g.setFont(font);// 使用字体
		g.setColor(Color.BLACK);// 使用黑色
		g.drawString("单人游戏模式", 300, 300);// 绘制第一行文字
		g.drawString("双人游戏模式", 300, 360);// 绘制第二行文字
		g.drawString("预览关卡地图", 300, 420);// 绘制第三行文字
		g.drawString("自定义地图模式", 300, 480);// 绘制第四行文字
		
		g.drawImage(tank, 260, tankY, this);// 绘制坦克图标
	}

	/**
	 * 跳转关卡面板
	 */
	private void gotoLevelPanel() {
		frame.removeKeyListener(this);// 主窗体删除键盘监听
		frame.setPanel(new LevelPanel(1, frame, type));// 主窗体跳转至关卡面板
	}

	/**
	 * 添加组件监听
	 */
	private void addListener() {
		frame.addKeyListener(this);// 主窗体载入键盘监听，本类已实现KeyListener接口
	}

	/**
	 * 当按键按下时
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();// 获取按下的按键值
		switch (code) {// 判断按键值
		case KeyEvent.VK_UP:// 如果按下的是“↑”
			if(tankY == y1) {
				tankY = y4;
			}else if(tankY == y4) {
				tankY = y3;
			}else if(tankY == y3){
				tankY = y2;
			}else if(tankY == y2){
				tankY=y1;
			}
			repaint();// 按键按下之后，需要重新绘图
			break;
		case KeyEvent.VK_DOWN:// 如果按下的是“↓”
			if (tankY == y4) {
				tankY = y1;
			}else if(tankY ==y1){
				tankY = y2;
			}else if(tankY == y2){
				tankY = y3;
			}else if(tankY == y3){
				tankY=y4;
			}
			repaint();// 按键按下之后，需要重新绘图
			break;
		case KeyEvent.VK_ENTER:// 如果按下的是“Enter”
			if (tankY == y1) {// 如果坦克图标在第一个位置
				type=GameType.ONE_PLAYER;
				gotoLevelPanel();// 跳转关卡面板
			}
			if(tankY == y2){
				type = GameType.TWO_PLAYER;// 游戏模式为双人模式
				gotoLevelPanel();// 跳转关卡面板
			}
			if(tankY == y4){
				type = null;
				frame.removeKeyListener(this);//这里一定要把键盘监听移除掉，否则会自动触发
				frame.setPanel(new MapEditorPanel(frame));
			}
			if(tankY == y3)	{
				type=null;
				frame.removeKeyListener(this);
				frame.setPanel(new MapPreViewPanel(frame));
			}
		}
		
	}

	/**
	 * 按键抬起时
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		// 不实现此方法，但不可删除
	}

	/**
	 * 键入某按键事件
	 */
	@Override
	public void keyTyped(KeyEvent e) {
		// 不实现此方法，但不可删除
	}
	
}
