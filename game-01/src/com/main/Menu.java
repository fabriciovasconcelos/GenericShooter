package com.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.entities.Entity;
import com.entities.Player;
import com.world.World;

public class Menu {

	public static String[] options = {"novo jogo", "carregar jogo", "sair"};
	public static int currentOption = 0;
	public int maxOption = options.length -1;
	public boolean up,down, enter;
	public static boolean pause= false, saveExist = false, saveGame = false;
	public void tick() {
		//Sound.musicMenu.loop();
		File file = new File ("save.txt");
		if(file.exists()) {
			saveExist = true;
		}else {
			saveExist = false;
		}
		if(up) {
			up = false;
			currentOption--;
			if(currentOption < 0) {
				currentOption = maxOption;
			}
		}
		if(down) {
			down = false;
			currentOption++;
			if(currentOption > maxOption) {
				currentOption = 0;
			}
		}
		if(enter) {
			//Sound.music.loop();
			enter = false;
			if(options[currentOption] == "novo jogo") {
				Game.gameState = "NORMAL";
				pause=false;
				
			}else if (options[currentOption] == "carregar jogo") {
				file = new File("save.txt");
				if(file.exists()) {
					String saver = loadGame(10);
					apllySave(saver);
					}
			}
			else if(options[currentOption] == "sair") {
				System.exit(1);
			}
		}
	}

	public static void apllySave(String str) {
		String[] spl = str.split("/");
		for (int i = 0; i < spl.length; i++) {
			String[] spl2 = spl[i].split(":");
			switch (spl2[0]) {
			case "level":
				String levelSave = "level" + spl2[1] + ".png";
				Game.newWorld = levelSave;
				Game.CUR_LVL =  Integer.parseInt(spl2[1]);
				World.restartGame(levelSave);
				Game.gameState = "NORMAL";
				System.out.println("rodando");
				pause=false;
				break;
				
			case "life":
				Player.life = Integer.parseInt(spl2[1]);
				break;
			case "positionX":
				Game.player.x = Integer.parseInt(spl2[1]);
				
			case "positionY":
				Game.player.y = Integer.parseInt(spl2[1]);
			case "bullets":
				Game.player.bullets = Integer.parseInt(spl2[1]);
			}
		}
	}

	public static String loadGame(int encode) {
		String line = "";
		File file = new File("save.txt");
		if(file.exists()) {
			try {
				String singleLine = null;
				BufferedReader reader = new BufferedReader(new FileReader("save.txt"));
				try {
					while ((singleLine = reader.readLine()) != null) {
						String [] transition = singleLine.split(":");
						char[] val = transition[1].toCharArray();
						transition[1] = "";
						for(int i =0; i< val.length; i++) {
							val[i] -=encode;
							transition[1] +=val[i];
						}
						line+=transition[0];
						line+=":";
						line+=transition[1];
						line+="/";
					}
				} catch (IOException e) {}
			} catch (FileNotFoundException e) {}
		}
		return line;
	}

	public static void saveGame(String[] val1, int[] val2, int encode) {
		BufferedWriter write = null;
		try {
			write = new BufferedWriter(new FileWriter("save.txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		for(int i = 0; i<val1.length; i++) {
			String current = val1[i];
			current +=":";
			char [] value = Integer.toString(val2[i]).toCharArray();
			// for para criptografar o save
			for(int n = 0; n<value.length; n++) {
				value[n] +=encode;
				current+=value[n];
			}
			try {
				write.write(current);
				if(i < val1.length -1)
					write.newLine();
			} catch (IOException e) {}
		}
		try {
			write.flush();
			write.close();      
		} catch (IOException e) {}
	}

	public void render(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(new Color(0,0,0,100));
		g2.fillRect(0, 0, Game.WIDTH * Game.SCALE, Game.HEIGHT * Game.SCALE);
		g.setColor(Color.white);
		g.setFont(Game.pixelFontMenu);
		g.drawString("Generic Shooter", (Game.WIDTH * Game.SCALE)/2 - 180, (Game.HEIGHT * Game.SCALE)/2 - 160);

		g.setFont(Game.pixelFontMenu);
		g.drawString("Novo Jogo", (Game.WIDTH * Game.SCALE)/2 - 90, (Game.HEIGHT * Game.SCALE)/2 - 80);
		g.drawString("Continuar Jogo", (Game.WIDTH * Game.SCALE)/2 - 150, (Game.HEIGHT * Game.SCALE)/2 - 20);
		g.drawString("Sair", (Game.WIDTH * Game.SCALE)/2 - 20, (Game.HEIGHT * Game.SCALE)/2 + 40);

		if(options[currentOption] == "novo jogo") {
			g.drawString(">", (Game.WIDTH * Game.SCALE)/2 - 110, (Game.HEIGHT * Game.SCALE)/2 - 80);
		}else if(options[currentOption] == "carregar jogo") {
			g.drawString(">", (Game.WIDTH * Game.SCALE)/2 - 170, (Game.HEIGHT * Game.SCALE)/2 - 20);
		}else if(options[currentOption] == "sair") {
			g.drawString(">", (Game.WIDTH * Game.SCALE)/2 - 40, (Game.HEIGHT * Game.SCALE)/2 + 40);
		}

	}

}
