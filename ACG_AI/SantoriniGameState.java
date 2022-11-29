import java.awt.Point;

public class SantoriniGameState {
	public static final int MAXHEIGHT=4, WINHEIGHT=3;
	public static final int MOVEDL=1, MOVED=2, MOVEDR=3, MOVEL=4, MOVE0=5, MOVER=6, MOVEUL=7, MOVEU=8, MOVEUR=9; // hint: as movements on numpad! MOVE0=5 is not valid!
	
	private byte[][] board;
	private boolean isTeamAturn;
	private int boardSize, numWorkers;
	private Point[] teamA, teamB;
	
	/**
	 * Initialize board with specific size and workets
	 * @param boardSize size of board (min:5 max:10)
	 * @param numWorkers number of workers per team (min:2 max:4)
	 */
	public SantoriniGameState(int boardSize, int numWorkers)
	{
		isTeamAturn=true;
		if (boardSize<5) boardSize=5;
		if (boardSize>10) boardSize=10;
		this.boardSize = boardSize;
		if (numWorkers<2 || boardSize<=6) numWorkers=2;
		if (numWorkers>4) numWorkers=4;
		this.numWorkers = numWorkers;
		teamA = new Point[numWorkers];
		teamB = new Point[numWorkers];
		board = new byte[boardSize][boardSize];
		for (int i=0; i<boardSize; i++)
			for (int j=0; j<boardSize; j++)
				board[i][j]=0;
		// default placement (off one or two diagonally from center tile)
		int delta = (boardSize-2)/2;
		teamA[0] = new Point(delta,delta);
		teamA[1] = new Point(boardSize-1-delta, boardSize-1-delta);
		teamB[0] = new Point(boardSize-1-delta,delta);
		teamB[1] = new Point(delta, boardSize-1-delta);
		delta--;
		if (numWorkers>=3)
		{
			teamB[2] = new Point(delta,delta);
			teamA[2] = new Point(boardSize-1-delta,delta);
		}
		if (numWorkers>=4)
		{
			teamB[3] = new Point(boardSize-1-delta, boardSize-1-delta);
			teamA[3] = new Point(delta, boardSize-1-delta);			
		}
	}
		
	/**
	 * Prints the game state to the console
	 */
	public void printState()
	{
		System.out.print("_|");
		for (int col=0; col<boardSize; col++)
		{
			System.out.printf("_%1d|", col+1);
		}
		System.out.println();
		for (int row=0; row<boardSize; row++)
		{
			System.out.printf("%c|", 'a'+row);
			for (int col=0; col<boardSize; col++)
			{
				String s = "  ";
				int temp = workerOnGridCoords(true, row, col);
				if (temp>=0)
					s = "A"+(temp+1);
				else
				{
					temp = workerOnGridCoords(false, row, col);
					if (temp>=0)
						s = "B"+(temp+1);
				}
				System.out.print(s+"|");
			}
			System.out.print("\n_|");
			for (int col=0; col<boardSize; col++)
			{
				if (board[row][col]>0)
					System.out.printf("_%1d|", board[row][col]);
				else
					System.out.print("__|");
			}
			System.out.println();
		}
		if (isTeamAturn)
			System.out.println("NEXT MOVE: TEAM A");
		else
			System.out.println("NEXT MOVE: TEAM B");
	}
	
	/**
	 * Returns which worker of this team is on grid coordinates (row,col) or -1 if no such worker
	 * @param isFirstTeam check workers of the first team
	 * @param row row coordinate
	 * @param col column coordinate
	 * @return index of worker (0..numWorkers-1) or -1 if no such worker (index=number of worker - 1)
	 */
	public int workerOnGridCoords(boolean isFirstTeam, int row, int col)
	{
		Point[] workers = (isFirstTeam)?teamA:teamB;
		for (int i=0; i<workers.length; i++)
		{
			Point p = workers[i];
			if (p.x==col && p.y==row)
			{
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Converts a move (1-9 not 5) to a relative move from current pos (dx,dy)
	 * @param move input move
	 * @return an array [dx,dy] with the relative new position
	 */
	public int[] move2coords(int move)
	{
		if (move<1 || move>9)
			return null;
		int dy = (9-move)/3-1;
		int dx = (move-1)%3-1;
		return new int[] {dx,dy};
	}
	
	/**
	 * Checks if given a valid move
	 * @param workerNo which worker is moved (given index  between 0 and numWorkers-1) : index is number of worker -1
	 * @param move give 1-9 (not 5) based on numpad where to move
	 * @param build give 1-9 (not 5) based on numpad where to build (from new worker position)
	 * @return true if move is valid, false otherwise (or if input is incorrect)
	 */
	public boolean validateMove(int workerNo, int move, int build)
	{
		if (workerNo<0 || workerNo>=numWorkers)
			return false;
		Point worker = ((isTeamAturn)?teamA:teamB)[workerNo];
		if (move<1 || move>9 || move==MOVE0)
			return false;
		int[] relativeMove = move2coords(move);
		int newPosX = worker.x+relativeMove[0];
		if (newPosX<0 || newPosX>=boardSize) // new position is outside the board
			return false;
		int newPosY = worker.y+relativeMove[1];
		if (newPosY<0 || newPosY>=boardSize) // new position is outside the board
			return false;
		if (board[newPosY][newPosX]>=MAXHEIGHT) // cannot move to new position
			return false;
		if (board[newPosY][newPosX]>board[worker.y][worker.x]+1) // too high to move there
			return false;
		if (workerOnGridCoords(true, newPosY, newPosX)>=0 || workerOnGridCoords(false, newPosY, newPosX)>=0) // cannot move on top of other player
			return false;
		if (board[newPosY][newPosX]==WINHEIGHT) // you win by moving there, so it's a valid move
			return true;
		if (build<1 || build>9 || build==MOVE0)
			return false;
		int[] relativeBuild = move2coords(build);
		int newBuildX = newPosX+relativeBuild[0];
		if (newBuildX<0 || newBuildX>=boardSize) // build position is outside the board
			return false;
		int newBuildY = newPosY+relativeBuild[1];
		if (newBuildY<0 || newBuildY>=boardSize) // build position is outside the board
			return false;
		if (newBuildX==worker.x && newBuildY==worker.y) // can always build in the spot where I'm moving from
			return true;
		if (board[newBuildY][newBuildX]>=MAXHEIGHT) // cannot build further there
			return false;
		if (workerOnGridCoords(true, newBuildY, newBuildX)>=0 || workerOnGridCoords(false, newBuildY, newBuildX)>=0) // cannot build where another player is
			return false;
		return true;
	}
	
	/**
	 * Executes a move (after validating it)
	 * @param workerNo which worker is moved (given index  between 0 and numWorkers-1) : index is number of worker -1
	 * @param move give 1-9 (not 5) based on numpad where to move
	 * @param build give 1-9 (not 5) based on numpad where to build (from new worker position)
	 * @return true if move wins the game, false otherwise
	 */
	public boolean executeMove(int workerNo, int move, int build)
	{
		//System.out.println(workerNo+" "+move+" "+build);
		if (!validateMove(workerNo, move, build))
		{
			System.err.println("ERROR:INVALID MOVE!");
			return false;
		}
		Point worker = ((isTeamAturn)?teamA:teamB)[workerNo];
		int[] relativeMove = move2coords(move);
		int newPosX = worker.x+relativeMove[0];
		int newPosY = worker.y+relativeMove[1];
		worker.x = newPosX;
		worker.y = newPosY;
		if (board[newPosY][newPosX]==WINHEIGHT)
			return true;
		int[] relativeBuild = move2coords(build);
		int newBuildX = newPosX+relativeBuild[0];
		int newBuildY = newPosY+relativeBuild[1];
		board[newBuildY][newBuildX]++;
		isTeamAturn = !isTeamAturn;
		return false;
	}
	
	/**
	 * Checks if the worker has possible moves
	 * @param workerNo which worker is checked (given index between 0 and numWorkers-1) : index is number of worker -1
	 * @return true if not valid moves found (so worker is stuck), false if at least one move exists
	 */
	public boolean checkIfNoMovesForWorker(int workerNo)
	{
		for (int move=1; move<=9; move++)
			if (validateMove(workerNo, move, 10-move))
				return false;
		return true;
	}
	
	/**
	 * Returns true if the current team cannot play (i.e. lost)
	 * @return true if no worker can move, false otherwise
	 */
	public boolean checkNoMoves()
	{
		for (int i=0; i<numWorkers; i++)
			if (!checkIfNoMovesForWorker(i))
				return false;
		return true;
	}
	
	public int getNumWorkers()
	{
		return numWorkers;
	}
	
	public int getBoardSize()
	{
		return boardSize;
	}
	
	public boolean getIsTeamATurn()
	{
		return isTeamAturn;
	}
	
	
	// --- I ADDED THESE METHODS AFTER DISTRIBUTION TO STUDENTS ---
	/**
	 * Returns a copy of the board
	 * @return copy of the board (with building heights)
	 */
	public byte[][] getBoardCopy()
	{
		byte[][] board2 = new byte[boardSize][boardSize];
		for (int i=0; i<boardSize; i++)
			for (int j=0; j<boardSize; j++)
				board2[i][j]=board[i][j];
		return board2;
	}
	
	/**
	 * Get the coordinates of a team's worker
	 * @param teamAworker if true from teamA otherwise from teamB
	 * @param workerNo which worker is moved (given index  between 0 and numWorkers-1) : index is number of worker -1
	 * @return [x,y] ie [column,row] coordinates of worker
	 */
	public int[] getWorkerCoords(boolean teamAworker, int workerNo)
	{
		Point worker = (teamAworker?teamA:teamB)[workerNo];
		return new int[] {worker.x, worker.y};
	}
	
	public int[][] getAllWorkerCoords(boolean isTeamA)
	{
		int[][] res = new int[numWorkers][2];
		for (int i=0; i<numWorkers; i++)
		{
			res[i]=getWorkerCoords(isTeamA, i);
		}
		return res;
	}
	
}
