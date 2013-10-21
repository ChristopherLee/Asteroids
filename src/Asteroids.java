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


public class Asteroids extends TablePanel implements WindowConstants{

/** The frame for the animation. */
private JPTFrame frame = null;

/** The main panel. */
private TablePanel mainPanel = null;

/** The gui panel. */
private TablePanel guiPanel = null;

/** Animation panel */
private BufferedPanel animationPanel = null;

/** The common panel gap. */
private int gap = 10;

/** Whether or not the animation is running. */
private volatile boolean running = false;

/** The animation end. */
private int S;

/** Ship 1 */
private Ship ship1;

/** Ship 2 */
private Ship ship2;

/** Frame height */
public static final int backgroundHeight = 500;

/** Frame width */
public static final int backgroundWidth = 500;

/** Animation Panel Height */
public static final int animationHeight = 900;

/** Animation Panel Width */
public static final int animationWidth = 900;

/** The slider for the animation speed in steps per second. */
private SliderView speedSlider =
    new SliderView(HORIZONTAL, 0, 100, 1);

/** Shortand for KeyEvent.VK_UP. */
private static final int VK_UP    = KeyEvent.VK_UP;

/** Shortand for KeyEvent.VK_DOWN. */
private static final int VK_DOWN  = KeyEvent.VK_DOWN;

/** Shortand for KeyEvent.VK_LEFT. */
private static final int VK_LEFT  = KeyEvent.VK_LEFT;

/** Shortand for KeyEvent.VK_RIGHT. */
private static final int VK_RIGHT = KeyEvent.VK_RIGHT;

/** Shortand for KeyEvent.VK_CTRL. */
private static final int VK_CTRL = KeyEvent.VK_CONTROL;

/**
 * <p>The object for animation synchronization.</p>
 * 
 * <p>When a thread synchronizes on this object,
 * the thread can complete its tasks before
 * another thread can change state.</p> 
 */
private Object animationLock = new Object();

/**
 * The key press-release listener.
 * 
 * The key press-release listener is initialized with
 * the keys we plan to track in this demo.
 * 
 * Use the default constructor with no arguments to
 * be able to track all keys.
 */
private KeyPressReleaseListener keyPR_Listener =
    new KeyPressReleaseListener
        ();


/**
 * The thread delay of the key press-release listener
 * action in milliseconds.
 */
private static final int keyPR_Delay = 50;


/** The key press-release action. */
private SimpleAction keyPR_Action =
    new SimpleAction() {
        public void perform() { keyPR_Action(); }
};


/** The threaded key press-release action. */
private ThreadedAction threaded_KeyPR_Action =
    new ThreadedAction(keyPR_Action);


/**
 * The thread delay of the move action
 * in milliseconds.
 */
private static final int move_Delay = 10;

/** Run the animation in a separate thread. */
private ThreadedAction runAnimation =
    new ThreadedAction
        (new SimpleAction("runAnimation") {
            public void perform()
            { runAnimation(); }
});

/** The move action. */
private SimpleAction move_Action =
    new SimpleAction() {
        public void perform() { move_Action(); }
};

/** The threaded move action. */
private ThreadedAction threaded_Move_Action =
    new ThreadedAction(move_Action);

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

private int delay = 50;


/** The sliding action for the speed slider. */
private SimpleAction speedSliding =
    new SimpleAction("Speed Sliding") {
        public void perform() { speedSliding(); }
};        

/** The label that echoes the animation speed. */
private Annotation speedLabel =
    new Annotation("1");

/** The panel with the sliders and the labels. */
private TablePanel sliderPanel = null;

// constructor
public Asteroids () {
    
    createMainPanel ();
    initializeShips ();
    createFrame();
    decorateSliders ();
    populateGuiPanel ();
    populateAnimationPanel ();
    startListenersAndThreads();
    
    }



/** Create the main panel. */ 
private void createMainPanel() {
    int rows = 1;
    int cols = 2;
    
    mainPanel = new TablePanel(rows, cols, gap, gap, CENTER);
    
    mainPanel.emptyBorder(gap);
    
    guiPanel = new TablePanel (3, 1, gap, gap, CENTER);
    mainPanel.add (guiPanel, 0, 0);
    
    animationPanel = new BufferedPanel (animationHeight, animationWidth, Color.black);
    mainPanel.add(animationPanel, 0, 1);
    add(mainPanel);    
}

/** initialize ships */
public void initializeShips() {
    ship1 = new Ship ();
    //ship2 = new Ship ();    
}

/** Place the animation elements into the animation panel. */
private void populateAnimationPanel() {
    animationPanel.setBackground (Color.black);
    animationPanel.addPaintable(ship1);
    animationPanel.addPaintable(ship2);
    animationPanel.repaint();
    System.out.println ("populated animation panel");

}

/** Place the GUI elements into the gui panel. */
private void populateGuiPanel() {
    
    guiPanel.addObject(speedSliding, 0, 0);
    
    Object[] actions = { runAction, pause };
    HTable buttons = new HTable(actions, gap, gap, CENTER);
    
    guiPanel.addObject(buttons, 2, 0);
    guiPanel.repaint();
    
}

/**
 * The method to create the application frame and
 * install the window closing behavior.
 */
private void createFrame() {
    frame = frame("Asteroids");
    frame.maximize();
    frame.setVisible (true);
    setWindowClosingAction();
}

/**
 * Places the sliders into a panel with labels
 * and perform other slider settings.
 */
private void decorateSliders() {
        
    speedSlider.setMajorTickSpacing(10);
    speedSlider.installStandardLabels();
    speedSlider.addSlidingAction(speedSliding);
    
    Object[][] sliderStuff = {
        { "Steps Per Second", speedSlider, speedLabel }
    };
    
    sliderPanel = new TablePanel
        (sliderStuff, gap, gap, CENTER);
    
    int width = TextFieldView.getSampleWidth("0000");
    sliderPanel.setMinimumColumnWidth(2, width);
}

/** Enable the key listener and starts the threads. */
private void startListenersAndThreads() {
    // enable the keyPR_Listener
    mainPanel.setFocusable(true);
    mainPanel.requestFocusInWindow();
    mainPanel.addKeyListener(keyPR_Listener);
    
    // start the threaded actions
    threaded_KeyPR_Action.actionPerformed(null);
    threaded_Move_Action.actionPerformed(null);
}


/**
 * The key press-release action to perform periodically.
 * 
 * The action will in fact be performed in a separate thread.
 * 
 * It is performed only as long as the application frame is
 * showing.
 */
private void keyPR_Action() {
    while (frame.isShowing()) {        
        JPTUtilities.pauseThread(keyPR_Delay);
    }
}

/**
 * The key move action to perform periodically.
 * 
 * The action will in fact be performed in a separate thread.
 * 
 * It is performed only as long as the application frame is
 * showing.
 * 
 * This action uses a longer pause if it has moved an object
 * than if no arrow key is currently pressed and nothing has
 * been moved.
 */
private void move_Action() {
    while(frame.isShowing()) {                   
        handleKeyEvent();
        JPTUtilities.pauseThread(keyPR_Delay);
        ship1.tick();
        JPTUtilities.pauseThread(move_Delay);
    }
}   


/** The code to move the paintable using the state
 * of up arrow, down arrow, left arrow, right arrow,
 * and shift.
 */
private void handleKeyEvent() {
    
    if (keyPR_Listener.getState(VK_UP)) {
        ship1.thrust();
        System.out.println ("Thrust");
    }
    
    if (keyPR_Listener.getState(VK_DOWN)) {
        ship1.dethrust();
        System.out.println ("Dethrust");
    }
    
    if (keyPR_Listener.getState(VK_LEFT)) {
        ship1.turnLeft();
        System.out.println ("TurnLeft");
    }
    
    if (keyPR_Listener.getState(VK_RIGHT)) {
        ship1.turnRight();
        System.out.println ("TurnRight");
    }
    
    if (keyPR_Listener.getState(VK_CTRL)) {
        ship1.shoot();
        System.out.println ("Shoot");
    }
   
    mainPanel.repaint();
}

// pause the game
public void pause () {
    setStopState();
}

/**
 * The animation loop method that will
 * execute in a separate thread.
 */

private void animate () {
    startListenersAndThreads();
}

/** The action to run the animation in a separate thread. */
private ThreadedAction runAction =
    new ThreadedAction
        (new SimpleAction("Run") {
            public void perform()
            { runAnimation(); }
});


/** The action to stop the animation. */
private SimpleAction pause =
    new SimpleAction("Pause") {
        public void perform() { pause(); }
};

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
        
        JPTUtilities.pauseThread(delay);
    }
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
        pause.setEnabled(true);
        
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
        
        pause.setEnabled(false);
        runAction. setEnabled(true);
    }
}

/**
 * Returns the speed
 * and guarantees that the speed is positive.
 */
private int getSpeed() {
    int s = speedSlider.getValue();
    
    return (s >= 1) ? s : 1;
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

/** The method to set the window closing action. */
private void setWindowClosingAction() {
    // remove default closing operation
    frame.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    
    // set special closing operation
    WindowActionAdapter adapter = new WindowActionAdapter(frame);
    adapter.addWindowClosingAction(closingAction);
}
    
	public static void main(String[] args) {
        new Asteroids ();
	}

}
