package com.graficos;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import com.entities.Player;
import com.main.Game;

public class UI {

	public void render(Graphics g) {
		g.setColor(Color.red);
		g.fillRect(8,4,50,8);
		g.setColor(Color.green);
		g.fillRect(8, 4, (int)((Player.life/Player.maxLife)*50),8);
		g.setColor(Color.white);
		g.setFont(Game.pixelFont);
		g.drawString((int)Player.life+"/"+(int)Player.maxLife, 10, 12);
		
		
		g.setColor(Color.white);
		g.setFont(Game.pixelFont);
		g.drawString("Munição: " + Player.bullets,180,12);
		
	}
	
}
