/*
 * Program22.java
 *
 * Environment test #3, modeled on Program 2.2 (shaders, drawing a POINT)
 * with the Section 2.2 / Program 2.3 error checks and the Program 2.4
 * file-loaded GLSL (Gordon & Clevenger, Ch. 2).
 *
 * Loads a vertex and fragment shader from shaders/430 or shaders/410
 * (detected from os.name, override with -Dglsl.version=410|430), compiles
 * and links them with compile-status / link-status / info-log /
 * checkOpenGLError reporting, then draws a single point.
 *
 * The default OpenGL point is one pixel (as the book notes). To make the
 * test visibly succeed we set gl_PointSize in the vertex shader and enable
 * GL_PROGRAM_POINT_SIZE (required in a core profile). Pass -triangle to
 * draw the three-vertex triangle from Program 2.5 instead of the point.
 *
 * Usage:
 *   java Program22
 *   java Program22 -triangle
 *   java Program22 -autoClose [millis]
 */

import javax.swing.*;
import static com.jogamp.opengl.GL4.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;

public class Program22 extends JFrame implements GLEventListener
{
	private GLCanvas myCanvas;
	private int renderingProgram;
	private int vao[] = new int[1];
	private boolean drawTriangle;

	public Program22(String[] args)
	{
		drawTriangle = hasFlag(args, "-triangle") || hasFlag(args, "--triangle");
		String title = drawTriangle
				? "CSC405 Module 1 - Program 2.5 (triangle, shader compile/link)"
				: "CSC405 Module 1 - Program 2.2 (point, shader compile/link)";
		Utils.configureFrame(this, title);
		myCanvas = Utils.createCanvas();
		myCanvas.addGLEventListener(this);
		this.add(myCanvas);
		this.setVisible(true);
		Utils.scheduleAutoClose(this, args);
	}

	public void display(GLAutoDrawable drawable)
	{
		GL4 gl = (GL4) GLContext.getCurrentGL();
		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		gl.glClear(GL_DEPTH_BUFFER_BIT);
		gl.glClear(GL_COLOR_BUFFER_BIT);

		if (renderingProgram == 0) return; // compile/link failed; leave a black window

		gl.glUseProgram(renderingProgram);
		// Core-profile point size is written from the vertex shader.
		gl.glEnable(GL_PROGRAM_POINT_SIZE);
		if (drawTriangle)
		{
			gl.glDrawArrays(GL_TRIANGLES, 0, 3); // Program 2.5
		}
		else
		{
			gl.glDrawArrays(GL_POINTS, 0, 1);    // Program 2.2
		}
		if (Utils.checkOpenGLError())
		{
			System.err.println("OpenGL error reported after glDrawArrays.");
		}
	}

	public void init(GLAutoDrawable drawable)
	{
		GL4 gl = (GL4) GLContext.getCurrentGL();
		Utils.printContextInfo(drawable);

		String vertName = drawTriangle ? "vertTriangle.glsl" : "vertPoint.glsl";
		String fragName = drawTriangle ? "fragTriangle.glsl" : "fragPoint.glsl";
		String vertPath = Utils.shaderPath(vertName);
		String fragPath = Utils.shaderPath(fragName);
		System.out.println("Loading shaders:");
		System.out.println("  " + vertPath);
		System.out.println("  " + fragPath);

		renderingProgram = Utils.createShaderProgram(vertPath, fragPath);
		if (renderingProgram == 0)
		{
			System.err.println("Shader compile/link test: FAIL");
			return;
		}

		// OpenGL requires at least one VAO whenever shaders are used, even if
		// (as here) the vertices are hardcoded in the vertex shader. See Program 2.2.
		gl.glGenVertexArrays(vao.length, vao, 0);
		gl.glBindVertexArray(vao[0]);
		Utils.checkOpenGLError();
		System.out.println("Shader compile/link test: PASS");
	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) { }

	public void dispose(GLAutoDrawable drawable) { }

	public static void main(String[] args)
	{
		new Program22(args);
	}

	private static boolean hasFlag(String[] args, String flag)
	{
		if (args == null) return false;
		for (String a : args)
		{
			if (a.equalsIgnoreCase(flag)) return true;
		}
		return false;
	}
}
