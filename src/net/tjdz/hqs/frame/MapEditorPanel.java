package net.tjdz.hqs.frame;

import net.tjdz.hqs.enumtype.WallType;
import net.tjdz.hqs.model.Level;
import net.tjdz.hqs.model.Map;
import net.tjdz.hqs.model.wall.*;
import net.tjdz.hqs.util.ImageUtil;
import net.tjdz.hqs.util.MapIO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.Random;

/**
 * 地图编辑器面板
 */
public class MapEditorPanel extends JPanel implements MouseListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//墙块图片数组
	private Image[] wallImgs= {
		Toolkit.getDefaultToolkit().createImage(ImageUtil.BRICKWALL_IMAGE_URL),
		Toolkit.getDefaultToolkit().createImage(ImageUtil.GRASSWALL_IMAGE_URL),
		Toolkit.getDefaultToolkit().createImage(ImageUtil.IRONWALL_IMAGE_URL),
		Toolkit.getDefaultToolkit().createImage(ImageUtil.RIVERWALL_IMAGE_URL)
	};

	private WallType wallType;
	private Graphics gra;
	
	int count=Level.getCount();
	int level=1;
	public static List<Wall> walls=Map.getWalls();
	private BaseWall base;
	private MainFrame frame;

	
	public  MapEditorPanel(final MainFrame frame) {
		this.frame=frame;
		this.addMouseListener(this);
		
		base = new BaseWall(360, 520);
		//初始化地图
		initWalls();
		
		JButton save=new JButton("保存");
		save.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				count++;
				Boolean b = MapIO.writeMap(count+"");
				if(b) {
					JOptionPane.showMessageDialog(null, "保存成功");
				}
				repaint();
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
		this.add(save);
		this.add(back);
		
	}
	private void gotoLoginPanel() {
		frame.removeMouseListener(this);
		frame.setPanel(new LoginPanel(frame));
	}
	@Override
	public void paint(Graphics g) {
		super.setBackground(Color.BLACK);
		super.paint(g);
		gra=g;
		
		g.setColor(Color.ORANGE);
		
		g.drawString("当前关卡："+level, 0, 12);
		g.drawString("关卡总数："+count, 0, 24);
		g.setColor(Color.CYAN);
		// 画出横向线段，参考线
		for(int i=0;i<560;i+=40) {
			g.drawLine(0, i, 760, i);
		}
		// 画出纵向线段
		for(int j=0;j<780;j+=40) {
			//g.drawLine(x1, y1, x2, y2);
			g.drawLine(j, 0, j, 600);
		}
		// 画出几个固定的墙块图
		g.drawImage(wallImgs[0], 762, 0, this);
		g.drawImage(wallImgs[1], 762, 20, this);
		g.drawImage(wallImgs[2], 762, 40, this);
		g.drawImage(wallImgs[3], 762, 60, this);
		
		// 画一个擦子
		g.setColor(Color.MAGENTA);
//		g.drawLine(762, 80, 762, 100);
//		g.drawLine(762, 80, 782, 80);
//		g.drawLine(782, 80, 782, 100);
//		g.drawLine(762, 100, 782, 100);
		g.drawRect(762, 80, 20, 19);
		//g.fillRect(762, 80, 20, 19);
		
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
				walls.remove(w);
			}
			if (w.isAlive()&&(w.x<760)) {// 如果墙块有效
				gra.drawImage(w.getImage(), w.x, w.y, this);// 绘制墙块
			} else {// 如果墙块无效
				walls.remove(i);// 在集合中刪除此墙块
				i--;// 循环变量-1，保证下次循环i的值不会变成i+1，以便有效遍历集合，且防止下标越界
			}
		}
	}
	/**
	 * 初始化墙块
	 */
	public void initWalls() {
		Random r=new Random();
		level=r.nextInt(count)+1;// 随机获取一个关卡
		Map.getMap(level);// 获取当前关卡的地图对象
		walls.add(base);// 墙块集合添加基地
		//MapIO.removeDuplicate(walls);// 去掉可能存在的重复墙块
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		Point clickedPoint=e.getPoint();
		if(clickedPoint.x>=762&&clickedPoint.y<=100) {
			if(clickedPoint.y>0&&clickedPoint.y<20) {
				wallType=WallType.BRICK;
				setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
			}else if(clickedPoint.y>20&&clickedPoint.y<40) {
				wallType=WallType.GRASS;
				setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
			}else if(clickedPoint.y>40&&clickedPoint.y<60) {
				wallType=WallType.IRON;
				setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
			}else if(clickedPoint.y>60&&clickedPoint.y<80) {
				wallType=WallType.RIVER;
				setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
			}else if(clickedPoint.y>80&&clickedPoint.y<100) {
				wallType=null;
				setCursor(new Cursor(Cursor.HAND_CURSOR));
			}
		}
		repaint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		Point p=new Point();
		p=e.getPoint();//获取到鼠标的当前释放点
		p=new Point((p.x-p.x%20),(p.y-p.y%20));//将鼠标的当前按下点格式化为20的倍数，因为墙块是20x20的
		Point base1=new Point(360,520);
		Point base2=new Point(380,520);
		Point base3=new Point(360,540);
		Point base4=new Point(380,540);
		if((p.x<760)&&!p.equals(base1)&&!p.equals(base2)&&!p.equals(base3)&&!p.equals(base4)) {
			//遍历集合，判断释放点是否有墙块，如果有就擦掉
			for(int i=0;i<walls.size();i++) {
				Wall w=walls.get(i);
				if(w.x>=760||(w.x==p.x&&w.y==p.y&&!w.equals(base))) {
					//w.setAlive(false);
					walls.remove(w);
					repaint();
				}
			}
			//下面这样写会抛异常，这是个高级异常，这个异常叫做并发修改异常
//			for(Wall w:walls) {
//				if(w.x==p.x&&w.y==p.y) {
//					walls.remove(w);
//					repaint();
//				}
//			}
			if(wallType!=null) {//如果墙块类型不为空，添加墙块
				addWall(wallType, p);
			}
		}
	}
	private void addWall(WallType type,Point p) {
		switch(type) {
		case BRICK:
			BrickWall b=new BrickWall(p.x, p.y);
			for(int i=0;i<walls.size();i++) {
				Wall w=walls.get(i);
				if(w.equals(b)) {
					walls.remove(w);
				}
			}
			walls.add(b);
			break;
		case GRASS:
			GrassWall grass=new GrassWall(p.x, p.y);
			for(int i=0;i<walls.size();i++) {
				Wall w=walls.get(i);
				if(w.equals(grass)) {
					walls.remove(w);
				}
			}
			walls.add(grass);
			break;
		case IRON:
			IronWall iron=new IronWall(p.x, p.y);
			for(int i=0;i<walls.size();i++) {
				Wall w=walls.get(i);
				if(w.equals(iron)) {
					walls.remove(w);
				}
			}
			walls.add(iron);
			break;
		case RIVER:
			RiverWall river=new RiverWall(p.x, p.y);
			for(int i=0;i<walls.size();i++) {
				Wall w=walls.get(i);
				if(w.equals(river)) {
					walls.remove(w);
				}
			}
			walls.add(river);
			break;
		default:
			break;
		}
		repaint();
	}
		
}
