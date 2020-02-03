import java.awt.*;

/**
 * Class to represent a square on the game board
 * This is what is rendered on the game board
 * This class is also what is used to set the score
 *
 */
public class Rectangle {

    //the x-coordinate of this square
    private int x;
    //the y-coordinate of this square
    private int y;
    //the length of one side of this square
    private int size;
    //the color of the square
    private Color color;
    //is the square visible (or is it covered by its children)
    private boolean isVisible;
    //is this the currently selected square (yellow highlighted)
    private boolean isSelected;

    //the amount of this square is touching the edge of the game board
    private int borderSize;

    private int maxsize;


    /**
     * Contructor
     * @param x1 x coord
     * @param y1 y coord
     * @param sz length of size
     * @param colr color
     * @param visible is visible
     * @param selected is selected
     */
    public Rectangle(int x1, int y1, int sz, Color colr, boolean visible, boolean selected) {

        x = x1;
        y = y1;
        size = sz;
        color = colr;
        isVisible = visible;
        isSelected = selected;
        borderSize = 0;
        maxsize = 0;
    }

    //return the x coord
    public int getX() {
        return x;
    }

    public void setX(int x1){x = x1;}

    public void setMAx(int max){maxsize = max;}

    //return the y coord
    public int getY() {
        return y;
    }

    public void setY(int y1){y = y1;}

    //return the length of one side
    public int getSize() {
        return size;
    }

    //return the color
    public Color getColor() {
        return color;
    }


    //is this square visible
    public boolean isVisible() {
        return isVisible;
    }

    //is it selected (yellow highlighted)
    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected){this.isSelected = selected;}

    public void setVisible(boolean visible){this.isVisible = visible;}

    //set the amount of this square touching the edge of game board
    public void setBorderSize(int border) {
        borderSize = border;
    }

    //get the amount touching the edge of the game board
    public int getBorderSize() {
        borderSize = 0;
        if(!isVisible){return borderSize;}
        if (x==0 && y == 0){borderSize = 2*size;}
        else if(x == 0 && y == maxsize - size){borderSize = 2*size;}
        else if(x == maxsize-size && y == maxsize - size){borderSize = 2*size;}
        else if(x == maxsize - size && y != maxsize - size && y != 0){borderSize = size;}
        else if(x == 0 && y != maxsize - size && y != 0){borderSize = size;}
        else if(y == 0 && x == maxsize - size){borderSize = 2*size;}
        else if(y == maxsize - size && x != maxsize - size && x != 0){borderSize = size;}
        else if(y == 0 && x != maxsize - size && x != 0){borderSize = size;}
        else{borderSize = 0;}
        return borderSize;
    }

    public void selected(boolean s){isSelected = s;}//


}
