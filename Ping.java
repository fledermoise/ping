import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JDialog;
import javax.swing.JTextField;

public class Ping implements ActionListener
{
	private static Update update;
	private static Chart chart;

	private String host = "store.steampowered.com";
	private Font font;
	private boolean running = true;
	private double screenW, screenH;

	private File saveHostDir, saveHost;
	private BufferedImage image;
	private Graphics2D g2d;
	private SystemTray tray;
	private TrayIcon trayIcon;
	private PopupMenu popup;
	private Menu hostMenu;
	private MenuItem changeHost, showChart, pause, close;
	private JTextField hostInput;
	private JDialog hostDialog;

	public Ping() throws AWTException {
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		screenW = screen.getWidth();
		screenH = screen.getHeight();

		tray = SystemTray.getSystemTray();
		font = new Font("Arial", 0, 15);

		try { // read host from host.cfg in AppData/Roaming/Ping/host.cfg if existing
			saveHostDir = new File(System.getenv("AppData") + "\\Ping");
			if (! saveHostDir.exists()) saveHostDir.mkdirs();
			saveHost = new File(System.getenv("AppData") + "\\Ping\\host.cfg");
			if (! saveHost.exists()) saveHost.createNewFile();

			BufferedReader reader = new BufferedReader(new FileReader(saveHost));
			String line;
			while ((line = reader.readLine()) != null) {
				if (! line.replace(" ", "").equals("")) host = line;
			}
			reader.close();
		}
		catch (IOException ex) {
			ex.printStackTrace();
			System.out.println("Error in creating and/or reading from file.");
		}

		image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		g2d = image.createGraphics();
		g2d.setFont(font);
		g2d.setColor(Color.WHITE);

		g2d.drawString("--", 0, 13);
		g2d.dispose();

		popup = new PopupMenu();

		hostMenu = new Menu(host + "   ");
		changeHost = new MenuItem("Change");
		showChart = new MenuItem("Show Chart   ");
		pause = new MenuItem("Pause   ");
		close = new MenuItem("Close   ");

		changeHost.addActionListener(this);
		showChart.addActionListener(this);
		pause.addActionListener(this);
		close.addActionListener(this);

		hostMenu.add(changeHost);
		popup.add(hostMenu);
		popup.add(showChart);
		popup.add(pause);
		popup.add(close);

		trayIcon = new TrayIcon(image, "Ping (Connecting...)", popup);
		trayIcon.setImageAutoSize(true);
		trayIcon.addActionListener(this);
		tray.add(trayIcon);
	}

	public String getHost() {
		return host;
	}

	public void setTrayIcon(String text, Color color) throws AWTException {
		image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		g2d = image.createGraphics();
		g2d.setFont(font);
		g2d.setColor(color);

		g2d.drawString(text, 0, 13);
		g2d.dispose();

		trayIcon.setImage(image);
		if (text.equals(" /")) trayIcon.setToolTip("Ping (No Connection)");
		else if (running) trayIcon.setToolTip("Ping (" + host + ")");
	}

	public void actionPerformed(ActionEvent event) {
		Object e = event.getSource();
		System.out.println("Triggered");
		if (e == trayIcon) {
			chart.showChart();
		}
		if (e == changeHost) {
			hostInput = new JTextField(host, 20);
			hostDialog = new JDialog((JDialog) null);
			hostDialog.setTitle("Enter new hostname");
			//hostInput.setSelectionColor(Color.GREEN);
			hostInput.setSelectionStart(0);
			hostInput.setSelectionEnd(host.length());

			hostInput.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent keyEvent) {
					int ee = keyEvent.getKeyCode();
					if (ee == KeyEvent.VK_ENTER) {
						host = hostInput.getText();
						update.restart();
						try {
							PrintWriter writer = new PrintWriter(new FileWriter(saveHost), true);
							writer.println(host);
							writer.close();
						}
						catch (IOException ex) {
							ex.printStackTrace();
							System.out.println("Error in writing to host.cfg");
						}
						hostMenu.setLabel(host + "   ");
						hostDialog.dispose();
						trayIcon.setToolTip("Ping (Connecting...)");
					}
					else if (ee == KeyEvent.VK_ESCAPE) {
						hostDialog.dispose();
					}
				}
			});
			hostDialog.add(hostInput);

			hostDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			hostDialog.pack();
			hostDialog.setResizable(false);
			hostDialog.setLocation((int) (screenW - hostDialog.getWidth()) / 2, (int) (screenH - hostDialog.getHeight()) / 2);
			hostDialog.setVisible(true);
		}
		if (e == showChart) {
			chart.showChart();;
		}
		if (e == pause) {
			if (running) {
				running = false;
				update.pause();
				pause.setLabel("Continue");
				trayIcon.setToolTip("Ping (Paused)");
			}
			else {
				running = true;
				update.resume();
				pause.setLabel("Pause");
				trayIcon.setToolTip("Ping (Connecting...)");
			}
		}
		if (e == close) System.exit(0);
	}

	public static void main(String[] args) throws AWTException{
		Ping ping = new Ping();
		chart = new Chart();
		update = new Update(ping, chart);
		Thread updateThread = new Thread(update);
		updateThread.start();
	}
}
