package com.main;


import java.awt.Canvas;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import com.entities.*;
import com.graficos.Spritesheet;
import com.graficos.UI;
import com.world.Camera;
import com.world.FloorTile;
import com.world.Tile;
import com.world.World;


public class Game extends Canvas implements Runnable, KeyListener,MouseListener, MouseMotionListener{
	public static boolean rodando = true;
	public static JFrame frame;
	public static final int WIDTH = 240;
	public static int HEIGHT =  180;
	public static final int SCALE = 3;
	private BufferedImage image;
	public static ArrayList<Entity> entities;
	public static ArrayList<Enemy> enemies;
	public static ArrayList<BulletShoot> bullets;
	public static Spritesheet spritesheet;
	public static World world;
	public static Player player;
	public static Npc npc;
	public static Random random;
	public Menu menu;
	public static UI ui;
	public static int CUR_LVL = 1;
	private int MAX_LVL = 2;
	static String gameState = "MENU";
	boolean showMessageGameOver = true;
	public int gameOverFrames = 0;
	public boolean restartGame = false;
	public static String newWorld = "";
	public boolean saveGame = false;
	public InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream("pixelfont.ttf");
	public InputStream streamMenu = ClassLoader.getSystemClassLoader().getResourceAsStream("pixelfont.ttf");
	public static Font pixelFont;
	public static Font pixelFontMenu;
	public static float  fontSize = 16f;
	public int mx, my;
	public int[] pixels;
	public int [] lightPixels;
 	public int xx, yy;
	public BufferedImage lightmap;
	public static BufferedImage miniMap;
	public static int [] miniMapPixels;
	public boolean goNextLevel = false;
	
	public Game() { 
		random = new Random();
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		this.setPreferredSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE));
		InitFrame();
		//inicializando objetos
		ui = new UI();
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		try {
			lightmap = ImageIO.read(getClass().getResource("/lightmap.png"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		lightPixels = new int[lightmap.getWidth() * lightmap.getHeight()];
		lightmap.getRGB(0, 0, lightmap.getWidth(), lightmap.getHeight(), lightPixels, 0, lightmap.getWidth());
		pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
		entities = new ArrayList<Entity>();
		enemies = new ArrayList<Enemy>();
		bullets = new ArrayList<BulletShoot>();
		spritesheet = new Spritesheet("/spritesheet.png");
		player = new Player(0, 0, 16, 16, spritesheet.getSpriteint(32, 0, 16, 16));
		npc = new Npc(0, 0, 16, 16,spritesheet.getSpriteint(48, 48, 16, 16));
		entities.add(player);
		entities.add(npc);
		world = new World("/level1.png");
		
		try {
			pixelFont =Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(fontSize);
		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			pixelFontMenu = Font.createFont(Font.TRUETYPE_FONT, streamMenu).deriveFont(90f);
		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		menu = new Menu();

	}

	public void InitFrame() {
		frame = new JFrame("Generic Shooter =D");
		frame.add(this);
		frame.setResizable(false);
		frame.pack();
		Image icon = null;
		try {
			icon = ImageIO.read(getClass().getResource("/icon.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Image cursorImage = toolkit.getImage(getClass().getResource("/cursor.png"));
		Cursor cursor = toolkit.createCustomCursor(cursorImage, new Point(0,0), "img");
		frame.setCursor(cursor);
		frame.setIconImage(icon);
		frame.setAlwaysOnTop(true);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
	}

	public synchronized void Start() {
		Thread thread = new Thread(this);
		rodando = true;
		thread.start();

	}

	public synchronized void stop() {

	}
	public static void main(String[] args) {
		Game game= new Game();
		game.Start();
	}

	public void tick() {
		if(gameState == "NORMAL") {
			if(this.saveGame) { 
				saveGame = false;
				String [] opt1  = {"level", "life", "positionX", "positionY", "bullets"};
				int [] opt2 = {Game.CUR_LVL, (int) player.life, player.getX(), player.getY(), player.bullets};
				Menu.saveGame(opt1, opt2, 10);
				System.out.println("Jogo Salvo");
			}
		for(int i=0; i<entities.size(); i++) {
			Entity e = entities.get(i);
			e.tick();
		}
		for(int i=0; i<bullets.size(); i++) {
			bullets.get(i).tick();
		}
		}
		nextLevel();
		gameOverMessagerAnimation();
		gameOver();
		menu();

	}
	

	public void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if(bs == null) {
			this.createBufferStrategy(3);// Buffs para ajuda na renderização do jogo
			return;
		}
		Graphics g = image.getGraphics();
		g.setColor(new Color(0,0,0));//seleciono uma cor RGB
		g.fillRect(0, 0, WIDTH, HEIGHT);
		/*renderizar o jogo*/
		//Graphics2D g2 = (Graphics2D) g;
		world.render(g);
		Collections.sort(entities, Entity.nodeSorterEntity);
		for(int i=0; i<entities.size(); i++) {
			Entity e = entities.get(i);
			e.render(g);
		}
		for(int i=0; i<bullets.size(); i++) {
			bullets.get(i).render(g);

		}
		
		//applyLight();
		
		ui.render(g);
		///***////
		g.dispose();// funçao para ajudar na otimizar melhor o game
		g = bs.getDrawGraphics();
		g.drawImage(image, 0, 0, WIDTH*SCALE, HEIGHT*SCALE, null);// renderiza a imagem.
		if(gameState == "GAME_OVER" && Player.life <= 0) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(new Color(0 , 0, 0 , 100));
			g2.fillRect(0, 0, WIDTH * SCALE, HEIGHT * SCALE);
			g.setFont(new Font("arial", Font.BOLD, 36));
			g.setColor(Color.white);
			g.drawString("Game Over",(WIDTH * SCALE)/2 -85, (HEIGHT * SCALE)/2 -20);
			g.setFont(new Font("arial", Font.BOLD, 32));
			if(showMessageGameOver) {
			g.drawString(">Pressione Enter para reiniciar<", (WIDTH * SCALE)/2 - 250, (HEIGHT * SCALE)/2 +20);
			g.drawString(">Pressione Esc para voltar ao menu<", (WIDTH * SCALE)/2 - 250, (HEIGHT * SCALE)/2 +50);
			}
		}else if(gameState == "MENU") {
			menu.render(g);
		}else if(gameState == "PAUSE") {
			g.setColor(new Color(0 , 0, 0 , 100));
			g.fillRect(0, 0, WIDTH * SCALE, HEIGHT * SCALE);
			g.setFont(new Font("arial", Font.BOLD, 36));
			g.setColor(Color.white);
			g.drawString("PAUSADO",(WIDTH * SCALE)/2 -85, (HEIGHT * SCALE)/2 -20);
		}else if(gameState == "NORMAL") {
			miniMap = new BufferedImage(World.WIDTH	, World.HEIGHT, BufferedImage.TYPE_INT_RGB);
		miniMapPixels = ((DataBufferInt)miniMap.getRaster().getDataBuffer()).getData();
			World.renderMiniMap();
		}
		/*Metodo para girar um objeto de acordo com o mouse
		 * Graphics2D g2 = (Graphics2D) g;
		double angleMouse = Math.atan2(my - 225, mx - 225);
		g2.rotate(angleMouse, 225, 225);
		g.setColor(Color.red);
		g.fillRect(200, 200, 50, 50);
		*/
		
		g.drawImage(miniMap, 600, 50, World.WIDTH * 5, World.HEIGHT * 5, null);
		bs.show();
		
	}

	@Override
	public void run() {
		requestFocus();
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ms = 1000000000 / amountOfTicks;
		double delta = 0;
		int frames = 0;
		double timer = System.currentTimeMillis();

		while(rodando) {

			long now = System.nanoTime();
			delta+=(now - lastTime)/ms;
			lastTime=now;
			if(delta>=1) {
				tick();
				render();
				frames++;
				delta--;
			}

			if(System.currentTimeMillis() - timer>= 1000) {
				//System.out.println("FPS: " + frames);
				frames = 0;
				timer+=1000;
			}
		}



	}
	/*public void drawRectangle(int xoff, int yoff) {
		for (int xx = 0; xx < 50; xx++) {
			for (int yy = 0; yy < 32; yy++) {
				int xOff = xx +xoff;
				int yOff = yy + yoff;
				if(xOff < 0 || yOff < 0 || xOff >=WIDTH || yOff >= HEIGHT)
					continue;
				pixels[xOff + (yOff*WIDTH)] = 0xff0000;
			}
		}
	}
	*/
	
	public void applyLight() {
		for (int xx = 0; xx < Game.WIDTH; xx++) {
			for (int yy = 0; yy < Game.HEIGHT; yy++) {
				if(lightPixels[xx+(yy * Game.WIDTH)] == 0xffffffff) {
					int pixel = Pixel.getLightBlend(pixels[xx +(yy*Game.WIDTH)], 0x4C4C4C, 0);
					pixels[xx +(yy*Game.WIDTH)] =pixel;
				}
			}
		}
	}
	
	public void nextLevel() {
		if(Game.enemies.size() == 0 && goNextLevel == true) {
			CUR_LVL++;
			if(CUR_LVL > MAX_LVL) {
				CUR_LVL = 1;
			}
			goNextLevel = false;
			newWorld = "level" + CUR_LVL + ".png";
			World.restartGame(newWorld);
			
		}
	}
	
	public void gameOverMessagerAnimation() {
		if(gameState == "GAME_OVER") {
			gameOverFrames++;
			if(gameOverFrames == 25) {
				gameOverFrames =0;
				if(showMessageGameOver) {
					showMessageGameOver = false;
				}else {
					showMessageGameOver = true;
				}
			}
		}
	}
	
	public void restartGame() {
		if(Player.life <= 0) {
			gameState = "MENU";
			Player.z= 0;
			Enemy.damage = 0;
			Player.isDamaged = false;
			Player.iCanMove = false;
			Player.hasGun = false;
			if(restartGame && Player.life <=0) {
				gameState = "NORMAL";
				Player.z= 0;
				Player.iCanMove = false;
				Player.bullets = 0;
				Enemy.damage = 5;
				Player.life = 100;
				Player.hasGun = false;
				restartGame = false;
				CUR_LVL = 1;
				newWorld = "level" + CUR_LVL + ".png";
				World.restartGame(newWorld);
				if(Player.life >=100) {
					Player.z= 0;
					Player.iCanMove = true;
					Player.hasGun = false;
				}
			}
		}
	}
	
	public void gameOver() {
		if(Player.life <= 0) {
			gameState = "GAME_OVER";
			Enemy.damage = 0;
			Player.isDamaged = false;
			Player.iCanMove = false;
			Player.hasGun = false;
			if(restartGame && Player.life <=0) {
				gameState = "NORMAL";
				Player.iCanMove = false;
				Player.bullets = 0;
				Enemy.damage = 5;
				Player.life = 100;
				Player.hasGun = false;
				restartGame = false;
				CUR_LVL = 1;
				newWorld = "level" + CUR_LVL + ".png";
				World.restartGame(newWorld);
				if(Player.life >=100) {
					Player.iCanMove = true;
					Player.hasGun = false;
				}
			}
		}
	}
	
	public void menu() {
		if(gameState == "MENU") {
			player.updateCamera();
			menu.tick();
		}
	}
	
	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_Z) {
			player.jump = true;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_D) {
			player.right = true;

		}else if(e.getKeyCode() == KeyEvent.VK_A) {
			player.left = true;
		}

		if(e.getKeyCode() == KeyEvent.VK_W) {
			player.up = true;

		}else if(e.getKeyCode() == KeyEvent.VK_S) {
			player.down = true;

		}

		if(e.getKeyCode() == KeyEvent.VK_X) {
			player.isShooted = true;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			if(Math.abs(player.getX() - npc.getX()) < 25 && Math.abs(player.getY() - npc.getY()) < 10) {
				npc.show = true;
			}if(npc.show == true && npc.showDialog) {
				npc.show = false;
				npc.showDialog = false;
				goNextLevel = true;
			}
			if(gameState == "GAME_OVER") {
			restartGame = true;
			}
			if(gameState == "MENU") {
				menu.enter = true;
			}
		}
		
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			if(gameState == "NORMAL") {
				gameState = "PAUSE";
			}else if(gameState == "PAUSE") {
				gameState = "NORMAL";
			}
		}
		
		if(e.getKeyCode() == KeyEvent.VK_SPACE) {
			if(gameState == "NORMAL")
			this.saveGame = true;
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_D) {
			player.right = false;

		}else if(e.getKeyCode() == KeyEvent.VK_A) {
			player.left = false;
		}

		if(e.getKeyCode() == KeyEvent.VK_W) {
			player.up = false;

		}else if(e.getKeyCode() == KeyEvent.VK_S) {
			player.down = false;

		}
		
		if(e.getKeyCode() == KeyEvent.VK_DOWN) {
			if(gameState == "MENU") {
				menu.down = true;
			}
		}
		
		if(e.getKeyCode() == KeyEvent.VK_UP) {
			if(gameState == "MENU") {
				menu.up = true;
			}
			
		}

		if(e.getKeyCode() == KeyEvent.VK_X) {
			player.isShooted = false;
		}	
		
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
		
		player.mouseIsShooted = true;
		player.mouseX = (e.getX() /3);
		player.mouseY = (e.getY() / 3);
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mx = e.getX();
		my = e.getY();
	}
}

