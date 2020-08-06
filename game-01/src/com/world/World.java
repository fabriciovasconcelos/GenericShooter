package com.world;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.entities.Bullets;
import com.entities.Enemy;
import com.entities.Entity;
import com.entities.Gun;
import com.entities.Heart;
import com.entities.Npc;
import com.entities.Particle;
import com.entities.Player;
import com.graficos.Spritesheet;
import com.main.Game;
import com.main.Menu;

public class World {

	public static Tile[] tiles;
	public static int WIDTH,HEIGHT;
	public static final int TILE_SIZE = 16;

	public World(String path) {
		try {
			BufferedImage map = ImageIO.read(getClass().getResource(path));
			int [] pixels = new int [map.getWidth()* map.getHeight()];
			tiles = new Tile[map.getWidth()* map.getHeight()];
			WIDTH = map.getWidth();
			HEIGHT = map.getHeight();
			map.getRGB(0, 0, map.getWidth(), map.getHeight(), pixels, 0, map.getWidth());
			for(int xx = 0; xx< map.getWidth(); xx++ ) {
				for(int yy = 0; yy< map.getWidth(); yy++ ) {
					int pixelAtual = pixels[ xx+ (yy * map.getWidth())];
					int pixelPreto = 0xFF000000;
					int pixelBranco = 0xFFFFFFFF;
					int pixelAzul = 0xFF0000FF;
					int pixelVermelho = 0xFFFF0000;
					int pixelLaranja = 0xFFFF6A00;
					int pixelAmarelo = 0xFFFFD800;
					int pixelVerde = 0xFF00FF21;
					int pixelAreia = 0xFFFF5D00;
					int pixelCacto = 0xFF007F0E;
					int pixelRatNpc = 0xFFFF00DC;
					if(Game.newWorld.equals("") ||Game.newWorld.equals("level1.png") )
						tiles[xx+ (yy * map.getWidth())] = new FloorTile(xx*16, yy*16, Tile.TILE_FLOOR);
					 if(Game.newWorld.equals("level2.png")) 
						tiles[xx+ (yy * map.getWidth())] = new FloorTile(xx*16, yy*16, Tile.TILE_SAND);
					 
					 
						if(pixelAtual == pixelPreto) {
							tiles[xx+ (yy * map.getWidth())] = new FloorTile(xx*16, yy*16, Tile.TILE_FLOOR);
						}else if(pixelAtual == pixelBranco) {
							tiles[xx+ (yy * map.getWidth())] = new WallTile(xx*16, yy*16, Tile.TILE_WALL);
						}else if(pixelAtual == pixelAzul) {
							Game.player.setX(xx*16);
							Game.player.setY(yy*16);
						}else if(pixelAtual == pixelVermelho) {
							BufferedImage[] buf = new BufferedImage[3];
							if(Game.newWorld.equals("") ||Game.newWorld.equals("level1.png")) {
							buf[0] = Game.spritesheet.getSpriteint(112,0,16,16);
							buf[1] = Game.spritesheet.getSpriteint(112+16,0,16,16);
							buf[2] = Game.spritesheet.getSpriteint(112 + 32, 0, 16, 16);
							}else if(Game.newWorld.equals("level2.png")) {
								buf[0] = Game.spritesheet.getSpriteint(112,16,16,16);
								buf[1] = Game.spritesheet.getSpriteint(112+16,16,16,16);
								buf[2] = Game.spritesheet.getSpriteint(112 + 32, 16, 16, 16);
							}
							Enemy en = new Enemy(xx*16,yy*16,16,16,buf);
							Game.entities.add(en);
							Game.enemies.add(en);
						}else if(pixelAtual == pixelVerde) {
							Game.entities.add(new Heart(xx*16, yy*16, 16,16, Enemy.HEARTLIFE_EN));
						}else if(pixelAtual == pixelLaranja) {
							Game.entities.add(new Gun(xx*16, yy*16, 16,16, Enemy.GUN_EN));
						}else if (pixelAtual == pixelAmarelo) {
							Game.entities.add(new Bullets(xx*16, yy*16, 16,16, Enemy.BULLETS_EN));
						}else if (pixelAtual == pixelAreia) {
							tiles[xx+ (yy * map.getWidth())] = new FloorTile(xx*16, yy*16, Tile.TILE_SAND);
						}else if (pixelAtual == pixelCacto) {
							tiles[xx+ (yy * map.getWidth())] = new WallTile(xx*16, yy*16, Tile.TILE_CACTO);
						}
						else if(pixelAtual == pixelRatNpc) {
							Game.npc.setX(xx * 16);
							Game.npc.setY(yy * 16);
						}

				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void generatedParticles(int amount, double x, double y) {
		for (int i = 0; i <amount; i++) {
			Game.entities.add(new Particle((int)x,(int)y,1,1, null));
		}
	}
	
	public static boolean isFreeDynamic(int xnext,  int ynext, int width, int height) {
		int x1 = xnext / TILE_SIZE;
		int y1 = ynext / TILE_SIZE;
		int x2 = (xnext +TILE_SIZE -1) / TILE_SIZE;
		int y2 = ynext / TILE_SIZE;

		int x3 = xnext / TILE_SIZE;
		int y3 = (ynext +height -1) / TILE_SIZE;

		int x4 = (xnext +width -1) / TILE_SIZE;
		int y4 = (ynext +height -1)/ TILE_SIZE;
		return !((tiles[x1 +(y1*World.WIDTH)]instanceof WallTile ||
				tiles[x2 +(y2*World.WIDTH)]instanceof WallTile  ||
				tiles[x3 +(y3*World.WIDTH)]instanceof WallTile  ||
				tiles[x4 +(y4*World.WIDTH)]instanceof WallTile ));
	}

	public static boolean isFree(int xnext,  int ynext, int zPlayer) {
		int x1 = xnext / TILE_SIZE;
		int y1 = ynext / TILE_SIZE;

		int x2 = (xnext +TILE_SIZE -1) / TILE_SIZE;
		int y2 = ynext / TILE_SIZE;

		int x3 = xnext / TILE_SIZE;
		int y3 = (ynext +TILE_SIZE -1) / TILE_SIZE;

		int x4 = (xnext +TILE_SIZE -1) / TILE_SIZE;
		int y4 = (ynext +TILE_SIZE -1)/ TILE_SIZE;

		
		if(!((tiles[x1 +(y1*World.WIDTH)]instanceof WallTile ||
				tiles[x2 +(y2*World.WIDTH)]instanceof WallTile  ||
				tiles[x3 +(y3*World.WIDTH)]instanceof WallTile  ||
				tiles[x4 +(y4*World.WIDTH)]instanceof WallTile ))) {
			return true;
		}
		
		if(zPlayer > 0) {
			return true;
		}
		
		return false;


	}

	public static void restartGame(String level) {
		Game.entities.clear();
		Game.enemies.clear();
		Game.bullets.clear();
		Player.hasGun = true;
		Game.entities = new ArrayList<Entity>();
		Game.enemies = new ArrayList<Enemy>();
		Game.spritesheet = new Spritesheet("/spritesheet.png");
		Game.player = new Player(0, 0, 16, 16,Game.spritesheet.getSpriteint(32, 0, 16, 16));
		Game.npc = new Npc(0, 0, 16, 16, Game.spritesheet.getSpriteint(48, 48, 16, 16));
		Game.entities.add(Game.player);
		Game.entities.add(Game.npc);
		Game.world = new World("/" + level);
	}
	
	public static void renderMiniMap() {
		for(int i =0; i < Game.miniMapPixels.length; i++) {
			Game.miniMapPixels[i] = 0;
		}
		for (int xx = 0; xx < WIDTH; xx++) {
			for (int yy = 0; yy < HEIGHT; yy++) {
				if(tiles[ xx +(yy*WIDTH)] instanceof WallTile) {
					Game.miniMapPixels[xx +(yy*WIDTH)] = 0x7F3300;
				}
				if(tiles[ xx +(yy*WIDTH)] instanceof FloorTile) {
					Game.miniMapPixels[xx +(yy*WIDTH)] = 0x007F0E;
				}
			}
		}
		
		int xPlayer = Game.player.getX()/ 16;
		int yPlayer = Game.player.getY()/ 16;
		Game.miniMapPixels[xPlayer +(yPlayer*WIDTH)] = 0xffffff;
	}

	public void render(Graphics g) {
		int xStart = Camera.x/16;
		int yStart = Camera.y/16;
		int xFinal = xStart +(Game.WIDTH/16);
		int yFinal = yStart + (Game.HEIGHT/16);



		for(int xx =xStart; xx<=xFinal; xx++) {
			for(int yy = yStart; yy <= yFinal; yy++) {
				if(xx<0 || yy<0 || xx>=WIDTH || yy>=HEIGHT) {
					continue;
				}
				Tile tile= tiles[ xx +(yy*WIDTH)];
				tile.render(g);
			}
		}

	}

}
