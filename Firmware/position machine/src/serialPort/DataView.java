package serialPort;

import java.awt.Button;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Label;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JOptionPane;

import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

public class DataView extends Frame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<String> commList = null; // 保存可用端口号
	private SerialPort serialPort = null; // 保存串口对象

	private final int WINDOW_WIDTH = 800;
	private final int WINDOW_HEIGHT = 800;
	private final int WINDOW_X = 800;
	private final int WINDOW_Y = 100;
	private Font font = new Font("微软雅黑", Font.BOLD, 25);

	private Label tem = new Label("暂无距离数据", Label.CENTER); // 温度
	private Label hum = new Label("暂无温度数据", Label.CENTER); // 湿度

	private Label bpslab = new Label("波特率：", Label.CENTER); // 温度
	private Label commlab = new Label("选择串口：", Label.CENTER); // 湿度

	private Choice commChoice = new Choice(); // 串口选择（下拉框）
	private Choice bpsChoice = new Choice(); // 波特率选择

	private Button openSerialButton = new Button("打开串口");
	private Button closeSerialButton = new Button("关闭串口");
	private static boolean ISOPEN;
	Image offScreen = null; // 重画时的画布
	private DistanceView distanceview;
	private TemperatureView temperatureview;

	public DataView() {
		commList = SerialTool.findPort(); // 程序初始化时就扫描一次有效串口
	}

	public static void main(String[] args) {
		DataView dataview = new DataView(); // 主界面类（显示监控数据主面板）
		dataview.setVisible(true); // 显示监测界面
		dataview.dataFrame(); // 初始化监测界面
	}

	/**
	 * 主菜单窗口显示； 添加Label、按钮、下拉条及相关事件监听；
	 */
	public void dataFrame() {
		this.setBounds(WINDOW_X, WINDOW_Y, WINDOW_WIDTH, WINDOW_HEIGHT);
		this.setTitle("串口实时监测项目");
		this.setBackground(Color.white);
		this.setLayout(null);

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent arg0) {
				if (serialPort != null) {
					// 程序退出时关闭串口释放资源
					SerialTool.closePort(serialPort);
				}
				System.exit(0);
			}

		});

		tem.setBounds(100, 100, 225, 50);
		tem.setBackground(Color.black);
		tem.setFont(font);
		tem.setForeground(Color.white);
		add(tem);

		hum.setBounds(450, 100, 225, 50);
		hum.setBackground(Color.black);
		hum.setFont(font);
		hum.setForeground(Color.white);
		add(hum);

		bpslab.setBounds(30, 385, 100, 50);
		bpslab.setFont(new Font("微软雅黑", Font.BOLD, 20));
		add(bpslab);

		commlab.setBounds(425, 385, 100, 50);
		commlab.setFont(new Font("微软雅黑", Font.BOLD, 20));
		add(commlab);

		// 添加串口选择选项
		commChoice.setBounds(130, 400, 200, 200);
		// 检查是否有可用串口，有则加入选项中
		if (commList == null || commList.size() < 1) {
			JOptionPane.showMessageDialog(null, "没有搜索到有效串口！", "错误", JOptionPane.INFORMATION_MESSAGE);
		} else {
			for (String s : commList) {
				commChoice.add(s);
			}
		}
		add(commChoice);
		// 添加波特率选项
		bpsChoice.setBounds(526, 400, 200, 200);
		bpsChoice.add("1200");
		bpsChoice.add("2400");
		bpsChoice.add("4800");
		bpsChoice.add("9600");
		bpsChoice.add("14400");
		bpsChoice.add("19200");
		bpsChoice.add("115200");
		add(bpsChoice);
		// 添加打开串口按钮
		openSerialButton.setBounds(50, 490, 300, 50);
		openSerialButton.setBackground(Color.lightGray);
		openSerialButton.setFont(new Font("微软雅黑", Font.BOLD, 20));
		openSerialButton.setForeground(Color.darkGray);
		add(openSerialButton);

		closeSerialButton.setBounds(450, 490, 300, 50);
		closeSerialButton.setBackground(Color.lightGray);
		closeSerialButton.setFont(new Font("微软雅黑", Font.BOLD, 20));
		closeSerialButton.setForeground(Color.darkGray);
		add(closeSerialButton);
		closeSerialButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					SerialTool.closePort(serialPort);
					ISOPEN = false;
				} catch (Exception e2) {
					JOptionPane.showMessageDialog(null, "串口关闭失败！", "错误", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		// 添加打开串口按钮的事件监听
		openSerialButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				// 获取串口名称
				String commName = commChoice.getSelectedItem();
				// 获取波特率
				String bpsStr = bpsChoice.getSelectedItem();

				// 检查串口名称是否获取正确
				if (commName == null || commName.equals("")) {
					JOptionPane.showMessageDialog(null, "没有搜索到有效串口！", "错误", JOptionPane.INFORMATION_MESSAGE);
				} else {
					// 检查波特率是否获取正确
					if (bpsStr == null || bpsStr.equals("")) {
						JOptionPane.showMessageDialog(null, "波特率获取错误！", "错误", JOptionPane.INFORMATION_MESSAGE);
					} else {
						if (!ISOPEN) {
							// 串口名、波特率均获取正确时
							int bps = Integer.parseInt(bpsStr);
							try {

								// 获取指定端口名及波特率的串口对象
								serialPort = SerialTool.openPort(commName, bps);
								// 在该串口对象上添加监听器
								SerialTool.addListener(serialPort, new SerialListener());
								ISOPEN = true;
							} catch (Exception e1) {
								JOptionPane.showMessageDialog(null, "监听失败，请重试！", "错误", JOptionPane.INFORMATION_MESSAGE);
							}
						} else
							JOptionPane.showMessageDialog(null, "请先关闭当前串口！", "错误", JOptionPane.INFORMATION_MESSAGE);
					}
				}

			}
		});

		this.setResizable(false);

		new Thread(new RepaintThread()).start(); // 启动重画线程

	}

	/*
	 * 重画线程（每隔30毫秒重画一次）
	 */
	private class RepaintThread implements Runnable {
		public void run() {
			while (true) {
				// 扫描可用串口
				commList = SerialTool.findPort();
				if (commList != null && commList.size() > 0) {

					// 添加新扫描到的可用串口
					for (String s : commList) {

						// 该串口名是否已存在，初始默认为不存在（在commList里存在但在commChoice里不存在，则新添加）
						boolean commExist = false;

						for (int i = 0; i < commChoice.getItemCount(); i++) {
							if (s.equals(commChoice.getItem(i))) {
								// 当前扫描到的串口名已经在初始扫描时存在
								commExist = true;
								break;
							}
						}

						if (commExist) {
							// 当前扫描到的串口名已经在初始扫描时存在，直接进入下一次循环
							continue;
						} else {
							// 若不存在则添加新串口名至可用串口下拉列表
							commChoice.add(s);
						}
					}

					// 移除已经不可用的串口
					for (int i = 0; i < commChoice.getItemCount(); i++) {

						// 该串口是否已失效，初始默认为已经失效（在commChoice里存在但在commList里不存在，则已经失效）
						boolean commNotExist = true;

						for (String s : commList) {
							if (s.equals(commChoice.getItem(i))) {
								commNotExist = false;
								break;
							}
						}
						if (commNotExist) {
							// System.out.println("remove" +
							// commChoice.getItem(i));
							commChoice.remove(i);
						} else {
							continue;
						}
					}

				} else {
					// 如果扫描到的commList为空，则移除所有已有串口
					commChoice.removeAll();
				}

				try {
					Thread.sleep(30);
				} catch (InterruptedException e) {

					JOptionPane.showMessageDialog(null, "线程异常", "错误", JOptionPane.INFORMATION_MESSAGE);
					System.exit(0);
				}
			}
		}

	}

	/**
	 * 以内部类形式创建一个串口监听类
	 * 
	 * @author
	 *
	 */
	private class SerialListener implements SerialPortEventListener {

		public void serialEvent(SerialPortEvent serialPortEvent) {

			switch (serialPortEvent.getEventType()) {

			case SerialPortEvent.BI: // 10 通讯中断
				JOptionPane.showMessageDialog(null, "与串口设备通讯中断", "错误", JOptionPane.INFORMATION_MESSAGE);
				break;

			case SerialPortEvent.OE: // 7 溢位（溢出）错误

			case SerialPortEvent.FE: // 9 帧错误

			case SerialPortEvent.PE: // 8 奇偶校验错误

			case SerialPortEvent.CD: // 6 载波检测

			case SerialPortEvent.CTS: // 3 清除待发送数据

			case SerialPortEvent.DSR: // 4 待发送数据准备好了

			case SerialPortEvent.RI: // 5 振铃指示

			case SerialPortEvent.OUTPUT_BUFFER_EMPTY: // 2 输出缓冲区已清空
				break;

			case SerialPortEvent.DATA_AVAILABLE: // 1 串口存在可用数据

				// System.out.println("found data");
				byte[] data = null;

				try {
					if (serialPort == null) {
						JOptionPane.showMessageDialog(null, "串口对象为空！监听失败！", "错误", JOptionPane.INFORMATION_MESSAGE);
					} else {
						data = SerialTool.readFromPort(serialPort); // 读取数据，存入字节数组
						// System.out.println(new String(data));

						// 自定义解析过程
						if (data == null || data.length < 1) { // 检查数据是否读取正确
							JOptionPane.showMessageDialog(null, "读取数据过程中未获取到有效数据！请检查设备或程序！", "错误",
									JOptionPane.INFORMATION_MESSAGE);
							System.exit(0);
						} else {
							String dataOriginal = new String(data); // 将字节数组数据转换位为保存了原始数据的字符串

							System.out.print(dataOriginal);
							try {
								if (dataOriginal.charAt(0) == 'D') {
									tem.setText("距离：" + dataOriginal.substring(1) + "M");
									if (distanceview == null) {
										distanceview = new DistanceView();
										distanceview.setVisible(true);
									} else
										distanceview.getdat(dataOriginal.substring(1));
								} else if (dataOriginal.charAt(0) == 'T') {
									hum.setText("温度：" + dataOriginal.substring(1) + "°C");
									if (temperatureview == null) {
										temperatureview = new TemperatureView();
										temperatureview.setVisible(true);
									} else{
										System.out.print(dataOriginal);
										temperatureview.getdat(dataOriginal.substring(1));}
								}
							} catch (ArrayIndexOutOfBoundsException e) {
								JOptionPane.showMessageDialog(null, "数据解析过程出错，更新界面数据失败！请检查设备或程序！", "错误",
										JOptionPane.INFORMATION_MESSAGE);
								System.exit(0);
							}
						}

					}

				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "未知错误，退出系统", "错误", JOptionPane.INFORMATION_MESSAGE);
					System.exit(0);
				}

				break;

			}

		}
	}

}
