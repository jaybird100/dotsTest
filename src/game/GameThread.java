package game;

import minMax.MinMax;
import minMax.Node;

import java.awt.*;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;

import static game.Graph.*;

public class GameThread extends Thread{
    static int minMaxCounter=0;
    public void run(){
        try{
            while(!checkFinished()) {
                if(allWaysReplay){
                    sleep(30);
                }
                if(Graph.getSleep()>0) {
                    sleep(500*Graph.getSleep());
                }
                availableLines=availCheck(Graph.getAvailableLines());
                if (Graph.getActivateBaseBot() && Graph.player1Turn == Graph.getBaseBotPlayer1()) {
                    if(Graph.getSleep()>0) {
                        sleep(250*Graph.getSleep());
                    }
                    if(allWaysReplay){
                        sleep(15);
                    }
                    
                    
                    Graph.getRandomBot().placeRandomEdge();
//                    Graph.getMCTS().placeEdge();
                }
                if(isBaseBotPlusOn()&& isBaseBotPlusP1()==Graph.getPlayer1Turn()){
                    long start =System.nanoTime();
                    Graph.baseB.getEdge();
                    long stop = System.nanoTime();
                    //   System.out.println("B: "+((stop-start)/1000000));
                }
                if(bothMinMax){
                    long start =System.nanoTime();
                    minMaxCounter++;
                    Node state;
                    if(Graph.isMiniMaxP1()) {
                        state = new Node(Node.matrixCopy(Graph.getMatrix()), Graph.getPlayer1Score(), Graph.getPlayer2Score(), Node.avCopy(Graph.getAvailableLines()), true, false, false, null,doubleCross);
                    }else{
                        state = new Node(Node.matrixCopy(Graph.getMatrix()), Graph.getPlayer2Score(), Graph.getPlayer1Score(), Node.avCopy(Graph.getAvailableLines()), true, false, false, null,doubleCross);
                    }
                    // placeEdge(t.minMaxFunction(state,3,true).move);
                    int minMaxDepth;
                    if(availableLines.size()<20){
                        minMaxDepth=Math.max(availableLines.size(),10);
                    }else{
                        minMaxDepth=10;
                    }
                    boolean contin=true;
                    long minMaxExpansion;
                    if(minMaxCounter<3){
                        minMaxExpansion=(minMaxNodesExpansion/(12/(minMaxCounter*2)));
                    }else {
                        minMaxExpansion = minMaxNodesExpansion;
                    }
                    while(contin){
                        minMaxDepth--;
                        BigInteger num = factorial(BigInteger.valueOf(availableLines.size())).divide(factorial(BigInteger.valueOf(availableLines.size()-minMaxDepth)));
                        // System.out.println("MinMaxNodes: "+minMaxDepth+" NUM: "+num+" < "+ minMaxNodesExpansion);
                        if(num.compareTo(BigInteger.valueOf(minMaxExpansion))==-1){
                            contin=false;
                        }
                    }
                    actualMinMaxDepth=minMaxDepth;
                    //System.out.println("minMaxDepth: "+minMaxDepth);
                    placeEdge(t.alphaBeta(state, minMaxDepth, -1000000, 1000000, true).move);
                    long stop = System.nanoTime();
                    System.out.println("MM: "+((stop-start)/1000000));
                }
                if(Graph.isMiniMax()&&Graph.isMiniMaxP1()== player1Turn){
                    minMaxCounter++;
                    long start =System.nanoTime();
                    Node state;
                    if(Graph.isMiniMaxP1()) {
                        state = new Node(Node.matrixCopy(Graph.getMatrix()), Graph.getPlayer1Score(), Graph.getPlayer2Score(), Node.avCopy(Graph.getAvailableLines()), true, false, false, null,doubleCross);
                    }else{
                        state = new Node(Node.matrixCopy(Graph.getMatrix()), Graph.getPlayer2Score(), Graph.getPlayer1Score(), Node.avCopy(Graph.getAvailableLines()), true, false, false, null,doubleCross);
                    }
                    // placeEdge(t.minMaxFunction(state,3,true).move);
                    int minMaxDepth;
                    if(availableLines.size()<20){
                        minMaxDepth=Math.max(availableLines.size(),10);
                    }else{
                        minMaxDepth=10;
                    }
                    boolean contin=true;
                    long minMaxExpansion;
                    if(minMaxCounter<3){
                        minMaxExpansion=(minMaxNodesExpansion/(12/(minMaxCounter*2)));
                    }else {
                        minMaxExpansion = minMaxNodesExpansion;
                    }
                    while(contin){
                        minMaxDepth--;
                        BigInteger num = factorial(BigInteger.valueOf(availableLines.size())).divide(factorial(BigInteger.valueOf(availableLines.size()-minMaxDepth)));
                        // System.out.println("MinMaxNodes: "+minMaxDepth+" NUM: "+num+" < "+ minMaxNodesExpansion);
                        if(num.compareTo(BigInteger.valueOf(minMaxExpansion))==-1){
                            contin=false;
                        }
                    }
                    actualMinMaxDepth=minMaxDepth;
                    System.out.println("minMaxDepth: "+minMaxDepth);
                    placeEdge(t.alphaBeta(state, minMaxDepth, -1000000, 1000000, true).move);
                    MinMax.counter =0;
                    long stop = System.nanoTime();
                    System.out.println("MM: "+((stop-start)/1000000));
                }
                if(completelyRandom){
                    random();
                }
                if (Graph.isMCTS()&& Graph.player1Turn == Graph.isMCTSP1()) {
                    if(Graph.getSleep()>0) {
                        sleep(250*Graph.getSleep());
                    }
                    if(allWaysReplay){
                        sleep(15);
                    }
                    
                    
                    Graph.getMCTS().placeEdge();
                }
                if(completelyRandom&&randP1==Graph.getPlayer1Turn()){
                    random();
                }
                if (Graph.getNumOfMoves() < 1) {
                    Graph.setNumOfMoves(1);
                    if (Graph.player1Turn) {
                        Graph.player1Turn = false;
                    } else {
                        Graph.player1Turn = true;
                    }
                }
                
            }
                try {
                    Graph.getScreen().toggle();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void random() {
        ELine line = Graph.getAvailableLines().get((int)(Math.random()*availableLines.size()));
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
            }
            // if every counterBox has been activated, the game is over
        } else {
            Graph.setNumOfMoves(0);
            // switches turn. If randomBot is active switches to their turn.
        }
    }
    public static BigInteger factorial(BigInteger number) {
        BigInteger result = BigInteger.valueOf(1);

        for (long factor = 2; factor <= number.longValue(); factor++) {
            result = result.multiply(BigInteger.valueOf(factor));
        }

        return result;
    }

    public static boolean checkFinished(){
        for(ScoreBox box: Graph.getCounterBoxes()){
            if(!box.getActivated()){
                return false;
            }
        }
        return true;
    }
    public static void placeEdge(ELine line) throws InterruptedException {
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
            if(boxes.size()>1){
                doubleCross=!doubleCross;
            }
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
            }
            // if every counterBox has been activated, the game is over
        } else {
            Graph.setNumOfMoves(0);
            // switches turn. If randomBot is active switches to their turn.
        }
    }
    public static void clickEdge(int index) throws InterruptedException {
//    	System.out.println("Check finished is: "+checkFinished());
        ELine line = Graph.getAvailableLines().get(index);
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
            if(boxes.size()>1){
                doubleCross=!doubleCross;
            }
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
            }
            // if every counterBox has been activated, the game is over
        } else {
            Graph.setNumOfMoves(0);
            // switches turn. If randomBot is active switches to their turn.
        }
    }
    // checks to find the matching box in counterBoxes through their average x and y coordinates, then displays it.
    public static void checkMatching(ArrayList<Vertex> box){
        int avgX=0;
        int avgY=0;
        for(Vertex v:box){
            avgX+=v.getWidth();
            avgY+=v.getHeight();
        }
        avgX=avgX/4;
        avgY=avgY/4;
        for(ScoreBox sc: Graph.getCounterBoxes()){
            if(sc.getAvgX()==avgX&&sc.getAvgY()==avgY){
                sc.setText();
            }
        }
    }
    // checks whether an edge creates a box, through the adjacency matrix
    public static ArrayList<ArrayList<Vertex>> checkBox(ELine line){
        ArrayList<ArrayList<Vertex>> listOfBoxes = new ArrayList<>();
        if(line.getHorizontal()){
            if(line.vertices.get(0).getUpVertex()!=null){
                if(Graph.getMatrix()[line.vertices.get(0).getID()][line.vertices.get(0).getUpVertex().getID()]==2&&Graph.getMatrix()[line.vertices.get(1).getID()][line.vertices.get(1).getUpVertex().getID()]==2&&Graph.getMatrix()[line.vertices.get(0).getUpVertex().getID()][line.vertices.get(1).getUpVertex().getID()]==2){
                    ArrayList<Vertex> box = new ArrayList<>();
                    box.add(line.vertices.get(0));
                    box.add(line.vertices.get(1));
                    box.add(line.vertices.get(0).getUpVertex());
                    box.add(line.vertices.get(1).getUpVertex());
                    listOfBoxes.add(box);
                }
            }
            if(line.vertices.get(0).getDownVertex()!=null){
                if(Graph.getMatrix()[line.vertices.get(0).getID()][line.vertices.get(0).getDownVertex().getID()]==2&&Graph.getMatrix()[line.vertices.get(1).getID()][line.vertices.get(1).getDownVertex().getID()]==2&&Graph.getMatrix()[line.vertices.get(0).getDownVertex().getID()][line.vertices.get(1).getDownVertex().getID()]==2){
                    ArrayList<Vertex> box2 = new ArrayList<>();
                    box2.add(line.vertices.get(0));
                    box2.add(line.vertices.get(1));
                    box2.add(line.vertices.get(0).getDownVertex());
                    box2.add(line.vertices.get(1).getDownVertex());
                    listOfBoxes.add(box2);
                }
            }
        }else{
            if(line.vertices.get(0).getRightVertex()!=null){
                if(Graph.getMatrix()[line.vertices.get(0).getID()][line.vertices.get(0).getRightVertex().getID()]==2&&Graph.getMatrix()[line.vertices.get(1).getID()][line.vertices.get(1).getRightVertex().getID()]==2&&Graph.getMatrix()[line.vertices.get(0).getRightVertex().getID()][line.vertices.get(1).getRightVertex().getID()]==2){
                    ArrayList<Vertex> box3 = new ArrayList<>();
                    box3.add(line.vertices.get(0));
                    box3.add(line.vertices.get(1));
                    box3.add(line.vertices.get(0).getRightVertex());
                    box3.add(line.vertices.get(1).getRightVertex());
                    listOfBoxes.add(box3);
                }
            }
            if(line.vertices.get(0).getLeftVertex()!=null){
                if(Graph.getMatrix()[line.vertices.get(0).getID()][line.vertices.get(0).getLeftVertex().getID()]==2&&Graph.getMatrix()[line.vertices.get(1).getID()][line.vertices.get(1).getLeftVertex().getID()]==2&&Graph.getMatrix()[line.vertices.get(0).getLeftVertex().getID()][line.vertices.get(1).getLeftVertex().getID()]==2){
                    ArrayList<Vertex> box4 = new ArrayList<>();
                    box4.add(line.vertices.get(0));
                    box4.add(line.vertices.get(1));
                    box4.add(line.vertices.get(0).getLeftVertex());
                    box4.add(line.vertices.get(1).getLeftVertex());
                    listOfBoxes.add(box4);
                }
            }
        }
        // if it creates no boxes, return null.
        if(listOfBoxes.isEmpty()){
            return null;
        }
        return listOfBoxes;
    }
}
