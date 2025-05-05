package painting;

import java.awt.*;

public interface Painter {
    Dimension getSize();
    void setSize(Dimension d);
    void setSize(int width, int height);
    void paint(Graphics g);
}
