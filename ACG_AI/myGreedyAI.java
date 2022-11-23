import java.util.Random;
public class myGreedyAI extends SantoriniAI{
	public myGreedyAI() {}

	public myGreedyAI(SantoriniGameState gs)
	{
		this.gameState = gs;
		this.rng = new Random();
	}
    @Override
	public int nextMove()
	{
		boolean original_isTeamAturn=gameState.getIsTeamATurn();
		byte[][] original_board=gameState.getBoardCopy(); //[row][col]->board height
		int[][] original_workercoordsA=gameState.getAllWorkerCoords(true);
		int[][] original_workercoordsB=gameState.getAllWorkerCoords(false);

		int[][] original_workerp={{original_workercoordsA[0][1],original_workercoordsA[0][0]},{original_workercoordsA[1][1],original_workercoordsA[1][0]},{original_workercoordsB[0][1],original_workercoordsB[0][0]},{original_workercoordsB[1][1],original_workercoordsB[1][0]}}; //[row][col]
				//row,col worker p [0]=wa1,[1]=wa2,[2]=wb1,[3]=wb2




		//iterate over all possible moves and select the best using evaluate funciton -> team A the most positive score, team B the most negative

        int maxscore;
        if(original_isTeamAturn==true){maxscore=-10000;}
        else{maxscore=10000;}
		int bestmove=0;
		int movex;
		int movey;
		int buildx;
		int buildy;
		byte[][] copy_board;
		int[][] copy_workerp;
		boolean copy_isTeamAturn=original_isTeamAturn;
			for(int worker=0;worker<2;worker++){
				for (int move=2;move<9;move+=2){
					for (int build=2;build<9;build+=2){		
						if (gameState.validateMove(worker, move, build)){

							//System.out.println("checking move: "+((worker+1)*100+move*10+build));
								
								copy_board=original_board;
								copy_workerp=original_workerp;
								copy_isTeamAturn=original_isTeamAturn;



								if(move==4 || move==6){movex=move-5;}
								else{movex=0;}

								if(move==2){movey=1;}
								else if(move==8){movey=-1;}
								else{movey=0;}

								if(build==4 || build==6){buildx=build-5;}
								else{buildx=0;}

								if(build==2){buildy=1;}
								else if(build==8){buildy=-1;}
								else{buildy=0;}


								if(copy_isTeamAturn){
									copy_workerp[worker][0]+=movey;
									copy_workerp[worker][1]+=movex;
									if(copy_board[(copy_workerp[worker][0])][(copy_workerp[worker][1])]==3){
										return((worker+1)*100+10*move+10-move);
									}

									copy_board[(copy_workerp[worker][0]+buildy)][(copy_workerp[worker][1]+buildx)]+=1;
									
								}
								else{
									copy_workerp[(worker+2)][0]+=movey;
									copy_workerp[(worker+2)][1]+=movex;
									if(copy_board[(copy_workerp[worker+2][0])][(copy_workerp[worker+2][1])]==3){
										return((worker+1)*100+10*move+10-move);
									}
									copy_board[(copy_workerp[worker+2][0]+buildy)][(copy_workerp[worker+2][1]+buildx)]+=1;									
								}

								int eval=evaluate_position(copy_isTeamAturn,copy_board,copy_workerp);


								if(eval>maxscore && original_isTeamAturn==true){
									maxscore=eval;
									bestmove=(worker+1)*100+10*move+build;
								}
								else if(eval<maxscore && original_isTeamAturn==false){
									maxscore=eval;
									bestmove=(worker+1)*100+10*move+build;
								}
								//reverse
								if(copy_isTeamAturn){
									copy_board[(copy_workerp[worker][0]+buildy)][(copy_workerp[worker][1]+buildx)]-=1;
									copy_workerp[worker][0]-=movey;
									copy_workerp[worker][1]-=movex;
								}
								else{
									copy_board[(copy_workerp[worker+2][0]+buildy)][(copy_workerp[worker+2][1]+buildx)]-=1;		
									copy_workerp[(worker+2)][0]-=movey;
									copy_workerp[(worker+2)][1]-=movex;
																
								}





							}
					}

				}
			}

			if(bestmove!=0){return bestmove;}
		
		// else make random move
		for (int w=0; w<gameState.getNumWorkers(); w++)
		{
			if (!gameState.checkIfNoMovesForWorker(w))
			{
				int m = rng.nextInt(9)+1;
				if (gameState.validateMove(w, m, 10-m))
					return (w+1)*100+10*m+10-m;
			}
		}
		return nextMove();
		//return nextMove(); // this will get stuck forever if no moves are possible!
	}

	public int evaluate_position(boolean isTeamATurn,byte[][] board,int[][] workerp){

		int teamAheight=0,teamBheight=0,teamAmob=0,teamBmob=0,teamAcentre=0,teamBcentre=0,teamAlvl2=0,teamBlvl2=0;

		if(board[workerp[0][0]][workerp[0][1]]==1){teamAheight+=40;}
		else if(board[workerp[0][0]][workerp[0][1]]==2){teamAheight+=60;}
		else if(board[workerp[0][0]][workerp[0][1]]==3){teamAheight+=3000;}

		if(board[workerp[1][0]][workerp[1][1]]==1){teamAheight+=40;}
		else if(board[workerp[1][0]][workerp[1][1]]==2){teamAheight+=60;}
		else if(board[workerp[1][0]][workerp[1][1]]==3 && !(board[workerp[0][0]][workerp[0][1]]==3)){teamAheight+=3000;}

		if(board[workerp[2][0]][workerp[2][1]]==1){teamBheight+=40;}
		else if(board[workerp[2][0]][workerp[2][1]]==2){teamBheight+=60;}
		else if(board[workerp[2][0]][workerp[2][1]]==3){teamBheight+=3000;}

		if(board[workerp[3][0]][workerp[3][1]]==1){teamBheight+=40;}
		else if(board[workerp[3][0]][workerp[3][1]]==2){teamBheight+=60;}
		else if(board[workerp[3][0]][workerp[3][1]]==3 && !(board[workerp[2][0]][workerp[2][1]]==3)){teamBheight+=3000;}

		//iterate over all board squares to calculate vertical mobility
		for(int x=0;x<5;x++){
			for(int y=0;y<5;y++){	
				if(!(x==workerp[0][0] && y==workerp[0][1]) && !(x==workerp[1][0] && y==workerp[1][1]) && !(x==workerp[2][0] && y==workerp[2][1]) && !(x==workerp[3][0] && y==workerp[3][1]) ){
				if(Math.abs(x-workerp[0][0]) + Math.abs(y-workerp[0][1])==1){
					if(board[x][y]-board[workerp[0][0]][workerp[0][1]]==1){teamAmob+=5;}
					else if(board[x][y]-board[workerp[0][0]][workerp[0][1]]>=2){teamAmob-=15;}}

				if(Math.abs(x-workerp[1][0]) + Math.abs(y-workerp[1][1])==1){
					if(board[x][y]-board[workerp[1][0]][workerp[1][1]]==1){teamAmob+=5;}
					else if(board[x][y]-board[workerp[1][0]][workerp[1][1]]>=2){teamAmob-=15;}}

				if(Math.abs(x-workerp[2][0]) + Math.abs(y-workerp[2][1])==1){
					if(board[x][y]-board[workerp[2][0]][workerp[2][1]]==1){teamBmob+=5;}
					else if(board[x][y]-board[workerp[2][0]][workerp[2][1]]>=2){teamBmob-=15;}}

				if(Math.abs(x-workerp[3][0]) + Math.abs(y-workerp[3][1])==1){
					if(board[x][y]-board[workerp[3][0]][workerp[3][1]]==1){teamBmob+=5;}
					else if(board[x][y]-board[workerp[3][0]][workerp[3][1]]>=2){teamBmob-=15;}}
				}
			}
		}
		//controls the center

		if((workerp[0][0]==3 &&workerp[0][1]==2) || (workerp[1][0]==2 &&workerp[1][1]==2)){teamAcentre+=15;}
		else if((workerp[2][0]==3 &&workerp[2][1]==2) || (workerp[3][0]==2 &&workerp[3][1]==2)){teamBcentre+=15;}

		//lvl2 Threat

		for(int x=0;x<5;x++){
			for(int y=0;y<5;y++){	
				if(!(x==workerp[0][0] && y==workerp[0][1]) && !(x==workerp[1][0] && y==workerp[1][1]) && !(x==workerp[2][0] && y==workerp[2][1]) && !(x==workerp[3][0] && y==workerp[3][1]) && board[x][y]>=2){
				if(((Math.abs(x-workerp[0][0]) + Math.abs(y-workerp[0][1])==1) && board[workerp[0][0]][workerp[0][1]]==2) || ((Math.abs(x-workerp[1][0]) + Math.abs(y-workerp[1][1])==1) && board[workerp[1][0]][workerp[1][1]]==2)){teamAlvl2+=500;}


				if(((Math.abs(x-workerp[2][0]) + Math.abs(y-workerp[2][1])==1) && board[workerp[2][0]][workerp[2][1]]==2) || ((Math.abs(x-workerp[3][0]) + Math.abs(y-workerp[3][1])==1) && board[workerp[3][0]][workerp[3][1]]==2)){teamBlvl2+=500;}



				}
			}
		}


		// System.out.println("height "+(teamAheight-teamBheight));
		// System.out.println("mob "+(teamAmob-teamBmob));
		// System.out.println("centre "+(teamAcentre-teamBcentre));
		// System.out.println("lvl2threat "+(teamAlvl2-teamBlvl2));
		//System.out.println("evaluation: "+(teamAheight-teamBheight+teamAmob-teamBmob+teamAcentre-teamBcentre+teamAlvl2-teamBlvl2));
		return teamAheight-teamBheight+teamAmob-teamBmob+teamAcentre-teamBcentre+teamAlvl2-teamBlvl2;
	}
   
}