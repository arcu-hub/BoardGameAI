import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class SantoriniFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private BoardComponent boardcomp;
	
	public SantoriniFrame(int boardsize)
	{
		boardcomp = new BoardComponent(boardsize);
		add(boardcomp);
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(1200, 910);
		setTitle("Santorini GUI Viewer");
		setVisible(true);
	}
	
	public void updateBoard(byte[][] board, int[][] workersA, int[][] workersB, int times[], int lastMove[])
	{
		boardcomp.updateBoard(board, workersA, workersB, times, lastMove);
	}

}
