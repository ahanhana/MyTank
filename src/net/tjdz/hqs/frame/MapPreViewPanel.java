package net.tjdz.hqs.frame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;

import net.tjdz.hqs.model.Level;
import net.tjdz.hqs.model.Map;
import net.tjdz.hqs.model.wall.BaseWall;
import net.tjdz.hqs.model.wall.Wall;

/**
 * 关卡地图预览面板
 */
public class MapPreViewPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int level=1;
	private List<Wall> walls= new ArrayList<>();
	private BaseWall base;
	private Graphics gra;
	private int count= Level.getCount();
	private MainFrame frame;

	
	public MapPreViewPanel(final MainFrame frame) {
		this.frame=frame;

		base=new BaseWall(360, 520);
		
		//初始化地图
		initWalls();
				
		JButton levelReduce=new JButton("上一关");
		levelReduce.addActionListener(new ActionListener() {
					
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				level--;
				if(level==0) {
					level=count;
				}
				initWalls();
				repaint();
			}
		});
		JButton levelPlus=new JButton("下一关");
		levelPlus.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				level++;
				if(level>count) {
					level=1;
				}
				//System.out.println(level);
				initWalls();
				repaint();
				//System.out.println("关卡："+level+"，墙块总数："+walls.size());
			}
		});	
		JButton back=new JButton("返回");
		back.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				frame.requestFocus();
				gotoLoginPanel();
			}
		});
		this.add(back);
		this.add(levelReduce);
		this.add(levelPlus);
	}
	private void gotoLoginPanel() {
		frame.setPanel(new LoginPanel(frame));
	}
	@Override
	public void paint(Graphics g) {
		super.setBackground(Color.BLACK);
		super.paint(g);
		gra = g;
		
		g.setColor(Color.ORANGE);
		g.drawString("当前关卡："+level, 0, 12);
		g.drawString("关卡总数："+count, 0, 24);


		
		//画出地图
		paintWalls();
	}
	/**
	 * 绘制墙块
	 */
	private void paintWalls() {
		for (int i = 0; i < walls.size(); i++) {// 循环遍历墙块集合
			Wall w = walls.get(i);// 获取墙块对象
			if(w.x>=760) {
				w.setAlive(false);
			}
			if (w.isAlive()) {// 如果墙块有效
				gra.drawImage(w.getImage(), w.x, w.y, this);// 绘制墙块
			} else {// 如果墙块无效
				walls.remove(i);// 在集合中刪除此墙块
				i--;// 循环变量-1，保证下次循环i的值不会变成i+1，以便有效遍历集合，且防止下标越界
			}
		}
	}
	public void initWalls() {
		Map.getMap(level);// 获取当前关卡的地图对象
		//walls=map.getWalls();
		//walls.addAll(map.getWalls());// 墙块集合添加当前地图中所有墙块
		walls.add(base);// 墙块集合添加基地
	}
	
}
