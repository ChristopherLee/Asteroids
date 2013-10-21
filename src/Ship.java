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

public class Ship extends PaintableSequenceComposite{

PaintableSequence sequence = getPaintableSequence();
int energy;
Posn position;
int direction;
Velocity velocity;
ArrayList <Weapon> weapons;

//ship direction 15 degree intervals
//velocity direction any angle

//variables

final static ArrayList <Weapon> initialWeapons = new ArrayList <Weapon> 
();
final static Velocity initialVelocity = new Velocity (0, 0);
final static int initialDirection = 0;
final static int bulletDamage = 1000;
final static int initialEnergy = 1000;
final static int energyRecharge = 5;
final static int bulletEnergy = 700;
final static Posn startingPosition = new Posn 
(Asteroids.backgroundHeight/2, Asteroids.backgroundWidth/2);
final static int turnGap = 10;
double topSpeed = 10;
int acceleration = 1;
int decceleration = acceleration * -1;
//imagine ship enclosed by rectangle
final static int shipHeight = 35;
final static int shipLength = 25;

Color outline = Colors.silver;
//Color edge = Colors.black;
Color fill = Colors.white;
Font font = new Font ("Times New Roman", Font.BOLD, 11);
ShapePaintable body;


// constructor
private Ship (int energy, Posn position, int direction, Velocity 
velocity, ArrayList <Weapon> weapons) {
 this.energy = energy;
 this.position = position;
 this.direction = direction;
 this.velocity = velocity;
 this.weapons = weapons;
    update ();
}

public Ship () {
    this (initialEnergy, startingPosition, initialDirection, 
initialVelocity, initialWeapons);
    update ();
}

// METHODS

// create paintable body

public void createPaintable () {
    int startX1 = 0;
    int startX2 = 50;
    int startX3 = 0;
    int startY1 = 0;
    int startY2 = 25;
    int startY3 = 50;
    float[][] startVertex = { { startX1, startY1 }, { startX2, startY2 
}, { startX3, startY3 } };

    PolygonShape startTriangle = new PolygonShape (startVertex);
    
    body = new ShapePaintable (startTriangle, PaintMode.FILL, fill);
    moveCenterTo (50, 25);

}
// accelerate forwards
public void thrust () {
    
    double initSpeed = this.velocity.getSpeed();
    double initAngle = this.velocity.getDirection();
    
    double initSpeedX = initSpeed * MathUtilities.cosdeg(initAngle);
    double initSpeedY = initSpeed * MathUtilities.sindeg(initAngle);
    
    double deltaAngle = this.direction;
    
    double deltaSpeedX = acceleration * (MathUtilities.cosdeg(deltaAngle));
    double deltaSpeedY = acceleration * (MathUtilities.sindeg(deltaAngle));
        
    double newSpeedX = (initSpeedX + deltaSpeedX);
    double newSpeedY = (initSpeedY + deltaSpeedY);
    
    System.out.println ("" + newSpeedX);
    System.out.println ("" + newSpeedY);
    
    /*
    double newDirection = Math.atan(newSpeedY/newSpeedX);
    double newSpeed = newSpeedY/Math.sin (newDirection);     
    this.velocity = new Velocity (newSpeed, newDirection);
    */
    
    double square = newSpeedX*newSpeedX + newSpeedY*newSpeedY;
    double newSpeed = Math.sqrt(square);
    if (newSpeed > topSpeed)
    	  newSpeed = topSpeed;
    
    double newAngle = MathUtilities.atan2deg(newSpeedY, newSpeedX);
    
    this.velocity = new Velocity(newSpeed, newAngle);
    
    System.out.println (this.velocity.toString());
    update();
}

// accelerate backwards
public void dethrust () {
	double initSpeed = this.velocity.getSpeed();
    double initAngle = this.velocity.getDirection();
    
    double initSpeedX = initSpeed * MathUtilities.cosdeg(initAngle);
    double initSpeedY = initSpeed * MathUtilities.sindeg(initAngle);
    
    double deltaAngle = this.direction;
    
    double deltaSpeedX = decceleration * (MathUtilities.cosdeg(deltaAngle));
    double deltaSpeedY = decceleration * (MathUtilities.sindeg(deltaAngle));
        
    double newSpeedX = (initSpeedX + deltaSpeedX);
    double newSpeedY = (initSpeedY + deltaSpeedY);
    
    System.out.println ("" + newSpeedX);
    System.out.println ("" + newSpeedY);
    
    /*
    double newDirection = Math.atan(newSpeedY/newSpeedX);
    double newSpeed = newSpeedY/Math.sin (newDirection);     
    this.velocity = new Velocity (newSpeed, newDirection);
    */
    
    double square = newSpeedX*newSpeedX + newSpeedY*newSpeedY;
    double newSpeed = Math.sqrt(square);
    if (newSpeed > topSpeed)
    	  newSpeed = topSpeed;
    
    double newAngle = MathUtilities.atan2deg(newSpeedY, newSpeedX);
    
    this.velocity = new Velocity(newSpeed, newAngle);
    
    System.out.println (this.velocity.toString());
    update();
}

// turn direction of ship
private void turn (int gap) {
 int newDirection = this.direction + turnGap;
 if (newDirection == 360)
  this.direction = 0;
 else
  this.direction = newDirection;
    rotate (gap);
}

// turn direction of ship left by turngap

public void turnLeft () {
    this.direction += turnGap;

    if (this.direction > 360)
        this.direction -= 360;
    
    update();

}

public void turnRight () {
    this.direction -= turnGap;
    
    if (this.direction < 0)
        this.direction += 360;
    update();    
}

// string of ship
public String toString () {
 return "Ship: " + "\n    Energy: " + this.energy + "\n    Position: " 
+ this.position.toString () + "\n    Direction: " + this.direction + 
"\n    Velocity: " +  this.velocity.toString () + "\n    Weapons: " + 
this.weapons;
 
}

// tick the next instance of a ship
public void tick() {
 tickEnergy (this.energy);
 this.position.tick(this.velocity);
    
 //ArrayList <Weapon> newWeapons = new ArrayList <Weapon> ();
 Iterator <Weapon> it = this.weapons.iterator();
    while (it.hasNext())
  ((Weapon)it.next()).tick();
        //Weapon newWeapon = this.weapons.get(1);
  //Weapon newWeapon2 = newWeapon.tick();
  //newWeapons.add(newWeapon);
  //this.weapons.remove (1);
    
    System.out.println (this.toString());
}

// tick the next instance of ship energy
public void tickEnergy (int energy) {
 int newEnergy = this.energy + energyRecharge;
 if (newEnergy > initialEnergy)
  newEnergy = initialEnergy;
 
 this.energy = newEnergy;
}

// shoot a bullet from the ship
public void shoot() {
 if (this.energy > bulletEnergy) {
  this.energy = this.energy - bulletEnergy;
  this.weapons.add (new Weapon (this));
        update();
  }
 else
  return;
}

// draw the ship
public void update () {
    sequence.clearSequence();

    int x1 = position.getX();
    int x2 = position.getX() - shipHeight;
    int x3 = position.getX() - shipHeight;
    int y1 = position.getY();
    int y2 = position.getY() + (shipLength/2);
    int y3 = position.getY() - (shipLength/2);
    if (x1 < 0 || x2 < 0 || x3 < 0 || y1 < 0 || y2 < 0 || y3 < 0 ||
        x1 > Asteroids.animationWidth || y1 > Asteroids.animationHeight 
||
        x2 > Asteroids.animationWidth || y2 > Asteroids.animationHeight 
||
        x3 > Asteroids.animationWidth || y3 > 
Asteroids.animationHeight) {
        System.out.println ("You printed a ship out of bounds, stupid");
        return;
    }
    float[][] triangleVertex = { { x1, y1 }, { x2, y2 }, { x3, y3 } };
    PolygonShape triangle = new PolygonShape (triangleVertex);
    body = new ShapePaintable (triangle, PaintMode.FILL, fill);
    moveCenterTo ((position.getX()-(shipHeight/2)), position.getY());
    body.rotate (-1*this.direction);
    sequence.addPaintable (body);
    
    // add energy text
    TextPaintable eng = new TextPaintable (this.energy + "", font, 
Color.red, ((float)this.position.x-50), ((float)this.position.y+20));
    sequence.addPaintable (eng);
}

/**
  * @param args
  */
    public static void main(String[] args) {
        new Asteroids ();
    }

}
