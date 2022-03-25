package xyz.jayfromfuture;

public class Main {

    private static final int WIDTH = 900;
    private static final int HEIGHT = 600;

    public static void main(String[] args) {
        DrawPanel drawPanel = new DrawPanel(WIDTH, HEIGHT);
        ControlPanel controlPanel = new ControlPanel(drawPanel);
        new MainWindow("LabWork3", WIDTH, HEIGHT, drawPanel, controlPanel);
    }
}
