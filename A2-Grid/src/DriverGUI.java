import javax.swing.*;
import java.awt.*;

/**
 * Main program entry point
 */
public class DriverGUI {

    public static void main(String [] args) {

        JFrame frame = new JFrame("Grid Game");

        GridRenderer renderer = new GridRenderer();

        frame.add(renderer);

        frame.setSize(new Dimension(NestedGrid.MAX_SIZE, NestedGrid.MAX_SIZE+132));
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}
