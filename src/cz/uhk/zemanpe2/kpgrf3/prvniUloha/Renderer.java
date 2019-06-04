package cz.uhk.zemanpe2.kpgrf3.prvniUloha;

import com.jogamp.opengl.GL2GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;

import com.jogamp.opengl.awt.GLCanvas;
import oglutils.*;
import transforms.*;

import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public class Renderer implements GLEventListener, MouseListener,
        MouseMotionListener, KeyListener {

    private GLCanvas canvas;
    private Cursor defaultCursor;

    private int width, height;
    private boolean showLines = false;
    private boolean ortho = false;
    private boolean strip = false;
    private boolean mouseLook = false;
    private boolean circle = true;
    private int setting = 1;

    private OGLBuffers buffers;
    private OGLTextRenderer textRenderer;
    private OGLRenderTarget renderTarget;
    private OGLTexture2D.Viewer textureViewer;
    private OGLTexture2D texture;

    private int shaderProgram, locTime, locView, locProj, locVPLight, locLightPosition, locSetting;
    private int shaderProgramLight, locLightTime, locLightView, locLightProj, locLightSetting;

    private float time = 0;
    private Mat4 proj, projLight;
    private Camera camera, cameraLight;
    private int mx, my;
    private String text;
    private Robot robot;
    private Color color;

    Renderer(GLCanvas canvas) {
        this.canvas = canvas;
        this.defaultCursor = canvas.getCursor();
    }

    @Override
    public void init(GLAutoDrawable glDrawable) {
        GL2GL3 gl = glDrawable.getGL().getGL2GL3();
        OGLUtils.shaderCheck(gl);

        OGLUtils.printOGLparameters(gl);
        gl.glPolygonMode(GL2GL3.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);
        gl.glEnable(GL2GL3.GL_DEPTH_TEST);

        textRenderer = new OGLTextRenderer(gl, glDrawable.getSurfaceWidth(), glDrawable.getSurfaceHeight());

        shaderProgram = ShaderUtils.loadProgram(gl, "/start");
        shaderProgramLight = ShaderUtils.loadProgram(gl, "/light");

        buffers = GridFactory.generateGrid(gl, 100, 100);

        camera = new Camera()
                .withPosition(new Vec3D(0))
                .addAzimuth(5 / 4. * Math.PI)
                .addZenith(-1 / 5. * Math.PI)
                .withFirstPerson(false)
                .withRadius(5);
        cameraLight = new Camera()
                .withPosition(new Vec3D(0))
                .addAzimuth(5 / 4. * Math.PI)
                .addZenith(-1 / 5. * Math.PI)
                .withFirstPerson(false)
                .withRadius(5);

        locTime = gl.glGetUniformLocation(shaderProgram, "time");
        locLightTime = gl.glGetUniformLocation(shaderProgramLight, "lightTime");
        locView = gl.glGetUniformLocation(shaderProgram, "view");
        locLightView = gl.glGetUniformLocation(shaderProgramLight, "lightView");
        locProj = gl.glGetUniformLocation(shaderProgram, "proj");
        locLightProj = gl.glGetUniformLocation(shaderProgramLight, "lightProj");
        locSetting = gl.glGetUniformLocation(shaderProgram, "setting");
        locLightSetting = gl.glGetUniformLocation(shaderProgramLight, "lightSetting");
        locVPLight = gl.glGetUniformLocation(shaderProgram, "lightVP");
        locLightPosition = gl.glGetUniformLocation(shaderProgram, "lightPosition");

        texture = new OGLTexture2D(gl, "/textures/bricks.jpg");
        textureViewer = new OGLTexture2D.Viewer(gl);
        renderTarget = new OGLRenderTarget(gl, 1024, 1024);

        color = new Color(0, 0, 0);
        try {
            this.robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }

        text = "";
    }

    @Override
    public void display(GLAutoDrawable glDrawable) {
        GL2GL3 gl = glDrawable.getGL().getGL2GL3();
        time += 0.1;
        cameraLight = cameraLight.addAzimuth(0.01);

        if (!mouseLook && circle) {
            camera = camera.addAzimuth(0.01);
        }

        renderLightShader(gl);
        renderShader(gl);

        textureViewer.view(texture, -1, -1, 0.5);
        textureViewer.view(renderTarget.getColorTexture(), -1, -0.5, 0.5);
        textureViewer.view(renderTarget.getDepthTexture(), -1, 0, 0.5);

        textRenderer.drawStr2D(3, height - 20, text);
        textRenderer.drawStr2D(width - 43, 87, "T - Strip");
        textRenderer.drawStr2D(width - 49, 75, "L - Lines");
        textRenderer.drawStr2D(width - 50, 63, "O - Ortho");
        textRenderer.drawStr2D(width - 56, 51, "ESC - Exit");
        textRenderer.drawStr2D(width - 77, 39, "C - Stop circle");
        textRenderer.drawStr2D(width - 84, 27, "M - Mouse look");
        textRenderer.drawStr2D(width - 146, 15, "1,2,3,4,5,6 - Shader switch");
        textRenderer.drawStr2D(width - 168, 3, "Bc. Petr Zeman (c) PGRF UHK");
    }

    private void renderLightShader(GL2GL3 gl) {
        gl.glUseProgram(shaderProgramLight);
        renderTarget.bind();
        gl.glClearColor(0.3f, 0.0f, 0.0f, 1.0f);
        gl.glClear(GL2GL3.GL_COLOR_BUFFER_BIT | GL2GL3.GL_DEPTH_BUFFER_BIT);

        gl.glUniform1f(locLightTime, time);
        gl.glUniformMatrix4fv(locLightView, 1, false, cameraLight.getViewMatrix().floatArray(), 0);
        gl.glUniformMatrix4fv(locLightProj, 1, false, projLight.floatArray(), 0);

        drawToBuffers(gl, locLightSetting, shaderProgramLight);
    }

    private void renderShader(GL2GL3 gl) {
        gl.glUseProgram(shaderProgram);
        gl.glBindFramebuffer(GL2GL3.GL_FRAMEBUFFER, 0);
        gl.glViewport(0, 0, width, height);
        gl.glClearColor(0.0f, 0.3f, 0.0f, 1.0f);
        gl.glClear(GL2GL3.GL_COLOR_BUFFER_BIT | GL2GL3.GL_DEPTH_BUFFER_BIT);

        gl.glUniform1f(locTime, time);
        if (showLines) {
            gl.glPolygonMode(GL2GL3.GL_FRONT_AND_BACK, GL2GL3.GL_LINE);
        } else {
            gl.glPolygonMode(GL2GL3.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);
        }
        gl.glUniformMatrix4fv(locView, 1, false, camera.getViewMatrix().floatArray(), 0);
        gl.glUniformMatrix4fv(locProj, 1, false, proj.floatArray(), 0);
        gl.glUniformMatrix4fv(locVPLight, 1, false, cameraLight.getViewMatrix().mul(projLight).floatArray(), 0);
        gl.glUniform3fv(locLightPosition, 1, ToFloatArray.convert(cameraLight.getPosition()), 0);
        texture.bind(shaderProgram, "textureID", 0);
        renderTarget.getDepthTexture().bind(shaderProgram, "depthTexture", 2);

        drawToBuffers(gl, locSetting, shaderProgram);
    }

    private void drawToBuffers(GL2GL3 gl, int setting, int shaderProgram) {
        gl.glUniform1i(setting, 7);
        if (strip) {
            buffers.draw(GL2GL3.GL_TRIANGLE_STRIP, shaderProgram);
        } else {
            buffers.draw(GL2GL3.GL_TRIANGLES, shaderProgram);
        }

        gl.glUniform1i(setting, 8);
        if (strip) {
            buffers.draw(GL2GL3.GL_TRIANGLE_STRIP, shaderProgram);
        } else {
            buffers.draw(GL2GL3.GL_TRIANGLES, shaderProgram);
        }

        gl.glUniform1i(setting, this.setting);
        if (strip) {
            buffers.draw(GL2GL3.GL_TRIANGLE_STRIP, shaderProgram);
        } else {
            buffers.draw(GL2GL3.GL_TRIANGLES, shaderProgram);
        }

    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        this.width = width;
        this.height = height;
        textRenderer.updateSize(width, height);
        projLight = new Mat4OrthoRH(5 / (height / (double) width), 5, 0.1, 20);
        changeProjection();
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
        mx = e.getX();
        my = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (!mouseLook) {
            camera = camera.addAzimuth(Math.PI * (mx - e.getX()) / width);
            camera = camera.addZenith(Math.PI * (e.getY() - my) / width);
        }

        mx = e.getX();
        my = e.getY();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (mouseLook) {
            double azimuth = width / 2 - e.getXOnScreen();
            camera = camera.addAzimuth(azimuth * 0.01);

            double zenith = height / 2 - e.getYOnScreen();
            camera = camera.addZenith(zenith * 0.01);
            robot.mouseMove(width / 2, height / 2);
        }

        color = robot.getPixelColor(e.getX(), e.getY());
        String colorText = "Red: " + color.getRed() +
                "Green: " + color.getGreen() +
                "Blue: " + color.getBlue() +
                "Alpha: " + color.getAlpha();

        this.text = "X:" + e.getX() + ", Y: " + e.getY() + ", barva: " + colorText;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                camera = camera.forward(1);
                break;
            case KeyEvent.VK_D:
                camera = camera.right(1);
                break;
            case KeyEvent.VK_S:
                camera = camera.backward(1);
                break;
            case KeyEvent.VK_A:
                camera = camera.left(1);
                break;
            case KeyEvent.VK_CONTROL:
                camera = camera.down(1);
                break;
            case KeyEvent.VK_SHIFT:
                camera = camera.up(1);
                break;
            case KeyEvent.VK_SPACE:
                camera = camera.withFirstPerson(!camera.getFirstPerson());
                break;
            case KeyEvent.VK_R:
                camera = camera.mulRadius(0.9f);
                break;
            case KeyEvent.VK_F:
                camera = camera.mulRadius(1.1f);
                break;
            case KeyEvent.VK_L:
                showLines = !showLines;
                break;
            case KeyEvent.VK_O:
                ortho = !ortho;
                changeProjection();
                break;
            case KeyEvent.VK_T:
                strip = !strip;
                break;
            case KeyEvent.VK_M:
                toggleMouseLook();
                break;
            case KeyEvent.VK_C:
                if (!mouseLook) {
                    circle = !circle;
                }
                break;
            case KeyEvent.VK_1:
            case KeyEvent.VK_NUMPAD1:
                setting = 1;
                break;
            case KeyEvent.VK_2:
            case KeyEvent.VK_NUMPAD2:
                setting = 2;
                break;
            case KeyEvent.VK_3:
            case KeyEvent.VK_NUMPAD3:
                setting = 3;
                break;
            case KeyEvent.VK_4:
            case KeyEvent.VK_NUMPAD4:
                setting = 4;
                break;
            case KeyEvent.VK_5:
            case KeyEvent.VK_NUMPAD5:
                setting = 5;
                break;
            case KeyEvent.VK_6:
            case KeyEvent.VK_NUMPAD6:
                setting = 6;
                break;
        }
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
        gl.glDeleteProgram(shaderProgramLight);
    }

    private void changeProjection() {
        if (ortho) {
            proj = new Mat4OrthoRH(5 / (height / (double) width), 5, 0.1, 20);
        } else {
            proj = new Mat4PerspRH(Math.PI / 3, height / (double) width, 1, 20);
        }
    }

    private void toggleMouseLook() {
        mouseLook = !mouseLook;
        Cursor cursor = defaultCursor;
        if (mouseLook) {
            camera = camera.backward(5).withFirstPerson(true);
            Toolkit tk = Toolkit.getDefaultToolkit();
            cursor = tk.createCustomCursor(tk.getImage(""), new Point(), "trans");
        } else {
            camera = camera.forward(5).withFirstPerson(false);
        }

        canvas.setCursor(cursor);
        robot.mouseMove(width / 2, height / 2);
    }

}