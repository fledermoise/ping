package org.fledermoise.ping.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;

public class ChartPanel extends JPanel {

    private ChartWindow chartWindow;

    public ChartPanel(ChartWindow chartWindow) {
        super();
        this.chartWindow = chartWindow;
    }

    public BufferedImage draw() {
        int totalWidth = getWidth();
        int totalHeight = getHeight();

        int chartWidth = (int) (totalWidth * 0.9);
        int chartHeight = (int) (totalHeight * 0.9);
        int scaleWidth = totalWidth - chartWidth;
        int scaleHeight = chartHeight;
        int timelineWidth = chartWidth;
        int timelineHeight = totalHeight - chartHeight;

        BufferedImage image = new BufferedImage(getWidth(), getHeight() ,BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        // graphics.setColor(Color.RED);
        // graphics.fillRect(0, 0, getWidth(), getHeight());

        BufferedImage chartImage = createChartImage(chartWidth, chartHeight);
        graphics.drawImage(chartImage, 0, 0, null);

        BufferedImage scaleImage = createScaleImage(scaleWidth, scaleHeight);
        graphics.drawImage(scaleImage, chartWidth, 0, null);

        BufferedImage timelineImage = createTimelineImage(timelineWidth, timelineHeight);
        graphics.drawImage(timelineImage, 0, chartHeight, null);

        graphics.dispose();
        return image;
    }

    private BufferedImage createChartImage(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        ArrayList<Integer> pingValues = chartWindow.getPingValues();
        if (pingValues.size() == 0) return image;

        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, width, height);
        graphics.setStroke(new BasicStroke(height / 250.0f));

        int pingValue = pingValues.get(pingValues.size() - 1);
        int lineY = height - pingValue * height / 100;
        if (pingValue != -1) {
            graphics.setColor(Color.LIGHT_GRAY);
            graphics.drawLine(0, lineY, width, lineY);
        }
        graphics.setColor(Color.BLACK);

        pingValue = pingValues.get(0);
        double lastX = width - pingValues.size() * width / 100;
        double lastY = height - pingValue * height / 100;
        for (int i = 1; i < pingValues.size(); i++) {
            pingValue = pingValues.get(i);
            double x = lastX + width / 100;
            double y = height - pingValue * height / 100;

            if (pingValue != -1 && pingValues.get(i - 1) != -1) {
                graphics.drawLine((int) lastX, (int) lastY, (int) x, (int) y);
            }

            lastX = x;
            lastY = y;
        }

        if (pingValue != -1) {
            graphics.setColor(Color.GREEN);
            graphics.drawOval((int) lastX - height / 100, (int) lastY - height / 100, height / 50, height / 50);
        }

        graphics.dispose();
        return image;
    }

    private BufferedImage createScaleImage(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // graphics.setColor(Color.CYAN);
        // graphics.fillRect(0, 0, width, height);
        graphics.setColor(Color.BLACK);

        Dimension textBounds = new Dimension((int) (width * 0.8), height / 15);

        graphics.setFont(new Font("Arial", Font.PLAIN, 100));
        int fontAscent = graphics.getFontMetrics().getAscent();
        int stringWidth = graphics.getFontMetrics().stringWidth("100 ms");

        double verticalScalingRatio = textBounds.getHeight() / fontAscent;
        double horizontalScalingRatio = textBounds.getWidth() / stringWidth;
        double scalingRatio = Math.min(verticalScalingRatio, horizontalScalingRatio);

        graphics.setFont(new Font("Arial", Font.PLAIN, (int) (100 * scalingRatio)));
        fontAscent = graphics.getFontMetrics().getAscent();
        stringWidth = graphics.getFontMetrics().stringWidth("100 ms");

        int actualHeight = height - fontAscent;
        double spacing = actualHeight / 10.0;

        int paddingLeft = (int) (width * 0.1);

        for (int i = 0; i <= 10; i++) {
            String text = i * 10 + " ms";
            int x = paddingLeft + stringWidth - graphics.getFontMetrics().stringWidth(text);
            graphics.drawString(text, x, (int) (height - i * spacing));
        }

        graphics.dispose();
        return image;
    }

    private BufferedImage createTimelineImage(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // graphics.setColor(Color.PINK);
        // graphics.fillRect(0, 0, width, height);
        graphics.setColor(Color.BLACK);

        int lineY = height * 1/3;
        int lineEndYStart = lineY - height / 4;
        int lineEndYEnd = lineY + height / 4;

        float strokeWidth = height / 30.0f;
        graphics.setStroke(new BasicStroke(strokeWidth));
        graphics.drawLine(0, lineY, width, lineY);
        graphics.drawLine(0, lineEndYStart, 0, lineEndYEnd);
        graphics.drawLine((int) (width - strokeWidth/2), lineEndYStart, (int) (width - strokeWidth/2), lineEndYEnd);

        String text = "100 ICMP packets";
        graphics.setFont(new Font("Arial", Font.PLAIN, 100));
        double fontAscent = graphics.getFontMetrics().getAscent();
        double verticalScalingRatio = height / 2.5 / fontAscent;

        graphics.setFont(new Font("Arial", Font.PLAIN, (int) (100 * verticalScalingRatio)));
        fontAscent = graphics.getFontMetrics().getAscent();
        int textWidth = graphics.getFontMetrics().stringWidth(text);

        graphics.drawString(text, (width - textWidth) / 2, (int) (height * 2/3 + fontAscent / 2));

        graphics.dispose();
        return image;
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        BufferedImage image = draw();
        if (image != null) {
            g.drawImage(image, 0, 0, null);
        }
    }
}
