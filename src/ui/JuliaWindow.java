package ui;

import math.Complex;
import math.fractal.Julia;

public class JuliaWindow extends MainWindow {
    public JuliaWindow(){
        super("Julia Set Viewer",
                new Julia());
    }

    public JuliaWindow(Complex complex){
        super("Julia Set Viewer",
                new Julia(complex));
    }
}
