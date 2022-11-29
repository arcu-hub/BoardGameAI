import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.Color;
import java.awt.Font;

public class BoardComponent extends JComponent {

	private static final long serialVersionUID = 1L;
	
	private int boardsize;
	private BufferedImage image, clock;
	private byte[][] board;
	private int[][] workersA, workersB;
	private int[] times = {0, 0};
	private int[] lastMove = {-1,-1};
	
	
	public BoardComponent(int boardsize)
	{
		this.boardsize = boardsize;
		this.board = new byte[boardsize][boardsize];
		this.workersA = new int[4][2];
		this.workersB = new int[4][2];
		try {
			image = ImageIO.read(new File("board.jpg"));
			clock = ImageIO.read(new File("clock.png"));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}		
	}
	
	public void updateBoard(byte[][] board, int[][] workersA, int[][] workersB, int times[], int lastMove[])
	{
		this.board = board;
		this.workersA = workersA;
		this.workersB = workersB;
		this.times = times;
		this.lastMove = lastMove;
		repaint();
	}
	
	private void paintaPiece(int x, int y, int fullSize, int size, boolean isBlack, Graphics2D g2)
	{
		int margin = (fullSize-size)/2;
		if (isBlack)
			g2.setColor(Color.BLACK);
		else
			g2.setColor(Color.WHITE);
		Ellipse2D.Double circleOut = new Ellipse2D.Double(x+margin+size/10, y+margin+size/10, 4*size/5, 4*size/5);
		g2.fill(circleOut);
		if (isBlack)
			g2.setColor(Color.WHITE);
		else
			g2.setColor(Color.BLACK);
		g2.draw(circleOut);
		Ellipse2D.Double circleIn = new Ellipse2D.Double(x+margin+size/5, y+margin+size/5, 3*size/5, 3*size/5);
		g2.draw(circleIn);
	}
	
	private void paintaBuildingPiece(int x, int y, int fullSize, int size, Color color, Graphics2D g2)
	{
		int margin = (fullSize-size)/2;
		g2.setColor(color);
		if (color!=Color.BLUE)
			g2.fillRect(x+margin+size/10, y+margin+size/10, 4*size/5, 4*size/5);
		else
			g2.fillOval(x+margin+size/10, y+margin+size/10, 4*size/5, 4*size/5);
	}
	
	private String intToString(int x)
	{
		int x10 = x/10;
		x = x%10;
		return x10+""+x;
	}
	
	private String timeToString(int t)
	{
		return intToString(t/60)+":"+intToString(t%60);
	}
	
	public void paintComponent(Graphics g)
	{
		g.drawImage(image, 50, 50, null);
		
		int dy = image.getHeight()/boardsize;
		int dx = image.getWidth()/boardsize;
		
		int origX = 50 + (image.getWidth() - dx*boardsize +1)/2;
		int origY = 50 + (image.getHeight() - dy*boardsize +1)/2;
		
		Graphics2D g2 = (Graphics2D) g;
		
		// draw lines
		g2.setColor(Color.YELLOW);
		for (int i=0; i<=boardsize; i++)
		{
			g2.draw(new Line2D.Double(origX+i*dx, origY, origX+i*dx, origY+boardsize*dy));
			g2.draw(new Line2D.Double(origX, origY+i*dy, origX+boardsize*dx, origY+i*dy));
		}
		
		// print rows and columns
		g2.setColor(Color.BLUE);
		g2.setFont(new Font(null,Font.PLAIN, 300/boardsize));
		for (int i=1; i<=boardsize; i++)
		{
			char c = (char) ('a'+i-1);
			g2.drawString(c+"", 50+i*dx-3*dx/5, 40);
			g2.drawString(i+"", 50-230/boardsize, 50+i*dy-7*dy/20);			
		}
		
		// draw the buildings {Color.BLACK, Color.WHITE, Color.YELLOW, Color.CYAN, Color.BLUE};
		for (int y=0; y<boardsize; y++)
		{
			for (int x=0; x<boardsize; x++)
			{
				if (board[y][x]==0) continue;
				// paint building 1
				paintaBuildingPiece(50+x*dx, 50+y*dy, dx, dx, Color.WHITE, g2);
				if (board[y][x]>=2)
				{
					paintaBuildingPiece(50+x*dx, 50+y*dy, dx, dx*9/10, Color.YELLOW, g2);
				}
				if (board[y][x]>=3)
				{
					paintaBuildingPiece(50+x*dx, 50+y*dy, dx, dx*8/10, Color.CYAN, g2);
				}
				if (board[y][x]>=4)
				{
					paintaBuildingPiece(50+x*dx, 50+y*dy, dx, dx*7/10, Color.BLUE, g2);
				}				
			}
		}
		// draw workers
		for (int i=0; i<workersA.length; i++)
		{
			paintaPiece(50+workersA[i][0]*dx, 50+workersA[i][1]*dy, dx, dx*2/3, true, g2);
		}
		for (int i=0; i<workersB.length; i++)
		{
			paintaPiece(50+workersB[i][0]*dx, 50+workersB[i][1]*dy, dx, dx*2/3, false, g2);
		}
		
		g.drawImage(clock, 900, 150, null);
		g.drawImage(clock, 900, 550, null);
		paintaPiece(900, 50, 100, 100, true, g2);
		paintaPiece(900, 450, 100, 100, false, g2);
		g2.setColor(Color.ORANGE);
		g2.setFont(new Font(null,Font.PLAIN, 40));
		g2.drawString("A", 928, 115);
		g2.drawString("B", 928, 515);
		g2.setColor(Color.RED);
		g2.drawString(timeToString(times[0]), 980, 350);
		g2.drawString(timeToString(times[1]), 980, 750);
		
		if (lastMove[0]+lastMove[1]>=0)
			g2.drawString("*", origX+lastMove[0]*dx+dx/2-7, origY+lastMove[1]*dy+dy/2+20);
	}
}
