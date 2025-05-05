package math.fractal;

import math.Complex;

import java.awt.*;

public class Mandelbrot implements FractalSet{
    private static final double R = 4.0;
    private final int maxIterations = 2000;

    @Override
    public float isInSet(Complex c) {
        int iteration = 0;
        Complex z = new Complex();
        while (iteration < maxIterations && z.abs2() < R) {
            z.timesAssign(z);  // z = z^2
            z.plusAssign(c);   // z = z^2 + c
            iteration++;
        }

        return (float) iteration / maxIterations;
    }
}
