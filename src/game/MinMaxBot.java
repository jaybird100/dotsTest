package game;

import algorithms.MinMax;
import algorithms.TreeGenerator;
import algorithms.TreeNode;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class MinMaxBot extends BaseBot {
    // places edges randomly except will always complete a box and won't set up boxes for the other player
    public MinMaxBot() {
    }

    // places the edge
    public static void placeEdge() {
        boolean stop = false;
        // init the game tree
        TreeNode root = TreeGenerator.generateTree(TreeGenerator.copyMatrix(Graph.getMatrix()), TreeGenerator.copyElines(Graph.getAvailableLines()));
        int chosen = MinMax.minMax(root, 2).getEdgeIndex();
        // effectively mirrors the actionListener in ELine.
        Graph.getAvailableLines().get(chosen).setActivated(true);
        Graph.getAvailableLines().get(chosen).setBackground(Color.BLACK);
        Graph.getAvailableLines().get(chosen).repaint();
        Graph.matrix[Graph.getAvailableLines().get(chosen).vertices.get(0).getID()][Graph.getAvailableLines().get(chosen).vertices.get(1).getID()] = 2;
        Graph.matrix[Graph.getAvailableLines().get(chosen).vertices.get(1).getID()][Graph.getAvailableLines().get(chosen).vertices.get(0).getID()] = 2;
        ArrayList<ArrayList<Vertex>> boxes = Graph.getAvailableLines().get(chosen).checkBox(Graph.getMatrix());
        if (boxes != null) {
            for (ArrayList<Vertex> box : boxes) {
                Graph.getAvailableLines().get(chosen).checkMatching(box);
                if (Graph.getPlayer1Turn()) {
                    Graph.setPlayer1Score(Graph.getPlayer1Score() + 1);
                    Graph.getScore1().setScore();
                } else {
                    Graph.setPlayer2Score(Graph.getPlayer2Score() + 1);
                    Graph.getScore2().setScore();
                }
            }
            if (Graph.getAvailableLines().get(chosen).checkFinished(Graph.getCounterBoxes())) {
                try {
                    Graph.getScreen().toggle();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                stop = true;
            }
            // if it completes a box, it gets to go again
            if (Graph.getBaseBotPlayer1() == Graph.getPlayer1Turn() && !stop) {
                stop = true;
                // removes the edge from availableLines
                if (Graph.getAvailableLines().size() > 0 && !(Graph.getAvailableLines().size() == 1 && chosen > 0)) {
                    Graph.getAvailableLines().remove(chosen);
                }
                Graph.getRandomBot().placeRandomEdge();
            }
        } else {
            Graph.setPlayer1Turn(!Graph.getPlayer1Turn());
        }
        // removes the edge from availableLines so long as it hasn't already removed the edge in the same method call.
        // e.g makes sure that repeated placeRandomEdge() calls won't remove double the edges when it completes a series of boxes
        if (Graph.getAvailableLines().size() > 0 && !(Graph.getAvailableLines().size() - 1 < chosen) && !stop) {
            Graph.getAvailableLines().remove(chosen);
        }
    }


}
