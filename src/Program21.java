/*
 * Program21.java
 *
 * Environment test #2, modeled on Program 2.1 (Gordon & Clevenger, Ch. 2):
 * a JFrame holding a GLCanvas, implementing GLEventListener, with
 * init / display / reshape / dispose. Program 2.1 only clears the window
 * to red; this version also prints GL_VENDOR, GL_RENDERER, GL_VERSION,
 * GL_SHADING_LANGUAGE_VERSION, and the JOGL version once the context exists.
 *
 * Mac: uses GLProfile.getMaxProgrammableCore(true) as in Appendix B.
 * Windows / Linux: requests the GL4 profile (OpenGL 4.3 in the book).
 *
 * Usage:
 *   java Program21
 *   java Program21 -autoClose [millis]    # close after a delay (scripted test)
 */

import javax.swing.*;
import static com.jogamp.opengl.GL4.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;

public class Program21 extends JFrame implements GLEventListener
{
	private GLCanvas myCanvas;
	private boolean printed = false;

	public Program21(String[] args)
	{
		Utils.configureFrame(this, "CSC405 Module 1 - Program 2.1 (GL info)");
		myCanvas = Utils.createCanvas();
		myCanvas.addGLEventListener(this);
		this.add(myCanvas);
		this.setVisible(true);
		Utils.scheduleAutoClose(this, args);
	}

	public void display(GLAutoDrawable drawable)
	{
		GL4 gl = (GL4) GLContext.getCurrentGL();
		if (!printed)
		{
			Utils.printContextInfo(drawable);
			printed = true;
		}
		// Program 2.1 clears the color buffer to red (1, 0, 0, 1).
		gl.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
		gl.glClear(GL_COLOR_BUFFER_BIT);
	}

	public void init(GLAutoDrawable drawable)
	{
		// A current context exists here; printing in display() also works,
		// but init() is the book's usual place for one-time setup.
		System.out.println("Program21.init(): OpenGL context is current.");
		Utils.printContextInfo(drawable);
		printed = true;
	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) { }

	public void dispose(GLAutoDrawable drawable) { }

	public static void main(String[] args)
	{
		new Program21(args);
	}
}
