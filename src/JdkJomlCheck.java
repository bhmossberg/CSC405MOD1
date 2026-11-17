/*
 * JdkJomlCheck.java
 *
 * Headless environment test #1 for CSC405 Module 1.
 * Prints the JDK identity and performs a couple of JOML Matrix4f / Vector3f
 * operations with expected results. No OpenGL or window is required.
 *
 * Target: Java SE 11 and JOML 1.10.0 (Gordon & Clevenger, Appendix A / B).
 */

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class JdkJomlCheck
{
	private static final float EPS = 1.0e-5f;

	public static void main(String[] args)
	{
		System.out.println("==== CSC405 Module 1: JDK / JOML check ====");
		System.out.println("java.version:               " + System.getProperty("java.version"));
		System.out.println("java.vendor:                " + System.getProperty("java.vendor"));
		System.out.println("java.vm.name:               " + System.getProperty("java.vm.name"));
		System.out.println("java.specification.version: " + System.getProperty("java.specification.version"));
		System.out.println("java.home:                  " + System.getProperty("java.home"));
		System.out.println("os.name:                    " + System.getProperty("os.name"));
		System.out.println("os.arch:                    " + System.getProperty("os.arch"));
		System.out.println("os.version:                 " + System.getProperty("os.version"));

		String spec = System.getProperty("java.specification.version", "");
		if (!spec.equals("11"))
		{
			System.out.println("NOTE: The textbook targets Java SE 11. This runtime reports "
					+ "specification version " + spec + ". That is fine if you compiled with "
					+ "--release 11, but install a JDK 11 if you want to match the book exactly.");
		}

		System.out.println();
		System.out.println("==== JOML 1.10.0 operations ====");

		// 1) Identity * point is unchanged.
		Vector3f p = new Vector3f(1.0f, 2.0f, 3.0f);
		Vector3f outIdentity = new Matrix4f().transformPosition(p, new Vector3f());
		boolean okIdentity = approx(outIdentity, 1.0f, 2.0f, 3.0f);
		System.out.printf("Identity * (1, 2, 3)        = (%.4f, %.4f, %.4f)  expected (1.0000, 2.0000, 3.0000)  %s%n",
				outIdentity.x, outIdentity.y, outIdentity.z, okIdentity ? "OK" : "FAIL");

		// 2) Translation by (2, 3, 4) applied to (1, 1, 1) should yield (3, 4, 5).
		Vector3f outTrans = new Matrix4f().translation(2.0f, 3.0f, 4.0f)
				.transformPosition(new Vector3f(1.0f, 1.0f, 1.0f), new Vector3f());
		boolean okTrans = approx(outTrans, 3.0f, 4.0f, 5.0f);
		System.out.printf("Translate(2,3,4) * (1,1,1)  = (%.4f, %.4f, %.4f)  expected (3.0000, 4.0000, 5.0000)  %s%n",
				outTrans.x, outTrans.y, outTrans.z, okTrans ? "OK" : "FAIL");

		// 3) A +90 degree rotation about Z takes the unit X axis onto the unit Y axis
		//    (right-handed, JOML Matrix4f.rotationZ).
		Vector3f outRot = new Matrix4f().rotationZ((float) Math.toRadians(90.0))
				.transformPosition(new Vector3f(1.0f, 0.0f, 0.0f), new Vector3f());
		boolean okRot = approx(outRot, 0.0f, 1.0f, 0.0f);
		System.out.printf("RotateZ(90 deg) * (1,0,0)   = (%.4f, %.4f, %.4f)  expected (0.0000, 1.0000, 0.0000)  %s%n",
				outRot.x, outRot.y, outRot.z, okRot ? "OK" : "FAIL");

		boolean allOk = okIdentity && okTrans && okRot;
		System.out.println();
		System.out.println(allOk
				? "JDK / JOML check: PASS"
				: "JDK / JOML check: FAIL");
		if (!allOk) System.exit(1);
	}

	private static boolean approx(Vector3f v, float x, float y, float z)
	{
		return Math.abs(v.x - x) < EPS && Math.abs(v.y - y) < EPS && Math.abs(v.z - z) < EPS;
	}
}
