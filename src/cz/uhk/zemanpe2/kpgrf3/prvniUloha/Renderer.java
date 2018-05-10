package cz.uhk.zemanpe2.kpgrf3.prvniUloha;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL2GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;

import java.awt.event.*;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Renderer implements GLEventListener, MouseListener, MouseMotionListener, KeyListener {

    private int width;
    private int height;

    private int[] vertexBuffer = new int[1];
    private int[] indexBuffer = new int[1];

    @Override
    public void init(GLAutoDrawable glAutoDrawable) {
        GL2GL3 gl = glAutoDrawable.getGL().getGL2GL3();

        System.out.println("Init GL is " + gl.getClass().getName());
        System.out.println("OpenGL version " + gl.glGetString(GL2GL3.GL_VERSION));
        System.out.println("OpenGL vendor " + gl.glGetString(GL2GL3.GL_VENDOR));
        System.out
                .println("OpenGL renderer " + gl.glGetString(GL2GL3.GL_RENDERER));
        System.out.println("OpenGL extension "
                + gl.glGetString(GL2GL3.GL_EXTENSIONS));

        createBuffers(gl);

        // set render mode GL_FILL is default, other possibility GL_LINE or GL_POINT
        gl.glPolygonMode(GL2GL3.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);
    }

    private void createBuffers(GL2GL3 gl) {
        // create and fill vertex buffer data
        float[] vertexBufferData = {
                -1, -1,
                1, 0,
                0, 1
        };
        // create buffer required for sending data to a native library
        FloatBuffer vertexBufferBuffer = Buffers
                .newDirectFloatBuffer(vertexBufferData);

        gl.glGenBuffers(1, vertexBuffer, 0);
        gl.glBindBuffer(GL2GL3.GL_ARRAY_BUFFER, vertexBuffer[0]);
        gl.glBufferData(GL2GL3.GL_ARRAY_BUFFER, vertexBufferData.length * 4,
                vertexBufferBuffer, GL2GL3.GL_STATIC_DRAW);

        // create and fill index buffer data (element buffer in OpenGL terminology)
        short[] indexBufferData = { 0, 1, 2 };

        // create buffer required for sending data to a native library
        ShortBuffer indexBufferBuffer = Buffers
                .newDirectShortBuffer(indexBufferData);

        gl.glGenBuffers(1, indexBuffer, 0);
        gl.glBindBuffer(GL2GL3.GL_ELEMENT_ARRAY_BUFFER, indexBuffer[0]);
        gl.glBufferData(GL2GL3.GL_ELEMENT_ARRAY_BUFFER,
                indexBufferData.length * 2, indexBufferBuffer,
                GL2GL3.GL_STATIC_DRAW);
    }

    @Override
    public void display(GLAutoDrawable glDrawable) {
        GL2GL3 gl = glDrawable.getGL().getGL2GL3();
        gl.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        gl.glClear(GL2GL3.GL_COLOR_BUFFER_BIT | GL2GL3.GL_DEPTH_BUFFER_BIT);

        // set the current shader to be used, could have been done only once (in
        // init) in this sample (only one shader used)
        gl.glUseProgram(0);
        // to use the default shader of the "fixed pipeline", call
        // gl.glUseProgram(0);


        // bind the vertex and index buffer to shader, could have been done only
        // once (in init) in this sample (only one geometry used)
        bindBuffers(gl);
        // draw
        gl.glDrawElements(GL2GL3.GL_TRIANGLES, 3, GL2GL3.GL_UNSIGNED_SHORT, 0);

    }

    private void bindBuffers(GL2GL3 gl) {
        // internal OpenGL ID of a vertex shader input variable
        int locPosition = gl.glGetAttribLocation(0, "inPosition");

        gl.glBindBuffer(GL2GL3.GL_ARRAY_BUFFER, vertexBuffer[0]);
        // bind the shader variable to specific part of vertex data (attribute)
        // - describe how many components of which type correspond to it in the
        // data, how large is one vertex (its stride in bytes) and at which byte
        // of the vertex the first component starts
        // 2 components, of type float, do not normalize (convert to [0,1]),
        // vertex of 8 bytes, start at the beginning (byte 0)
        gl.glVertexAttribPointer(locPosition, 2, GL2GL3.GL_FLOAT, false, 8, 0);
        gl.glEnableVertexAttribArray(locPosition);
        gl.glBindBuffer(GL2GL3.GL_ELEMENT_ARRAY_BUFFER, indexBuffer[0]);
    }

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) {
    }

    @Override
    public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

}
