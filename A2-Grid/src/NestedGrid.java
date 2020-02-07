import java.awt.*;
import java.util.ArrayList;

/**
 * The back-end for the Grid Game
 * Each Square in the Grid Game can have either 0 or 4
 * Children - it's a quad tree
 * Provided for the assignment, altered by Tyler Arsenault and Jordan Luke
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
    /*
    The MAX_SIZE is the maximum length for the root node, allBlocks is an ArrayList
    containing all of the rectangles, levels indicates the maximum number of levels
    allowed, level is the level that the current selected Node/Rectangle is on,
    root is the root Node, currentNode, is the Node currently selected (can be the
    root).
     */
    public static final int MAX_SIZE = 512;
    private ArrayList<Rectangle> allBlocks;
    private int levels, level;//, //cWCount;
    private Node root;
    private Node currentNode;

    /**
     * Create a NestedGrid w/ 5 random colored squares to start
     * a root and its 4 children the root is at level 1 and children at 2
     * the selected square (denoted as yellow highlight)
     * is the root square (the owner of the 4 child squares)
     * @param mxLevels the max depth of the game board
     * @param palette the color palette to use
     */
    public NestedGrid(int mxLevels, Color[] palette) {
        allBlocks = new ArrayList<Rectangle>();
        levels = mxLevels;
        root = new Node(null, 0, 0, MAX_SIZE, 1);//Create the root Node
        root.rectangle.setColor(Color.BLACK);
        root.createChildren();//Creates the root Node's children Nodes
        root.select();//Causes the root Node to be selected
        currentNode = root;
        level = 1;
        //cWCount = 0;
    }


    /**
     * An internal Node Class. It constructs a Rectangle for every Node,
     * booleans for children, if the Node is selected, and if the Rectangle
     * is visible. There are Nodes for the Node's parent and children.
     * There are ints for x, y, size, cl (the colour id), id (the number
     * associated with that child), and a rotation number. There is also
     * a Color that is sent out for.
     */
    private class Node{
        private Rectangle rectangle;
        private boolean children, selected, visible;//children was for an alternate method
        private Node parent, ul, ur, ll, lr;
        private int x,y,size,cl,id, cwRotation;
        private Color colour;

        /**
         * This is one of the two constructors. This is the one that will
         * only be used at the beginning of NestedGrid, and fill construct
         * the root Node.
         * @param n the identity of the parent (null, as this is the parent)
         * @param x1 the x-coordinate of the rectangle (starting point)
         * @param y1 the y-coordinate of the rectangle (starting point)
         * @param size1 The side length of the rectangle
         * @param identity This is used in some methods to distinguish
         *                 between child Nodes.
         */
        private Node(Node n, int x1, int y1, int size1, int identity){
            id = identity;
            parent = n;
            cwRotation = 400;
            x = x1;
            y = y1;
            cl = (int)(Math.random() * 8);
            colour = ColorTemplate.SERENITY_NOW[cl];
            size = size1;
            children = false;
            selected = true;
            visible = true;
            ul = null;//This child starts in the Upper Left corner of the parent
            ur = null;//This child starts in the Upper right corner of the parent
            ll = null;//This child starts in the lower Left corner of the parent
            lr = null;//This child starts in the lower right corner of the parent

            rectangle = new Rectangle(x,y,size,colour,visible,selected);
            select();
            allBlocks.add(rectangle);
        }

        /**
         * This is one of the two constructors. This is the one that will
         * only be used at the beginning of NestedGrid, and fill construct
         * the root Node.
         * @param n the identity of the parent
         * @param x1 the x-coordinate of the rectangle (starting point)
         * @param y1 the y-coordinate of the rectangle (starting point)
         * @param size1 The side length of the rectangle
         * @param identity This is used in some methods to distinguish
         *                 between child Nodes.
         */
        private Node(int x1, int y1, int size1, Node n, int identity ){
            id = identity;
            parent = n;
            cwRotation = 400;
            x = x1;
            y = y1;
            cl = (int)(Math.random() * 8);
            colour = ColorTemplate.SERENITY_NOW[cl];
            size = size1;
            children = false;
            selected = false;
            visible = true;
            ul = null;
            ur = null;
            ll = null;
            lr = null;
            rectangle = new Rectangle(x,y,size,colour,visible,selected);
            rectangle.setMAx(MAX_SIZE);

            deselect();//When initially created, the Nodes should not be selected.
            allBlocks.add(rectangle);//add the new rectangle to the ArrayList of rectangles
        }

        /**
         * These methods adjust the cwRotation to account for an alteration
         * in the Nodes position. The number goes up each time it rotates
         * clockwise, and down each time it rotates counterclockwise.
         */
        private void clockwise(){cwRotation++;}
        private void counterclockwise(){cwRotation--;}

        /**
         * This method creates children for this Node. The children are give starting
         * points relative to the parent Node. When this happens, the parent is no
         * longer visible.
         */
        private void createChildren(){
            ul = new Node(x,y,size/2,this, 1);
            ur = new Node(x + size/2, y, size/2,this, 2);
            ll = new Node(x,y+size/2,size/2,this, 4);
            lr = new Node(x+size/2,y+size/2,size/2,this, 3);
            visible = false;
        }

        /**
         * This returns the id of the Node
         */
        private int getID(){return id;}

        /**
         * When one of the flip operations takes place, the cwRotation needs
         * to be corrected. That correction is done here based on a value sent
         * to this method, by a calculation in the calling method.
         * @param c Tthis is the replacement cwRotation number
         */
        private void setCWR(int c){cwRotation = c;}

        /**
         * There were previous methods that looked for the return of a
         * specific Node. In the event one of those calls returns this
         * method is still present.
         * @param p the number for the requested Node
         * @return the requested Node or null is returned
         */
        private Node selectNode(int p){
            switch(p){
                case 1:
                    return parent;
                case 2:
                    return ll;
                case 3:
                    return ul;
                case 4:
                    return lr;
                case 5:
                    return ur;
                default:
                    return  null;
            }
        }

        private int getX(){return x;}
        private int getY(){return y;}

        /**
         * This method alters the x for the node
         * @param x1
         */
        private void setX(int x1){
            x = x1;
            rectangle.setX(x1);
        }

        /**
         * This method alters the y for the node
         * @param y1
         */
        private void setY(int y1){
            y = y1;
            rectangle.setY(y1);
        }

        /**
         * This switches the boolean in this inner class, and
         * its connected rectangle to show as selected.
         */
        private void select(){
            rectangle.selected(true);
            selected = true;
        }

        /**
         * This switches the boolean in this inner class, and
         * its connected rectangle to show as not selected.
         */
        private void deselect(){
            rectangle.selected(false);
            selected = false;
        }

        /**
         * This is an alternate method that can be used to determine if the Node
         * has children
         * @return boolean children
         */
        private boolean hasChildren(){return children;}

    }

    /**
     * The selected square moves up to be its parent (if possible). If the selected Node
     * is the root, it will not move up, and will inform the User that it can't move up.
     * If the level does change, the method alters the value for the current level int
     */
    public void moveUp() {
        if(level <= 1 || currentNode.parent == null){
            System.out.println("Move up failed");
            return;}
        currentNode.deselect();
        currentNode = currentNode.parent;
        currentNode.select();
        level--;
    }

    /**
     * the selected square moves into the upper right child (if possible)
     * of the currently selected square. It will select whichever child
     * (if any) is currently in the upper right. If the current Node has
     * no children, it will not move down, and will so inform the user.
     * If it does move down, it will also adjust the level int accordingly
     */
    public void moveDown() {
        if(level >= levels || currentNode.ll == null){
            System.out.println("Move down failed");
            return;
        }
        currentNode.deselect();
        if(currentNode.ul.x > currentNode.x && currentNode.ul.y == currentNode.y){
            currentNode = currentNode.ul;
        }
        else if(currentNode.ll.x > currentNode.x && currentNode.ll.y == currentNode.y){
            currentNode = currentNode.ll;
        }
        else if(currentNode.ur.x > currentNode.x && currentNode.ur.y == currentNode.y){
            currentNode = currentNode.ur;
        }
        else{currentNode = currentNode.lr;}
        currentNode.select();
        level++;
    }

    /**
     * the selected square moves counter clockwise to a sibling. It checks the
     * position of the current Node, and will move to the appropriate next Node.
     */
    public void moveLeft() {
        if(level == 1 || currentNode.parent == null){
            System.out.println("At root, no movement possible");
            return;
        }
        Node parent = currentNode.parent;
        Node ul = currentNode.parent.ul;
        Node ll = currentNode.parent.ll;
        Node lr = currentNode.parent.lr;
        Node ur = currentNode.parent.ur;
        boolean x1 = false;
        boolean x2 = false;
        boolean x3 = false;
        boolean x4 = false;
        boolean y1 = false;
        boolean y2 = false;
        boolean y3 = false;
        boolean y4 = false;
        if(ul.x == parent.x){x1 = true;}
        if(ll.x == parent.x){x2 = true;}
        if(lr.x == parent.x){x3 = true;}
        if(ur.x == parent.x){x4 = true;}
        if(ul.y == parent.y){y1 = true;}
        if(ll.y == parent.y){y2 = true;}
        if(lr.y == parent.y){y3 = true;}
        if(ur.y == parent.y){y4 = true;}
        if(currentNode.x == parent.x && currentNode.y == parent.y){
            if (x1 && !y1) {
                currentNode.deselect();
                currentNode = ul;
                currentNode.select();
                return;
            }
            else if (x2 && !y2) {
                currentNode.deselect();
                currentNode = ll;
                currentNode.select();
                return;
            }
            else if (x3 && !y3) {
                currentNode.deselect();
                currentNode = lr;
                currentNode.select();
                return;
            }
            else if (x4 && !y4) {
                currentNode.deselect();
                currentNode = ur;
                currentNode.select();
                return;
            }
        }
        else if(currentNode.x != parent.x && currentNode.y == parent.y){
            if (x1 && y1) {
                currentNode.deselect();
                currentNode = ul;
                currentNode.select();
                return;
            }
            else if (x2 && y2) {
                currentNode.deselect();
                currentNode = ll;
                currentNode.select();
                return;
            }
            else if (x3 && y3) {
                currentNode.deselect();
                currentNode = lr;
                currentNode.select();
                return;
            }
            else if (x4 && y4) {
                currentNode.deselect();
                currentNode = ur;
                currentNode.select();
                return;
            }
        }
        else if(currentNode.x == parent.x && currentNode.y != parent.y){
            if (!x1 && !y1) {
                currentNode.deselect();
                currentNode = ul;
                currentNode.select();
                return;
            }
            else if (!x2 && !y2) {
                currentNode.deselect();
                currentNode = ll;
                currentNode.select();
                return;
            }
            else if (!x3 && !y3) {
                currentNode.deselect();
                currentNode = lr;
                currentNode.select();
                return;
            }
            else if (!x4 && !y4) {
                currentNode.deselect();
                currentNode = ur;
                currentNode.select();
                return;
            }
        }
        else{
            if (!x1 && y1) {
                currentNode.deselect();
                currentNode = ul;
                currentNode.select();
                return;
            }
            else if (!x2 && y2) {
                currentNode.deselect();
                currentNode = ll;
                currentNode.select();
                return;
            }
            else if (!x3 && y3) {
                currentNode.deselect();
                currentNode = lr;
                currentNode.select();
                return;
            }
            else if (!x4 && y4) {
                currentNode.deselect();
                currentNode = ur;
                currentNode.select();
                return;
            }
        }
    }

    /**
     * Move the selected square to the next sibling clockwise. It checks the
     * position of the current Node, and will move to the appropriate next Node.
     */
    public void moveRight() {
        if(level == 1 || currentNode.parent == null){
            System.out.println("At root, no movement possible");
            return;
        }
        Node parent = currentNode.parent;
        Node ul = currentNode.parent.ul;
        Node ll = currentNode.parent.ll;
        Node lr = currentNode.parent.lr;
        Node ur = currentNode.parent.ur;
        boolean x1 = false;
        boolean x2 = false;
        boolean x3 = false;
        boolean x4 = false;
        boolean y1 = false;
        boolean y2 = false;
        boolean y3 = false;
        boolean y4 = false;
        if(ul.x == parent.x){x1 = true;}
        if(ll.x == parent.x){x2 = true;}
        if(lr.x == parent.x){x3 = true;}
        if(ur.x == parent.x){x4 = true;}
        if(ul.y == parent.y){y1 = true;}
        if(ll.y == parent.y){y2 = true;}
        if(lr.y == parent.y){y3 = true;}
        if(ur.y == parent.y){y4 = true;}
        if(currentNode.x == parent.x && currentNode.y == parent.y){
            if (!x1 && y1) {
                currentNode.deselect();
                currentNode = ul;
                currentNode.select();
                return;
            }
            else if (!x2 && y2) {
                currentNode.deselect();
                currentNode = ll;
                currentNode.select();
                return;
            }
            else if (!x3 && y3) {
                currentNode.deselect();
                currentNode = lr;
                currentNode.select();
                return;
            }
            else if (!x4 && y4) {
                currentNode.deselect();
                currentNode = ur;
                currentNode.select();
                return;
            }
        }
        else if(currentNode.x != parent.x && currentNode.y == parent.y){
            if (!x1 && !y1) {
                currentNode.deselect();
                currentNode = ul;
                currentNode.select();
                return;
            }
            else if (!x2 && !y2) {
                currentNode.deselect();
                currentNode = ll;
                currentNode.select();
                return;
            }
            else if (!x3 && !y3) {
                currentNode.deselect();
                currentNode = lr;
                currentNode.select();
                return;
            }
            else if (!x4 && !y4) {
                currentNode.deselect();
                currentNode = ur;
                currentNode.select();
                return;
            }
        }
        else if(currentNode.x == parent.x && currentNode.y != parent.y){
            if (x1 && y1) {
                currentNode.deselect();
                currentNode = ul;
                currentNode.select();
                return;
            }
            else if (x2 && y2) {
                currentNode.deselect();
                currentNode = ll;
                currentNode.select();
                return;
            }
            else if (x3 && y3) {
                currentNode.deselect();
                currentNode = lr;
                currentNode.select();
                return;
            }
            else if (x4 && y4) {
                currentNode.deselect();
                currentNode = ur;
                currentNode.select();
                return;
            }
        }
        else{
            if (x1 && !y1) {
                currentNode.deselect();
                currentNode = ul;
                currentNode.select();
                return;
            }
            else if (x2 && !y2) {
                currentNode.deselect();
                currentNode = ll;
                currentNode.select();
                return;
            }
            else if (x3 && !y3) {
                currentNode.deselect();
                currentNode = lr;
                currentNode.select();
                return;
            }
            else if (x4 && !y4) {
                currentNode.deselect();
                currentNode = ur;
                currentNode.select();
                return;
            }
        }
        return;
    }

    /**
     * Return an array of the squares (as class Rectangle) to draw on the screen
     * @return
     */
    public Rectangle[] rectanglesToDraw ( ) {
        return allBlocks.toArray(new Rectangle[allBlocks.size()]);
    }

    /**
     * smash a square into 4 smaller squares (if possible)
     * a square at max depth level is not allowed to be smashed
     * leave the selected square as the square that was just
     * smashed (it's just not visible anymore)
     * The method checks to see if the the Rectangle has undergone
     * a Smash. If it does, it does not continue. It it hasn't, it
     * removes the current block's visibility (though stays selected)
     * and creates 4 child nodes/rectangles that are now visible.
     */
    public void smash() {
        if(level == levels || currentNode.ll != null){
            System.out.println("Smash failed");
            return;
        }
        currentNode.rectangle.setColor(Color.BLACK);
        currentNode.createChildren();
        System.out.println("Smash succeeded");
    }

    /**
     * Rotate the descendants of the currently selected square. The method checks the x and y
     * values for all Nodes, then adjusts their x and y for the position that they should now be in.
     * @param clockwise if true rotate clockwise, else counterclockwise
     */
    public void rotate(boolean clockwise) {
        if(currentNode.ll == null){
            System.out.println("No children to rotate");
            return;
        }
        int x1,x2,x3,x4,y1,y2,y3,y4;
        if(currentNode.ul.x > currentNode.x && currentNode.ul.y > currentNode.y){
            x1 = currentNode.x;
            y1 = currentNode.ul.y;
        }
        else if(currentNode.ul.x == currentNode.x && currentNode.ul.y > currentNode.y){
            x1 = currentNode.x;
            y1 = currentNode.y;
        }
        else if(currentNode.ul.x == currentNode.x && currentNode.ul.y == currentNode.y){
            x1 = currentNode.x + currentNode.size/2;
            y1 = currentNode.y;
        }
        else{
            x1 = currentNode.x + currentNode.size/2;
            y1 = currentNode.y + currentNode.size/2;
        }

        if(currentNode.ll.x > currentNode.x && currentNode.ll.y > currentNode.y){//ll
            x2 = currentNode.x;
            y2 = currentNode.ll.y;
        }
        else if(currentNode.ll.x == currentNode.x && currentNode.ll.y > currentNode.y){
            x2 = currentNode.x;
            y2 = currentNode.y;
        }
        else if(currentNode.ll.x == currentNode.x && currentNode.ll.y == currentNode.y){
            x2 = currentNode.x + currentNode.size/2;
            y2 = currentNode.y;
        }
        else{
            x2 = currentNode.x + currentNode.size/2;
            y2 = currentNode.y + currentNode.size/2;
        }

        if(currentNode.ur.x > currentNode.x && currentNode.ur.y > currentNode.y){//ur
            x3 = currentNode.x;
            y3 = currentNode.ur.y;
        }
        else if(currentNode.ur.x == currentNode.x && currentNode.ur.y > currentNode.y){
            x3 = currentNode.x;
            y3 = currentNode.y;
        }
        else if(currentNode.ur.x == currentNode.x && currentNode.ur.y == currentNode.y){
            x3 = currentNode.x + currentNode.size/2;
            y3 = currentNode.y;
        }
        else{
            x3 = currentNode.x + currentNode.size/2;
            y3 = currentNode.y + currentNode.size/2;
        }

        if(currentNode.lr.x > currentNode.x && currentNode.lr.y > currentNode.y){//lr
            x4 = currentNode.x;
            y4 = currentNode.lr.y;
        }
        else if(currentNode.lr.x == currentNode.x && currentNode.lr.y > currentNode.y){
            x4 = currentNode.x;
            y4 = currentNode.y;
        }
        else if(currentNode.lr.x == currentNode.x && currentNode.lr.y == currentNode.y){
            x4 = currentNode.x + currentNode.size/2;
            y4 = currentNode.y;
        }
        else{
            x4 = currentNode.x + currentNode.size/2;
            y4 = currentNode.y + currentNode.size/2;
        }
        int size = currentNode.size / 2;


        if(clockwise){
            currentNode.clockwise();
            moveCW(x1,y1,size,currentNode.ul);
            moveCW(x2,y2,size,currentNode.ll);
            moveCW(x3,y3,size,currentNode.ur);
            moveCW(x4,y4,size,currentNode.lr);
            currentNode.ll.id = 4;
            currentNode.ul.id = 1;
            currentNode.ur.id = 2;
            currentNode.lr.id = 3;
        }
        else{
            currentNode.counterclockwise();

            if(currentNode.ul.x > currentNode.x && currentNode.ul.y > currentNode.y){
                x1 = currentNode.x + currentNode.size/2;
                y1 = currentNode.y;
            }
            else if(currentNode.ul.x == currentNode.x && currentNode.ul.y > currentNode.y){
                x1 = currentNode.x + currentNode.size/2;
                y1 = currentNode.y + currentNode.size/2;
            }
            else if(currentNode.ul.x == currentNode.x && currentNode.ul.y == currentNode.y){
                x1 = currentNode.x;
                y1 = currentNode.y + currentNode.size/2;
            }
            else{
                x1 = currentNode.x;
                y1 = currentNode.y;
            }

            if(currentNode.ll.x > currentNode.x && currentNode.ll.y > currentNode.y){//ll
                x2 = currentNode.x + currentNode.size/2;
                y2 = currentNode.y;
            }
            else if(currentNode.ll.x == currentNode.x && currentNode.ll.y > currentNode.y){
                x2 = currentNode.x + currentNode.size/2;
                y2 = currentNode.y + currentNode.size/2;
            }
            else if(currentNode.ll.x == currentNode.x && currentNode.ll.y == currentNode.y){
                x2 = currentNode.x;
                y2 = currentNode.y + currentNode.size/2;
            }
            else{
                x2 = currentNode.x;
                y2 = currentNode.y;
            }

            if(currentNode.ur.x > currentNode.x && currentNode.ur.y > currentNode.y){//ur
                x3 = currentNode.x + currentNode.size/2;
                y3 = currentNode.y;
            }
            else if(currentNode.ur.x == currentNode.x && currentNode.ur.y > currentNode.y){
                x3 = currentNode.x + currentNode.size/2;
                y3 = currentNode.y + currentNode.size/2;
            }
            else if(currentNode.ur.x == currentNode.x && currentNode.ur.y == currentNode.y){
                x3 = currentNode.x;
                y3 = currentNode.y + currentNode.size/2;
            }
            else{
                x3 = currentNode.x;
                y3 = currentNode.y;
            }

            if(currentNode.lr.x > currentNode.x && currentNode.lr.y > currentNode.y){//lr
                x4 = currentNode.x + currentNode.size/2;
                y4 = currentNode.y;
            }
            else if(currentNode.lr.x == currentNode.x && currentNode.lr.y > currentNode.y){
                x4 = currentNode.x + currentNode.size/2;
                y4 = currentNode.y + currentNode.size/2;
            }
            else if(currentNode.lr.x == currentNode.x && currentNode.lr.y == currentNode.y){
                x4 = currentNode.x;
                y4 = currentNode.y + currentNode.size/2;
            }
            else{
                x4 = currentNode.x;
                y4 = currentNode.y;
            }
            moveCCW(x2,y2,size,currentNode.ll);
            moveCCW(x3,y3,size,currentNode.ur);
            moveCCW(x4,y4,size,currentNode.lr);
            moveCCW(x1,y1,size,currentNode.ul);
            currentNode.ll.id = 4;
            currentNode.ul.id = 1;
            currentNode.ur.id = 2;
            currentNode.lr.id = 3;
        }
    }

    /**
     * The method checks cwRotation of the Node, then adjusts their x and y for
     * the position that they should now be in.
     * @param x1 x of parent
     * @param y1 y of parent
     * @param size size
     * @param n the node being moved
     */
    public void moveCW(int x1, int y1, int size, Node n){
        n.setX(x1);
        n.setY(y1);
        if(n.ll == null){
            return;
        }
        else{
            n.clockwise();
            if(Math.abs(n.cwRotation)%4 == 0) {
                moveCW(x1 + size / 2, y1, size / 2, n.ur);
                moveCW(x1 + size / 2, y1 + size / 2, size / 2, n.lr);
                moveCW(x1, y1 + size / 2, size / 2, n.ll);
                moveCW(x1, y1, size / 2, n.ul);

                return;
            }
            else if (Math.abs(n.cwRotation) % 4 == 3) {
                moveCW(x1+size/2,y1,size/2,n.lr);
                moveCW(x1+size/2,y1+size/2,size/2,n.ll);
                moveCW(x1,y1+size/2,size/2,n.ul);
                moveCW(x1,y1,size/2,n.ur);

                return;
            }
            else if (Math.abs(n.cwRotation) % 4 == 2) {
                moveCW(x1+size/2,y1,size/2,n.ll);
                moveCW(x1+size/2,y1+size/2,size/2,n.ul);
                moveCW(x1,y1+size/2,size/2,n.ur);
                moveCW(x1,y1,size/2,n.lr);

                return;
            }
            else{
                moveCW(x1+size/2,y1,size/2,n.ul);
                moveCW(x1+size/2,y1+size/2,size/2,n.ur);
                moveCW(x1,y1+size/2,size/2,n.lr);
                moveCW(x1,y1,size/2,n.ll);

                return;
            }
        }
    }

    /**
     * The method checks cwRotation of the Node, then adjusts their x and y for
     * the position that they should now be in.
     * @param x1 x of parent
     * @param y1 y of parent
     * @param size size
     * @param n the node being moved
     */
    public void moveCCW(int x1, int y1, int size, Node n){
        n.setX(x1);
        n.setY(y1);
        if(n.ll == null){
            return;
        }
        else{
            n.counterclockwise();
            System.out.println("CWR: " + n.cwRotation + " %4: " + n.cwRotation%4);
            if(Math.abs(n.cwRotation)%4 == 0) {
                moveCCW(x1,y1,size/2,n.ul);
                moveCCW(x1+size/2,y1,size/2,n.ur);
                moveCCW(x1+size/2,y1+size/2,size/2,n.lr);
                moveCCW(x1,y1+size/2,size/2,n.ll);

                return;
            }

            else if (Math.abs(n.cwRotation) % 4 == 3) {
                moveCCW(x1,y1,size/2,n.ur);
                moveCCW(x1+size/2,y1,size/2,n.lr);
                moveCCW(x1+size/2,y1+size/2,size/2,n.ll);
                moveCCW(x1,y1+size/2,size/2,n.ul);

                return;
            }
            else if (Math.abs(n.cwRotation % 4) == 2) {
                moveCCW(x1,y1,size/2,n.lr);
                moveCCW(x1+size/2,y1,size/2,n.ll);
                moveCCW(x1+size/2,y1+size/2,size/2,n.ul);
                moveCCW(x1,y1+size/2,size/2,n.ur);
                return;
            }

            else{
                moveCCW(x1,y1,size/2,n.ll);
                moveCCW(x1+size/2,y1,size/2,n.ul);
                moveCCW(x1+size/2,y1+size/2,size/2,n.ur);
                moveCCW(x1,y1+size/2,size/2,n.lr);
                return;
            }





        }
    }

    /**
     * This is a method for adjusting Node coordinates through a horizontal flip.
     * It compares the coordinates to what they should be, and makes the necessary changes.
     * It also corrcts the cwRoatation
     * @param n
     * @param sameX
     * @param sameY
     */
    public void swapX(Node n, boolean sameX, boolean sameY){
        Node parent = n.parent;
        boolean x1=false;
        boolean x2=false;
        boolean x3=false;
        boolean x4=false;
        boolean y1=false;
        boolean y2=false;
        boolean y3=false;
        boolean y4=false;
        if (n.ll != null){
            if(n.ul.x == n.x){x1 = true;}
            if(n.ll.x == n.x){x2 = true;}
            if(n.lr.x == n.x){x3 = true;}
            if(n.ur.x == n.x){x4 = true;}
            if(n.ul.y == n.y){y1 = true;}
            if(n.ll.y == n.y){y2 = true;}
            if(n.lr.y == n.y){y3 = true;}
            if(n.ur.y == n.y){y4 = true;}
        }
        if(sameX){
            n.setX(parent.x + parent.size/2);
        }
        else{
            n.setX(parent.x);
        }
        if(sameY){
            n.setY(parent.y);
        }
        else{
            n.setY(parent.y + parent.size/2);
        }
        if(n.ll == null){return;}


        swapX(n.ul,x1,y1);
        swapX(n.ll,x2,y2);
        swapX(n.lr,x3,y3);
        swapX(n.ur,x4,y4);

      /*
        if(n.ul.x == n.x){x1 = true;}
        if(n.ll.x == n.x){x2 = true;}
        if(n.lr.x == n.x){x3 = true;}
        if(n.ur.x == n.x){x4 = true;}
        if(n.ul.y == n.y){y1 = true;}
        if(n.ll.y == n.y){y2 = true;}
        if(n.lr.y == n.y){y3 = true;}
        if(n.ur.y == n.y){y4 = true;}


        if(x1 && y1){n.ul.setCWR(0);}
        else if(x1 && !y1){n.ul.setCWR(3);}
        else if(!x1 && y1){n.ul.setCWR(1);}
        else {n.ul.setCWR(2);}

        if(x2 && y2){n.ll.setCWR(1);}//
        else if(x2 && !y2){n.ll.setCWR(0);}
        else if(!x2 && y2){n.ll.setCWR(2);}
        else {n.ll.setCWR(3);}

        if(x3 && y3){n.lr.setCWR(2);}//
        else if(x3 && !y3){n.lr.setCWR(1);}
        else if(!x3 && y3){n.lr.setCWR(3);}
        else {n.lr.setCWR(0);}

        if(x4 && y4){n.ur.setCWR(3);}//
        else if(x4 && !y4){n.ur.setCWR(2);}
        else if(!x4 && y4){n.ur.setCWR(0);}
        else {n.ur.setCWR(1);}*/


        Node lowerLeft = n.ll;
        Node upperLeft = n.ul;
        Node upperRight = n.ur;
        Node lowerRight = n.lr;
        n.ll = lowerRight;
        n.lr = lowerLeft;
        n.ur = upperLeft;
        n.ul = upperRight;
        n.ll.id = 4;
        n.ul.id = 1;
        n.ur.id = 2;
        n.lr.id = 3;

        return;
    }

    /**
     * This is a method just for adjusting the Node through a vertical flip.
     * It compares the coordinates to what they should be, and replaces them as needed.
     * It also corrcts the cwRoatation
     * @param n
     * @param sameY
     * @param sameX
     */
    public void swapY(Node n, boolean sameY, boolean sameX){
        Node parent = n.parent;
        boolean x1=false;
        boolean x2=false;
        boolean x3=false;
        boolean x4=false;
        boolean y1=false;
        boolean y2=false;
        boolean y3=false;
        boolean y4=false;
        if (n.ll != null){
            if(n.ul.x == n.x){x1 = true;}
            if(n.ll.x == n.x){x2 = true;}
            if(n.lr.x == n.x){x3 = true;}
            if(n.ur.x == n.x){x4 = true;}
            if(n.ul.y == n.y){y1 = true;}
            if(n.ll.y == n.y){y2 = true;}
            if(n.lr.y == n.y){y3 = true;}
            if(n.ur.y == n.y){y4 = true;}
        }
        if(sameY){
            n.setY(parent.y + parent.size/2);
        }
        else{
            n.setY(parent.y);
        }
        if(sameX){
            n.setX(parent.x);
        }
        else{
            n.setX(parent.x + parent.size/2);
        }
        if(n.ll == null){return;}
        swapY(n.ul, y1,x1);
        swapY(n.ll,y2,x2);
        swapY(n.lr,y3,x3);
        swapY(n.ur,y4,x4);
/*        if(n.ul.x == n.x){x1 = true;}
        if(n.ll.x == n.x){x2 = true;}
        if(n.lr.x == n.x){x3 = true;}
        if(n.ur.x == n.x){x4 = true;}
        if(n.ul.y == n.y){y1 = true;}
        if(n.ll.y == n.y){y2 = true;}
        if(n.lr.y == n.y){y3 = true;}
        if(n.ur.y == n.y){y4 = true;}

        if(x1 && y1){n.ul.setCWR(0);}
        else if(x1 && !y1){n.ul.setCWR(3);}
        else if(!x1 && y1){n.ul.setCWR(1);}
        else {n.ul.setCWR(2);}

        if(x2 && y2){n.ll.setCWR(1);}
        else if(x2 && !y2){n.ll.setCWR(0);}
        else if(!x2 && y2){n.ll.setCWR(2);}
        else {n.ll.setCWR(3);}

        if(x3 && y3){n.lr.setCWR(2);}
        else if(x3 && !y3){n.lr.setCWR(1);}
        else if(!x3 && y3){n.lr.setCWR(3);}
        else {n.lr.setCWR(0);}

        if(x4 && y4){n.ur.setCWR(3);}
        else if(x4 && !y4){n.ur.setCWR(2);}
        else if(!x4 && y4){n.ur.setCWR(0);}
        else {n.ur.setCWR(1);}*/

        Node lowerLeft = n.ll;
        Node upperLeft = n.ul;
        Node upperRight = n.ur;
        Node lowerRight = n.lr;
        n.ll = upperLeft;
        n.lr = upperRight;
        n.ur = lowerRight;
        n.ul = lowerLeft;
        n.ll.id = 4;
        n.ul.id = 1;
        n.ur.id = 2;
        n.lr.id = 3;


        return;

    }

    /**
     * flip the descendants of the currently selected square
     * the descendants will become the mirror image
     * The method checks the x and y of the Nodes, tells them how
     * they should flip, and adjusts their cwRotation
     * @param horizontally if true then flip over the x-axis,
     *                     else flip over the y-axis
     */
    public void swap (boolean horizontally) {
        if(currentNode.ll == null){
            System.out.println("Can not swap, no children present to swap");
            return;
        }

        boolean x1=false;
        boolean x2=false;
        boolean x3=false;
        boolean x4=false;
        if(currentNode.ul.x == currentNode.x){x1 = true;}
        if(currentNode.ll.x == currentNode.x){x2 = true;}
        if(currentNode.lr.x == currentNode.x){x3 = true;}
        if(currentNode.ur.x == currentNode.x){x4 = true;}

        boolean y1=false;
        boolean y2=false;
        boolean y3=false;
        boolean y4=false;
        if(currentNode.ul.y == currentNode.y){y1 = true;}
        if(currentNode.ll.y == currentNode.y){y2 = true;}
        if(currentNode.lr.y == currentNode.y){y3 = true;}
        if(currentNode.ur.y == currentNode.y){y4 = true;}

        if(horizontally){
            swapY(currentNode.ul,y1,x1);
            swapY(currentNode.ll,y2,x2);
            swapY(currentNode.lr,y3,x3);
            swapY(currentNode.ur,y4,x4);
            Node lowerLeft = currentNode.ll;
            Node upperLeft = currentNode.ul;
            Node upperRight = currentNode.ur;
            Node lowerRight = currentNode.lr;
            currentNode.ll = upperLeft;
            currentNode.lr = upperRight;
            currentNode.ur = lowerRight;
            currentNode.ul = lowerLeft;
            currentNode.ll.id = 4;
            currentNode.ul.id = 1;
            currentNode.ur.id = 2;
            currentNode.lr.id = 3;
        }
        else {
            swapX(currentNode.ul, x1, y1);
            swapX(currentNode.ll, x2, y2);
            swapX(currentNode.lr, x3, y3);
            swapX(currentNode.ur, x4, y4);
            Node lowerLeft = currentNode.ll;
            Node upperLeft = currentNode.ul;
            Node upperRight = currentNode.ur;
            Node lowerRight = currentNode.lr;
            currentNode.ll = lowerRight;
            currentNode.lr = lowerLeft;
            currentNode.ur = upperLeft;
            currentNode.ul = upperRight;
            currentNode.ll.id = 4;
            currentNode.ul.id = 1;
            currentNode.ur.id = 2;
            currentNode.lr.id = 3;
        }

        if(currentNode.ul.x == currentNode.x){x1 = true;}
        if(currentNode.ll.x == currentNode.x){x2 = true;}
        if(currentNode.lr.x == currentNode.x){x3 = true;}
        if(currentNode.ur.x == currentNode.x){x4 = true;}

        if(currentNode.ul.y == currentNode.y){y1 = true;}
        if(currentNode.ll.y == currentNode.y){y2 = true;}
        if(currentNode.lr.y == currentNode.y){y3 = true;}
        if(currentNode.ur.y == currentNode.y){y4 = true;}

/*        if(x1 && y1){currentNode.ul.setCWR(3);}
        else if(x1 && !y1){currentNode.ul.setCWR(2);}
        else if(!x1 && y1){currentNode.ul.setCWR(0);}
        else {currentNode.ul.setCWR(1);}

        if(x2 && y2){currentNode.ll.setCWR(0);}
        else if(x2 && !y2){currentNode.ll.setCWR(3);}
        else if(!x2 && y2){currentNode.ll.setCWR(1);}
        else {currentNode.ll.setCWR(2);}

        if(x3 && y3){currentNode.lr.setCWR(1);}
        else if(x3 && !y3){currentNode.lr.setCWR(0);}
        else if(!x3 && y3){currentNode.lr.setCWR(2);}
        else {currentNode.lr.setCWR(3);}

        if(x4 && y4){currentNode.ur.setCWR(2);}
        else if(x4 && !y4){currentNode.ur.setCWR(1);}
        else if(!x4 && y4){currentNode.ur.setCWR(3);}
        else {currentNode.ur.setCWR(0);}*/

    }
}
