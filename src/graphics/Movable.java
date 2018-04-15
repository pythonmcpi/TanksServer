package graphics;

import java.awt.Graphics;

public abstract class Movable
{
	public double posX;
	public double posY;
	public double vX;
	public double vY;
	public int cooldown = 0;
	public boolean destroy = false;
	
	public Movable(double x, double y)
	{
		this.posX = x;
		this.posY = y;
	}
	
	public void update()
	{
		this.posX += this.vX / 2 * Math.max(0, Game.tank_size - Game.player.destroyTimer) / Game.tank_size;
		this.posY += this.vY / 2 * Math.max(0, Game.tank_size - Game.player.destroyTimer) / Game.tank_size;
		this.checkCollision();
	}
	
	public void setMotionInDirection(double x, double y, double velocity)
	{
		x -= this.posX;
		y -= this.posY;
		
		double angle = 0;
		if (x > 0)
			angle = Math.atan(y/x);
		else if (x < 0)
			angle = Math.atan(y/x) + Math.PI;
		else
		{
			if (y > 0)
				angle = Math.PI / 2;
			else if (y < 0)
				angle = Math.PI * 3 / 2;
		}
		double velX = velocity * Math.cos(angle);
		double velY = velocity * Math.sin(angle);
		this.vX = velX;
		this.vY = velY;
				
	}
	
	public double getAngleInDirection(double x, double y)
	{
		x -= this.posX;
		y -= this.posY;
		
		double angle = 0;
		if (x > 0)
			angle = Math.atan(y/x);
		else if (x < 0)
			angle = Math.atan(y/x) + Math.PI;
		else
		{
			if (y > 0)
				angle = Math.PI / 2;
			else if (y < 0)
				angle = Math.PI * 3 / 2;
		}
		
		return angle;
	}
	
	public void setPolarMotion(double angle, double velocity)
	{
		double velX = velocity * Math.cos(angle);
		double velY = velocity * Math.sin(angle);
		this.vX = velX;
		this.vY = velY;			
	}
	
	public void moveInDirection(double x, double y, double amount)
	{
		this.posX += amount * x;
		this.posY += amount * y;	
	}
	
	public abstract void checkCollision();
	
	public abstract void draw(Graphics p);
	
	public static double distanceBetween(final Movable a, final Movable b)
	{
		return Math.sqrt((a.posX-b.posX)*(a.posX-b.posX) + (a.posY-b.posY)*(a.posY-b.posY));
	}
}