package math.fractal;

import math.Complex;

public class Julia implements FractalSet {
    private static final double ESCAPE_RADIUS = 4.0;
    private final int maxIterations = 2000;
    private final Complex constant; // Параметр для множества Жюлиа

    public Julia() {
        constant = new Complex(-0.8, 0.156); // Красивые значения по умолчанию
    }

    public Julia(Complex dot){
        constant = new Complex(dot);
    }

    @Override
    public float isInSet(Complex z0) {
        int iterations = 0;
        Complex z = new Complex(z0);

        while (iterations < maxIterations && z.abs2() < ESCAPE_RADIUS) {
            z.timesAssign(z);
            z.plusAssign(constant);
            iterations++;
        }
        return (float) iterations / maxIterations;
    }
}