import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * The back-end for the Grid Game
 * Each Square in the Grid Game can have either 0 or 4
 * Children - it's a quad tree
 *
 **/
public class NestedGrid {


    //Comes with a static array of colors
    private Color[] palette;

    private Node root;

    //The level I am using to order the nodes at a specific height
    private int level = 1;

    //referencing the parent node to work with the four children
    private Node parent;
    private Node tempNode;

    private Node levelUp;
    private Node levelDown;

    //collecting all of the rectangles to show later
    ArrayList<Rectangle> blocks = new ArrayList<Rectangle>();

    private Node[] history = new Node[10];

    int[] randomNumber = new int[5];




    private class Node{
        private Rectangle rectangle;
        private int level;
        private Node upperRight;
        private Node lowerRight;
        private Node lowerLeft;
        private Node upperLeft;
        public Node(Rectangle rectangle, int level, Node upperRight, Node lowerRight, Node lowerLeft, Node upperLeft){
            this.rectangle = rectangle;
            this.level = level;
            this.upperRight = upperRight;
            this.lowerRight = lowerRight;
            this.lowerLeft = lowerLeft;
            this.upperLeft = upperLeft;
        }



    }

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
        initializeGrid(palette);
        this.palette = palette;
        this.level = 1;
    }


    //Initializing the grid with two levels
    public void initializeGrid(Color[] palette){
        int[] initialRandomNumber = new int[5];
        for(int i = 0; i < 5; i++){
            initialRandomNumber[i] = (int)(Math.random()*8);
        }

        int startingSize = 512;
        int startingX = 0;
        int startingY = 0;

        Rectangle rootRectangle = new Rectangle(startingX, startingY, startingSize, palette[initialRandomNumber[4]], false, true);
        blocks.add(rootRectangle);
        Rectangle[] rectangle4 = make4Rectangle(startingSize, initialRandomNumber, palette, rootRectangle.getX(), rootRectangle.getY());
        root = new Node(rootRectangle, level, null, null, null, null);

        //Initializing the second level after the first

        this.level = level + 1;
        root.upperRight = new Node(rectangle4[0], level, null, null, null, null);
        root.upperLeft = new Node(rectangle4[1], level, null, null, null, null);
        root.lowerLeft = new Node(rectangle4[2], level, null, null, null, null);
        root.lowerRight = new Node(rectangle4[3], level, null, null, null ,null);
        parent = root;
    }


    //Code to make four rectangles out of the parent node
    public Rectangle[] make4Rectangle(int size, int[] randomNumber, Color[] palette, int x, int y){
        Rectangle upperRight = new Rectangle(size/2 + x, y, size / 2, palette[randomNumber[0]], true, false);
        Rectangle upperLeft = new Rectangle(x, y, size / 2, palette[randomNumber[1]], true, false);
        Rectangle lowerLeft = new Rectangle(x, size / 2 + y, size /2, palette[randomNumber[2]], true, false);
        Rectangle lowerRight = new Rectangle(size/2 + x, size/2 + y, size /2, palette[randomNumber[3]], true, false);


        Rectangle[] rectangles = new Rectangle[4];


        rectangles[0] = upperRight;
        rectangles[1] = upperLeft;
        rectangles[2] = lowerLeft;
        rectangles[3] = lowerRight;
        blocks.addAll(Arrays.asList(rectangles));
        return rectangles;

    }

    public void setNewRandomNumber(){
        int[] randomNumber = new int[4];
        for(int i = 0; i < 4; i++){
            randomNumber[i] = (int)(Math.random()*8);
        }
        this.randomNumber = randomNumber;
    }


    //Setting the selection boolean for each rectangle
    public void setRectangles(boolean bParent, boolean bLowerLeft, boolean bLowerRight, boolean bUpperRight, boolean bUpperLeft){
        parent.rectangle.setSelected(bParent);
        parent.lowerLeft.rectangle.setSelected(bLowerLeft);
        parent.lowerRight.rectangle.setSelected(bLowerRight);
        parent.upperRight.rectangle.setSelected(bUpperRight);
        parent.upperLeft.rectangle.setSelected(bUpperLeft);
    }

    /**
     * The selected square moves up to be its parent (if possible)
     */
    //Moving up the tree, there is a problem where if you move up too many times at the start you keep inflating the level
    //so probably have to set a condition where you decrease the level only at a specific time, or use something else
    public void moveUp() {
        this.level = level - 1;
        //Finds the parent and selects it
        findParent(root, level);
        System.out.println("Level " + level);
        setRectangles(true, false, false, false, false);
    }

    public void findParent(Node node, int level){
        if(node == null){
            return;
        }

        if(node.level == level && node.upperRight != null && node.upperLeft != null && node.lowerLeft != null && node.lowerRight != null){
            System.out.println("I was hit");
            this.parent = node;
        }

        else{
            findParent(node.upperRight, level);
            findParent(node.lowerRight, level);
            findParent(node.lowerLeft, level);
            findParent(node.upperLeft, level);
        }
    }

    /**
     * the selected square moves into the upper right child (if possible)
     * of the currently selected square
     */
    //Sometimes this doesn't work for some squares and you have to split it to move into it, since the parent is not set here
    public void moveDown() {
        level = level + 1;
        System.out.println("Level " + level);
        System.out.println(this.parent.rectangle.getSize());
        setRectangles(false, false, false, true, false);
    }





    /**
     * the selected square moves counter clockwise to a sibling
     */
    public void moveLeft() {
        if(parent.upperRight.rectangle.isSelected()){
            setRectangles(false, false, false, false, true);
        }
        else if(parent.upperLeft.rectangle.isSelected()){
            setRectangles(false, true, false, false, false);
        }
        else if(parent.lowerLeft.rectangle.isSelected()){
            setRectangles(false, false, true, false, false);
        }
        else if(parent.lowerRight.rectangle.isSelected()){
            setRectangles(false, false, false, true, false);
        }
    }


    /**
     * Move the selected square to the next sibling clockwise
     */
    public void moveRight() {
        if(parent.upperRight.rectangle.isSelected()){
            setRectangles(false, false, true, false, false);
        }
        else if(parent.upperLeft.rectangle.isSelected()){
            setRectangles(false, false, false, true, false);
        }
        else if(parent.lowerLeft.rectangle.isSelected()){
            setRectangles(false, false, false, false, true);
        }
        else if(parent.lowerRight.rectangle.isSelected()){
            setRectangles(false, true, false, false, false);
        }
    }

    /**
     * Return an array of the squares (as class Rectangle) to draw on the screen
     * @return
     */
    public Rectangle[] rectanglesToDraw ( ) {

        //placeholder code with hard coded values
        //just to show something on the screen
        //delete this code to complete the assignment

/*        ArrayList<Rectangle> allBlocks = new ArrayList<Rectangle>();

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
        allBlocks.add(lr);*/

        return blocks.toArray(new Rectangle[blocks.size()]);
    }

    public void setParentNodes(Rectangle[] rectangles, int level){
        parent.upperRight = new Node(rectangles[0], level, null, null, null, null);
        parent.upperLeft = new Node(rectangles[1], level, null, null, null, null);
        parent.lowerLeft = new Node(rectangles[2], level, null, null, null, null);
        parent.lowerRight = new Node(rectangles[3], level, null, null, null ,null);
    }

    /**
     * smash a square into 4 smaller squares (if possible)
     * a square at max depth level is not allowed to be smashed
     * leave the selected square as the square that was just
     * smashed (it's just not visible anymore)
     */
    public void smash() {
        setNewRandomNumber();
        if(parent.upperRight.rectangle.isSelected()){
            parent.upperRight.rectangle.setVisible(false);
            Rectangle[] rectangles = make4Rectangle(parent.upperRight.rectangle.getSize(), this.randomNumber, this.palette, parent.upperRight.rectangle.getX(),
                    parent.upperRight.rectangle.getY());
            System.out.println(parent.rectangle.getSize());
            parent = parent.upperRight;
            //this.level = parent.level;
            //level++;
            setParentNodes(rectangles, level + 1);
        }
        else if(parent.upperLeft.rectangle.isSelected()){
            parent.upperLeft.rectangle.setVisible(false);
            Rectangle[] rectangles = make4Rectangle(parent.upperLeft.rectangle.getSize(), this.randomNumber, this.palette, parent.upperLeft.rectangle.getX(),
                    parent.upperLeft.rectangle.getY());
            parent = parent.upperLeft;
            //this.level = parent.level;
            //level++;
            setParentNodes(rectangles, level + 1);
        }
        else if(parent.lowerLeft.rectangle.isSelected()){
            parent.lowerLeft.rectangle.setVisible(false);
            Rectangle[] rectangles = make4Rectangle(parent.lowerLeft.rectangle.getSize(), this.randomNumber, this.palette, parent.lowerLeft.rectangle.getX(),
                    parent.lowerLeft.rectangle.getY());
            parent = parent.lowerLeft;
            //this.level = parent.level;
            //level++;
            setParentNodes(rectangles, level + 1);
        }
        else if(parent.lowerRight.rectangle.isSelected()){
            parent.lowerRight.rectangle.setVisible(false);
            Rectangle[] rectangles = make4Rectangle(parent.lowerRight.rectangle.getSize(), this.randomNumber, this.palette, parent.lowerRight.rectangle.getX(),
                    parent.lowerRight.rectangle.getY());
            parent = parent.lowerRight;
            //this.level = parent.level;
            //level++;
            setParentNodes(rectangles, level + 1);
        }

    }

    /**
     * Rotate the descendants of the currently selected square
     * @param clockwise if true rotate clockwise, else counterclockwise
     */
    //These methods should be easy to implement if everything else works.
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
