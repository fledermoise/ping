import java.awt.AWTException;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Update implements Runnable {

	private Ping ping;
	private Chart chart;

	private Process process;
	private Pattern pattern;
	private Matcher matcher;
	private BufferedReader reader;
	private String pingText;
	private int pingTime;
	private boolean restart = false;
	private boolean running = true;

	public Update(Ping ping, Chart chart) {
		this.ping = ping;
		this.chart = chart;
	}

	public void pause() {
		running = false;
	}

	public void resume() {
		running = true;
	}

	public void restart() {
		restart = true;
	}

	public void run() {
		while (true) {
			restart = false;
			if (running) {
				try {
					process = Runtime.getRuntime().exec("ping -t " + ping.getHost());
					pattern = Pattern.compile("=([0-9]+)ms");
					reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

					String output = "";
					while ((output = reader.readLine()) != null && running && !restart) {
						matcher = pattern.matcher(output);
						if (matcher.find()) {
							pingText = matcher.group(0).substring(1, matcher.group(0).length() - 2);
							pingTime = Integer.valueOf(pingText);
							System.out.println(pingTime);
							chart.drawToChart(pingTime);
							if (pingText.length() == 1) pingText = "0" + pingText;
							if (pingText.length() >= 3) pingText = "99";

							if (pingTime <= 35) ping.setTrayIcon(pingText, Color.GREEN);
							else if (pingTime <= 70) ping.setTrayIcon(pingText, Color.YELLOW);
							else ping.setTrayIcon(pingText, Color.RED);
						}
						else {
							System.out.println("--");
							ping.setTrayIcon(" /", Color.WHITE);
							chart.drawToChart(-1);
						}
					}
					process.destroy();
					if (restart) System.out.println("Setting new host");
					if (! running) chart.drawToChart(-1);
				}
				catch (IOException | AWTException ex) {
					ex.printStackTrace();
					System.out.println("Error in Update Thread");
				}
			}
			else chart.drawToChart(-1);

			try {
				if (running && ! restart) ping.setTrayIcon(" /", Color.WHITE);
				else ping.setTrayIcon("--", Color.WHITE);
				Thread.sleep(1000);
			} catch (AWTException | InterruptedException ex) {
				ex.printStackTrace();
			}
		}
	}
}
