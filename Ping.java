import java.awt.AWTException;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.Socket;
import java.util.Locale;

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

	private static SystemTray tray = null;
	private static TrayIcon trayIcon = null;

	private static String locale = Locale.getDefault().getLanguage();
	private static int seconds = 1;

	public static void setTrayIcon(String text, Color color, boolean update) throws AWTException {
		image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		g2d = image.createGraphics();
		g2d.setFont(font);
		g2d.setColor(color);

		g2d.drawString(text, 0, 13);
		g2d.dispose();

		if (update) {
			trayIcon.setImage(image);
			if (text.equals(" /")) {
				if (locale.equals("de")) trayIcon.setToolTip("Ping (Keine verbindung)");
				else trayIcon.setToolTip("Ping (No Connection)");
			}
			else trayIcon.setToolTip("Ping (" + host + ")");
		}
		else {
			PopupMenu popup = new PopupMenu();
			Menu menu = new Menu();
			if (locale.equals("de")) menu.setLabel("Aktualisierung     ");
			else menu.setLabel("update rate");
			MenuItem sec1 = new MenuItem("1 sec");
			sec1.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {seconds = 1;}});
			MenuItem sec2 = new MenuItem("2 sec");
			sec2.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {seconds = 2;}});
			MenuItem sec3 = new MenuItem("3 sec");
			sec3.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {seconds = 3;}});
			MenuItem sec5 = new MenuItem("5 sec");
			sec5.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {seconds = 5;}});
			MenuItem sec10 = new MenuItem("10 sec");
			sec10.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {seconds = 10;}});
			menu.add(sec1);
			menu.add(sec2);
			menu.add(sec3);
			menu.add(sec5);
			menu.add(sec10);

			MenuItem item = new MenuItem();
			if (locale.equals("de")) item.setLabel("Beenden");
			else item.setLabel("Close");
			item.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {System.exit(0);}});

			popup.add(menu);
			popup.add(item);

			trayIcon = new TrayIcon(image, "Ping", popup);
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
				System.out.println(pingTime);
				ping = String.valueOf(pingTime);
				connect.close();

				if (ping.length() == 1) ping = "0" + ping;
				if (ping.length() >= 3) ping = "99";

				if (pingTime <= 35) setTrayIcon(ping, Color.GREEN, true);
				else if (pingTime <= 70) setTrayIcon(ping, Color.YELLOW, true);
				else setTrayIcon(ping, Color.RED, true);
			}
			catch (IOException ex) {setTrayIcon(" /", Color.WHITE, true);}

			Thread.sleep(seconds * 1000);
		}
	}
}
