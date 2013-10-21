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


public class Weapon extends PaintableSequenceComposite{
    PaintableSequence sequence = getPaintableSequence();
Posn position;
Velocity velocity;
int lifespan;

XLine2D bulletLine = new XLine2D (10, 10, 20, 10);

Color bulletColor = Colors.blue;

ShapePaintable bullet = new ShapePaintable (bulletLine);

// constructor

public Weapon (Posn position, Velocity velocity, int lifespan) {
    this.position = position;
    this.velocity = velocity;
    this.lifespan = lifespan;
}

public Weapon (Ship s) {
    position = s.position;
    velocity = new Velocity (bulletAcceleration, s.direction);
    lifespan = initialLifespan;
    sequence.addPaintable (bullet);
}
final int bulletAcceleration = 10;
final int initialLifespan = 5;

// tick the next instance of the weapon
public void tick () {
	this.position.tick(this.velocity);
}

}
