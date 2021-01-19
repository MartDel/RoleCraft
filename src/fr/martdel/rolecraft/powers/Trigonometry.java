package fr.martdel.rolecraft.powers;

public class Trigonometry {

	private double yaw;
	private double pitch;
	private double d;
	private double x;
	private double y;
	private double z;
	
	public void calculWithCoordinates(boolean toDegrees) {
		this.d = Math.sqrt(Math.pow(x, 2) + Math.pow(z, 2));
		this.yaw = Math.atan(z/x);
		this.pitch = Math.atan(Math.abs(y) / Math.abs(d));
		if(toDegrees) {
			this.yaw = Math.toDegrees(this.yaw);
			this.pitch = Math.toDegrees(this.pitch);
		}
	}
	public void calculWithCoordinates() {
		calculWithCoordinates(true);
	}
	
	public void calculWithoutCoordinates(boolean toDegrees) {
		this.x = d * Math.cos(yaw);
		this.y = d * Math.sin(pitch);
		this.z = d * Math.sin(yaw);
		if(toDegrees) {
			this.x = Math.toDegrees(x);
			this.y = Math.toDegrees(y);
			this.z = Math.toDegrees(z);
		}
	}
	public void calculWithoutCoordinates() {
		calculWithoutCoordinates(true);
	}
	
	public double getYaw() {
		return yaw;
	}
	public void setYaw(double yaw) {
		this.yaw = yaw;
	}
	public double getPitch() {
		return pitch;
	}
	public void setPitch(double pitch) {
		this.pitch = pitch;
	}
	public double getDistance() {
		return d;
	}
	public void setDistance(double d) {
		this.d = d;
	}

	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	public double getZ() {
		return z;
	}
	public void setZ(double z) {
		this.z = z;
	}
	
}
