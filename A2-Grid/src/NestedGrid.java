import java.awt.*;
import java.util.ArrayList;

/**
 * The back-end for the Grid Game
 * Each Square in the Grid Game can have either 0 or 4
 * Children - it's a quad tree
 *
 **/
public class NestedGrid {

    /**
     *    |-----|-----|
     *    |  UL |  UR |
     *    |-----+-----|
     *    |  LL |  LR |
     *    |_____|_____|
     */
    public static final int MAX_SIZE = 512;

    /**
     * Create a NestedGrid w/ 5 random colored squares to start
     * a root and its 4 children the root is at level 1 and children at 2
     * the selected square (denoted as yellow highlight)
     * is the root square (the owner of the 4 child squares)
     * @param mxLevels the max depth of the game board
     * @param palette the color palette to use
     */
    public NestedGrid(int mxLevels, Color[] palette) {

    }

    /**
     * The selected square moves up to be its parent (if possible)
     */
    public void moveUp() {

    }

    /**
     * the selected square moves into the upper right child (if possible)
     * of the currently selected square
     */
    public void moveDown() {


    }

    /**
     * the selected square moves counter clockwise to a sibling
     */
    public void moveLeft() {

    }

    /**
     * Move the selected square to the next sibling clockwise
     */
    public void moveRight() {

    }

    /**
     * Return an array of the squares (as class Rectangle) to draw on the screen
     * @return
     */
    public Rectangle[] rectanglesToDraw ( ) {

        //placeholder code with hard coded values
        //just to show something on the screen
        //delete this code to complete the assignment

        ArrayList<Rectangle> allBlocks = new ArrayList<Rectangle>();

        Rectangle r = new Rectangle(0,0, 512, ColorTemplate.SERENITY_NOW[0], false, true);
        r.setBorderSize(512*4);
        Rectangle ur = new Rectangle(256,0, 256, ColorTemplate.SERENITY_NOW[1], true, false);
        ur.setBorderSize(256*2);
        Rectangle ul = new Rectangle(0,0, 256, ColorTemplate.SERENITY_NOW[2], true, false);
        ul.setBorderSize(256*2);
        Rectangle ll = new Rectangle(0,256, 256, ColorTemplate.SERENITY_NOW[3], true, false);
        ll.setBorderSize(256*2);
        Rectangle lr = new Rectangle(256,256, 256, ColorTemplate.SERENITY_NOW[4], true, false);
        lr.setBorderSize(256*2);

        allBlocks.add(r);
        allBlocks.add(ur);
        allBlocks.add(ul);
        allBlocks.add(ll);
        allBlocks.add(lr);

        return allBlocks.toArray(new Rectangle[allBlocks.size()]);
    }

    /**
     * smash a square into 4 smaller squares (if possible)
     * a square at max depth level is not allowed to be smashed
     * leave the selected square as the square that was just
     * smashed (it's just not visible anymore)
     */
    public void smash() {

    }

    /**
     * Rotate the descendants of the currently selected square
     * @param clockwise if true rotate clockwise, else counterclockwise
     */
    public void rotate(boolean clockwise) {

    }

    /**
     * flip the descendants of the currently selected square
     * the descendants will become the mirror image
     * @param horizontally if true then flip over the x-axis,
     *                     else flip over the y-axis
     */
    public void swap (boolean horizontally) {

    }

}
