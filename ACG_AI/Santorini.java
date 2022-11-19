import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * A main class that has all the functionality for the AI playing against a human
 * or another AI via saving its moves in the appropriate files
 * @author Ioannis A. Vetsikas (modified by student: Dimitrios Moiras)
 *
 */
public class Santorini {

	private boolean againstHuman;
	private boolean computerIsTeamA;
	private SantoriniGameState gameState;
	private myGreedyAI myAI;

	public Santorini(boolean againstHuman, boolean computerisTeamA)
	{
		this.againstHuman = againstHuman;
		System.out.println("Playing against a human:"+againstHuman);
		this.computerIsTeamA = computerisTeamA;
		System.out.println("Computer is: "+(computerisTeamA?"Team A":"Team B"));
		this.gameState = new SantoriniGameState(5, 2);
		this.myAI = new myGreedyAI(this.gameState);
	}

	/**
	 * Saves the move to a file
	 * @param currMove : number of current move (used to generate the file name)
	 * @param moves : the current move (in the format wmb w=workerNo, m=move, b=build)
	 */
	private void saveToFile(int currMove, int move)
	{
		String name = currMove+".txt";
		PrintWriter out;
		try {
			out = new PrintWriter(name);
			out.println(move);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	/**
	 * Reads the opponent move from a file
	 * @param currMove : number of current move (used to generate the file name)
	 * @return : the current move
	 */
	private int readFromFile(int currMove)
	{
		String name = currMove+".txt";
		int move = -1;
		while (move<0)
		{
			File inputFile = new File(name);
			while (!inputFile.exists())
			{
				try {
					Thread.sleep(100);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}				
			}
			try {
				Scanner in2 = new Scanner(inputFile);
				move = Integer.parseInt(in2.nextLine());
				in2.close();
			} catch (FileNotFoundException e) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}				
			}
		}
		System.out.println("Read move "+currMove+" from file: " + move);
		return move;
	}
	
	public static void main(String[] args) {
		int choice=-1;
		Scanner in = new Scanner(System.in);
		if (args.length>0)
		{
			if (args[0].charAt(0)=='V' || args[0].charAt(0)=='v')
			{
				choice=0;
			}
			else
			{
				try
				{
					choice = Integer.parseInt(args[0]);
				}
				catch (NumberFormatException e)
				{
					choice=-1;
				}
			}
		}
		if (choice<0 || choice>4)
		{
			System.out.println("1. Play against human, computer is team A (goes first)");
			System.out.println("2. Play against human, computer is team B (plays second)");
			System.out.println("3. Play using files, this computer is team A (goes first)");
			System.out.println("4. Play using files, this computer is team B (plays second)");
			while (choice <1 || choice > 4)
			{
				System.out.print("Make a choice:");
				try 
				{
					choice = in.nextInt();
				}
				catch (InputMismatchException e)
				{
					in.next();
					choice = -1;
				}
			}
		}		
		Santorini player;
		switch (choice)
		{
			case 0:	// only reachable if given v or V as a command line argument (not implemented)
				player = new Santorini(false, false);
				player.viewGame();
				break;
			case 1:
				player = new Santorini(true, true);
				player.playGame();
				break;
			case 2:
				player = new Santorini(true, false);
				player.playGame();
				break;
			case 3:
				player = new Santorini(false, true);
				player.playGame();
				break;
			case 4:
				player = new Santorini(false, false);
				player.playGame();
				break;
			default:
				System.exit(0);	
		}
		
		in.close();
		
	}
	
	/**
	 * Used to just view the game from files!
	 */
	public void viewGame()
	{
		int times[] = {0, 0};
		SantoriniFrame disp = new SantoriniFrame(gameState.getBoardSize());
		disp.updateBoard(gameState.getBoardCopy(),gameState.getAllWorkerCoords(true), gameState.getAllWorkerCoords(false), times, new int[]{-1,-1});
		int currentMove = 1;

		long timePrev = System.currentTimeMillis();
		while (true)
		{
			int inp = readFromFile(currentMove);
			int b=inp%10;
			inp /= 10;
			int m=inp%10;
			inp /= 10;
			int w=inp-1;
			if (!gameState.validateMove(w, m, b))
			{
				System.err.println("MOVE "+currentMove+" is not valid!");
				continue;
			}
			if (gameState.executeMove(w, m, b))
			{
				// game over
				disp.updateBoard(gameState.getBoardCopy(),gameState.getAllWorkerCoords(true), gameState.getAllWorkerCoords(false), times, gameState.getWorkerCoords(gameState.getIsTeamATurn(), w));
				System.out.println("Game is over");
				return;
			}
			if (currentMove>2)
			{
				times[(currentMove+1)%2] += (System.currentTimeMillis()-timePrev)/1000+1;
			}
			timePrev = System.currentTimeMillis();
			disp.updateBoard(gameState.getBoardCopy(),gameState.getAllWorkerCoords(true), gameState.getAllWorkerCoords(false), times, gameState.getWorkerCoords(!gameState.getIsTeamATurn(), w));
			
			currentMove++;
		}

	}

	
	public void playGame() {
		Scanner keyb = new Scanner(System.in);
		boolean isGameOverWin = false;
		int currentMove = 0;
		while (!isGameOverWin)
		{
			currentMove++;
			gameState.printState();
			if (gameState.checkNoMoves())
				break;
			int inp=-1;
			if (gameState.getIsTeamATurn()!=computerIsTeamA)
			{
				//either human or read from file
				if (againstHuman)
				{
					System.out.print("Give me a move (wmb) w=workerNo, m=move, b=build :");
					try {
						inp = keyb.nextInt();
					}
					catch (Exception e)
					{
						System.err.println("Invalid input...");
					}
				}
				else
				{
					inp = readFromFile(currentMove);
				}
			}
			else
			{
				inp=myAI.nextMove();
				System.out.println("AI plays:"+inp);
			}
			int b=inp%10;
			inp /= 10;
			int m=inp%10;
			inp /= 10;
			int w=inp-1;
			if (gameState.validateMove(w, m, b))
			{
				if ((gameState.getIsTeamATurn()==computerIsTeamA) && !againstHuman)
				{
					// save computer move to file
					saveToFile(currentMove, 100*(w+1)+10*m+b);
				}
				isGameOverWin = gameState.executeMove(w, m, b);
			}
			else
				System.err.println("ERROR:INVALID MOVE!");
		}
		if (isGameOverWin)
			System.out.println("TEAM "+(gameState.getIsTeamATurn()?"A":"B")+" WINS");
		else
			System.out.println("TEAM "+(gameState.getIsTeamATurn()?"A":"B")+" IS STUCK AND LOSES");
				
	}
	
	public static void OLDmain(String[] args) {
		SantoriniGameState gs = new SantoriniGameState(5, 2);
		myGreedyAI ai = new myGreedyAI(gs);
		@SuppressWarnings("resource")
		Scanner keyb = new Scanner(System.in);
		boolean isGameOver = false;
		while (!isGameOver)
		{
			gs.printState();
			if (gs.checkNoMoves())
				break;
			int inp=-1;
			if (gs.getIsTeamATurn())
			{
				System.out.print("Give me a move (wmb) w=workerNo, m=move, b=build :");
				try {
					inp = keyb.nextInt();
				}
				catch (Exception e)
				{
					System.err.println("Invalid input...");
				}
			}
			else
			{
				inp=ai.nextMove();
				System.out.println("AI plays:"+inp);
			}
			int b=inp%10;
			inp /= 10;
			int m=inp%10;
			inp /= 10;
			int w=inp-1;
			if (gs.validateMove(w, m, b))
			{
				isGameOver = gs.executeMove(w, m, b);
			}
			else
				System.err.println("ERROR:INVALID MOVE!");
		}
		if (isGameOver)
			System.out.println("TEAM "+(gs.getIsTeamATurn()?"A":"B")+" WINS");
		else
			System.out.println("TEAM "+(gs.getIsTeamATurn()?"A":"B")+" IS STUCK AND LOSES");
		
	}

}
