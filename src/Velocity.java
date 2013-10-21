
public class Velocity {
double speed;
double direction;

// constructor
public Velocity (double speed, double direction) {
	this.speed = speed;
	this.direction = direction;
}

// get speed
public double getSpeed () {
	return this.speed;
}

// get direction
public double getDirection () {
	return this.direction;
}

// string of velocity
public String toString () {
	return "" + this.speed + ", " + this.direction + "degrees";
}	
}
