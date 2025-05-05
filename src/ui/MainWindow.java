package ui;

import convert.Converter;
import math.Complex;
import math.fractal.FractalFunction;
import painting.FractalPainter;
import painting.PaintPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Stack;

public class MainWindow extends JFrame {
    private static final int MAX_SZ = GroupLayout.DEFAULT_SIZE;

    protected PaintPanel mainPanel;
    protected Converter converter = new Converter(-2,1,-1,1);
    protected FractalPainter fractalPainter;
    protected Stack<Converter> zoomHistory = new Stack<>();

    public MainWindow(String title, FractalFunction fractal) {
        setTitle(title);
        fractalPainter = new FractalPainter(converter,fractal);

        initComponents();
        setupLayout();
    }

    private void initComponents() {
        setMinimumSize(new Dimension(1000, 700));
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        mainPanel = new PaintPanel();
        mainPanel.setBackground(Color.white);
        mainPanel.setPaintAction(fractalPainter::paint);
        mainPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Dimension size = mainPanel.getSize();
                fractalPainter.setSize(size);
                mainPanel.repaint();
            }
        });
        zoomHistory.push(new Converter(converter));

        mainPanel.setSelectedAction(r ->{
            try {
                double xMin = converter.xScr2Crt(r.getX());
                double xMax = converter.xScr2Crt(r.getX() + r.getWidth());
                double yMin = converter.yScr2Crt(r.getY() + r.getHeight());
                double yMax = converter.yScr2Crt(r.getY());

                double screenRatio = (double)mainPanel.getWidth() / mainPanel.getHeight();
                double mathRatio = (xMax - xMin) / (yMax - yMin);

                // Корректируем координаты для сохранения пропорций
                if (mathRatio > screenRatio) {
                    // Ширина слишком большая - увеличиваем высоту
                    double centerY = (yMin + yMax) / 2;
                    double newHeight = (xMax - xMin) / screenRatio;
                    yMin = centerY - newHeight/2;
                    yMax = centerY + newHeight/2;
                } else {
                    // Высота слишком большая - увеличиваем ширину
                    double centerX = (xMin + xMax) / 2;
                    double newWidth = (yMax - yMin) * screenRatio;
                    xMin = centerX - newWidth/2;
                    xMax = centerX + newWidth/2;
                }

                converter.setIntervalX(xMin,xMax);
                converter.setIntervalY(yMin,yMax);

                zoomHistory.push(new Converter(converter));
                mainPanel.repaint();
            }
            catch (Exception ex) {
                JOptionPane.showMessageDialog(mainPanel, ex.getMessage());
            }
        });
        mainPanel.setStepAction(l ->{
            try{
                int differenceX = l.getPointStart().x - l.getPointEnd().x;
                int differenceY = l.getPointStart().y - l.getPointEnd().y;

                double xMin = converter.xCrt2Scr(converter.getXMin()) + differenceX;
                double xMax = converter.xCrt2Scr(converter.getXMax()) + differenceX;
                double yMin = converter.yCrt2Scr(converter.getYMin()) + differenceY;
                double yMax = converter.yCrt2Scr(converter.getYMax()) + differenceY;

                converter.setIntervalX(converter.xScr2Crt(xMin), converter.xScr2Crt(xMax));
                converter.setIntervalY(converter.yScr2Crt(yMin), converter.yScr2Crt(yMax));

                mainPanel.repaint();
            }
            catch (Exception ex) {
                JOptionPane.showMessageDialog(mainPanel, ex.getMessage());
            }
        });


        Action action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                zoomOut();
            }
        };
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control Z"));

        mainPanel.getActionMap().put("Zoom back", action);
        mainPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                (KeyStroke) action.getValue(Action.ACCELERATOR_KEY), "Zoom back");

        JMenuBar menuBar = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenuItem saveAs = saveMenuItem();
        JMenuItem load = loadMenuItem();
        file.add(saveAs);
        file.add(load);

        JMenu actions = new JMenu("Actions");
        JMenuItem undo = new JMenuItem("Undo");
        undo.addActionListener(action);
        actions.add(undo);

        JMenu fractal = new JMenu("Fractals");
        JMenuItem fractalJulia = new JMenuItem("Julia");
        fractalJulia.addActionListener(open ->{
            JuliaWindow juliaFractal = new JuliaWindow();
            juliaFractal.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            juliaFractal.setVisible(true);
        });
        fractal.add(fractalJulia);

        JMenu colorScheme = getColorSchemeMenu();

        menuBar.add(file);
        menuBar.add(actions);
        menuBar.add(fractal);
        menuBar.add(colorScheme);
        setJMenuBar(menuBar);


        mainPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount()==2){
                    double re = converter.xScr2Crt(e.getX());
                    double im = converter.yScr2Crt(e.getY());
                    JuliaWindow juliaFractal = new JuliaWindow(new Complex(re,im));
                    juliaFractal.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    juliaFractal.setVisible(true);
                }
            }
        });
    }

    private JMenu getColorSchemeMenu() {
        JMenu colorScheme = new JMenu("Color scheme");
        JMenuItem schemeFunc = new JMenuItem("Different functions");
        schemeFunc.addActionListener(l->{
            fractalPainter.setColorSchemes1();
            mainPanel.repaint();
        });
        JMenuItem schemeThunder = new JMenuItem("Thunder");
        schemeThunder.addActionListener(l->{
            fractalPainter.setColorSchemes3();
            mainPanel.repaint();
        });
        JMenuItem schemeWaterColor = new JMenuItem("Water color");
        schemeWaterColor.addActionListener(l->{
            fractalPainter.setColorSchemes2();
            mainPanel.repaint();
        });

        colorScheme.add(schemeFunc);
        colorScheme.add(schemeWaterColor);
        colorScheme.add(schemeThunder);
        return colorScheme;
    }

    private JMenuItem loadMenuItem() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter fractalFilter = new FileNameExtensionFilter("Fractal data (*.frac)", "frac");

        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileFilter(fractalFilter);

        JMenuItem load = new JMenuItem("Load fractal");

        load.addActionListener(e -> {
            fileChooser.setDialogTitle("Load file");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            int result = fileChooser.showOpenDialog(this);
            if(result == JFileChooser.APPROVE_OPTION){
                double xMin = 0, xMax = 0, yMin = 0, yMax = 0;
                File inputFile = fileChooser.getSelectedFile();
                try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(":");
                        if (parts.length == 2) {
                            switch (parts[0]) {
                                case "xMin" -> xMin = Double.parseDouble(parts[1]);
                                case "xMax" -> xMax = Double.parseDouble(parts[1]);
                                case "yMin" -> yMin = Double.parseDouble(parts[1]);
                                case "yMax" -> yMax = Double.parseDouble(parts[1]);
                            }
                        }
                    }
                    converter.setIntervalX(xMin, xMax);
                    converter.setIntervalY(yMin, yMax);
                    mainPanel.repaint();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error loading fractal: " + ex.getMessage());
                }
            }
        });
        return load;
    }

    private JMenuItem saveMenuItem() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter jpegFilter = new FileNameExtensionFilter("JPEG images (*.jpeg)", "jpeg");
        FileNameExtensionFilter pngFilter = new FileNameExtensionFilter("PNG images (*.png)", "png");
        FileNameExtensionFilter fractalFilter = new FileNameExtensionFilter("Fractal data (*.frac)", "frac");

        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(jpegFilter);
        fileChooser.addChoosableFileFilter(pngFilter);
        fileChooser.addChoosableFileFilter(fractalFilter);
        fileChooser.setFileFilter(jpegFilter);

        JMenuItem saveAs = new JMenuItem("Save as");

        saveAs.addActionListener(e -> {
            fileChooser.setDialogTitle("Save file");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            int result = fileChooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File outputFile = fileChooser.getSelectedFile();
                FileNameExtensionFilter selectedFilter = (FileNameExtensionFilter) fileChooser.getFileFilter();
                String description = selectedFilter.getDescription();
                boolean isFractal = false;

                String format = "jpeg";
                if (description.contains("*.png")) {
                    format = "png";
                } else if (description.contains("*.frac")) {
                    format = "frac";
                    isFractal = true;
                }

                String extension = "." + format;
                if (!outputFile.getName().toLowerCase().endsWith(extension)) {
                    outputFile = new File(outputFile.getAbsolutePath() + extension);
                }

                try {
                    if (isFractal) {
                        try (PrintWriter writer = new PrintWriter(outputFile)) {
                            writer.println("xMin:" + converter.getXMin());
                            writer.println("xMax:" + converter.getXMax());
                            writer.println("yMin:" + converter.getYMin());
                            writer.println("yMax:" + converter.getYMax());
                        }
                    } else {
                        BufferedImage img = getBufferedImage();
                        ImageIO.write(img, format, outputFile);
                    }
                    JOptionPane.showMessageDialog(this,
                            "File '" + outputFile.getName() + "' saved successfully");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Failed to save: " + ex.getMessage());
                }
            }
        });
        return saveAs;
    }

    private BufferedImage getBufferedImage() {
        BufferedImage img = new BufferedImage(
                mainPanel.getWidth(),
                mainPanel.getHeight(),
                BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = img.createGraphics();
        mainPanel.paint(g2d);
        String coordinates = "Coordinates - X min: " + converter.getXMin() +
                " X max: " + converter.getXMax() +
                " Y min: " + converter.getYMin() + " Y max: " + converter.getYMax();
        g2d.setXORMode(Color.WHITE);
        g2d.drawString(coordinates,10,20);
        g2d.dispose();
        return img;
    }

    private void zoomOut() {
        if (zoomHistory.size() > 1) {
            zoomHistory.pop();
            Converter previousConverter = zoomHistory.peek();

            converter.setIntervalX(previousConverter.getXMin(), previousConverter.getXMax());
            converter.setIntervalY(previousConverter.getYMin(), previousConverter.getYMax());

            mainPanel.repaint();
        }
    }


    private void setupLayout() {
        GroupLayout gl = new GroupLayout(getContentPane());
        getContentPane().setLayout(gl);

        gl.setVerticalGroup(gl.createSequentialGroup()
                .addGap(10)
                .addComponent(mainPanel, MAX_SZ, MAX_SZ, MAX_SZ)
                .addGap(10)
        );

        gl.setHorizontalGroup(gl.createSequentialGroup()
                .addGap(10)
                .addComponent(mainPanel, MAX_SZ, MAX_SZ, MAX_SZ)
                .addGap(10)
        );
    }
}