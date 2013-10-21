
import edu.neu.ccs.*;
import edu.neu.ccs.gui.*;
import edu.neu.ccs.codec.*;
import edu.neu.ccs.console.*;
import edu.neu.ccs.filter.*;
import edu.neu.ccs.jpf.*;
import edu.neu.ccs.parser.*;
import edu.neu.ccs.pedagogy.*;
import edu.neu.ccs.quick.*;
import edu.neu.ccs.util.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.font.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;
import java.util.*;
import java.math.*;
import java.beans.*;
import java.lang.reflect.*;
import java.net.URL;
import java.util.regex.*;
import java.text.ParseException;

public class Posn {
int x;
int y;

// constructor 
public Posn (int x, int y) {
	this.x = x;
	this.y = y;
}


public void tick (Velocity v) {
	int dx = (int) (MathUtilities.cosdeg (v.direction) * v.getSpeed());
	int dy = (int) (MathUtilities.sindeg(360-v.direction) * v.getSpeed());
	this.x += dx;
	this.y += dy;
}

// string of position
public String toString () {
	return "(" + this.x + ", " + this.y + ")";
}

//get x
public int getX () {
    return this.x;
}

//get y
public int getY () {
    return this.y;
}

}

