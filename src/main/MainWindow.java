package main;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {

    public MainWindow(String title, int width, int height, DrawPanel drawPanel, ControlPanel controlPanel)
            throws HeadlessException {
        super(title);
        setSize(new Dimension(width, height));
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());
        add(drawPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        setVisible(true);
    }
}
