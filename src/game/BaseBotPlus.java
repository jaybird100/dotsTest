package game;

import minMax.Node;

import java.awt.*;
import java.util.ArrayList;

import static game.Graph.doubleCross;
import static game.Graph.player1Turn;

public class BaseBotPlus {
    public void getEdge(){
        BaseBotPlusNode state;
        if(player1Turn){
            state = new BaseBotPlusNode(Node.matrixCopy(Graph.getMatrix()), Graph.getPlayer1Score(), Graph.getPlayer2Score(), Node.avCopy(Graph.getAvailableLines()), true, false, false, null, doubleCross);
        }else{
            state = new BaseBotPlusNode(Node.matrixCopy(Graph.getMatrix()), Graph.getPlayer2Score(), Graph.getPlayer1Score(), Node.avCopy(Graph.getAvailableLines()), true, false, false, null,doubleCross);
        }
        double max=-1*Double.MAX_VALUE;
        ArrayList<ELine> options = new ArrayList<>();
        for (ELine v : Graph.availableLines) {
            BaseBotPlusNode temp = state.performMove(v);
            temp.setParent(state);
            double val = temp.evaluation();
            if(val>max){
                options=new ArrayList<>();
                max=val;
            }
            if(val==max){
                options.add(temp.move);
            }
        }
        ELine line=options.get((int)(Math.random()*options.size()));
        line.setActivated(true);
        // make it black
        if(player1Turn) {
            line.setBackground(Color.RED);
        }else{
            line.setBackground(Color.BLUE);
        }
        line.repaint();
        // set the adjacency matrix to 2, 2==is a line, 1==is a possible line
        Graph.matrix[line.vertices.get(0).getID()][line.vertices.get(1).getID()] = 2;
        Graph.matrix[line.vertices.get(1).getID()][line.vertices.get(0).getID()] = 2;
        // gets an arrayList of each box the ELine creates. The box is an arrayList of 4 vertices.
        ArrayList<ArrayList<Vertex>> boxes = GameThread.checkBox(line);
        if (boxes != null) {
            if(boxes.size()>1){
                doubleCross=!doubleCross;
            }
            for (ArrayList<Vertex> box : boxes) {
                // looks through the counterBoxes arrayList and sets the matching one visible.
                GameThread.checkMatching(box);
                // updates the score board
                if (Graph.getPlayer1Turn()) {
                    Graph.setPlayer1Score(Graph.getPlayer1Score()+1);
                    Graph.getScore1().setScore();
                } else {
                    Graph.setPlayer2Score(Graph.getPlayer2Score()+1);
                    Graph.getScore2().setScore();
                }
            }
            // if every counterBox has been activated, the game is over
        } else {
            Graph.setNumOfMoves(0);
            // switches turn. If randomBot is active switches to their turn.
        }
    }


}
