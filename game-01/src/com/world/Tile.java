package com.world;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.main.Game;

public class Tile {

	public static BufferedImage TILE_FLOOR = Game.spritesheet.getSpriteint(0, 0, 16, 16);
	public static BufferedImage TILE_WALL = Game.spritesheet.getSpriteint(16, 0, 16, 16);
	public static BufferedImage TILE_SAND = Game.spritesheet.getSpriteint(0, 80, 16, 16);
	public static BufferedImage TILE_CACTO = Game.spritesheet.getSpriteint(16, 80, 16, 16);
	private BufferedImage sprite;
	private int x,y;
	
	public Tile( int x, int y, BufferedImage sprite){
		this.x =x;
		this.y=y;
		this.sprite = sprite;
	}
	
	public void render(Graphics g) {
		g.drawImage(sprite, x - Camera.x, y - Camera.y, null);
	}
	
}
