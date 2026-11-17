/*
 * Utils.java
 *
 * Shared helpers in the style of Gordon & Clevenger, Computer Graphics
 * Programming in OpenGL with Java, 3rd ed. (2021):
 *   - Section 2.2 / Program 2.3: checkOpenGLError, printShaderLog, printProgramLog
 *   - Section 2.3 / Program 2.4: readShaderSource, createShaderProgram
 *   - Appendix B: Mac GL profile (getMaxProgrammableCore) and GLSL #version 410
 *
 * These start as private methods on Code in the early chapter examples and are
 * later gathered into a Utils class (see Section 2.6 and later chapters).
 *
 * Platform handling is switchable:
 *   -Dgl.profile=maxcore|gl4     force the GL profile (default: maxcore on Mac, GL4 elsewhere)
 *   -Dglsl.version=410|430       force which shaders/ folder is used (default: 410 on Mac, 430 elsewhere)
 *   -autoClose [millis]          close the test window after a delay (for scripted checks)
 */

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.Timer;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.JoglVersion;
import static com.jogamp.opengl.GL4.*;

public class Utils
{
	/** True when running on macOS (os.name contains "mac"). */
	public static boolean isMac()
	{
		String os = System.getProperty("os.name", "").toLowerCase();
		return os.contains("mac");
	}

	/**
	 * Directory containing the GLSL files that match this platform.
	 * Windows and Linux use OpenGL 4.3 shaders (#version 430, Appendix A).
	 * macOS uses OpenGL 4.1 shaders (#version 410, Appendix B), because Apple's
	 * OpenGL implementation tops out at 4.1.
	 *
	 * Override with -Dglsl.version=410 or -Dglsl.version=430.
	 */
	public static String shaderFolder()
	{
		String override = System.getProperty("glsl.version", "").trim();
		if (override.equals("410") || override.equals("430"))
		{
			return "shaders" + File.separator + override;
		}
		if (isMac()) return "shaders" + File.separator + "410";
		return "shaders" + File.separator + "430";
	}

	/** Resolves a shader file name against the platform folder (e.g. vertPoint.glsl). */
	public static String shaderPath(String fileName)
	{
		return shaderFolder() + File.separator + fileName;
	}

	/**
	 * Build a GLCanvas with an explicit GL profile.
	 *
	 * Appendix B says that many Macs default to a much earlier OpenGL version, so
	 * the book's Mac setup replaces "new GLCanvas()" with:
	 *
	 *     GLProfile glp = GLProfile.getMaxProgrammableCore(true);
	 *     GLCapabilities caps = new GLCapabilities(glp);
	 *     myCanvas = new GLCanvas(caps);
	 *
	 * On Windows the book uses a plain "new GLCanvas()" and assumes OpenGL 4.3.
	 * Here we request the GL4 profile (OpenGL 4.x core) so #version 430 shaders
	 * can compile, and fall back to getMaxProgrammableCore if GL4 is missing.
	 *
	 * Override with -Dgl.profile=maxcore or -Dgl.profile=gl4.
	 */
	public static GLCanvas createCanvas()
	{
		String override = System.getProperty("gl.profile", "").trim().toLowerCase();
		GLProfile glp;

		if (override.equals("maxcore") || (override.isEmpty() && isMac()))
		{
			glp = GLProfile.getMaxProgrammableCore(true);
			System.out.println("GLProfile: getMaxProgrammableCore(true)  [Appendix B / override]");
		}
		else if (override.equals("gl4") || override.isEmpty())
		{
			if (GLProfile.isAvailable(GLProfile.GL4))
			{
				glp = GLProfile.get(GLProfile.GL4);
				System.out.println("GLProfile: GL4 (OpenGL 4.x core; book default on Windows is OpenGL 4.3)");
			}
			else
			{
				System.out.println("GL4 is not advertised; falling back to getMaxProgrammableCore(true)");
				glp = GLProfile.getMaxProgrammableCore(true);
			}
		}
		else
		{
			System.out.println("Unknown -Dgl.profile=" + override + " ; using getMaxProgrammableCore(true)");
			glp = GLProfile.getMaxProgrammableCore(true);
		}

		System.out.println("Selected GLProfile: " + glp);
		GLCapabilities caps = new GLCapabilities(glp);
		return new GLCanvas(caps);
	}

	/**
	 * Program 2.3: print the GLSL compiler's info log for a shader object.
	 */
	public static void printShaderLog(int shader)
	{
		GL4 gl = (GL4) GLContext.getCurrentGL();
		int[] len = new int[1];
		int[] chWrittn = new int[1];
		byte[] log = null;
		gl.glGetShaderiv(shader, GL_INFO_LOG_LENGTH, len, 0);
		if (len[0] > 0)
		{
			log = new byte[len[0]];
			gl.glGetShaderInfoLog(shader, len[0], chWrittn, 0, log, 0);
			System.out.println("Shader Info Log: ");
			for (int i = 0; i < log.length; i++)
			{
				System.out.print((char) log[i]);
			}
		}
	}

	/**
	 * Program 2.3: print the GLSL linker's info log for a program object.
	 */
	public static void printProgramLog(int prog)
	{
		GL4 gl = (GL4) GLContext.getCurrentGL();
		int[] len = new int[1];
		int[] chWrittn = new int[1];
		byte[] log = null;
		gl.glGetProgramiv(prog, GL_INFO_LOG_LENGTH, len, 0);
		if (len[0] > 0)
		{
			log = new byte[len[0]];
			gl.glGetProgramInfoLog(prog, len[0], chWrittn, 0, log, 0);
			System.out.println("Program Info Log: ");
			for (int i = 0; i < log.length; i++)
			{
				System.out.print((char) log[i]);
			}
		}
	}

	/**
	 * Program 2.3: drain the OpenGL error flag and print each error with
	 * gluErrorString(). Returns true if at least one error was present.
	 * The book recommends calling this after glCompileShader, glLinkProgram,
	 * and after runtime draws such as glDrawArrays.
	 */
	public static boolean checkOpenGLError()
	{
		GL4 gl = (GL4) GLContext.getCurrentGL();
		boolean foundError = false;
		GLU glu = new GLU();
		int glErr = gl.glGetError();
		while (glErr != GL_NO_ERROR)
		{
			System.err.println("glError: " + glu.gluErrorString(glErr));
			foundError = true;
			glErr = gl.glGetError();
		}
		return foundError;
	}

	/**
	 * Program 2.4: read a GLSL file into a String array, one element per line,
	 * each line terminated with "\\n" (required so #version stays on its own line).
	 *
	 * The book's loop uses Scanner.hasNext() / nextLine(). This version uses
	 * hasNextLine() so blank lines are preserved, and closes the scanner.
	 */
	public static String[] readShaderSource(String filename)
	{
		Vector<String> lines = new Vector<String>();
		String[] program;
		File file = new File(filename);
		if (!file.isFile())
		{
			// Fall back to a path resolved against the working directory.
			file = new File(System.getProperty("user.dir"), filename);
		}
		if (!file.isFile())
		{
			System.err.println("IOException reading file: shader not found: " + filename);
			System.err.println("  cwd:          " + System.getProperty("user.dir"));
			System.err.println("  shader folder assumed: " + shaderFolder());
			System.err.println("  Run these programs from the repo root so relative shader paths resolve.");
			return null;
		}
		try (Scanner sc = new Scanner(file))
		{
			while (sc.hasNextLine())
			{
				lines.addElement(sc.nextLine());
			}
			program = new String[lines.size()];
			for (int i = 0; i < lines.size(); i++)
			{
				program[i] = (String) lines.elementAt(i) + "\n";
			}
		}
		catch (IOException e)
		{
			System.err.println("IOException reading file: " + e);
			return null;
		}
		System.out.println("Read " + program.length + " lines from " + file.getPath());
		return program;
	}

	/**
	 * Section 2.2 / 2.3: compile a vertex + fragment shader from files, link them,
	 * and report compile status, link status, info logs, and OpenGL errors.
	 * Returns the program id, or 0 on failure.
	 */
	public static int createShaderProgram(String vS, String fS)
	{
		GL4 gl = (GL4) GLContext.getCurrentGL();
		// One-element arrays are used because the associated JOGL calls require arrays.
		int[] vertCompiled = new int[1];
		int[] fragCompiled = new int[1];
		int[] linked = new int[1];

		String[] vshaderSource = readShaderSource(vS);
		String[] fshaderSource = readShaderSource(fS);
		if (vshaderSource == null || fshaderSource == null)
		{
			System.err.println("Aborting createShaderProgram; shader source was not read.");
			return 0;
		}

		int vShader = gl.glCreateShader(GL_VERTEX_SHADER);
		gl.glShaderSource(vShader, vshaderSource.length, vshaderSource, null, 0);
		gl.glCompileShader(vShader);
		checkOpenGLError();
		gl.glGetShaderiv(vShader, GL_COMPILE_STATUS, vertCompiled, 0);
		if (vertCompiled[0] != 1)
		{
			System.out.println("vertex compilation failed.");
			printShaderLog(vShader);
		}
		else
		{
			System.out.println("vertex compilation succeeded.");
			printShaderLog(vShader); // some drivers also write warnings here
		}

		int fShader = gl.glCreateShader(GL_FRAGMENT_SHADER);
		gl.glShaderSource(fShader, fshaderSource.length, fshaderSource, null, 0);
		gl.glCompileShader(fShader);
		checkOpenGLError();
		gl.glGetShaderiv(fShader, GL_COMPILE_STATUS, fragCompiled, 0);
		if (fragCompiled[0] != 1)
		{
			System.out.println("fragment compilation failed.");
			printShaderLog(fShader);
		}
		else
		{
			System.out.println("fragment compilation succeeded.");
			printShaderLog(fShader);
		}

		if ((vertCompiled[0] != 1) || (fragCompiled[0] != 1))
		{
			System.out.println("\nCompilation error; return-flags:");
			System.out.println(" vertCompiled = " + vertCompiled[0] + " ; fragCompiled = " + fragCompiled[0]);
			return 0;
		}

		int vfprogram = gl.glCreateProgram();
		gl.glAttachShader(vfprogram, vShader);
		gl.glAttachShader(vfprogram, fShader);
		gl.glLinkProgram(vfprogram);
		checkOpenGLError();
		gl.glGetProgramiv(vfprogram, GL_LINK_STATUS, linked, 0);
		if (linked[0] != 1)
		{
			System.out.println("linking failed.");
			printProgramLog(vfprogram);
			return 0;
		}
		else
		{
			System.out.println("program link succeeded.");
			printProgramLog(vfprogram);
		}

		// Shaders can be deleted after a successful link; they stay attached to the program.
		gl.glDeleteShader(vShader);
		gl.glDeleteShader(fShader);
		System.out.println("createShaderProgram: compile and link succeeded for:");
		System.out.println("  vertex:   " + vS);
		System.out.println("  fragment: " + fS);
		return vfprogram;
	}

	/**
	 * Print the strings Program 2.1's environment test needs, plus the JOGL version.
	 * Call from init() or display() once a current GL context exists.
	 */
	public static void printContextInfo(GLAutoDrawable drawable)
	{
		GL4 gl = (GL4) GLContext.getCurrentGL();
		System.out.println("---- OpenGL / JOGL context ----");
		System.out.println("GL_VENDOR:                   " + gl.glGetString(GL_VENDOR));
		System.out.println("GL_RENDERER:                 " + gl.glGetString(GL_RENDERER));
		System.out.println("GL_VERSION:                  " + gl.glGetString(GL_VERSION));
		System.out.println("GL_SHADING_LANGUAGE_VERSION: " + gl.glGetString(GL_SHADING_LANGUAGE_VERSION));
		System.out.println("JOGL version:                " + JoglVersion.getInstance().getImplementationVersion());
		System.out.println("JOGL implementation title:   " + JoglVersion.getInstance().getImplementationTitle());
		if (drawable != null)
		{
			System.out.println("Drawable GLProfile:          " + drawable.getGLProfile());
		}
		System.out.println("os.name:                     " + System.getProperty("os.name"));
		System.out.println("shader folder:               " + shaderFolder());
		System.out.println("--------------------------------");
	}

	public static boolean wantsAutoClose(String[] args)
	{
		if (args == null) return false;
		for (String a : args)
		{
			if (a.equalsIgnoreCase("-autoClose") || a.equals("--autoClose")) return true;
		}
		return false;
	}

	public static int autoCloseMillis(String[] args)
	{
		if (args == null) return 2500;
		for (int i = 0; i < args.length; i++)
		{
			if ((args[i].equalsIgnoreCase("-autoClose") || args[i].equals("--autoClose"))
					&& i + 1 < args.length)
			{
				try { return Integer.parseInt(args[i + 1]); }
				catch (NumberFormatException ignored) { }
			}
		}
		return 2500;
	}

	/**
	 * For scripted environment checks (Xvfb, CI, or a quick smoke test):
	 * pass -autoClose [millis] and the window will close itself so the JVM exits.
	 * Interactive runs omit the flag and leave the window open.
	 */
	public static void scheduleAutoClose(JFrame frame, String[] args)
	{
		if (!wantsAutoClose(args)) return;
		int ms = autoCloseMillis(args);
		System.out.println("Auto-close scheduled in " + ms + " ms (-autoClose).");
		Timer t = new Timer(ms, e -> {
			frame.dispose();
			System.exit(0);
		});
		t.setRepeats(false);
		t.start();
	}

	/** Standard JFrame setup used by Programs 2.1 / 2.2. */
	public static void configureFrame(JFrame frame, String title)
	{
		frame.setTitle(title);
		frame.setSize(600, 400);
		frame.setLocation(200, 200);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
