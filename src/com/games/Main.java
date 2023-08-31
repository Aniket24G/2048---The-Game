package com.games;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.nio.Buffer;

public class Main extends Canvas implements Runnable {

    public static final int width = 400,height=400;
    public static float scale = 1.5F;

    public JFrame frame;
    public Thread thread;
    public Keyboard key;
    public Game game;
    public boolean running = false;

    public static BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
    public static int[]pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

    public Main(){
        setPreferredSize(new Dimension((int) (width*scale),(int)(height*scale)));
        frame = new JFrame();
        game = new Game();
        key = new Keyboard();
        addKeyListener(key);
    }

    public void start(){
        running = true;
        thread = new Thread(this,"loopThread");
        thread.start();
    }

    public void stop(){
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        long lastTimeInNanoSeconds = System.nanoTime();
        long timer = System.currentTimeMillis();
        double nanoSecondsPerUpdate = 1000000000.0 / 60.0;
        double updateToPerform = 0.0;
        int frames = 0;
        int updates = 0;
        requestFocus();

        while (running) {
            long currentTimeInNAanoSeconds = System.nanoTime();
            updateToPerform += (currentTimeInNAanoSeconds - lastTimeInNanoSeconds) / nanoSecondsPerUpdate;
            if (updateToPerform >= 1) {
            update();
            updates++;
            updateToPerform--;
            }
            lastTimeInNanoSeconds = currentTimeInNAanoSeconds;
            render();
            frames++;

            if(System.currentTimeMillis() - timer > 1000){
                frame.setTitle("2048 " + updates + " updates " + frames+" frames");
                updates=0;
                frames=0;
                timer+=1000;
            }
        }
    }

    public void update(){
        game.update();
        key.update();
    }

    public void render(){
        BufferStrategy bs = getBufferStrategy();
        if(bs==null){
            createBufferStrategy(3);
            return;
        }
        game.render();

        Graphics2D g = (Graphics2D) bs.getDrawGraphics();
        g.drawImage(image,0,0,(int)(width*scale),(int)(height*scale),null);
        game.renderText(g);
        g.dispose();
        bs.show();
    }

    public static void main(String[] args) {

        Main m =  new Main();
        m.frame.setResizable(false);
        m.frame.setTitle("2048 - The Game");
        m.frame.add(m);
        m.frame.pack();
        m.frame.setVisible(true);
        m.frame.setLocationRelativeTo(null);
        m.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        m.frame.setAlwaysOnTop(true);
        m.start();

    }
}
