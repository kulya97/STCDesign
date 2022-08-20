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
 
	private List<Integer> values;// ������ܵ���������
	//private static final int MAX_VALUE = 270;// ���ܵ����������ֵ
	private static final int MAX_COUNT_OF_VALUES = 90;// ��ౣ�����ݵĸ���
	// private
	private MyCanvas draw;
	//���ڴ�С
	private final int WINDOW_WIDTH=700;
	private final int WINDOW_HEIGHT=500;
	private final int WINDOW_X=100;
	private final int WINDOW_Y=100;
	//������С
	private final int JPANE_WIDTH=600;
	private final int JPANE_HEIGHT=400;
	private final int JPANE_X=30;
	private final int JPANE_Y=30;
	// �����᳤��
	private final int AXIS_X = 500;// ��
	private final int AXIS_Y = 300;// ��
	// ԭ������
	private final int Origin_X = 50;
	private final int Origin_Y = JPANE_HEIGHT - 50;
	
	// X���ϵ�ʱ��ֶ�ֵ��1�ֶ�=40���أ�
	private final int TIME_INTERVAL = 50;
	// Y����ֵ
	private final int DISTANCE_INTERVAL = 30;
	private final int TIME_NUM = AXIS_X/TIME_INTERVAL-1;
	
	private final int DISTANCE_NUM = AXIS_Y/DISTANCE_INTERVAL-1;
	// X,Y���յ�����
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
		this.setBounds(WINDOW_X, WINDOW_Y, WINDOW_WIDTH, WINDOW_HEIGHT);// ���ô����С
		this.setBackground(Color.white);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// ���ô���ر�ģʽ
		draw = new MyCanvas();
		draw.setBounds(JPANE_X, JPANE_Y, JPANE_WIDTH, JPANE_HEIGHT);
		draw.setBackground(Color.white);
		add(draw);
		this.setTitle("��������ͼ");// ���ô������
		values = Collections.synchronizedList(new ArrayList<Integer>());// ��ֹ�����߳��쳣
		// ����һ��������߳�
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
		// ѭ����ʹ��һ���������ݵĿռ�
		if (values.size() > MAX_COUNT_OF_VALUES) {
			values.remove(0);
		}
		values.add(value);
	}
 
	// �����ػ�ͼ
	class MyCanvas extends JPanel {
		private static final long serialVersionUID = 1L;
 
		public void paintComponent(Graphics g) {
			Graphics2D g2D = (Graphics2D) g;
 
			Color c = new Color(200, 70, 0);
			g.setColor(c);
			super.paintComponent(g);
 
			// ����ƽ���������
			g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			int w = MAX_X;// ��ʼ��
			int xDelta = w / MAX_COUNT_OF_VALUES;
			int length = values.size();
			
			g2D.setStroke(new BasicStroke(1.5f));// ���ߴֶ�
			g2D.setColor(Color.red);
			for (int i = 0; i < length - 1; ++i) {
			//	g2D.drawLine(xDelta * (MAX_COUNT_OF_VALUES - length + i), values.get(i),
				//		xDelta * (MAX_COUNT_OF_VALUES - length + i + 1), values.get(i + 1));
			g2D.drawLine(Origin_X+xDelta*i, values.get(i),
					Origin_X+xDelta*(i+1), values.get(i + 1));
			}
			g.setColor(Color.black);
			// ��������
			g2D.setStroke(new BasicStroke(Float.parseFloat("2.0F")));// ���ߴֶ�
			// X���Լ������ͷ
			g.drawLine(Origin_X, Origin_Y, XAxis_X, XAxis_Y);// x���ߵ�����
			g.drawLine(XAxis_X, XAxis_Y, XAxis_X - 5, XAxis_Y - 5);// �ϱ߼�ͷ
			g.drawLine(XAxis_X, XAxis_Y, XAxis_X - 5, XAxis_Y + 5);// �±߼�ͷ
 
			// Y���Լ������ͷ
			g.drawLine(Origin_X, Origin_Y, YAxis_X, YAxis_Y);
			g.drawLine(YAxis_X, YAxis_Y, YAxis_X - 5, YAxis_Y + 5);
			g.drawLine(YAxis_X, YAxis_Y, YAxis_X + 5, YAxis_Y + 5);
 
			// ��X���ϵ�ʱ��̶ȣ���������ԭ����ÿ��TIME_INTERVAL(ʱ��ֶ�)���ػ�һʱ��㣬��X���յ�ֹ��
			g.setColor(Color.BLUE);
			g2D.setStroke(new BasicStroke(Float.parseFloat("1.0f")));
 
			// X��̶����α仯���
			for (int i = Origin_X, j = 0; i < XAxis_X; i += TIME_INTERVAL, j += 1) {
				g.drawString(" " + j, i - 10, Origin_Y + 20);
			}
			g.drawString("ʱ��/s", XAxis_X + 5, XAxis_Y + 5);
 
			// ��Y��̶�
			for (int i = Origin_Y, j = 0; i > YAxis_Y; i -= DISTANCE_INTERVAL, j += TIME_INTERVAL) {
				g.drawString(j + " ", Origin_X - 30, i + 3);
			}
			g.drawString("����/cm", YAxis_X - 5, YAxis_Y - 5);// Ѫѹ�̶�С��ͷֵ
			// ��������
			g.setColor(Color.BLACK);
			// �����ڲ�����
			for (int i = Origin_Y; i > YAxis_Y; i -= DISTANCE_INTERVAL) {
				g.drawLine(Origin_X, i, Origin_X + DISTANCE_NUM* TIME_INTERVAL, i);
			}
			// �����ڲ�����
			for (int i = Origin_X; i < XAxis_X; i += TIME_INTERVAL) {
				g.drawLine(i, Origin_Y, i, Origin_Y - TIME_NUM* DISTANCE_INTERVAL);
			}
 
		}
	}
 
}