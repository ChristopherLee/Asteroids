/* @(#)BallAnimation.java   26 September 2006 */

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


public class BallAnimation
    extends DisplayPanel
    implements WindowConstants
{
    /** The frame for the animation. */
    private JPTFrame frame = null;
    
    /** The main panel. */
    private TablePanel mainPanel = null;
    
    /** The common panel gap. */
    private int gap = 10;
    
    /** The ball to animate. */
    private ShapePaintable ball = null;
    
    /** The tile that serves as the animation track. */
    private Tile tile = null;
    
    /** The tile width. */
    private int W = 600;
    
    /** The ball diameter. */
    private int D;
    
    /** The ball radius and animation start. */
    private int R;
    
    /** The animation end. */
    private int S;
    
    /** Whether or not the animation is running. */
    private volatile boolean running = false;
    
    /**
     * <p>The object for animation synchronization.</p>
     * 
     * <p>When a thread synchronizes on this object,
     * the thread can complete its tasks before
     * another thread can change state.</p> 
     */
    private Object animationLock = new Object();
    
    /** The maximum animation delay in milliseconds. */
    private int maxDelay = 1000;
    
    /** The minimum animation delay in milliseconds. */
    private int minDelay = 10;
    
    /** The animation x position. */
    private int x;
    
    /** The animation direction = +1 or -1. */
    private int direction = +1;
    
    
    /** The action to run the animation in a separate thread. */
    private ThreadedAction runAction =
        new ThreadedAction
            (new SimpleAction("Run Animation") {
                public void perform()
                { runAnimation(); }
    });
    
    
    /** The action to stop the animation. */
    private SimpleAction stopAction =
        new SimpleAction("Stop Animation") {
            public void perform() { stopAnimation(); }
    };
    
    
    /** The sliding action for the step slider. */
    private SimpleAction stepSliding =
        new SimpleAction("Step Sliding") {
            public void perform() { stepSliding(); }
    };
    
    
    /** The sliding action for the speed slider. */
    private SimpleAction speedSliding =
        new SimpleAction("Speed Sliding") {
            public void perform() { speedSliding(); }
    };
    
    
    /**
     * <p>The window closing action.</p>
     *
     * <p>This action is required to properly terminate
     * the animation thread if the window is closed
     * while the animation is running.</p>
     */
    private SimpleAction closingAction
        = new SimpleAction("Window Closing Action") {
            public void perform() { closingAction(); }
    };
    
    
    /** The slider for the animation step size. */
    private SliderView stepSlider =
        new SliderView(HORIZONTAL, 0, 10, 1);
    
    
    /** The slider for the animation speed in steps per second. */
    private SliderView speedSlider =
        new SliderView(HORIZONTAL, 0, 100, 1);
    
    
    /** The label that echoes the animation step. */
    private Annotation stepLabel =
        new Annotation("1");
    
    
    /** The label that echoes the animation speed. */
    private Annotation speedLabel =
        new Annotation("1");
    
    
    /** The panel with the sliders and the labels. */
    private TablePanel sliderPanel = null;
    
    
    /** The constructor. */
    public BallAnimation() {
        createMainPanel();
        
        createBall();
        createTile();
        decorateSliders();
        stopAnimation();
        
        populateMainPanel();
        createFrame();
    }
    
    
    /** Create the main panel. */ 
    private void createMainPanel() {
        int rows = 3;
        int cols = 1;
        
        mainPanel = new TablePanel(rows, cols, gap, gap, CENTER);
        
        mainPanel.emptyBorder(gap);
    }
    
    
    /** Create the ball as a paintable object. */
    private void createBall() {
        int radius = 20;
        int thick  = 2;
        
        XCircle circle = new XCircle(radius);
        
        Color fill = Colors.red;
        Color draw = Colors.blue;
        BasicStroke stroke = new BasicStroke(thick);
        
        ball = new ShapePaintable
            (circle, PaintMode.FILL_DRAW, fill, draw, stroke);
        
        R = radius + (thick / 2);
        S = W - R;
        
        D = 2 * R;
        
        ball.moveCenterTo(R, R);
        
        x = R;
    }
   
    
    /** Create the animation track as a tile object. */
    private void createTile() {
        PaintableSequence sequence = new PaintableSequence();
        
        Rectangle2D bounds = new XRect(0, 0, W, D);
        sequence.setDefaultBounds2D(bounds);
        sequence.addPaintable(ball);
        
        tile = new Tile(sequence, Colors.lightgray);
    }
    
 
    /**
     * Places the sliders into a panel with labels
     * and perform other slider settings.
     */
    private void decorateSliders() {
        stepSlider.setMajorTickSpacing(1);
        stepSlider.installStandardLabels();
        stepSlider.addSlidingAction(stepSliding);
        
        speedSlider.setMajorTickSpacing(10);
        speedSlider.installStandardLabels();
        speedSlider.addSlidingAction(speedSliding);
        
        Object[][] sliderStuff = {
            { "Step Size",        stepSlider,  stepLabel  },
            { "Steps Per Second", speedSlider, speedLabel }
        };
        
        sliderPanel = new TablePanel
            (sliderStuff, gap, gap, CENTER);
        
        int width = TextFieldView.getSampleWidth("0000");
        sliderPanel.setMinimumColumnWidth(2, width);
    }
    
    
    /** Place the GUI elements into the main panel. */
    private void populateMainPanel() {
        // populate the main panel
        mainPanel.addObject(tile, 0, 0);
        
        mainPanel.addObject(sliderPanel, 1, 0);
        
        Object[] actions = { runAction, stopAction };
        HTable buttons = new HTable(actions, gap, gap, CENTER);
        
        mainPanel.addObject(buttons, 2, 0);
        
        // add the main panel to this panel object
        add(mainPanel);
    }
    
    
    /**
     * Returns the step size
     * and guarantees that the step size is positive.
     */
    private int getStep() {
        int s = stepSlider.getValue();
        
        return (s >= 1) ? s : 1;
    }
    
    /**
     * Returns the speed
     * and guarantees that the speed is positive.
     */
    private int getSpeed() {
        int s = speedSlider.getValue();
        
        return (s >= 1) ? s : 1;
    }
    
    
    /**
     * Returns the thread delay
     * and guarantees that the thread delay is at least minDelay.
     */
    private int getDelay() {
        int delay = maxDelay / getSpeed();
        
        return (delay > minDelay) ? delay : minDelay;
    }
    
    
    /** Perform one animation step. */
    private void animate() {
        int delta = getStep();
        if (direction > 0)
            x += delta;
        else
            x -= delta;
        
        if (x <= R) {
            direction = +1;
            
            // reflect
            x = R + R - x;
        }
        
        else if (x >= S) {
            direction = -1;
            
            // reflect
            x = S + S - x;
        }
        
        
        ball.moveCenterTo(x, R);
    }
    
    
    /**
     * The animation loop method that will
     * execute in a separate thread.
     */
    private void runAnimation() {
        // set state for run
        setRunState();
        
        // perform animation
        while(true) {
            synchronized (animationLock) {
                if (!running)
                    return;
                
                animate();
            }
            
            JPTUtilities.pauseThread(getDelay());
        }
    }
    
    
    /**
     * The method to stop the animation loop.
     */
    private void stopAnimation() {
        // set state for stop
        setStopState();
        
        // perform any desired cleanup activities
    }
    
    
    /**
     * <p>Set the enable/disable state of the actions
     * for the Run state.</p>
     * 
     * <p>Set boolean running to true.</p>
     */
    private void setRunState() {
        synchronized (animationLock) {
            runAction. setEnabled(false);
            stopAction.setEnabled(true);
            
            running = true;
        }
    }
    
    
    /**
     * <p>Set boolean running to false.</p>
     * 
     * <p>Set the enable/disable state of the actions
     * for the Stop state.</p>
     */
    private void setStopState() {
        synchronized (animationLock) {
            running = false;
            
            stopAction.setEnabled(false);
            runAction. setEnabled(true);
        }
    }
    
    
    /** The step sliding method. */
    private void stepSliding() {
        String s = "" + getStep();
        stepLabel.setText(s);
        stepLabel.repaint();
    }
    
    
    /** The speed sliding method. */
    private void speedSliding() {
        String s = "" + getSpeed();
        speedLabel.setText(s);
        speedLabel.repaint();
    }
    
    
    /**
     * <p>The window closing method.</p>
     *
     * <p>This method is required to properly terminate
     * the animation thread if the window is closed
     * while the animation is running.</p>
     */
    private void closingAction() {
        synchronized (animationLock) {
            running = false;
            
            if (frame != null)
                frame.dispose();
        }        
    }
    
    
    /**
     * The method to create the application frame and
     * install the window closing behavior.
     */
    private void createFrame() {
        frame = frame("Ball Animation");
        setWindowClosingAction();
    }
    
    
    /** The method to set the window closing action. */
    private void setWindowClosingAction() {
        // remove default closing operation
        frame.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        
        // set special closing operation
        WindowActionAdapter adapter = new WindowActionAdapter(frame);
        adapter.addWindowClosingAction(closingAction);
    }
    
    
    /**
     * The main program to launch the animation.
     * @param args ignored
     */
    public static void main(String[] args) {
        new BallAnimation();
    }
}
