import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Tetris extends JPanel {
	public static  BufferedImage T;
	public static  BufferedImage I;
	public static  BufferedImage O;
	public static  BufferedImage J;
	public static  BufferedImage L;
	public static  BufferedImage S;
	public static  BufferedImage Z;
	public static  BufferedImage background;
	public static  BufferedImage gameover;

	static {
		try {
			T = ImageIO.read(Tetris.class.getResource("\\tetris\\t.png"));
			I = ImageIO.read(Tetris.class.getResource("\\tetris\\i.png"));
			O = ImageIO.read(Tetris.class.getResource("\\tetris\\o.png"));
			J = ImageIO.read(Tetris.class.getResource("\\tetris\\j.png"));
			L = ImageIO.read(Tetris.class.getResource("\\tetris\\l.png"));
			S = ImageIO.read(Tetris.class.getResource("\\tetris\\s.png"));
			Z = ImageIO.read(Tetris.class.getResource("\\tetris\\z.png"));		
			background = ImageIO.read(Tetris.class.getResource("\\tetris\\bg.png"));
			gameover = ImageIO.read(Tetris.class.getResource("\\tetris\\go.png"));		
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	private Tetromino currentOne = Tetromino.randomOne();
	private Tetromino nextOne = Tetromino.randomOne();
	private Cell[][] wall=new Cell[20][10];

	public void paint(Graphics g) {
		g.drawImage(background, 0, 0, null);
		g.translate(15, 15);
		paintWall(g);
		paintCurrentOne(g);
		paintNextOne(g);
		paintScore(g);
		paintState(g);
	}

	private static final int CELL_SIZE = 26;
	public void paintWall(Graphics a) {
		for(int i = 0; i < 20; i++) {
			for(int j = 0; j < 10; j++) {
				int x = j*CELL_SIZE;
				int y = i*CELL_SIZE;
				Cell cell = wall[i][j];
				if(cell == null) {
					a.drawRect(x, y, CELL_SIZE, CELL_SIZE);
				}else{
					a.drawImage(cell.getImage(), x, y, null);
				}
			}
		}
	}
	
	public void paintCurrentOne(Graphics g) {
		Cell[] cells = currentOne.cells;
		for(Cell c:cells) {
			int x = c.getCol()*CELL_SIZE;
			int y = c.getRow()*CELL_SIZE;
			g.drawImage(c.getImage(), x, y, null);
		}
	}
	
	public void paintNextOne(Graphics g) {
		Cell[] cells = nextOne.cells;
		for(Cell c:cells) {
			int row = c.getRow();
			int col = c.getCol();
			int x = col*CELL_SIZE + 260;
			int y = row*CELL_SIZE + 26;
			g.drawImage(c.getImage(), x, y, null);
		}
	}

	int[] scores_pool = {0, 1, 2, 5, 10};
	private int totalScore = 0;
	private int totalLine = 0;
	
	public void paintScore(Graphics g){
		g.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 30));
		g.drawString("SCORES:" + totalScore, 285, 160);
		g.drawString("LINES:" + totalLine, 285, 215);
	}

	public static final int PLAYING = 0;
	public static final int PAUSE = 1;
	public static final int GAMEOVER = 2;
	private int game_state;
	
	String[] show_state = {"P[pause]", "C[continue]", "S[replay]"};
	public void paintState(Graphics g){
		if(game_state == GAMEOVER){
			g.drawImage(gameover, 0, 0, null);
			g.drawString(show_state[GAMEOVER], 285, 265);
		}
		if (game_state == PLAYING){
			g.drawString(show_state[PLAYING], 285, 265);
		}
		if (game_state == PAUSE){
			g.drawString(show_state[PAUSE], 285, 265);
		}		
	}

	public boolean outOfBounds() {
		Cell[] cells = currentOne.cells;
		for(Cell c:cells) {
			int col = c.getCol();
			int row = c.getRow();
			if(col < 0 || col > 9 || row > 19 || row < 0) {
				return true;
			}
		}
		return false;
	}
	
	public boolean coincide() {
		Cell[] cells = currentOne.cells;
		for(Cell c:cells) {
			int row = c.getRow();
			int col = c.getCol();
			if(wall[row][col] != null) {
				return true;
			}
		}
		return false;
	}
	
	public void moveRightAction() {
		currentOne.moveRight();
		if(outOfBounds() || coincide()) {
			currentOne.moveLeft();
		}
	}
	
	public void moveLeftAction() {
		currentOne.moveLeft();
		if(outOfBounds() || coincide()) {
			currentOne.moveRight();
		}
	}
	
	public void rotateRightAction() {
		currentOne.rotateRight();
		if(outOfBounds() || coincide()){
			currentOne.rotateLeft();
		}
	}
	
	public boolean isGameOver() {
		Cell[] cells = nextOne.cells;
		for(Cell c:cells) {
			int row = c.getRow();
			int col = c.getCol();
			if(wall[row][col] != null) {
				return true;
			}
		}
		return false;
	}

	public boolean isFullLine(int row) {
		Cell[] line = wall[row];
		for(Cell r:line) {
			if(r == null) {
				return false;
			}
		}
		return true;
	}
	
	public void destroyLine() {
		int lines = 0;
		Cell[] cells = currentOne.cells;
		for(Cell c:cells){
			int row = c.getRow();
			while(row < 20) {
				if(isFullLine(row)) {
					lines++;
					wall[row] = new Cell[10];
					for(int i = row; i > 0; i--) {
						System.arraycopy(wall[i-1], 0, wall[i], 0, 10);
					}
					wall[0] = new Cell[10];
				}
				row++;
			}	
		}
		totalScore+=scores_pool[lines];
		totalLine+=lines;
	}
	
	public boolean canDrop() {
		Cell[] cells = currentOne.cells;
		for(Cell c:cells) {
			int row = c.getRow();
			int col = c.getCol();
			if (row == 19) {
				return false;
			}
			if(wall[row+1][col] != null) {
				return false;
			}
			
		}
		return true;
	}
	
	public void landToWall() {
        Cell[] cells = currentOne.cells;
        for(Cell c:cells) {
            int row = c.getRow();
            int col = c.getCol();
            wall[row][col] = c;
        }
    }
	
	public void softDropAction() {
		if(canDrop()) {
			currentOne.softDrop();
		}else {
			landToWall();
			destroyLine();
			if(!isGameOver()) {
				currentOne = nextOne;
				nextOne = Tetromino.randomOne();
			}
			else {
				game_state = GAMEOVER;
			}
		}
	}
	

	public void handDropAction() {
		while(canDrop()) {
			currentOne.softDrop();
		}
		landToWall();
		destroyLine();
		if(!isGameOver()) {
			currentOne = nextOne;
			nextOne = Tetromino.randomOne();
		}else {
			game_state = GAMEOVER;
		}
	}
	
	public void start() {
		game_state = PLAYING;
		KeyListener l = new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				int code = e.getKeyCode();
				switch(code) {
					case KeyEvent.VK_P:
						if(game_state == PLAYING) {
							game_state = PAUSE;
						}
						break;
					case KeyEvent.VK_C:
						if(game_state == PAUSE) {
							game_state = PLAYING;
						}
						break;
					case KeyEvent.VK_S:
						game_state = PLAYING;
						wall = new Cell[20][10];
						currentOne = Tetromino.randomOne();
						nextOne = Tetromino.randomOne();
						totalScore = 0;
						totalLine = 0;
						break;				
					case KeyEvent.VK_DOWN:
						softDropAction();
						break;
					case KeyEvent.VK_LEFT:
						moveLeftAction();
						break;
					case KeyEvent.VK_RIGHT:
						moveRightAction();
						break;
					case KeyEvent.VK_UP:
						rotateRightAction();
						break;
					case KeyEvent.VK_SPACE:
						handDropAction();
						break;
				}
				repaint();
			}		
		};
		this.addKeyListener(l);
		this.requestFocus();
		
		while(true) {
			try {
				Thread.sleep(400);
			}catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(game_state == PLAYING) {			
				if(canDrop()) {
					currentOne.softDrop();
				}else {
					landToWall();
					destroyLine();
					if(!isGameOver()) {
						currentOne = nextOne;
						nextOne = Tetromino.randomOne();
					}else {
						game_state = GAMEOVER;
					}	
				}
			}
			repaint();


		}
	}
	
	public static void main (String[] args) {
		JFrame frame = new JFrame("Tetris");
		Tetris panel = new Tetris();
		frame.add(panel);
		frame.setVisible(true);
		frame.setSize(535, 595);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		panel.start();
	}
}
