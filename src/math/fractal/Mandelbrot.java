package math.fractal;

import math.Complex;

import java.awt.*;

public class Mandelbrot implements FractalSet{
    private static final double R = 4.0;

    @Override
    public Color isInSet(Complex c) {
        int iter = 0;
        Complex z = new Complex();
        int maxIterations = 200;
        while (z.abs2() < R && iter++ <= maxIterations) {
            z.timesAssign(z);  // z = z^2
            z.plusAssign(c);   // z = z^2 + c
        }

        if(iter > maxIterations){
            return new Color(0);
        }else{
            float x = (float) iter / maxIterations;
            int red = (int) (255 * Math.pow(x, 0.3)); // экспоненциальный рост
            int green = (int) (255 * Math.abs(Math.sin(x * Math.PI * 4))); // синусоидальная волна
            int blue = (int) (255 * Math.log(1 + 5 * x) / Math.log(6)); // логарифмическая шкала
            return new Color(blue,green,red);
        }

//        float hue = (iter % 256) / 255f;
//        float saturation = 0.9f;
//        float value = iter < maxIterations ? 1.0f : 0.0f;
//        return Color.getHSBColor(hue, saturation, value);
    }
}
