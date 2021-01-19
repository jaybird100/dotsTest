package MCTS;

import game.ELine;
import game.Graph;
import game.Vertex;

import java.awt.*;
import java.util.ArrayList;

import static game.GameThread.checkBox;
import static game.GameThread.checkMatching;
import static game.Graph.availableLines;

public class MCTSTree {
    private MCTSNode root;
    private ArrayList<MCTSNode> treeNodes = new ArrayList<MCTSNode>();
    private ArrayList<MCTSNode> path = new ArrayList<MCTSNode>();
    
    private int runs=10000;
    private int numberOfSims=0;

    /***Constructor method that creates a tree from the first turn of a bot.
     * 
     * @param matrix The board 
     * @param score1 current score of player 1 (opponent)
     * @param score2 current score of bot
     * @param botsTurn indicate if its the bots turn
     * @param inputAvailLines a list that holds all the available lines that can be drawn.
     */
    
    public MCTSTree() {}
    
    public void initialize(int[][] matrix, int score1, int score2, boolean botsTurn, ArrayList<ELine> inputAvailLines){
        root = new MCTSNode(new State((int[][]) matrix.clone(), score1, score2, botsTurn, (ArrayList<ELine>) inputAvailLines.clone()));
        treeNodes.add(root);
        generateChildren(root);

//    	System.out.println("MCTS Active");
    }
    
    public MCTSTree(int[][] matrix, int score1, int score2, boolean botsTurn, ArrayList<ELine> inputAvailLines){
        root = new MCTSNode(new State((int[][]) matrix.clone(), score1, score2, botsTurn, (ArrayList<ELine>) inputAvailLines.clone()));
        treeNodes.add(root);
        generateChildren(root);

//    	System.out.println("MCTS Active");
    }

    /***
     * Method that generates children for a certain node.
     * 
     * @param parent the node we want to generate children for
     */
    private void generateChildren(MCTSNode parent) {
    	ArrayList<State> chil= State.getStates(parent.getState());
    	for(int i=0; i<chil.size();i++) {
    		MCTSNode baby=new MCTSNode(chil.get(i));
//    		System.out.println(baby.getState().getScore1()+"__"+baby.getState().getScore2()+"_a_");
    		parent.addChild(baby);
    		baby.setParent(parent);
    		treeNodes.add(baby);
    	}
    }
    
    public void placeEdge() {
    	numberOfSims++;
    	ELine line;
    	 if(Graph.isMCTSP1()) {
    		 line = (getNextMove((int[][]) Graph.getMatrix().clone(), Graph.getPlayer2Score(), Graph.getPlayer1Score(), true, (ArrayList<ELine>) availableLines.clone()));
         }
         else {
        	 line = (getNextMove((int[][]) Graph.getMatrix().clone(), Graph.getPlayer1Score(), Graph.getPlayer2Score(), true, (ArrayList<ELine>) availableLines.clone()));
         }
    	 
        line.setActivated(true);
        // make it black
        line.setBackground(Color.BLACK);
        line.repaint();
        // set the adjacency matrix to 2, 2==is a line, 1==is a possible line
        Graph.matrix[line.vertices.get(0).getID()][line.vertices.get(1).getID()] = 2;
        Graph.matrix[line.vertices.get(1).getID()][line.vertices.get(0).getID()] = 2;
        // gets an arrayList of each box the ELine creates. The box is an arrayList of 4 vertices.
        ArrayList<ArrayList<Vertex>> boxes = checkBox(line);
        if (boxes != null) {
//        	System.out.println("boxes isnt null, size: "+boxes.size());
            for (ArrayList<Vertex> box : boxes) {
                // looks through the counterBoxes arrayList and sets the matching one visible.
                checkMatching(box);
                // updates the score board
                if (Graph.getPlayer1Turn()) {
                    Graph.setPlayer1Score(Graph.getPlayer1Score()+1);
                    Graph.getScore1().setScore();
                } else {
                    Graph.setPlayer2Score(Graph.getPlayer2Score()+1);
                    Graph.getScore2().setScore();
                }
//                Graph.setNumOftrueMoves(0);
            }
        } else {
            Graph.setNumOfMoves(0);
        }
        System.out.println("Tree size is: "+treeNodes.size());
    }
    
    public ELine getNextMove(int[][] matrix, int score1, int score2, boolean botsTurn, ArrayList<ELine> inputAvailLines) {
//    	System.out.println("We reached here");
    	MCTSNode O = new MCTSNode(new State((int[][]) Graph.getMatrix().clone(), score1, score2, botsTurn, (ArrayList<ELine>) inputAvailLines.clone()));

//    	System.out.println("I am looking for state");
    	
    	int c =inTree(O);
    	if(c==-1) {
    		root = O;
    	}
    	else {
    		root= (MCTSNode )treeNodes.get(c);
    	}
    	
    	updateTree();
//    	System.out.println("I am simulating");
    	simulateGames();

//    	System.out.println("I finished simulation, getting best move");
    	
    	MCTSNode next = getBestMove();
    	

//    	System.out.println("I got best move!!");
    	
    	
    	ELine nextEdge = root.getState().difference(next.getState());
    	
//    	System.out.println("Best move is: "+nextEdge);
    	return nextEdge;
    }
    
    public void updateTree() {
    	treeNodes = new ArrayList<>();
    	addNodes(root);
    }
    
    private void addNodes(MCTSNode n) {
    	treeNodes.add(n);
    	for(MCTSNode baby: n.getChildren()) {
    		addNodes(baby);
    	}
    }
    /***
     * This method return the node with the highest value
     * @return MCTSNode next, with the node that represents our next best possible state
     */
    private MCTSNode getBestMove() {
    	double best=-1;
    	int n=numberOfSims*runs;
    	MCTSNode next=null;
    	for(int i=0; i<root.getChildren().size(); i++) {
    		double c=root.getChildren().get(i).getValue(n);
//    		System.out.println("C is "+c);
//        	System.out.println(root.getChildren().get(i).getWon());
    		if(c > best) best=c; next=root.getChildren().get(i);
    	}
//    	System.out.println("Best is: "+best);
    	return next;
    }
    
    /***
     *Method to simulate (runs) number of possible games.
     */
    public void simulateGames() {
    	//Time limit to moves
    	/*long start =System.nanoTime();
    	long end=0;
    	while((end-start)/1000000 <1000) {
    		path = new ArrayList<MCTSNode>();
    		selection();
    		end=System.nanoTime();
    	}*/
    	//number limit to simulations
    	for(int i=0; i<runs; i++) {
    		path = new ArrayList<MCTSNode>();
    		selection();
    	}
    }
    
    /***
     * Method that simulates a game by choosing random moves
     */
    private void selection() {
    	MCTSNode currentNode = root;
    	path.add(currentNode);
    	while(currentNode.hasChildren()) {
    		if(currentNode.getChildren().size()==0) {
    			//Generating the tree(expansion)
    			generateChildren(currentNode);
    		}
    		int ra= (int) (Math.random() * (currentNode.getChildren().size()));
//    		System.out.println(currentNode.getChildren().size());
//    		System.out.println("Size: "+root.getChildren().size()+" Choice:"+ra+" rand "+Math.random());
    		currentNode = currentNode.getChildren().get(ra);
    		path.add(currentNode);
    	}
    	//Here we have reached an end game and want to know who won the game
    	boolean win = simulation(currentNode);
//    	System.out.println(win);
    	backTrack(currentNode, win);
    }
    
    /***
     * Method that returns who is winning in a specific game.
     * @param currentNode the node we want to find out who is winning at
     * @return true if bot is winning, false otherwise
     */
    private boolean simulation(MCTSNode currentNode) {
//    	System.out.println(currentNode.getState().getScoreTotal());
    	if(currentNode.getState().getScoreTotal()>=0) return true;
    	else return false;
    }
    
    /***
     * Method to backtrack through our expansion and update their values.
     * 
     * @param currentNode
     * @param win
     */
    private void backTrack(MCTSNode currentNode, boolean win) {
//    	int i=0;
    	/*while(currentNode != root) {
    		System.out.println("Update "+(++i)+" Score is "+currentNode.getState().getScore1()+" - "+currentNode.getState().getScore2());
    		currentNode.update(win);
    		currentNode=currentNode.getParent();
    	}*/
    	for(int i=0;i<path.size();i++) {
    		path.get(i).update(win);
    	}
    }
    
    /***
     * Method that returns the number of moves the bot has played so far in this game
     * 
     * @return
     */

    private int inTree(MCTSNode O){
        for(int i =0; i < treeNodes.size();i++){
            if(treeNodes.get(i).equals(O)){
                return i;
            }
        }
        return -1;
    }

}
