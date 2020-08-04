package com.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.graficos.Spritesheet;
import com.main.Game;
import com.world.Camera;
import com.world.World;

public class Player extends Entity {

	public boolean up;
	public boolean down;
	public boolean left;
	public boolean right;
	public int speed = 2;
	public int right_dir =0, left_dir=1, up_dir = 2, down_dir = 3;
	private int dir = down_dir;
	private BufferedImage[] rightPlayer;
	private BufferedImage[] leftPlayer;
	private BufferedImage[] upPlayer;
	private BufferedImage[] downPlayer;
	private BufferedImage playerDamage;
	private int frames = 0, maxFrames = 4, index = 0, maxIndex = 3;
	public static boolean moved;
	public static boolean isDamaged = false;
	public static boolean hasGun = false;
	public boolean isShooted = false;
	public boolean mouseIsShooted = false;
	public static double life = 100;
	public static double maxLife = 100;
	public static int bullets = 0;
	public int damageFrames =0;
	public int mouseX, mouseY;
	public static boolean iCanMove = true;
	public boolean jump = false;
	public boolean isJumping = false;
	public static int z = 0;
	public int jumpFrames = 25, jumpCur = 0;
	public boolean jumpUp = false, jumpDown = false;


	public Player(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);

		rightPlayer = new BufferedImage[4];
		leftPlayer = new BufferedImage[4];
		upPlayer = new BufferedImage[4];
		downPlayer = new BufferedImage[4];
		playerDamage = Game.spritesheet.getSpriteint(0, 48, 16, 16);
		for(int i = 0; i<4; i++) {
			rightPlayer[i] = Game.spritesheet.getSpriteint(64 + (i*16), 0, 16, 16);
		}
		for(int i = 0; i<4; i++) {
			leftPlayer[i] = Game.spritesheet.getSpriteint(64+ (i*16), 16, 16, 16);
		}
		for(int i = 0; i<4; i++) {
			downPlayer[i] = Game.spritesheet.getSpriteint(32 , 0 + (i*16), 16, 16);
		}
		for(int i = 0; i<4; i++) {
			upPlayer[i] = Game.spritesheet.getSpriteint(48 , 0 + (i*16), 16, 16);
		}
	}

	public void tick() {
		depth = 1;
		playerMovement();
		playerJumping();
		checkCollisionLifePack();
		checkCollisionBullets();
		checkCollisionGun();
		checkIsShooted();
		checkMouseShooted();
		checkDamage();
		updateCamera();

	}

	public void updateCamera() {
		Camera.x =  Camera.clamp(this.getX() -(Game.WIDTH/2), 0, World.WIDTH*16 - Game.WIDTH);
		Camera.y =  Camera.clamp(this.getY() -(Game.HEIGHT/2), 0, World.HEIGHT*16 - Game.HEIGHT);
	}

	public void checkCollisionBullets() {
		for(int i = 0; i<Game.entities.size(); i++) {
			Entity atual = Game.entities.get(i);
			if(atual instanceof Bullets) {
				if(Entity.isCollidding(this, atual) && z==0) {
					bullets+=10;
					Game.entities.remove(atual);
				}
			}
		}
	}

	public void checkCollisionLifePack() {
		for(int i = 0; i<Game.entities.size(); i++) {
			Entity atual = Game.entities.get(i);
			if(atual instanceof Heart) {
				if(Entity.isCollidding(this, atual) && z==0) {
					life+=25;
					if (life>100){
						life = 100;
					}
					Game.entities.remove(atual);
				}
			}
		}
	}
	public void checkCollisionGun() {
		for(int i = 0; i<Game.entities.size(); i++) {
			Entity atual = Game.entities.get(i);
			if(atual instanceof Gun) {
				if(Entity.isCollidding(this, atual) && z==0) {
					hasGun = true;
					Game.entities.remove(atual);
				}
			}
		}
	}
	public void checkDamage() {
		if(isDamaged == true) {
			damageFrames++;
			if(damageFrames == 8) {
				damageFrames = 0;
				isDamaged = false;
			}
		}
	}
	public void checkIsShooted() {
		if(isShooted) {
			isShooted = false;
			if(hasGun && bullets>0) {
				Player.bullets--;
				int dx = 0;
				int px = 0;
				int py = 8;
				if(dir == right_dir) {
					dx = 1;
					px = 12;
				}else {
					px = -2;
					dx = -1;
				}

				BulletShoot bullet = new BulletShoot(getX() + px, getY() + py, 7, 7, null, dx ,0);
				Game.bullets.add(bullet);
			}

		}
	}

	public void checkMouseShooted() {
		if(mouseIsShooted) {
			mouseIsShooted = false;
			if(hasGun && bullets>0) {
				bullets-=2;
				int px = 0,py = 8;
				double angle = 0;
				if(dir == right_dir) {
					px = 18;
					angle = Math.atan2(mouseY - (this.getY()+py - Camera.y),mouseX - (this.getX()+px - Camera.x));
				}else {
					px = -3;
					angle = Math.atan2(mouseY - (this.getY()+py - Camera.y),mouseX - (this.getX()+px - Camera.x));
				}

				double dx = Math.cos(angle);
				double dy = Math.sin(angle);

				BulletShoot bullet = new BulletShoot(this.getX() + px, this.getY() + py, 7, 7, null, dx ,dy);
				Game.bullets.add(bullet);
			}
		}
	}


	public void playerJumping() {
		if(jump) {
			if(isJumping == false) {
				jump = false;
				isJumping = true;
				jumpUp = true;
			}
		}

		if(isJumping == true) {
			z = jumpCur;
			if(jumpCur >= jumpFrames) {
				jumpUp = false;
				jumpDown = true;
			}
				if(jumpUp) {
					jumpCur ++;
				}
				else if(jumpDown) {
					jumpCur --;
					if(jumpCur <=0) {
						z= 0;
						isJumping = false;
						jumpDown = false;
						jumpUp = false;
					}
				}
		}
	}


	public  void playerMovement() {
		moved = false;
		if(right && World.isFree(this.getX() + speed, this.getY(), z) && iCanMove) {
			moved = true;
			dir = right_dir;
			x+=speed;
		}else if(left && World.isFree(this.getX() - speed, this.getY(), z) && iCanMove) {
			moved = true;
			dir = left_dir;
			x-=speed;
		}
		if(up && World.isFree(this.getX(), this.getY()  - speed, z)  && iCanMove) {
			moved = true;
			dir = up_dir;
			y-=speed;
		}else if(down && World.isFree(this.getX(), this.getY()  + speed, z ) && iCanMove) {
			moved = true;
			dir = down_dir;
			y+=speed;
		}

		if(moved) {
			frames++;
			if(frames == maxFrames) {
				frames = 0;
				index++;
				if(index == maxIndex) {
					index = 0;
				}
			}
		}
	}

	public void render(Graphics g) {
		if(!isDamaged) {
			if(dir == up_dir) {

				g.drawImage(upPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y -z, null );

			}else if(dir == down_dir) {

				g.drawImage(downPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y -z, null );

			}

			if(dir == right_dir) {

				g.drawImage(rightPlayer[index], this.getX() - Camera.x , this.getY() - Camera.y -z, null );
				/*if(hasGun) {
					g.drawImage(GUN_RIGHT, this.getX()-Camera.x + 2, this.getY()-Camera.y -2 -z, null);
				}
				*/
			}else if(dir == left_dir) {

				g.drawImage(leftPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y -z, null );
				/*if(hasGun) {
					g.drawImage(GUN_LEFT, this.getX()-Camera.x - 4, this.getY()-Camera.y -2 -z, null);
				}
				*/
			}
		}else {
			g.drawImage(playerDamage, getX() - Camera.x, getY()- Camera.y - z,null);
		}

		if(isJumping) {
			g.setColor(new Color(0,0,0,100));
			g.fillOval( this.getX() - Camera.x +4 , this.getY() - Camera.y + 5, 9, 9);
		}
	}

}
