package ui;

import math.Complex;
import math.fractal.Julia;

import java.awt.*;

public class JuliaWindow extends MainWindow {
    public JuliaWindow(){
        super("Julia Set Viewer",
                new Julia());
        converter.setIntervalX(-2,2);
    }

    public JuliaWindow(Complex complex){
        super("Julia Set Viewer",
                new Julia(complex));
        converter.setIntervalX(-2,2);
    }
}
