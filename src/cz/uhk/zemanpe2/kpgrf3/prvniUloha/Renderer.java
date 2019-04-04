package cz.uhk.zemanpe2.kpgrf3.prvniUloha;

import com.jogamp.opengl.GL2GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;

import oglutils.OGLBuffers;
import oglutils.OGLTextRenderer;
import oglutils.OGLUtils;
import oglutils.ShaderUtils;
import transforms.Camera;
import transforms.Mat4;
import transforms.Mat4PerspRH;
import transforms.Vec3D;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public class Renderer implements GLEventListener, MouseListener,
        MouseMotionListener, KeyListener {

    private int width, height;

    private OGLBuffers buffers;
    private OGLTextRenderer textRenderer;

    private int shaderProgram, locTime, locView, locProj;

    private float time = 0;
    private Mat4 proj;
    private Camera camera;
    private int mx, my;

    @Override
    public void init(GLAutoDrawable glDrawable) {
        GL2GL3 gl = glDrawable.getGL().getGL2GL3();
        OGLUtils.shaderCheck(gl);

        OGLUtils.printOGLparameters(gl);

        textRenderer = new OGLTextRenderer(gl, glDrawable.getSurfaceWidth(), glDrawable.getSurfaceHeight());

        shaderProgram = ShaderUtils.loadProgram(gl, "/start.vert",
                "/start.frag",
                null,null,null,null);

        //shorter version of loading shader program
        //shaderProgram = ShaderUtils.loadProgram(gl, "/start");

        buffers = GridFactory.generateGrid(gl, 100, 100);

        camera = new Camera()
                .withPosition(new Vec3D(0, 0, 0))
                .addAzimuth(5 / 4. * Math.PI)
                .addZenith(-1 / 5. * Math.PI)
                .withFirstPerson(false)
                .withRadius(5);

        locTime = gl.glGetUniformLocation(shaderProgram, "time");
        locProj = gl.glGetUniformLocation(shaderProgram, "proj");
        locView = gl.glGetUniformLocation(shaderProgram, "view");
    }

    @Override
    public void display(GLAutoDrawable glDrawable) {
        GL2GL3 gl = glDrawable.getGL().getGL2GL3();

        gl.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        gl.glClear(GL2GL3.GL_COLOR_BUFFER_BIT | GL2GL3.GL_DEPTH_BUFFER_BIT);

        gl.glUseProgram(shaderProgram);
        time += 0.1;
        gl.glUniform1f(locTime, time);
        gl.glUniformMatrix4fv(locView, 1, false, camera.getViewMatrix().floatArray(), 0);
        gl.glUniformMatrix4fv(locProj, 1, false, proj.floatArray(), 0);

        buffers.draw(GL2GL3.GL_TRIANGLES, shaderProgram);

        String text = this.getClass().getName();
        textRenderer.drawStr2D(3, height - 20, text);
        textRenderer.drawStr2D(width - 90, 3, " (c) PGRF UHK");
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        this.width = width;
        this.height = height;
        proj = new Mat4PerspRH(Math.PI / 4, height / (double) width, 0.01, 1000.0);
        textRenderer.updateSize(width, height);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        camera = camera.addAzimuth(Math.PI * (mx - e.getX()) / width);
        camera = camera.addZenith(Math.PI * (e.getY() - my) / width);
        mx = e.getX();
        my = e.getY();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void dispose(GLAutoDrawable glDrawable) {
        GL2GL3 gl = glDrawable.getGL().getGL2GL3();
        gl.glDeleteProgram(shaderProgram);
    }

}