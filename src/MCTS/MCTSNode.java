package MCTS;

import java.util.ArrayList;

public class MCTSNode {
	
	private int visited=0;
	private int won=0;
	
	private double c = Math.sqrt(2);
	
	private State state;
	
	private MCTSNode parent=null;
	
	private ArrayList<MCTSNode> children= new ArrayList<MCTSNode>();
	
	
	public MCTSNode(State s) {
		this.state=s;
	}

	public void setParent(MCTSNode parent) {this.parent=parent;}
	
	public void addChild(MCTSNode baby) {
		children.add(baby);
//		baby.setParent(this);
	}
	
	public MCTSNode getParent() {return this.parent;}
	
	public double getValue(int N) {
		if(visited==0 || won==0) return 0;
//		System.out.println("N: "+N+" won: "+won+" visited: "+visited+" ratio is: "+(((double) won)/(((double) visited))));
		double x= (double) won;
		double y= (double) visited;
		double value= x/y;
//		System.out.println("Value is "+value);
		value= (x/y)+ c*Math.sqrt((Math.log(N)/x));
		return value;
//		return (won/visited);
		}
		
	public int getVisited() {return this.visited;}
	
	public int getWon() {return this.won;}
	
	public State getState() {return this.state;}
	
	public ArrayList<MCTSNode> getChildren(){return this.children;}
	
	public boolean hasChildren() {
		//If we can still place lines then this node has children
		if(state.getAvailLines().size()==0) return false;
		return true;
	}
	
	public void update(boolean win) {
		visited++;
		if(win) {
			won++;
			}
//		System.out.println("Result is: "+win+" and Visited: "+visited+" Won: "+won);
	}
	
	public boolean equals(Object other) {
		if(other == null) return false;
		if(other.getClass().getName() != "MCTSNode") return false;
		MCTSNode o = (MCTSNode) other;
		if(o.getState().equals(state)) return true;
		return false;
	}
}
