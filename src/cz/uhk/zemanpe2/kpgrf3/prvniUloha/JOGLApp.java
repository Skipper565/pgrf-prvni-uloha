package cz.uhk.zemanpe2.kpgrf3.prvniUloha;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.SwingUtilities;

public class JOGLApp {

    private static final int FPS = 60; // animator's target frames per second

    private void start() {
        try {
            Frame testFrame = new Frame("TestFrame");
            testFrame.setSize(512, 384);

            // setup OpenGL version
            GLProfile profile = GLProfile.getMaxFixedFunc(true);
            GLCapabilities capabilities = new GLCapabilities(profile);

            // The canvas is the widget that's drawn in the JFrame
            GLCanvas canvas = new GLCanvas(capabilities);
            Renderer ren = new Renderer(canvas);
            canvas.addGLEventListener(ren);
            canvas.addMouseListener(ren);
            canvas.addMouseMotionListener(ren);
            canvas.addKeyListener(ren);
            canvas.setSize( 512, 384 );


            testFrame.add(canvas);

            //shutdown the program on windows close event

            //final Animator animator = new Animator(canvas);
            final FPSAnimator animator = new FPSAnimator(canvas, FPS, true);

            testFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    new Thread(() -> {
                        if (animator.isStarted()) animator.stop();
                        System.exit(0);
                    }).start();
                }
            });
            testFrame.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {}

                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        System.exit(0);
                    }
                }

                @Override
                public void keyReleased(KeyEvent e) {}
            });
            testFrame.setTitle(ren.getClass().getName());
            testFrame.pack();
            testFrame.setVisible(true);
            animator.start(); // start the animation loop


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param args default args of main method
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new JOGLApp().start());
    }

}