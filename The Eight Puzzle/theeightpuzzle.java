import java.util.*; 
/*
 * Class State
 * Represents a state of the puzzle with 3-by-3 2d array
 * booleans left, right, up, down are flags to indicate if a move to that direction for 0 is possible or not
 * cost variable are g and h integers
 * if boolean flags misplacedTile and manhattanDist are set to false then h(n) is set to 0
 * int hash is just the puzzle represented as a 9 digit number for a unique hashing
 * state parent is a pointer to the parent state for tracing the solution at the end
 */
class State {
	
	int[][] state = new int[3][3]; 
	
	//possible move options
	boolean left;
	boolean right;
	boolean up;
	boolean down;
	
	// cost 
	int g;
	int h;
	
	// flag for heuristic functions 
	// if false then heuristic will be set to 0
	boolean misplacedTile;
	boolean manhattanDist;

	//hash value
	int hash;
	
	//pointer to the parent
	State parent;

    // Constructor
	public State(int[][] state){
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				this.state[i][j] = state[i][j];
			}
		}
    	
		left = true;
    	right = true;
    	up = true;
    	down = true;

    	misplacedTile = false;
    	manhattanDist = false;
    	
    	hash = 0;
    	
    	parent = null;
    	
    	g = 0;
    	h = 0;
    }

 
	//copy constructor
	public State(State state) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				this.state[i][j] = state.state[i][j];
			}
		}
		this.left = true;
    	this.right = true;
    	this.up = true;
    	this.down = true;
    	
    	this.misplacedTile = state.misplacedTile;
    	this.manhattanDist = state.manhattanDist;
	}
} // end State class

public class theeightpuzzle {
	
	static Scanner input = new Scanner( System.in );
 
	public static void main(String[] args) {
		//initial state
		State initialState = null;
		
		System.out.println("Welcome to Sargis Abrahamyans 8-puzzle solver \nType \"1\" to use a default puzzle, or \"2\" to enter your own puzzle. ");
		int choice = input.nextInt();
		if(choice == 1)
		{
			int[][] ex1 = new int[][] {{ 1, 2, 3 }, { 4, 0, 6}, { 7, 5, 8 }};
			initialState = new State (ex1);
		}
		else if(choice == 2)
		{
			System.out.println("Enter 9 digits separated with a space, use 0 for blank.");

			int[][] ex1 = new int[3][3];
			 
			for (int i = 0; i < 3; i++)
				for (int j = 0; j < 3; j++)
				{
					ex1[i][j] = input.nextInt();
			    }
			initialState = new State (ex1);
		}

		System.out.println("Puzzle");
		printState(initialState);
		System.out.println("Enter your choice of algorithm\n	1. Uniform Cost Search\n	"
				+ "2. A* with the Misplaces Tile heuristic.\n	3. A* with the Manhattan distance heuristic ");
		int alg_choice = input.nextInt();
		
		if (alg_choice == 1)
		{
			//Uniform Cost Search
			//Set the h(n) flag to false
			initialState.misplacedTile = false;	
			initialState.manhattanDist = false;
		}
		if (alg_choice == 2)
		{
			//A* with the Misplaces Tile heuristic
			initialState.misplacedTile = true;
			initialState.manhattanDist = false;
			
		}
		if (alg_choice == 3)
		{
			//A* with the Manhattan distance heuristic
			initialState.misplacedTile = false;	
			initialState.manhattanDist = true;
		}
		
		//start expanding
		State solved = solve(initialState);
		
		//printSolution(solved);
	}
	
	
	public static State solve(State state) {
		
		
		State currentState = state;
		
		//total number of expanded states
		int numberOfStates = 0;
		
		//max number of states in the queue
		int maxStatesinQ = 0;
		
		int[][] winarr = new int[][] {{ 1,2,3 }, { 4,5,6 }, { 7,8,0 }};
		
	    State winState = new State(winarr);

	    //push the initial state on the queue
	    PriorityQueue<State> minHeap = new PriorityQueue<State>(1, new StateComparator()); 
	    minHeap.add(currentState);
	    maxStatesinQ = minHeap.size();
	    //hashing
	    HashSet<Integer>  hash = new HashSet<Integer>();
	    hashCode(currentState);
	    hash.add(currentState.hash);
		
		while (!minHeap.isEmpty())
		{
			
			if(minHeap.size() > maxStatesinQ)
				maxStatesinQ = minHeap.size();
			
			//take the top of the stack
			currentState = (State) minHeap.poll();
			
			//printing states as we go
			System.out.printf("The best state to expand with g(n) = %d and h(n) = %d is... \n", currentState.g, currentState.h);
			printState(currentState);
			
			//check if its a solution
			if (misplacedTiles(currentState, winState) == 0)
			{	
				System.out.println("WIN");
				System.out.printf("Total number of states expanded: %d\n", numberOfStates);
				System.out.printf("Depth of the solution: %d\n", currentState.g);
				System.out.printf("Maximum number of states in a queue at any time: %d\n", maxStatesinQ);
				
				return currentState;
			}	
			//expand to all possible moves and add them to the stack in no particular order ?? for this part
			// check each win hash table
			else
			{
				// find possible moves
				// set boolean flag for impossible move direction to false then expand on the ones that are true (if they aren't in the hash)
				for(int i = 0; i < currentState.state.length; i++) {
					for(int j = 0; j < currentState.state.length; j++) {
						if(currentState.state[i][j] == 0)
						{
							if (i <= 0)
							{
								currentState.up = false;
							}
							if (i >= currentState.state.length-1)
							{
								currentState.down = false;
							}
							if (j <= 0)
							{
								currentState.left = false;
							}
							if (j >= currentState.state.length-1)
							{
								currentState.right = false;
							}
							
							// expand to possible states
							if(currentState.up == true)
							{
								//create a new state that is a copy of the current state and swap 0 with up
								State newState = new State (currentState);
								newState.parent = currentState;
								
								//update the cost
								newState.g = currentState.g + 1;
								
								//move 0 up
								int temp = newState.state[i][j];
								newState.state[i][j] = newState.state[i-1][j];
								newState.state[i-1][j] = temp;

								//calculate the h cost
								//if flag is set to true then calculate h otherwise leave it 0
								if(newState.misplacedTile)
								{
									// h = misplaced tile heuristic
									newState.h = misplacedTiles(newState, winState);
								}
								else if(newState.manhattanDist)
								{
									//h = manhattan distance
									newState.h = manhattanDist(newState, winState);
								}
								else 
								{
									newState.h = 0;
								}
								
																
								//upadate hash value 
								hashCode(newState);
								
								//check if the new state is in the hash and add it to the heap
								if (!hash.contains(newState.hash))
								{
									hash.add(newState.hash);
									minHeap.add(newState);
									numberOfStates++;
								}
							}
							
							if(currentState.down == true)
							{
								//create a new state that is a copy of the current state and swap 0 with up
								State newState = new State (currentState);
								newState.parent = currentState;
								
								//update the cost
								newState.g = currentState.g + 1;
								// move 0 down
								int temp = newState.state[i][j];
								newState.state[i][j] = newState.state[i+1][j];
								newState.state[i+1][j] = temp;
								
								//calculate the h cost
								//if flag is set to true then calculate h otherwise leave it 0
								if(newState.misplacedTile)
								{
									// h = misplaced tile heuristic
									newState.h = misplacedTiles(newState, winState);
								}
								else if(newState.manhattanDist)
								{
									//h = manhattan distance
									newState.h = manhattanDist(newState, winState);
								}
								else 
								{
									newState.h = 0;
								}
								
								//upadate hash value 
								hashCode(newState);
								
								//check if the new state is in the hash and add it to the heap
								if (!hash.contains(newState.hash))
								{
									hash.add(newState.hash);
									minHeap.add(newState);
									numberOfStates++;
								}
					    	}
							if(currentState.left == true)
							{
								//create a new state that is a copy of the current state and swap 0 with up
								State newState = new State (currentState);
								newState.parent = currentState;
								
								//update the cost
								newState.g = currentState.g + 1;
								// move 0 left
								int temp = newState.state[i][j];
								newState.state[i][j] = newState.state[i][j-1];
								newState.state[i][j-1] = temp;
								
								//calculate the h cost
								//if flag is set to true then calculate h otherwise leave it 0
								if(newState.misplacedTile)
								{
									// h = misplaced tile heuristic
									newState.h = misplacedTiles(newState, winState);
								}
								else if(newState.manhattanDist)
								{
									//h = manhattan distance
									newState.h = manhattanDist(newState, winState);
								}
								else 
								{
									newState.h = 0;
								}
								
								//upadate hash value 
								hashCode(newState);
								
								//check if the new state is in the hash and add it to the heap
								if (!hash.contains(newState.hash))
								{
									hash.add(newState.hash);
									minHeap.add(newState);
									numberOfStates++;
								}
					    	}
							if(currentState.right == true)
							{
								//create a new state that is a copy of the current state and swap 0 with up
								State newState = new State (currentState);
								newState.parent = currentState;
								
								//update the cost
								newState.g = currentState.g + 1;
								
								//move 0 right
								int temp = newState.state[i][j];
								newState.state[i][j] = newState.state[i][j+1];
								newState.state[i][j+1] = temp;
								
								//calculate the h cost
								//if flag is set to true then calculate h otherwise leave it 0
								if(newState.misplacedTile)
								{
									// h = misplaced tile heuristic
									newState.h = misplacedTiles(newState, winState);
								}
								else if(newState.manhattanDist)
								{
									//h = manhattan distance
									newState.h = manhattanDist(newState, winState);
								}
								else 
								{
									newState.h = 0;
								}
								
								//upadate hash value 
								hashCode(newState);
								
								//check if the new state is in the hash and add it to the heap
								if (!hash.contains(newState.hash))
								{
									hash.add(newState.hash);
									minHeap.add(newState);
									numberOfStates++;
								}
					    	}
						}
						}
					}
			} // end expand 
	} // end while
		
		
		System.out.println("No Solution");
		System.out.printf("Total number of states expanded: %d\n", numberOfStates);
		System.out.printf("Maximum number of states in a queue at any time: %d\n", maxStatesinQ);
		
		return currentState;
	}
	
	//print a state
	public static void printState(State state) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
					System.out.print(state.state[i][j] + " ");
			}
			System.out.print("\n");
		}
		System.out.print("\n");
	}
	

	//return number of non matching tiles
	public static int misplacedTiles(State state, State winState) {
		
			  int misplaced = 0;
			  
			  for (int i = 0; i < 3; i++) {
					for (int j = 0; j < 3; j++) {
						if(state.state[i][j] != winState.state[i][j])
							misplaced++;
					}
				}
			  return misplaced;
	}
	
	// returns total distance of all wrong tiles
	// calculates by finding the difference between what i and j are and what they should be
	public static int manhattanDist(State state, State winState) {
		int totalDistance = 0;
				  				  
		 for (int currentDigit = 1; currentDigit < 9; currentDigit++)
		 {
			 for (int i = 0; i < 3; i++) {
				 for (int j = 0; j < 3; j++) {
					 if(state.state[i][j] == currentDigit)
					 {
						 switch(currentDigit)
						 {
						 	case 1:
						 		totalDistance = totalDistance + Math.abs(i - 0) + Math.abs(j - 0);;
						 		break;
						 	case 2:
						 		totalDistance = totalDistance + Math.abs(i - 0) + Math.abs(j - 1);
						 		break;
						 	case 3:
						 		totalDistance = totalDistance + Math.abs(i - 0) + Math.abs(j - 2);
						 		break;
						 	case 4:
						 		totalDistance = totalDistance + Math.abs(i - 1) + Math.abs(j - 0);
						 		break;
						 	case 5:
						 		totalDistance = totalDistance + Math.abs(i - 1) + Math.abs(j - 1);
						 		break;
						 	case 6:
						 		totalDistance = totalDistance + Math.abs(i - 1) + Math.abs(j - 2);
						 		break;
						 	case 7:
						 		totalDistance = totalDistance + Math.abs(i - 2) + Math.abs(j - 0);
						 		break;
						 	case 8:
						 		totalDistance = totalDistance + Math.abs(i - 2) + Math.abs(j - 1);
						 		break;
						 	default:
						 		break;
						 }	 
					 }
				 }
		     }
	     }
		 return totalDistance;
	}
		
	public static void hashCode(State state) {
		
		int hash = 0;
	    for (int i = 0; i < state.state.length; i++)
	        for (int j = 0; j < state.state.length; j++)
	        {
	        	hash = hash * 10 + state.state[i][j];
	        }
	   state.hash = hash;
	}
	
	//print a trace
	public static void printSolution(State state) {
		System.out.printf("\nSolution: \n");
		//solution vector
		Vector<State> solution = new Vector<State>();
		
		while(state != null)
		{	
			solution.add(state);
			state = state.parent;
		}
		for(int k = solution.size()-1; k >= 0; k--)
		{
			printState(solution.elementAt(k));
		}	
	}
}
//For ordering the heap
//Idea copied from https://www.geeksforgeeks.org/implement-priorityqueue-comparator-java/
class StateComparator implements Comparator<State>{
	public int compare(State s1, State s2) {
		if (s1.g + s1.h < s2.g + s2.h) 
			return -1; 
      else if (s1.g + s1.h > s2.g + s1.h) 
          return 1;
		return 0;
	} 
}