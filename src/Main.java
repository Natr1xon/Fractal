import math.fractal.Mandelbrot;
import ui.MainWindow;

public class Main {
    public static void main(String[] args) {
        MainWindow mainWindow = new MainWindow("Mandelbrot Set Viewer",
                new Mandelbrot());
        mainWindow.setVisible(true);
    }
}