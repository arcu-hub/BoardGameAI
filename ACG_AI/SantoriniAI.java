import java.util.Random;

public class SantoriniAI {
	SantoriniGameState gameState;
	Random rng;


	//to fix implicit super constructor undefined error
	public SantoriniAI() {}

	public SantoriniAI(SantoriniGameState gs)
	{
		this.gameState = gs;
		this.rng = new Random();
	}
	
	/**
	 * This method returns the next move of the AI
	 * OVERRIDE this method in order to provide your own AIs
	 * This method will make a random move essentially for one of the workers
	 * IMPORTANT: there must be at least one available move available to one of the workers for this to work!
	 * @return move (wmb) where w=workerNo, m=move, b=build (m and b should take values 1-9 not 5 as on the keypad for the relative move build)
	 */
	public int nextMove()
	{
		for (int w=0; w<gameState.getNumWorkers(); w++)
		{
			if (!gameState.checkIfNoMovesForWorker(w))
			{
				int m = rng.nextInt(9)+1;
				if (gameState.validateMove(w, m, 10-m))
					return (w+1)*100+10*m+10-m;
			}
		}
		return nextMove(); // this will get stuck forever if no moves are possible!

    }


}

