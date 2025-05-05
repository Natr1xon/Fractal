package math.fractal;

import math.Complex;
import painting.FractalPainter;

public class Julia implements FractalFunction {
    private static final double ESCAPE_RADIUS = 4.0;
    private final Complex constant;

    public Julia() {
        constant = new Complex(-0.8, 0.156);
    }

    public Julia(Complex dot){
        constant = new Complex(dot);
    }

    @Override
    public float isInSet(Complex z0) {
        int iterations = 0;
        Complex z = new Complex(z0);

        while (iterations < FractalPainter.maxIteration && z.abs2() < ESCAPE_RADIUS) {
            z.timesAssign(z);
            z.plusAssign(constant);
            iterations++;
        }
        return (float) iterations / FractalPainter.maxIteration ;
    }
}