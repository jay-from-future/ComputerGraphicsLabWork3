package main;

import interfaces.ControlPanelListener;
import interfaces.RotateListener;
import util.Point3D;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class ControlPanel extends JPanel {

    private static final String REPAINT_BUTTON_STR = "Нарисовать поверхность Безье";
    private static final String BASE_POINT_STR = "Отображать точки многогранника";
    private static final String CURVE_POINT_STR = "Отображать точки построения поверхности";
    private static final String DEFAULT_ROTATION_STR = "Установить углы вращения по умолчанию";
    private static final String POLYHEDRON_VERTICES_STR = "Задать координаты многогранника";

    private ControlPanelListener controlPanelListener;

    private JCheckBox isCurvePointMarked;
    private JCheckBox isBaseLineVisible;

    private List<JTextField> pointXFields;
    private List<JTextField> pointYFields;
    private List<JTextField> pointZFields;

    private PolyhedronVertices polyhedronVerticesDialog = new PolyhedronVertices();

    public ControlPanel(ControlPanelListener controlPanelListener) {

        this.controlPanelListener = controlPanelListener;

        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 10, 0));

        ButtonListener buttonListener = new ButtonListener();
        CheckBoxListener checkBoxListener = new CheckBoxListener();

        JButton repaintButton = new JButton(REPAINT_BUTTON_STR);
        repaintButton.addActionListener(buttonListener);
        JButton setDefaultRotationButton = new JButton(DEFAULT_ROTATION_STR);
        setDefaultRotationButton.addActionListener(buttonListener);

        JButton setPolyhedronVertices = new JButton(POLYHEDRON_VERTICES_STR);
        setPolyhedronVertices.addActionListener(buttonListener);

        isBaseLineVisible = new JCheckBox(BASE_POINT_STR, true);
        isBaseLineVisible.addActionListener(checkBoxListener);
        isCurvePointMarked = new JCheckBox(CURVE_POINT_STR, true);
        isCurvePointMarked.addActionListener(checkBoxListener);


        buttonPanel.add(repaintButton);
        buttonPanel.add(setDefaultRotationButton);
        buttonPanel.add(setPolyhedronVertices);
        buttonPanel.add(isBaseLineVisible);
        buttonPanel.add(isCurvePointMarked);
        add(buttonPanel);

        polyhedronVerticesDialog.setPoints();
        sendBasePoints();
    }

    public void setPointXFields(List<JTextField> pointXFields) {
        this.pointXFields = pointXFields;
    }

    public void setPointYFields(List<JTextField> pointYFields) {
        this.pointYFields = pointYFields;
    }

    public void setPointZFields(List<JTextField> pointZFields) {
        this.pointZFields = pointZFields;
    }

    private void sendBasePoints() {
        List<Point3D> basePoints = new ArrayList<Point3D>();
        double x;
        double y;
        double z;
        for (int i = 0; i < pointXFields.size(); i++) {
            x = Double.valueOf(pointXFields.get(i).getText());
            y = Double.valueOf(pointYFields.get(i).getText());
            z = Double.valueOf(pointZFields.get(i).getText());
            basePoints.add(new Point3D(x, y, z));
        }
        controlPanelListener.setBasePoints(basePoints);
    }

    private boolean isPointsSets() {
        if (pointXFields != null && pointYFields != null && pointZFields != null) {
            if (!pointXFields.isEmpty() && !pointYFields.isEmpty() && !pointZFields.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    class ButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals(ControlPanel.REPAINT_BUTTON_STR)) {
                try {
                    if (isPointsSets()) {
                        sendBasePoints();
                    } else {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException exception) {
                    JOptionPane.showMessageDialog(null, "Заданы некорректные значения координат точек.");
                }
            } else if (e.getActionCommand().equals(ControlPanel.DEFAULT_ROTATION_STR)) {
                ((RotateListener) controlPanelListener).setDefaultRotation();
            } else if (e.getActionCommand().equals(ControlPanel.POLYHEDRON_VERTICES_STR)) {
                polyhedronVerticesDialog.open();
            }
        }


    }

    class CheckBoxListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals(ControlPanel.BASE_POINT_STR)) {
                controlPanelListener.setBaseLineVisible(isBaseLineVisible.isSelected());
            } else if (e.getActionCommand().equals(CURVE_POINT_STR)) {
                controlPanelListener.setCurvePointMarked(isCurvePointMarked.isSelected());
            }
        }
    }

    class PolyhedronVertices extends JDialog {
        private static final String TITLE = "Меню ввода координат многогранника";

        private static final String SET_BASE_POINTS_STR = "Задать вершины многогранника";
        private static final String SET_DEFAULT_BASE_POINTS_STR = "Вершины по умолчанию";
        private static final String SET_RANDOM_BASE_POINTS_STR = "Случайные координаты";

        private PolyhedronVerticesButtonListener buttonListener;

        // начальные значения координат точек
        private double[] xValues = {-150, -150, -150, -150, -50, -50, -50, -50, 50, 50, 50, 50, 150, 150, 150, 150};
        private double[] yValues = {0, 50, 50, 0, 50, -300, 50, 50, 50, 50, 300, 50, 0, 50, 50, 0};
        private double[] zValues = {150, 50, -50, -150, 150, 50, -50, -150, 150, 50, -50, -150, 150, 50, -50, -150};

        private List<JTextField> pointXFields;
        private List<JTextField> pointYFields;
        private List<JTextField> pointZFields;

        PolyhedronVertices() throws HeadlessException {
            setTitle(TITLE);
            setModal(true);
            setSize(new Dimension(800, 200));
            setResizable(false);
            setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

            buttonListener = new PolyhedronVerticesButtonListener();

            JButton setBasePointsButton = new JButton(SET_BASE_POINTS_STR);
            setBasePointsButton.addActionListener(buttonListener);
            JButton setDefaultPointsButton = new JButton(SET_DEFAULT_BASE_POINTS_STR);
            setDefaultPointsButton.addActionListener(buttonListener);
            JButton setRandomBasePointsButton = new JButton(SET_RANDOM_BASE_POINTS_STR);
            setRandomBasePointsButton.addActionListener(buttonListener);

            JPanel buttonPanel = new JPanel(new GridLayout(1, 3));
            buttonPanel.add(setBasePointsButton);
            buttonPanel.add(setDefaultPointsButton);
            buttonPanel.add(setRandomBasePointsButton);


            pointXFields = new ArrayList<JTextField>();
            pointYFields = new ArrayList<JTextField>();
            pointZFields = new ArrayList<JTextField>();

            for (int i = 0; i < 16; i++) {
                pointXFields.add(new JTextField(String.valueOf(xValues[i]), 6));
                pointYFields.add(new JTextField(String.valueOf(yValues[i]), 6));
                pointZFields.add(new JTextField(String.valueOf(zValues[i]), 6));
            }

            JPanel coordinatesPanel = new JPanel(new GridLayout(4, 4, 10, 20));
            JPanel currPanel;
            for (int i = 0; i < 16; i++) {
                currPanel = new JPanel(new GridLayout(1, 3));
                currPanel.add(new JLabel(String.valueOf(i)));
                currPanel.add(pointXFields.get(i));
                currPanel.add(pointYFields.get(i));
                currPanel.add(pointZFields.get(i));
                coordinatesPanel.add(currPanel);
            }

            setLayout(new BorderLayout());
            add(coordinatesPanel, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);
            pack();
            setVisible(false);
        }

        public void open() {
            setVisible(true);
        }

        private void close() {
            setVisible(false);
        }

        public void setPoints() {
            setPointXFields(pointXFields);
            setPointYFields(pointYFields);
            setPointZFields(pointZFields);
        }

        class PolyhedronVerticesButtonListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals(SET_BASE_POINTS_STR)) {
                    setPoints();
                    close();
                } else if (e.getActionCommand().equals(SET_DEFAULT_BASE_POINTS_STR)) {
                    for (int i = 0; i < 16; i++) {
                        pointXFields.get(i).setText((String.valueOf(xValues[i])));
                        pointYFields.get(i).setText((String.valueOf(yValues[i])));
                        pointZFields.get(i).setText((String.valueOf(zValues[i])));
                    }
                } else if (e.getActionCommand().equals(SET_RANDOM_BASE_POINTS_STR)) {

                    double size = 200;

                    double startX = getRandomNumber(100);
                    double startY = getRandomNumber(100);
                    double startZ = getRandomNumber(100);

                    double x = startX;
                    double y = startY;
                    double z = startZ;

                    for (int i = 0; i < pointXFields.size(); i++) {
                        pointXFields.get(i).setText(String.valueOf(x).substring(0, 5));
                        pointYFields.get(i).setText(String.valueOf(y).substring(0, 5));
                        pointZFields.get(i).setText(String.valueOf(z).substring(0, 5));

                        if (i == 3 || i == 7 || i == 11 || i == 15) {
                            x += Math.abs(getRandomNumber(size));
                            z = startZ + getRandomNumber(50);
                        } else {
                            z -= Math.abs(getRandomNumber(size));
                        }
                        y = getRandomNumber(size);
                    }

                }
            }

            private double getRandomNumber(double max) {
                int sign = (Math.random() > 0.5) ? -1 : 1;
                return sign * (Math.random() * max);
            }
        }
    }
}

