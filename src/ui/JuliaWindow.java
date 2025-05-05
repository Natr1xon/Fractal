package ui;

import math.fractal.Julia;

public class JuliaWindow extends MainWindow {
    public JuliaWindow(){
        super("Julia Set Viewer",
                new Julia());
    }
}
