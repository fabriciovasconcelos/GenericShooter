package com.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.main.Game;
import com.world.Camera;
import com.world.World;

public class BulletShoot extends Entity{
	
	private double dx, dy;
	private double spd = 3.5;
	private int curlLife = 0, life = 20;
	
	public BulletShoot(int x, int y, int width, int height, BufferedImage sprite, double dx2, double dy2) {
		super(x, y, width, height, sprite);
		this.dx = dx2;
		this.dy = dy2;
	}

	public void tick() {
		if(World.isFreeDynamic((int)(x+(dx*spd)),(int) (y+(dy*spd)), 3, 3)) {
			x+=dx * spd;
			y+=dy * spd;
		}else {
			Game.bullets.remove(this);
			World.generatedParticles(200, x, y);
			return;
		}
		curlLife++;
		if(curlLife == life) {
			Game.bullets.remove(this);
			return;
		}
	}
	public void render(Graphics g) {
		g.setColor(new Color(0,148,255));
		g.fillOval(this.getX()- Camera.x, this.getY() - Camera.y, width, height);
	}
	
}

