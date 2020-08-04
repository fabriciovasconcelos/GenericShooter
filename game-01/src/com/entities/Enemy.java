package com.entities;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Random;

import com.main.Game;
import com.world.AStar;
import com.world.Camera;
import com.world.Vector2i;
import com.world.World;

public class Enemy extends Entity{

	private double speed = 3.5;
	private int life = 10;
	private int damageFrames = 0;
	private boolean isDamaged =false;
	private BufferedImage enemyDamage;
	private BufferedImage[] sprites;
	public static int damage = 5;
	private int frames = 0,maxFrames = 20,index = 0,maxIndex = 2;
	int z = 0;

	public Enemy(int x, int y, int width, int height, BufferedImage[] sprite) {
		super(x, y, width, height, null);
		enemyDamage = Game.spritesheet.getSpriteint(0, 64, 16, 16);
		sprites = new BufferedImage[3];
		this.sprites[0] = sprite[0];
		this.sprites[1] = sprite[1];
		this.sprites[2] = sprite[2];
	
	}


	public void tick() {
		depth = 0;
		if(this.isColliddingWithPlayer() == false) {
			enemyMovementAStar();
		}else{
			takePlayerLife();
		}
		enemyMovementAStartUpdate();
		frames++;
		if(frames == maxFrames) {
			frames = 0;
			index++;
			if(index > maxIndex)
				index = 0;
		}
		collidingBullet();
		destroySelf();
		checkDamage();
	}


	public boolean isColliddingWithPlayer() {
		Rectangle enemyCurrent = new Rectangle(this.getX(),this.getY(), World.TILE_SIZE, World.TILE_SIZE);
		Rectangle Player = new Rectangle(Game.player.getX(), Game.player.getY(),16, 16);
		return enemyCurrent.intersects(Player);
	}

	public boolean isCollidding(double xnext, double ynext) {
		Rectangle enemyCurrent = new Rectangle((int)xnext ,(int)ynext, World.TILE_SIZE, World.TILE_SIZE);
		for( int i = 0; i< Game.enemies.size(); i++) {
			Enemy e = Game.enemies.get(i);
			if(e == this) {	
				continue;
			}
			Rectangle targetEnemy = new Rectangle(e.getX(),e.getY(), World.TILE_SIZE, World.TILE_SIZE);
			if(enemyCurrent.intersects(targetEnemy)) {
				return true;
			}

		}

		return false;
	}
	public void destroySelf() {
		if(life == 0) {
			Game.enemies.remove(this);
			Game.entities.remove(this);
			return;
		}
	}
	
	public void collidingBullet() {
		for(int i=0; i<Game.bullets.size(); i++) {
			Entity e = Game.bullets.get(i);
			if(e instanceof BulletShoot) {
				if(Entity.isCollidding(this, e)) {
					life-=5;
					isDamaged = true;
					Game.bullets.remove(i);
					return;
				}
			}
			
		}
	}

	public void enemyMovementAStar() {
		if(path == null || path.size() == 0) {
			Vector2i start = new Vector2i(x/16, y/16);
			Vector2i end = new Vector2i(Game.player.x/16, Game.player.y/16);
			path = AStar.findPath(Game.world, start, end);
		}
		followPath(path);
	}
	
	public void enemyMovementAStartUpdate() {
		if(new Random().nextInt(100) < 20)
			followPath(path);
		
		if(new Random().nextInt(100) < 5) {
			Vector2i start = new Vector2i(x/16, y/16);
			Vector2i end = new Vector2i(Game.player.x/16, Game.player.y/16);
			path = AStar.findPath(Game.world, start, end);
		}
	}

	public void enemyMovement() {
		//if com alguns problemas devido as posições x e y serem int, deve se resolver os transformando em double;
		//if(this.calculatedDistance(this.getX(), this.getY(), Game.player.getX(), Game.player.getY()) < 300) {
		if(x < Game.player.getX() && World.isFree(x+ (int)speed, getY(), z)
				&& !isCollidding(x+ (int)speed, getY())) {
			x+= speed;
		}else if(x> Game.player.getX() && World.isFree(x- (int)speed, getY(), z)
				&& !isCollidding(x-(int)speed, getY())) {

			x-= speed;
		}

		if(y < Game.player.getY() && World.isFree(getX(), y+(int) speed, z)
				&& !isCollidding(getX(), y+ (int)speed)) {
			y+= speed;
		}else if(y> Game.player.getY() && World.isFree(getX(), y- (int)speed, z)
				&& !isCollidding(getX(), y- (int)speed)) {
			y-= speed;
		}
		//}
	}

	public void takePlayerLife () {
		if(Game.random.nextInt(100)<10) {
			Game.player.isDamaged = true;
			Game.player.life-= damage;
		}
	}

	public void checkDamage(){
		if(isDamaged == true) {
			damageFrames++;
			if(damageFrames == 8) {
				damageFrames = 0;
				isDamaged = false;
			}
		}
	}
	
	public void render(Graphics g) {
		if(!isDamaged) {
			g.drawImage(sprites[index], this.getX() - Camera.x,this.getY() - Camera.y,null);
		}else {
			g.drawImage(enemyDamage, getX() - Camera.x, getY()- Camera.y,null);
		}
	}


}
