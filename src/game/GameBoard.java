package game;


import graphics.Paths;
import minMax.MinMax;

import javax.swing.*;
import javax.swing.plaf.LayerUI;
import java.io.IOException;

public class GameBoard{
    // Overall launcher for the game
    private JFrame frame;
    // Graph is the background of the game
    private static Graph graph;
    // paintBoard is the JPanel for the edges, score counter and score boxes
    private PaintBoard panel;
    public GameBoard() throws IOException, InterruptedException {
        if(!Graph.allWaysReplay) {
            frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            graph = new Graph(Graph.getHeight(), Graph.getWidth(), frame);
            graph.createGraph();
            panel = new PaintBoard(graph);
            // dotDrawer draws the dots over the edges, I used layerUI because it draws over JLabels.
            LayerUI<JComponent> layerUI = new DotDrawer();
            JLayer<JComponent> jlayer = new JLayer<JComponent>(panel, layerUI);
            frame.setSize(Paths.FRAME_WIDTH, Paths.FRAME_HEIGHT);
            frame.setResizable(false);
            frame.add(jlayer);
            frame.setVisible(true);
            // activate randomBot
        }else{
            graph = new Graph(Graph.getHeight(), Graph.getWidth(), frame);
            graph.createGraph();
        }
        GameThread thread = new GameThread();
        thread.start();
    }
    public GameBoard(int h, int w) throws IOException, InterruptedException {
        if(!Graph.allWaysReplay) {
            frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            graph = new Graph(h, w, frame);
            graph.createGraph();
            panel = new PaintBoard(graph);
            // dotDrawer draws the dots over the edges, I used layerUI because it draws over JLabels.
            LayerUI<JComponent> layerUI = new DotDrawer();
            JLayer<JComponent> jlayer = new JLayer<JComponent>(panel, layerUI);
            frame.setSize(Paths.FRAME_WIDTH, Paths.FRAME_HEIGHT);
            frame.setResizable(false);
            frame.add(jlayer);
            frame.setVisible(true);
            // activate randomBot
        }else{
            graph = new Graph(h, w, frame);
            graph.createGraph();
        }
        GameThread thread = new GameThread();
        thread.start();
    }
    public GameBoard(int h, int w, MinMax t, int simulations, int nodesExpansion, boolean first) throws IOException {
        graph = new Graph(h, w, frame);
        Graph.setSims(simulations);
        Graph.minMaxNodesExpansion=nodesExpansion;
        Graph.setMiniMaxP1(first);
        Graph.setMiniMax(true);
        Graph.setBaseBotPlayer1(!first);
        Graph.setActivateBaseBot(true);
        Graph.setAllWaysReplay(true);
        Graph.GASim =true;
        Graph.sleep=0;
        graph.createGraph();
        Graph.setT(t);
        GameThread thread = new GameThread();
        thread.start();
    }

}
