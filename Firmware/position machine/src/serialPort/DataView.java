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

	private List<String> commList = null; // ������ö˿ں�
	private SerialPort serialPort = null; // ���洮�ڶ���

	private final int WINDOW_WIDTH = 800;
	private final int WINDOW_HEIGHT = 800;
	private final int WINDOW_X = 800;
	private final int WINDOW_Y = 100;
	private Font font = new Font("΢���ź�", Font.BOLD, 25);

	private Label tem = new Label("���޾�������", Label.CENTER); // �¶�
	private Label hum = new Label("�����¶�����", Label.CENTER); // ʪ��

	private Label bpslab = new Label("�����ʣ�", Label.CENTER); // �¶�
	private Label commlab = new Label("ѡ�񴮿ڣ�", Label.CENTER); // ʪ��

	private Choice commChoice = new Choice(); // ����ѡ��������
	private Choice bpsChoice = new Choice(); // ������ѡ��

	private Button openSerialButton = new Button("�򿪴���");
	private Button closeSerialButton = new Button("�رմ���");
	private static boolean ISOPEN;
	Image offScreen = null; // �ػ�ʱ�Ļ���
	private DistanceView distanceview;
	private TemperatureView temperatureview;

	public DataView() {
		commList = SerialTool.findPort(); // �����ʼ��ʱ��ɨ��һ����Ч����
	}

	public static void main(String[] args) {
		DataView dataview = new DataView(); // �������ࣨ��ʾ�����������壩
		dataview.setVisible(true); // ��ʾ������
		dataview.dataFrame(); // ��ʼ��������
	}

	/**
	 * ���˵�������ʾ�� ���Label����ť��������������¼�������
	 */
	public void dataFrame() {
		this.setBounds(WINDOW_X, WINDOW_Y, WINDOW_WIDTH, WINDOW_HEIGHT);
		this.setTitle("����ʵʱ�����Ŀ");
		this.setBackground(Color.white);
		this.setLayout(null);

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent arg0) {
				if (serialPort != null) {
					// �����˳�ʱ�رմ����ͷ���Դ
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
		bpslab.setFont(new Font("΢���ź�", Font.BOLD, 20));
		add(bpslab);

		commlab.setBounds(425, 385, 100, 50);
		commlab.setFont(new Font("΢���ź�", Font.BOLD, 20));
		add(commlab);

		// ��Ӵ���ѡ��ѡ��
		commChoice.setBounds(130, 400, 200, 200);
		// ����Ƿ��п��ô��ڣ��������ѡ����
		if (commList == null || commList.size() < 1) {
			JOptionPane.showMessageDialog(null, "û����������Ч���ڣ�", "����", JOptionPane.INFORMATION_MESSAGE);
		} else {
			for (String s : commList) {
				commChoice.add(s);
			}
		}
		add(commChoice);
		// ��Ӳ�����ѡ��
		bpsChoice.setBounds(526, 400, 200, 200);
		bpsChoice.add("1200");
		bpsChoice.add("2400");
		bpsChoice.add("4800");
		bpsChoice.add("9600");
		bpsChoice.add("14400");
		bpsChoice.add("19200");
		bpsChoice.add("115200");
		add(bpsChoice);
		// ��Ӵ򿪴��ڰ�ť
		openSerialButton.setBounds(50, 490, 300, 50);
		openSerialButton.setBackground(Color.lightGray);
		openSerialButton.setFont(new Font("΢���ź�", Font.BOLD, 20));
		openSerialButton.setForeground(Color.darkGray);
		add(openSerialButton);

		closeSerialButton.setBounds(450, 490, 300, 50);
		closeSerialButton.setBackground(Color.lightGray);
		closeSerialButton.setFont(new Font("΢���ź�", Font.BOLD, 20));
		closeSerialButton.setForeground(Color.darkGray);
		add(closeSerialButton);
		closeSerialButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					SerialTool.closePort(serialPort);
					ISOPEN = false;
				} catch (Exception e2) {
					JOptionPane.showMessageDialog(null, "���ڹر�ʧ�ܣ�", "����", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		// ��Ӵ򿪴��ڰ�ť���¼�����
		openSerialButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				// ��ȡ��������
				String commName = commChoice.getSelectedItem();
				// ��ȡ������
				String bpsStr = bpsChoice.getSelectedItem();

				// ��鴮�������Ƿ��ȡ��ȷ
				if (commName == null || commName.equals("")) {
					JOptionPane.showMessageDialog(null, "û����������Ч���ڣ�", "����", JOptionPane.INFORMATION_MESSAGE);
				} else {
					// ��鲨�����Ƿ��ȡ��ȷ
					if (bpsStr == null || bpsStr.equals("")) {
						JOptionPane.showMessageDialog(null, "�����ʻ�ȡ����", "����", JOptionPane.INFORMATION_MESSAGE);
					} else {
						if (!ISOPEN) {
							// �������������ʾ���ȡ��ȷʱ
							int bps = Integer.parseInt(bpsStr);
							try {

								// ��ȡָ���˿����������ʵĴ��ڶ���
								serialPort = SerialTool.openPort(commName, bps);
								// �ڸô��ڶ�������Ӽ�����
								SerialTool.addListener(serialPort, new SerialListener());
								ISOPEN = true;
							} catch (Exception e1) {
								JOptionPane.showMessageDialog(null, "����ʧ�ܣ������ԣ�", "����", JOptionPane.INFORMATION_MESSAGE);
							}
						} else
							JOptionPane.showMessageDialog(null, "���ȹرյ�ǰ���ڣ�", "����", JOptionPane.INFORMATION_MESSAGE);
					}
				}

			}
		});

		this.setResizable(false);

		new Thread(new RepaintThread()).start(); // �����ػ��߳�

	}

	/*
	 * �ػ��̣߳�ÿ��30�����ػ�һ�Σ�
	 */
	private class RepaintThread implements Runnable {
		public void run() {
			while (true) {
				// ɨ����ô���
				commList = SerialTool.findPort();
				if (commList != null && commList.size() > 0) {

					// �����ɨ�赽�Ŀ��ô���
					for (String s : commList) {

						// �ô������Ƿ��Ѵ��ڣ���ʼĬ��Ϊ�����ڣ���commList����ڵ���commChoice�ﲻ���ڣ�������ӣ�
						boolean commExist = false;

						for (int i = 0; i < commChoice.getItemCount(); i++) {
							if (s.equals(commChoice.getItem(i))) {
								// ��ǰɨ�赽�Ĵ������Ѿ��ڳ�ʼɨ��ʱ����
								commExist = true;
								break;
							}
						}

						if (commExist) {
							// ��ǰɨ�赽�Ĵ������Ѿ��ڳ�ʼɨ��ʱ���ڣ�ֱ�ӽ�����һ��ѭ��
							continue;
						} else {
							// ��������������´����������ô��������б�
							commChoice.add(s);
						}
					}

					// �Ƴ��Ѿ������õĴ���
					for (int i = 0; i < commChoice.getItemCount(); i++) {

						// �ô����Ƿ���ʧЧ����ʼĬ��Ϊ�Ѿ�ʧЧ����commChoice����ڵ���commList�ﲻ���ڣ����Ѿ�ʧЧ��
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
					// ���ɨ�赽��commListΪ�գ����Ƴ��������д���
					commChoice.removeAll();
				}

				try {
					Thread.sleep(30);
				} catch (InterruptedException e) {

					JOptionPane.showMessageDialog(null, "�߳��쳣", "����", JOptionPane.INFORMATION_MESSAGE);
					System.exit(0);
				}
			}
		}

	}

	/**
	 * ���ڲ�����ʽ����һ�����ڼ�����
	 * 
	 * @author
	 *
	 */
	private class SerialListener implements SerialPortEventListener {

		public void serialEvent(SerialPortEvent serialPortEvent) {

			switch (serialPortEvent.getEventType()) {

			case SerialPortEvent.BI: // 10 ͨѶ�ж�
				JOptionPane.showMessageDialog(null, "�봮���豸ͨѶ�ж�", "����", JOptionPane.INFORMATION_MESSAGE);
				break;

			case SerialPortEvent.OE: // 7 ��λ�����������

			case SerialPortEvent.FE: // 9 ֡����

			case SerialPortEvent.PE: // 8 ��żУ�����

			case SerialPortEvent.CD: // 6 �ز����

			case SerialPortEvent.CTS: // 3 �������������

			case SerialPortEvent.DSR: // 4 ����������׼������

			case SerialPortEvent.RI: // 5 ����ָʾ

			case SerialPortEvent.OUTPUT_BUFFER_EMPTY: // 2 ��������������
				break;

			case SerialPortEvent.DATA_AVAILABLE: // 1 ���ڴ��ڿ�������

				// System.out.println("found data");
				byte[] data = null;

				try {
					if (serialPort == null) {
						JOptionPane.showMessageDialog(null, "���ڶ���Ϊ�գ�����ʧ�ܣ�", "����", JOptionPane.INFORMATION_MESSAGE);
					} else {
						data = SerialTool.readFromPort(serialPort); // ��ȡ���ݣ������ֽ�����
						// System.out.println(new String(data));

						// �Զ����������
						if (data == null || data.length < 1) { // ��������Ƿ��ȡ��ȷ
							JOptionPane.showMessageDialog(null, "��ȡ���ݹ�����δ��ȡ����Ч���ݣ������豸�����", "����",
									JOptionPane.INFORMATION_MESSAGE);
							System.exit(0);
						} else {
							String dataOriginal = new String(data); // ���ֽ���������ת��λΪ������ԭʼ���ݵ��ַ���

							System.out.print(dataOriginal);
							try {
								if (dataOriginal.charAt(0) == 'D') {
									tem.setText("���룺" + dataOriginal.substring(1) + "M");
									if (distanceview == null) {
										distanceview = new DistanceView();
										distanceview.setVisible(true);
									} else
										distanceview.getdat(dataOriginal.substring(1));
								} else if (dataOriginal.charAt(0) == 'T') {
									hum.setText("�¶ȣ�" + dataOriginal.substring(1) + "��C");
									if (temperatureview == null) {
										temperatureview = new TemperatureView();
										temperatureview.setVisible(true);
									} else{
										System.out.print(dataOriginal);
										temperatureview.getdat(dataOriginal.substring(1));}
								}
							} catch (ArrayIndexOutOfBoundsException e) {
								JOptionPane.showMessageDialog(null, "���ݽ������̳������½�������ʧ�ܣ������豸�����", "����",
										JOptionPane.INFORMATION_MESSAGE);
								System.exit(0);
							}
						}

					}

				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "δ֪�����˳�ϵͳ", "����", JOptionPane.INFORMATION_MESSAGE);
					System.exit(0);
				}

				break;

			}

		}
	}

}
