package painting;

import math.Line;
import math.Rectangle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class PaintPanel extends JPanel {
    class SelectionListener extends MouseAdapter {
        Rectangle rectangle = new Rectangle();

        @Override
        public void mousePressed(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                rectangle.clear();
                rectangle.addPoint(e.getPoint());
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                if (!rectangle.isInvalid()) drawRect(rectangle);
                rectangle.addPoint(e.getPoint());
                if (!rectangle.isInvalid()) drawRect(rectangle);
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                if (!rectangle.isInvalid()) drawRect(rectangle);
                if (selectedAction != null) selectedAction.accept(rectangle);
            }
        }
    }

    class DragListener extends MouseAdapter {
        Line line = new Line();

        @Override
        public void mousePressed(MouseEvent e) {
            if (!SwingUtilities.isLeftMouseButton(e)) {
                line.clear();
                line.addPoint(e.getPoint());
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (!SwingUtilities.isLeftMouseButton(e)) {
                line.addPoint(e.getPoint());
                if (!line.isInvalid() && stepAction != null) stepAction.accept(line);
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (!SwingUtilities.isLeftMouseButton(e)) {
                setCursor(Cursor.getDefaultCursor());
            }
        }
    }

    private Consumer<Graphics> paintAction = null;
    private Consumer<Rectangle> selectedAction = null;
    private Consumer<Line> stepAction = null;
    private final SelectionListener selectionListener = new SelectionListener();
    private final DragListener dragListener = new DragListener();

    public PaintPanel(){
        super();
    }

    public void setPaintAction(Consumer<Graphics> action) {
        paintAction = action;
    }

    public void removePaintAction() {
        paintAction = null;
    }

    public void setSelectedAction(Consumer<Rectangle> rect){
        selectedAction = rect;
        addMouseListener(selectionListener);
        addMouseMotionListener(selectionListener);
    }

    public void removeSelectedAction(){
        selectedAction = null;
        removeMouseListener(selectionListener);
        removeMouseMotionListener(selectionListener);
    }

    public void setStepAction(Consumer<Line> line){
        stepAction = line;
        addMouseListener(dragListener);
        addMouseMotionListener(dragListener);
    }

    public void removeStepAction(){
        stepAction = null;
        removeMouseListener(dragListener);
        removeMouseMotionListener(dragListener);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if(paintAction != null) paintAction.accept(g);
    }

    private void drawRect(Rectangle rectangle){
        Graphics g = this.getGraphics();
        g.setXORMode(Color.WHITE);
        g.setColor(Color.black);
        try {
            g.drawRect(rectangle.getX(), rectangle.getY(),
                    rectangle.getWidth(), rectangle.getHeight());
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(this,e.getMessage());
        }
        finally {
            g.setPaintMode();
        }
    }
}
