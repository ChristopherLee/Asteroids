/* @(#)Methods.java   15 September 2005 */

/* Useful imports */

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

/** The sample starter class for Java Power Framework. */
public class Methods extends JPF 
{
    Ship s1;
    public static void main(String[] args) { 
        // To optionally adjust the look and feel, remove
        // the comments from one of the two statements below
        
        // In the second call, the font size adjustment should
        // be a small integer probably between 2 and 12
        
        // LookAndFeelTools.showSelectLookAndFeelDialog();
        // LookAndFeelTools.adjustAllDefaultFontSizes(2);
        
        new Methods();
    }
    

    public void Asteroids() {
        window.clearPanelAndSequence();
        window.installSimpleMouseActions(true);
        
        s1 = new Ship ();
        window.addPaintable(s1);
        //window.addPaintable(new Weapon(s1));
        String s = s1.energy + "";
        TextPaintable tp = new TextPaintable (s, 50, 50);
        window.addPaintable(s1.energy + "");
        window.repaint();
 }
    
    public void turn () {
        s1.turnLeft ();
        window.repaint();
    }
}