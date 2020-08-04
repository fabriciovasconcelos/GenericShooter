package com.entities;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.List;

import com.main.Game;
import com.world.Camera;
import com.world.Node;
import com.world.Vector2i;

public class Entity {
	public int x;
	public int y;
	protected int z;
	protected int width;
	protected int height;
	protected List<Node> path;
	protected BufferedImage sprite;
	public static BufferedImage HEARTLIFE_EN = Game.spritesheet.getSpriteint(0, 16, 16, 16);
	public static BufferedImage GUN_EN = Game.spritesheet.getSpriteint(16, 16, 16, 16);
	public static BufferedImage BULLETS_EN = Game.spritesheet.getSpriteint(0, 32, 16, 16);
	public static BufferedImage ENEMY_EN = Game.spritesheet.getSpriteint(16*7, 0, 16, 16);
	public static BufferedImage GUN_LEFT = Game.spritesheet.getSpriteint(16, 48, 16, 16);
	public static BufferedImage GUN_RIGHT = Game.spritesheet.getSpriteint(16, 32, 16, 16);
	private int maskX,maskY,maskWidth,maskHeight;
	public int depth;

	public Entity(int x, int y, int width, int height, BufferedImage sprite) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.sprite = sprite;
		this.maskX = 0;
		this.maskY = 0;
		this.maskWidth = width;
		this.maskHeight = height;
	}
	public void setMask(int maskX,int maskY,int maskWidth,int maskHeight) {
		this.maskX = maskX;
		this.maskY = maskY;
		this.maskWidth = maskWidth;
		this.maskHeight = maskHeight;

	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}
	public void tick() {}

	public void render(Graphics g) {
		g.drawImage(sprite, this.getX() - Camera.x, this.getY() - Camera.y, null);
		//g.setColor(Color.red);
		//g.fillRect( this.getX() - Camera.x + maskX, this.getY() - Camera.y + maskY, maskWidth, maskHeight);
	}
	public static boolean isCollidding(Entity e1, Entity e2) {
		Rectangle e1Mask = new Rectangle(e1.getX()+ e1.maskX, e1.getY()+ e1.maskY, e1.maskWidth, e1.maskHeight);
		Rectangle e2Mask = new Rectangle(e2.getX()+ e2.maskX, e2.getY()+ e2.maskY, e2.maskWidth, e2.maskHeight);
		if(e1Mask.intersects(e2Mask) && e1.z == e2.z) {
			return true;
		}
		return false;
	}
public static Comparator<Entity> nodeSorterEntity = new Comparator<Entity>() {
		
		@Override
		public int compare(Entity n0,Entity n1) {
			if(n1.depth < n0.depth)
				return +1;
			if(n1.depth > n0.depth)
				return -1;
			return 0;
		}
		
	};

	public double calculatedDistance(int x1, int x2, int y1, int y2){
		return Math.sqrt((x1-x2) * (x1-x2) + (y1-y2) * (y1-y2));
	}

	public void followPath(List<Node> path) {
		if(path != null) {
			if(path.size() > 0) {
				Vector2i target = path.get(path.size() - 1).tile;
				//xprev = x;
				//yprev = y;
				if(x< target.x * 16) {
					x++;
				}else if(x > target.x * 16) {
					x--;
				}

				if(y < target.y * 16) {
					y++;
				}else if(y>target.y * 16) {
					y--;	
				}
				
				if(x == target.x *16 && y == target.y * 16) {
					path.remove(path.size() - 1);
				}

			}
		}
	}
}
