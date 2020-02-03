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
    private ArrayList<Rectangle> allBlocks;
    private int levels, level, cWCount;

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
        root = new Node(null, 0, 0, MAX_SIZE, 1);
        root.createChildren();
        root.select();
        currentNode = root;
        level = 1;
        cWCount = 0;
        /*for(int i = 0; i < allBlocks.size();i++){
            Rectangle r = allBlocks.get(i);
            System.out.println("Index: " + i + " selected: " + r.isSelected());
        }*/

    }

    private Node root;
    private Node currentNode;

    /**
     *
     */
    private class Node{
        private Rectangle rectangle;
        private boolean children, selected, visible;
        private Node parent, ul, ur, ll, lr;
        private int x,y,size,cl,id, cwRotation;
        private Color colour;

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
            ul = null;
            ur = null;
            ll = null;
            lr = null;

            rectangle = new Rectangle(x,y,size,colour,visible,selected);
            select();
            allBlocks.add(rectangle);
        }

        private Node(int x1, int y1, int size1, Node n, int identity ){
            id = identity;
            parent = n;
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

            deselect();
            allBlocks.add(rectangle);
        }

        private void clockwise(){cwRotation++;}
        private void counterclockwise(){cwRotation--;}

        private void createChildren(){
            ul = new Node(x,y,size/2,this, 1);
            ur = new Node(x + size/2, y, size/2,this, 2);
            ll = new Node(x,y+size/2,size/2,this, 4);
            lr = new Node(x+size/2,y+size/2,size/2,this, 3);
            visible = false;
        }

        private int getID(){return id;}

        private void setCWR(int c){cwRotation = c;}

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

        private void setX(int x1){
            x = x1;
            rectangle.setX(x1);
        }

        private void setY(int y1){
            y = y1;
            rectangle.setY(y1);
        }

        private void select(){
            rectangle.selected(true);
            selected = true;
        }

        private void deselect(){
            rectangle.selected(false);
            selected = false;
        }

    }

    /**
     * The selected square moves up to be its parent (if possible)
     */
    public void moveUp() {
        /*for(int i = 0; i < allBlocks.size();i++){
            Rectangle r = allBlocks.get(i);
            System.out.println("Index: " + i + " selected: " + r.isSelected());
        }*/
        if(level <= 1 || currentNode.parent == null){
            System.out.println("Move up failed");
            return;}
        currentNode.deselect();
        currentNode = currentNode.parent;
        //currentNode = currentNode.selectNode(1);
        //assert currentNode != null;
        currentNode.select();
        /*for(int i = 0; i < allBlocks.size();i++){
            Rectangle r = allBlocks.get(i);
            System.out.println("Moved up: Index: " + i + " selected: " + r.isSelected());
        }*/
        level--;
        System.out.println("level: " + level);
    }

    /**
     * the selected square moves into the upper right child (if possible)
     * of the currently selected square
     */
    public void moveDown() {
        /*for(int i = 0; i < allBlocks.size();i++){
            Rectangle r = allBlocks.get(i);
            System.out.println("Index: " + i + " selected: " + r.isSelected());
        }*/
        if(level >= levels || currentNode.ll == null){
            System.out.println("Move down failed");
            return;
        }
        currentNode.deselect();
        currentNode = currentNode.ul;
        currentNode.select();
        level++;
        /*for(int i = 0; i < allBlocks.size();i++){
            Rectangle r = allBlocks.get(i);
            System.out.println("Moved down: Index: " + i + " selected: " + r.isSelected());
        }*/
        System.out.println("Level: " + level);
    }

    /**
     * the selected square moves counter clockwise to a sibling
     */
    public void moveLeft() {
        /*for(int i = 0; i < allBlocks.size();i++){
            Rectangle r = allBlocks.get(i);
            System.out.println("Move left: Index: " + i + " selected: " + r.isSelected());
        }*/
        if(level == 1 || currentNode.parent == null){
            System.out.println("At root, no movement possible");
            return;
        }
        if(currentNode.getID() == 4){//currentNode == currentNode.parent.ll
            currentNode.deselect();
            currentNode = currentNode.parent.lr;
            currentNode.select();
           /* for(int i = 0; i < allBlocks.size();i++){
                Rectangle r = allBlocks.get(i);
                System.out.println("Index: " + i + " selected: " + r.isSelected());
            }*/
            System.out.println("Level: " + level);
            return;
        }
        else if(currentNode.getID() == 1){//currentNode == currentNode.parent.ul
            currentNode.deselect();
            currentNode = currentNode.parent.ll;//make the rest of the alterations
            currentNode.select();
            /*for(int i = 0; i < allBlocks.size();i++){
                Rectangle r = allBlocks.get(i);
                System.out.println("Index: " + i + " selected: " + r.isSelected());
            }*/
            System.out.println("Level: " + level);
            return;
        }
        else if(currentNode.getID() == 2){//currentNode == currentNode.parent.ur
            currentNode.deselect();
            currentNode = currentNode.parent.ul;//make the rest of the alterations
            currentNode.select();
           /* for(int i = 0; i < allBlocks.size();i++){
                Rectangle r = allBlocks.get(i);
                System.out.println("Index: " + i + " selected: " + r.isSelected());
            }*/
            System.out.println("Level: " + level);
            return;
        }
        else if(currentNode.getID() == 3){//currentNode == currentNode.parent.lr
            currentNode.deselect();
            currentNode = currentNode.parent.ur;//make the rest of the alterations
            currentNode.select();
            /*for(int i = 0; i < allBlocks.size();i++){
                Rectangle r = allBlocks.get(i);
                System.out.println("Index: " + i + " selected: " + r.isSelected());
            }*/
            System.out.println("Level: " + level);
            return;
        }
        return;
    }

    /**
     * Move the selected square to the next sibling clockwise
     */
    public void moveRight() {
        /*for(int i = 0; i < allBlocks.size();i++){
            Rectangle r = allBlocks.get(i);
            System.out.println("Move right: Index: " + i + " selected: " + r.isSelected());
        }*/
        if(level == 1 || currentNode.parent == null){
            System.out.println("At root, no movement possible");
            return;
        }
        if(currentNode.getID() == 4){//currentNode == currentNode.parent.ll
            currentNode.deselect();
            currentNode = currentNode.parent.ul;
            currentNode.select();
            /*for(int i = 0; i < allBlocks.size();i++){
                Rectangle r = allBlocks.get(i);
                System.out.println("Index: " + i + " selected: " + r.isSelected());
            }*/
            System.out.println("Level: " + level);
            return;
        }
        else if(currentNode.getID() == 1){//currentNode == currentNode.parent.ul
            currentNode.deselect();
            currentNode = currentNode.parent.ur;
            currentNode.select();
            /*for(int i = 0; i < allBlocks.size();i++){
                Rectangle r = allBlocks.get(i);
                System.out.println("Index: " + i + " selected: " + r.isSelected());
            }*/
            System.out.println("Level: " + level);
            return;
        }
        else if(currentNode.getID() == 2){//currentNode == currentNode.parent.ur
            currentNode.deselect();
            currentNode = currentNode.parent.lr;//make the rest of the alterations
            currentNode.select();
            /*for(int i = 0; i < allBlocks.size();i++){
                Rectangle r = allBlocks.get(i);
                System.out.println("Index: " + i + " selected: " + r.isSelected());
            }*/
            System.out.println("Level: " + level);
            return;
        }
        else if(currentNode.getID() == 3){//currentNode == currentNode.parent.lr
            currentNode.deselect();
            currentNode = currentNode.parent.ll;//make the rest of the alterations
            currentNode.select();
            /*for(int i = 0; i < allBlocks.size();i++){
                Rectangle r = allBlocks.get(i);
                System.out.println("Index: " + i + " selected: " + r.isSelected());
            }*/
            System.out.println("Level: " + level);
            return;
        }
        return;
    }

    /**
     * Return an array of the squares (as class Rectangle) to draw on the screen
     * @return
     */
    public Rectangle[] rectanglesToDraw ( ) {

        //placeholder code with hard coded values
        //just to show something on the screen
        //delete this code to complete the assignment

        /*ArrayList<Rectangle> allBlocks = new ArrayList<Rectangle>();

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

        return allBlocks.toArray(new Rectangle[allBlocks.size()]);
    }

    /**
     * smash a square into 4 smaller squares (if possible)
     * a square at max depth level is not allowed to be smashed
     * leave the selected square as the square that was just
     * smashed (it's just not visible anymore)
     */
    public void smash() {
        if(level == levels || currentNode.ll != null){
            System.out.println("Smash failed");
            return;
        }
        currentNode.createChildren();
        System.out.println("Smash succeeded");
        System.out.println("Level: " + level);
    }

    /**
     * Rotate the descendants of the currently selected square
     * @param clockwise if true rotate clockwise, else counterclockwise
     */
    public void rotate(boolean clockwise) {
        if(currentNode.ll == null){
            System.out.println("No children to rotate");
            return;
        }
        /*int x1 = currentNode.ul.getX();
        int x2 = currentNode.ur.getX();
        int x3 = currentNode.lr.getX();
        int x4 = currentNode.ll.getX();
        int y1 = currentNode.ul.getY();
        int y2 = currentNode.ur.getY();
        int y3 = currentNode.lr.getY();
        int y4 = currentNode.ll.getY();*/
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
            //currentNode.clockwise();
            cWCount++;
            /*if(cWCount%4 == 0){
                moveCW(x2,y2,size,currentNode.ur);
                moveCW(x3,y3,size,currentNode.lr);
                moveCW(x4,y4,size,currentNode.ll);
                moveCW(x1,y1,size,currentNode.ul);
            }
            else if (cWCount % 3 == 0) {
                moveCW(x2,y2,size,currentNode.lr);
                moveCW(x3,y3,size,currentNode.ll);
                moveCW(x4,y4,size,currentNode.ul);
                moveCW(x1,y1,size,currentNode.ur);
            }
            else if (cWCount % 2 == 0) {
                moveCW(x2,y2,size,currentNode.ur);
                moveCW(x3,y3,size,currentNode.ul);
                moveCW(x4,y4,size,currentNode.ur);
                moveCW(x1,y1,size,currentNode.lr);
            }
            else{
                moveCW(x2,y2,size,currentNode.ul);
                moveCW(x3,y3,size,currentNode.ur);
                moveCW(x4,y4,size,currentNode.lr);
                moveCW(x1,y1,size,currentNode.ll);
            }
            moveCW(x2,y2,size,currentNode.ul);
            moveCW(x3,y3,size,currentNode.ur);
            moveCW(x4,y4,size,currentNode.lr);
            moveCW(x1,y1,size,currentNode.ll);*/
            moveCW(x1,y1,size,currentNode.ul);
            moveCW(x2,y2,size,currentNode.ll);
            moveCW(x3,y3,size,currentNode.ur);
            moveCW(x4,y4,size,currentNode.lr);
        }
        else{

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
            //currentNode.counterclockwise();
            cWCount--;
            moveCCW(x2,y2,size,currentNode.ll);
            moveCCW(x3,y3,size,currentNode.ur);
            moveCCW(x4,y4,size,currentNode.lr);
            moveCCW(x1,y1,size,currentNode.ul);
            /*
            moveCCW(x2,y2,size,currentNode.lr);
            moveCCW(x3,y3,size,currentNode.ll);
            moveCCW(x4,y4,size,currentNode.ul);
            moveCCW(x1,y1,size,currentNode.ur);*/
        }
        System.out.println("CW count: " + cWCount);
    }

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
           /* moveCW(x1, y1, size/2, n.ll);//new UL, send
            moveCW(x1 + size/2, y1, size/2, n.ul);//
            moveCW(x1 + size/2, y1 + size/2, size / 2, n.ur);//
            moveCW(x1, y1 + size/2, size/2, n.lr);*///
        }
        //return;
    }

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
            /*
            if(n.cwRotation%4 == 0) {
                moveCW(x1 + size / 2, y1, size / 2, n.ur);
                moveCW(x1 + size / 2, y1 + size / 2, size / 2, n.lr);
                moveCW(x1, y1 + size / 2, size / 2, n.ll);
                moveCW(x1, y1, size / 2, n.ul);
                return;
            }*/

            else if (Math.abs(n.cwRotation) % 4 == 3) {//
                moveCCW(x1,y1,size/2,n.ur);//ul
                moveCCW(x1+size/2,y1,size/2,n.lr);//ur
                moveCCW(x1+size/2,y1+size/2,size/2,n.ll);//lr
                moveCCW(x1,y1+size/2,size/2,n.ul);//ll
                return;
            }
            /*else if (n.cwRotation % 4 == 3) {
                moveCW(x1+size/2,y1,size/2,n.lr);
                moveCW(x1+size/2,y1+size/2,size/2,n.ll);
                moveCW(x1,y1+size/2,size/2,n.ul);
                moveCW(x1,y1,size/2,n.ur);
                return;
            }*/
            else if (Math.abs(n.cwRotation % 4) == 2) {
                moveCCW(x1,y1,size/2,n.lr);//ul
                moveCCW(x1+size/2,y1,size/2,n.ll);//ur
                moveCCW(x1+size/2,y1+size/2,size/2,n.ul);//lr
                moveCCW(x1,y1+size/2,size/2,n.ur);//ll
                return;
            }
             /*
            else if (n.cwRotation % 4 == 2) {
                moveCW(x1+size/2,y1,size/2,n.ll);
                moveCW(x1+size/2,y1+size/2,size/2,n.ul);
                moveCW(x1,y1+size/2,size/2,n.ur);
                moveCW(x1,y1,size/2,n.lr);
                return;
            }*/

            else{
                moveCCW(x1,y1,size/2,n.ll);//ul
                moveCCW(x1+size/2,y1,size/2,n.ul);//ur
                moveCCW(x1+size/2,y1+size/2,size/2,n.ur);//lr
                moveCCW(x1,y1+size/2,size/2,n.lr);//ll
                return;
            }
            /*
            else{
                moveCW(x1+size/2,y1,size/2,n.ul);
                moveCW(x1+size/2,y1+size/2,size/2,n.ur);
                moveCW(x1,y1+size/2,size/2,n.lr);
                moveCW(x1,y1,size/2,n.ll);
                return;
            }
             */
        }
        //return;
    }

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
        swapX(n.ur,x4,y4);//then add the Cswaps

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

        if(x1 && y1){n.ll.setCWR(1);}
        else if(x1 && !y1){n.ll.setCWR(0);}
        else if(!x1 && y1){n.ll.setCWR(2);}
        else {n.ll.setCWR(3);}

        if(x1 && y1){n.lr.setCWR(2);}
        else if(x1 && !y1){n.lr.setCWR(1);}
        else if(!x1 && y1){n.lr.setCWR(3);}
        else {n.lr.setCWR(0);}

        if(x1 && y1){n.ur.setCWR(3);}
        else if(x1 && !y1){n.ur.setCWR(2);}
        else if(!x1 && y1){n.ur.setCWR(0);}
        else {n.ur.setCWR(1);}
        System.out.println("reached swapx end");
        return;
    }

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
        swapY(n.ur,y4,x4);//then add the Cswaps


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

        if(x1 && y1){n.ll.setCWR(1);}
        else if(x1 && !y1){n.ll.setCWR(0);}
        else if(!x1 && y1){n.ll.setCWR(2);}
        else {n.ll.setCWR(3);}

        if(x1 && y1){n.lr.setCWR(2);}
        else if(x1 && !y1){n.lr.setCWR(1);}
        else if(!x1 && y1){n.lr.setCWR(3);}
        else {n.lr.setCWR(0);}

        if(x1 && y1){n.ur.setCWR(3);}
        else if(x1 && !y1){n.ur.setCWR(2);}
        else if(!x1 && y1){n.ur.setCWR(0);}
        else {n.ur.setCWR(1);}
        System.out.println("reached swapy end");


        return;

    }

    /**
     * flip the descendants of the currently selected square
     * the descendants will become the mirror image
     * @param horizontally if true then flip over the x-axis,
     *                     else flip over the y-axis
     */
    public void swap (boolean horizontally) {//use recursion swap r/l or u/l

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
            swapY(currentNode.ur,y4,x4);//then add the Cswaps
           // return;
        }
        else {
            swapX(currentNode.ul, x1, y1);
            swapX(currentNode.ll, x2, y2);
            swapX(currentNode.lr, x3, y3);
            swapX(currentNode.ur, x4, y4);//then add the Cswaps
        }

        if(currentNode.ul.x == currentNode.x){x1 = true;}
        if(currentNode.ll.x == currentNode.x){x2 = true;}
        if(currentNode.lr.x == currentNode.x){x3 = true;}
        if(currentNode.ur.x == currentNode.x){x4 = true;}

        if(currentNode.ul.y == currentNode.y){y1 = true;}
        if(currentNode.ll.y == currentNode.y){y2 = true;}
        if(currentNode.lr.y == currentNode.y){y3 = true;}
        if(currentNode.ur.y == currentNode.y){y4 = true;}

        if(x1 && y1){currentNode.ul.setCWR(0);}
        else if(x1 && !y1){currentNode.ul.setCWR(3);}
        else if(!x1 && y1){currentNode.ul.setCWR(1);}
        else {currentNode.ul.setCWR(2);}

        if(x1 && y1){currentNode.ll.setCWR(1);}
        else if(x1 && !y1){currentNode.ll.setCWR(0);}
        else if(!x1 && y1){currentNode.ll.setCWR(2);}
        else {currentNode.ll.setCWR(3);}

        if(x1 && y1){currentNode.lr.setCWR(2);}
        else if(x1 && !y1){currentNode.lr.setCWR(1);}
        else if(!x1 && y1){currentNode.lr.setCWR(3);}
        else {currentNode.lr.setCWR(0);}

        if(x1 && y1){currentNode.ur.setCWR(3);}
        else if(x1 && !y1){currentNode.ur.setCWR(2);}
        else if(!x1 && y1){currentNode.ur.setCWR(0);}
        else {currentNode.ur.setCWR(1);}
        //return;


        /*
        if currentNode.ul.x > currentNode.x && currentNode.UL.y > currentNode.y
            ul.cwrotation = 2
       if currentNode.ul.x > currentNode.x && currentNode.UL.y !> currentNode.y
            ul.cwrotation = 1
       if currentNode.ul.x !> currentNode.x && currentNode.UL.y !> currentNode.y
            ul.cwrotation = 0
        if currentNode.ul.x !> currentNode.x && currentNode.UL.y > currentNode.y
            ul.cwrotation = 3
        if currentNode.ur.x > currentNode.x && currentNode.Ur.y > currentNode.y
            ur.cwrotation = 1
       if currentNode.ul.x > currentNode.x && currentNode.Ur.y !> currentNode.y
            ur.cwrotation = 0
       if currentNode.ul.x !> currentNode.x && currentNode.Ur.y !> currentNode.y
            ur.cwrotation = 3
        if currentNode.ur.x !> currentNode.x && currentNode.ur.y > currentNode.y
            ur.cwrotation = 2
        if currentNode.ll.x > currentNode.x && currentNode.ll.y > currentNode.y
            ll.cwrotation = 3
       if currentNode.ul.x > currentNode.x && currentNode.ll.y !> currentNode.y
            ll.cwrotation = 2
       if currentNode.ul.x !> currentNode.x && currentNode.ll.y !> currentNode.y
            ll.cwrotation = 1
        if currentNode.ll.x !> currentNode.x && currentNode.ll.y > currentNode.y
            ll.cwrotation = 0
        if currentNode.lr.x > currentNode.x && currentNode.lr.y > currentNode.y
            lr.cwrotation = 0
       if currentNode.lr.x > currentNode.x && currentNode.lr.y !> currentNode.y
            lr.cwrotation = 3
       if currentNode.lr.x !> currentNode.x && currentNode.lr.y !> currentNode.y
            lr.cwrotation = 2
        if currentNode.lr.x !> currentNode.x && currentNode.lr.y > currentNode.y
            lr.cwrotation = 1

         */

    }

}
