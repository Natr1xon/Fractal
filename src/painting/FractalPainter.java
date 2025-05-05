package painting;

import math.Complex;
import convert.Converter;
import math.fractal.FractalSet;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class FractalPainter implements Painter {
    private Converter converter;
    private final FractalSet fractal;

    public FractalPainter(Converter converter, FractalSet fractal) {
        this.converter = converter;
        this.fractal = fractal;
    }

    public void setConverter(Converter converter){
        this.converter = converter;
    }

    @Override
    public Dimension getSize() {
        return new Dimension(converter.getImageWidth(), converter.getImageHeight());
    }

    @Override
    public void setSize(Dimension d) {
        converter.setImageWidth(d.width);
        converter.setImageHeight(d.height);
    }

    @Override
    public void setSize(int width, int height) {
        converter.setImageWidth(width);
        converter.setImageHeight(height);
    }

    @Override
    public void paint(Graphics g) {
        ArrayList<PaintHelper> paintHelpers = new ArrayList<>();
        ArrayList<Thread> threads = new ArrayList<>();

        BufferedImage sharedImage = new BufferedImage(
                converter.getImageWidth(),
                converter.getImageHeight(),
                BufferedImage.TYPE_INT_RGB
        );

        for(int i = 0; i < PaintHelper.MAX_THREADS; i++){
            paintHelpers.add(new PaintHelper(sharedImage, i));
            threads.add(new Thread(paintHelpers.getLast()));
            threads.getLast().start();
        }

        for(var t : threads){
            try {
                t.join();
            }
            catch (InterruptedException e){
                Thread.currentThread().interrupt();
                break;
            }
        }

        synchronized (g) {
            g.drawImage(sharedImage, 0, 0, null);
        }
    }

    private class PaintHelper implements Runnable {
        private final BufferedImage image;
        private final int part;
        public static final int MAX_THREADS = Runtime.getRuntime().availableProcessors();

        public PaintHelper(BufferedImage image, int part) {
            this.image = image;
            this.part = part;
        }

        @Override
        public void run() {
            Graphics g = image.getGraphics();
            try {
                for (int i = part; i < converter.getImageWidth(); i += MAX_THREADS) {
                    for (int j = 0; j < converter.getImageHeight(); j++) {
                        double x0 = converter.xScr2Crt(i);
                        double y0 = converter.yScr2Crt(j);

                        Complex c = new Complex(x0, y0);
                        g.setColor(fractal.isInSet(c));
                        g.fillRect(i, j, 1, 1);
                    }
                }
            } finally {
                g.dispose();
            }
        }
    }
}