package org.fledermoise.ping;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JTextField;

public class HostInput extends JFrame implements KeyListener {

    private App app;
    private JTextField inputField;

    public HostInput(App app) {
        super("Enter new hostname");
        this.app = app;

        inputField = new JTextField(null, 20);
        inputField.addKeyListener(this);

        add(inputField);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    public void display(String host) {
        inputField.setText(host);
        inputField.setSelectionStart(0);
        inputField.setSelectionEnd(host.length());

        pack();
        setVisible(true);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            dispose();
        }
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            app.changeHost(inputField.getText());
            dispose();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}
