package math.fractal;

import math.Complex;
import painting.FractalPainter;

public class Mandelbrot implements FractalFunction {
    private static final double R = 4.0;

    public Mandelbrot(){

    }

    @Override
    public float isInSet(Complex c) {
        int iteration = 0;
        Complex z = new Complex();
        while (iteration < FractalPainter.maxIteration && z.abs2() < R) {
            z.timesAssign(z);  // z = z^2
            z.plusAssign(c);   // z = z^2 + c
            iteration++;
        }

        return (float) iteration / FractalPainter.maxIteration;
    }
}
