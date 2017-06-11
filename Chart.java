import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Chart extends JFrame implements KeyListener{

	private ArrayList<Integer> chartData = new ArrayList<Integer>();

	public Chart() {
		super("Ping Chart");
		addKeyListener(this);

		//for (int i = 0; i < 100; i++) chartData.add(-1);			// graph start on right side

		//getContentPane().setPreferredSize(new Dimension(460, 225));

		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(460, 225));
		panel.setBackground(Color.WHITE);
		setContentPane(panel);

		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setVisible(false);
	}

	public void showChart() {
		setVisible(true);
		requestFocus();
	}

	public void drawChartSetup(Graphics2D g, int top, int left) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.BLACK);

		g.drawLine(left, top, left + 410, top);						// top line
		g.drawLine(left + 410, top, left + 410, top + 200);			// right line
		g.drawLine(left, top + 200, left + 410, top + 200);			// bottom line
		g.drawLine(left, top + 210, left + 396, top + 210);			// sub bottom line
		g.drawLine(left + 396, top + 205, left + 396, top + 215);	// sub bottom line end
		g.drawString("100 sec", left + 175, top + 225);

		g.drawString("- 00 ms", left + 415, top + 204);
		for (int i = 1; i < 10; i++) {
			g.drawString("- " + i * 10 + " ms", left + 415, top + 204 - i * 20);
		}
	}

	public void drawToChart(int pingTime) {
		int top = getInsets().top;
		int left = getInsets().left;

		chartData.add(pingTime);
		while (chartData.size() > 100) chartData.remove(0);

		BufferedImage chartImage = new BufferedImage(getWidth(), getHeight() ,BufferedImage.TYPE_INT_ARGB);
		Graphics2D chartg2d = chartImage.createGraphics();
		chartg2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		chartg2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		drawChartSetup(chartg2d, top, left);
		if (pingTime != -1) {	// draw red line
			chartg2d.setColor(new Color(255, 0, 0, 64));
			chartg2d.drawLine(left, top + 200 - pingTime * 2, left + 409, top + 200 - pingTime * 2);
		}
		for (int i = 0; i < chartData.size() - 1; i++) { // draw the chart
			chartg2d.setColor(Color.BLACK);
			if (chartData.get(i) != -1 && chartData.get(i + 1) != -1) chartg2d.drawLine(left + i * 4, top + 200 - chartData.get(i) * 2, left + (i + 1) * 4, top + 200 - chartData.get(i + 1) * 2);
		}
		if (pingTime != -1) { 	// draw green circle
			chartg2d.setColor(Color.GREEN);
			chartg2d.drawOval(left + (chartData.size() - 1) * 4 - 2, top + 200 - chartData.get(chartData.size() - 1) * 2 - 2, 4, 4);
		}

		chartg2d.dispose();

		Graphics2D g2d = (Graphics2D) getGraphics();
		g2d.drawImage(chartImage, 0, 0, null);
		g2d.dispose();
	}

	public void keyPressed(KeyEvent event) {
		int e = event.getKeyCode();
		if (e == KeyEvent.VK_ENTER || e == KeyEvent.VK_ESCAPE || e == KeyEvent.VK_SPACE) setVisible(false);
	}

	public void keyReleased(KeyEvent event) {}

	public void keyTyped(KeyEvent event) {}
}
