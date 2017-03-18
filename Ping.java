import java.awt.AWTException;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.Socket;

public class Ping
{
	private static String host = null;
	private static Socket connect;
	private static long startTime;
	private static long pingTime;
	private static String ping;
	private static Font font = null;

	private static BufferedImage image;
	private static Graphics2D g2d;

	private static PopupMenu menu;
	private static MenuItem item;
	private static SystemTray tray = null;
	private static TrayIcon trayIcon = null;

	public static void setTrayIcon(String text, Color color, boolean update) throws AWTException {
		menu = new PopupMenu();
		item = new MenuItem("Beenden");
		item.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {System.exit(0);}});
		menu.add(item);

		image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		g2d = image.createGraphics();
		g2d.setFont(font);
		g2d.setColor(color);

		g2d.drawString(text, 0, 13);
		g2d.dispose();

		if (update) {
			trayIcon.setImage(image);
			if (text.equals(" /")) trayIcon.setToolTip("Ping (No Connection)");
			else trayIcon.setToolTip("Ping (" + host + ")");
		}
		else {
			trayIcon = new TrayIcon(image, "Ping", menu);
			trayIcon.setImageAutoSize(true);
			tray.add(trayIcon);
		}
	}

	public static void main(String[] args) throws AWTException, InterruptedException {
		font = new Font("Arial", 0, 15);
		tray = SystemTray.getSystemTray();

		setTrayIcon(" ?", Color.WHITE, false);

		host = "www.de-cix.net";

		while (true) {
			try {
				startTime = System.currentTimeMillis();
				connect = new Socket(host, 80);
				pingTime = System.currentTimeMillis() - startTime;
				ping = String.valueOf(pingTime);
				connect.close();

				if (ping.length() == 1) ping = "0" + ping;
				if (ping.length() >= 3) ping = "99";

				if (pingTime <= 35) setTrayIcon(ping, Color.GREEN, true);
				else if (pingTime <= 70) setTrayIcon(ping, Color.YELLOW, true);
				else setTrayIcon(ping, Color.RED, true);
			}
			catch (IOException ex) {setTrayIcon(" /", Color.WHITE, true);}

			Thread.sleep(1000);
		}
	}
}
