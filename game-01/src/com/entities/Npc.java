package com.entities;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.main.Game;

public class Npc extends Entity {

	public String[] frases = new String[3];
	public boolean showDialog = false;
	public boolean show = false;
	private int curIndexMsg = 0;
	public int fraseIndex = 0;
	public int time =0;
	public int maxTime= 7;

	public Npc(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);
		frases[0] = "Valeu amigo =)";
		frases[1] = "Pode ir para o";
		frases[2] = "proximo mapa";
	}

	public void tick() {
		checkDistanceWithPlayer();
	}

	public void checkDistanceWithPlayer() {
		int xPlayer = Game.player.getX();
		int yPlayer = Game.player.getY();
		int xNpc = this.x;
		int yNpc = this.y;

		if(Math.abs(xPlayer - xNpc) < 25 && Math.abs(yPlayer - yNpc) < 10 && show == true) {
			showDialog = true;

		}else {
			show = false;
			showDialog = false;
			fraseIndex = 0;
			curIndexMsg = 0;
		}

		if(showDialog) {

			time++;
			if(time>= maxTime) {
				time = 0;
				if(curIndexMsg < frases[fraseIndex].length()) {
					curIndexMsg++;
				}else {
					if(fraseIndex < frases.length - 1) {
						fraseIndex ++;
						curIndexMsg = 0;
					}
				}
			}
		}
	}

	public void render(Graphics g) {
		super.render(g);
		if(showDialog && Game.enemies.size() == 0) {
			depth = 2;
			g.setColor(Color.WHITE);
			g.fillRect(80 - 7, 90-26, 90 + 2, 40 +2);
			g.setColor(Color.BLACK);
			g.fillRect(80 - 06, 90-25, 90, 40);
			g.setColor(Color.white);
			g.drawString(frases[fraseIndex].substring(0,curIndexMsg), 80, 90 );
		}
	}
}
