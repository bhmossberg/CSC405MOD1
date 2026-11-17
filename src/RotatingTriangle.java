/*
 * RotatingTriangle.java
 *
 * Optional environment test #4. Combines Program 2.5 (a three-vertex triangle)
 * with the animation pattern in Program 2.6, but uses JOGL's FPSAnimator
 * (the book also describes FPSAnimator; its own examples instantiate Animator
 * and recommend timing movement from elapsed time). The triangle rotates about
 * Z, with the rotation matrix built by JOML Matrix4f and sent as a uniform.
 *
 * Usage:
 *   java RotatingTriangle
 *   java RotatingTriangle -autoClose [millis]
 */

import javax.swing.*;
import static com.jogamp.opengl.GL4.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;
import org.joml.Matrix4f;

public class RotatingTriangle extends JFrame implements GLEventListener
{
	private GLCanvas myCanvas;
	private int renderingProgram;
	private int vao[] = new int[1];
	private FPSAnimator animator;
	private float angle = 0.0f;          // radians
	private final float inc = 0.02f;     // radians per frame at the animator rate
	private final Matrix4f rotMat = new Matrix4f();
	private final float[] rotArray = new float[16];

	public RotatingTriangle(String[] args)
	{
		Utils.configureFrame(this, "CSC405 Module 1 - rotating triangle (FPSAnimator)");
		myCanvas = Utils.createCanvas();
		myCanvas.addGLEventListener(this);
		this.add(myCanvas);
		this.setVisible(true);
		// FPSAnimator calls display() on the canvas at a fixed frame rate.
		// 60 fps is a comfortable default for this smoke test.
		animator = new FPSAnimator(myCanvas, 60);
		animator.start();
		Utils.scheduleAutoClose(this, args);
	}

	public void display(GLAutoDrawable drawable)
	{
		GL4 gl = (GL4) GLContext.getCurrentGL();
		gl.glClear(GL_DEPTH_BUFFER_BIT);
		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		gl.glClear(GL_COLOR_BUFFER_BIT); // clear each frame so the triangle does not leave a trail

		if (renderingProgram == 0) return;

		gl.glUseProgram(renderingProgram);

		angle += inc;
		rotMat.identity().rotationZ(angle);
		// JOML Matrix4f.get(float[]) writes column-major values, which is what
		// glUniformMatrix4fv expects when the transpose argument is false.
		rotMat.get(rotArray);
		int mvLoc = gl.glGetUniformLocation(renderingProgram, "mv_matrix");
		gl.glUniformMatrix4fv(mvLoc, 1, false, rotArray, 0);

		gl.glDrawArrays(GL_TRIANGLES, 0, 3);
		Utils.checkOpenGLError();
	}

	public void init(GLAutoDrawable drawable)
	{
		GL4 gl = (GL4) GLContext.getCurrentGL();
		Utils.printContextInfo(drawable);

		String vertPath = Utils.shaderPath("vertRotate.glsl");
		String fragPath = Utils.shaderPath("fragRotate.glsl");
		System.out.println("Loading shaders:");
		System.out.println("  " + vertPath);
		System.out.println("  " + fragPath);

		renderingProgram = Utils.createShaderProgram(vertPath, fragPath);
		if (renderingProgram == 0)
		{
			System.err.println("Rotating triangle shader compile/link: FAIL");
			return;
		}

		gl.glGenVertexArrays(vao.length, vao, 0);
		gl.glBindVertexArray(vao[0]);
		Utils.checkOpenGLError();
		System.out.println("Rotating triangle: shaders ready, FPSAnimator running at 60 fps.");
	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) { }

	public void dispose(GLAutoDrawable drawable)
	{
		if (animator != null && animator.isStarted())
		{
			animator.stop();
		}
	}

	public static void main(String[] args)
	{
		new RotatingTriangle(args);
	}
}
