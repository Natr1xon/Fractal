package painting;

import math.Complex;
import convert.Converter;
import math.fractal.FractalSet;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.function.Function;

public class FractalPainter implements Painter {
    private Function<Complex, Float> fractalFunction;
    private final FractalSet fractal;
    private Converter converter;

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

        g.drawImage(sharedImage, 0, 0, null);
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
                        fractalFunction = fractal::isInSet;
                        var r = fractalFunction.apply(c);

                        Color color = getColor(r);

                        g.setColor(color);
                        g.fillRect(i, j, 1, 1);
                    }
                }
            } finally {
                g.dispose();
            }
        }
    }

    private static Color getColor(float r) {
        Color color;
        if(r >= 1.0){
            color = new Color(0);
        }else{
            int red = (int) (255 * Math.pow(r, 0.3)); // экспоненциальный рост
            int green = (int) (255 * Math.abs(Math.sin(r * Math.PI * 4))); // синусоидальная волна
            int blue = (int) (255 * Math.log(1 + 5 * r) / Math.log(6)); // логарифмическая шкала
            color =new Color(blue,green,red);
        }
        return color;
    }
}