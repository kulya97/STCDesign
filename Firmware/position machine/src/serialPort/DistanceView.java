package serialPort;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
 
import javax.swing.JFrame;
import javax.swing.JPanel;

 
public class DistanceView extends JFrame {
 
	private List<Integer> values;// 保存接受的数据容器
	//private static final int MAX_VALUE = 270;// 接受到的数据最大值
	private static final int MAX_COUNT_OF_VALUES = 90;// 最多保存数据的个数
	// private
	private MyCanvas draw;
	//窗口大小
	private final int WINDOW_WIDTH=700;
	private final int WINDOW_HEIGHT=500;
	private final int WINDOW_X=100;
	private final int WINDOW_Y=100;
	//画布大小
	private final int JPANE_WIDTH=600;
	private final int JPANE_HEIGHT=400;
	private final int JPANE_X=30;
	private final int JPANE_Y=30;
	// 坐标轴长度
	private final int AXIS_X = 500;// 横
	private final int AXIS_Y = 300;// 纵
	// 原点坐标
	private final int Origin_X = 50;
	private final int Origin_Y = JPANE_HEIGHT - 50;
	
	// X轴上的时间分度值（1分度=40像素）
	private final int TIME_INTERVAL = 50;
	// Y轴上值
	private final int DISTANCE_INTERVAL = 30;
	private final int TIME_NUM = AXIS_X/TIME_INTERVAL-1;
	
	private final int DISTANCE_NUM = AXIS_Y/DISTANCE_INTERVAL-1;
	// X,Y轴终点坐标
	private final int XAxis_X = Origin_X + AXIS_X;
	private final int XAxis_Y = Origin_Y;
	private final int YAxis_X = Origin_X;
	private final int YAxis_Y = Origin_Y - AXIS_Y;
	private final int MAX_X=TIME_NUM*TIME_INTERVAL;
	private final int MAX_Y=DISTANCE_NUM*DISTANCE_INTERVAL;
	
	private double datnum=1;
	public void getdat(String str) {
		datnum = Double.valueOf(str);
		addValue((int) (Origin_Y-MAX_Y/4.5*datnum));
	}
//	public static void main(String[] args) {
//		new Chart_test().setVisible(true);
//	}
	public DistanceView() {
		super();
		setLayout(null);
		this.setBounds(WINDOW_X, WINDOW_Y, WINDOW_WIDTH, WINDOW_HEIGHT);// 设置窗体大小
		this.setBackground(Color.white);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// 设置窗体关闭模式
		draw = new MyCanvas();
		draw.setBounds(JPANE_X, JPANE_Y, JPANE_WIDTH, JPANE_HEIGHT);
		draw.setBackground(Color.white);
		add(draw);
		this.setTitle("距离折线图");// 设置窗体标题
		values = Collections.synchronizedList(new ArrayList<Integer>());// 防止引起线程异常
		// 创建一个随机数线程
		new Thread(new Runnable() {
			public void run() {
				try {
					while (true) {
						repaint();
						Thread.sleep(100);
					}
				} catch (InterruptedException b) {
					b.printStackTrace();
				}
			}
 
		}).start();

	}
 
	public void addValue(int value) {
		// 循环的使用一个接受数据的空间
		if (values.size() > MAX_COUNT_OF_VALUES) {
			values.remove(0);
		}
		values.add(value);
	}
 
	// 画布重绘图
	class MyCanvas extends JPanel {
		private static final long serialVersionUID = 1L;
 
		public void paintComponent(Graphics g) {
			Graphics2D g2D = (Graphics2D) g;
 
			Color c = new Color(200, 70, 0);
			g.setColor(c);
			super.paintComponent(g);
 
			// 绘制平滑点的曲线
			g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			int w = MAX_X;// 起始点
			int xDelta = w / MAX_COUNT_OF_VALUES;
			int length = values.size();
			
			g2D.setStroke(new BasicStroke(1.5f));// 轴线粗度
			g2D.setColor(Color.red);
			for (int i = 0; i < length - 1; ++i) {
			//	g2D.drawLine(xDelta * (MAX_COUNT_OF_VALUES - length + i), values.get(i),
				//		xDelta * (MAX_COUNT_OF_VALUES - length + i + 1), values.get(i + 1));
			g2D.drawLine(Origin_X+xDelta*i, values.get(i),
					Origin_X+xDelta*(i+1), values.get(i + 1));
			}
			g.setColor(Color.black);
			// 画坐标轴
			g2D.setStroke(new BasicStroke(Float.parseFloat("2.0F")));// 轴线粗度
			// X轴以及方向箭头
			g.drawLine(Origin_X, Origin_Y, XAxis_X, XAxis_Y);// x轴线的轴线
			g.drawLine(XAxis_X, XAxis_Y, XAxis_X - 5, XAxis_Y - 5);// 上边箭头
			g.drawLine(XAxis_X, XAxis_Y, XAxis_X - 5, XAxis_Y + 5);// 下边箭头
 
			// Y轴以及方向箭头
			g.drawLine(Origin_X, Origin_Y, YAxis_X, YAxis_Y);
			g.drawLine(YAxis_X, YAxis_Y, YAxis_X - 5, YAxis_Y + 5);
			g.drawLine(YAxis_X, YAxis_Y, YAxis_X + 5, YAxis_Y + 5);
 
			// 画X轴上的时间刻度（从坐标轴原点起，每隔TIME_INTERVAL(时间分度)像素画一时间点，到X轴终点止）
			g.setColor(Color.BLUE);
			g2D.setStroke(new BasicStroke(Float.parseFloat("1.0f")));
 
			// X轴刻度依次变化情况
			for (int i = Origin_X, j = 0; i < XAxis_X; i += TIME_INTERVAL, j += 1) {
				g.drawString(" " + j, i - 10, Origin_Y + 20);
			}
			g.drawString("时间/s", XAxis_X + 5, XAxis_Y + 5);
 
			// 画Y轴刻度
			for (int i = Origin_Y, j = 0; i > YAxis_Y; i -= DISTANCE_INTERVAL, j += TIME_INTERVAL) {
				g.drawString(j + " ", Origin_X - 30, i + 3);
			}
			g.drawString("距离/cm", YAxis_X - 5, YAxis_Y - 5);// 血压刻度小箭头值
			// 画网格线
			g.setColor(Color.BLACK);
			// 坐标内部横线
			for (int i = Origin_Y; i > YAxis_Y; i -= DISTANCE_INTERVAL) {
				g.drawLine(Origin_X, i, Origin_X + DISTANCE_NUM* TIME_INTERVAL, i);
			}
			// 坐标内部竖线
			for (int i = Origin_X; i < XAxis_X; i += TIME_INTERVAL) {
				g.drawLine(i, Origin_Y, i, Origin_Y - TIME_NUM* DISTANCE_INTERVAL);
			}
 
		}
	}
 
}