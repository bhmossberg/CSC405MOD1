# CSC405MOD1

**CSC405 Computer Graphics, CSU Global (Winter 2026, Session A). Module 1: Graphics Systems and Models**

OpenGL Environment Tests.

## Enabling Objectives

By the end of this module, you will be able to:

1. Identify graphics systems.
2. Describe imaging systems.
3. Conduct simulations and animations.
4. Create an application of computer graphics.
5. Apply images, physical and synthetic.

## Readings

**Required**

- Gordon, V. S., & Clevenger, J. (2021). Computer graphics programming in OpenGL with Java (3rd ed.). Mercury Learning.
  - Chapter 1: Getting Started
  - Chapter 2
    - Section 2.1: The OpenGL Pipeline
    - Section 2.2: Detecting OpenGL and GLSL Errors


**Recommended**

- Khronos Group. (2024). OpenGL 4.6 reference pages. https://registry.khronos.org/OpenGL-Refpages/gl4/
- JogAmp Community. (2023). JOGL — Java binding for the OpenGL API. https://jogamp.org/jogl/www/

## Graphics Concepts Covered

This module has no coding assignment (its only graded item is the discussion forum). Broad graphics concepts covered:

- Graphics systems: processor, GPU, memory, framebuffer, and input/output devices
- Display of information: from early plotting and CRTs to modern raster displays
- Pixels, resolution, color depth, and the framebuffer
- Objects and viewers: the synthetic-camera model and how images are formed
- The human visual system and how it shapes graphics design
- Physical vs. synthetic images, simulation, and animation as applications of graphics
- The OpenGL pipeline (vertex, tessellation, geometry, rasterization, fragment, and pixel stages) and detecting OpenGL/GLSL errors (Ch. 1, Sections 2.1-2.2)

## Suggested Project

OpenGL environment tests: confirm the JDK, JOGL, and JOML installation with small JOGL programs that open a window, report the OpenGL and GLSL versions, compile a vertex and fragment shader, check for OpenGL/GLSL errors, and render a simple shape (for example, textbook Programs 2.1 and 2.2).

## Testing the environment

This folder holds small, well-commented programs that confirm a Java SE 11 + JOGL 2.4 + JOML 1.10.0 + OpenGL setup, following Gordon and Clevenger, *Computer Graphics Programming in OpenGL with Java* (3rd ed.): Programs 2.1 and 2.2, the Section 2.2 error checks, and the installation notes in Appendix A (Windows) and Appendix B (Macintosh).

There is no graded coding assignment in Module 1. These tests are a suggested project so the same environment is ready for later modules. Do not commit compiled `.class` files or the JOGL/JOML jars; they are listed in `.gitignore`. Keep the jars in a folder *outside* this repository.

### What you should have installed

| Piece | Version the book uses | Notes |
| --- | --- | --- |
| JDK | Java SE 11 | Appendix A / B. A newer JDK is fine if you compile with `--release 11`. |
| JOGL | 2.4 | `jogl-all.jar` and `gluegen-rt.jar` from [jogamp.org/deployment/v2.4.0/jar/](https://jogamp.org/deployment/v2.4.0/jar/) (or Maven `org.jogamp.jogl` / `org.jogamp.gluegen`). |
| JOGL natives | matching 2.4 jars | Put these in the *same folder* as the two jars above, and also on the `CLASSPATH`. Windows: `jogl-all-natives-windows-amd64.jar`, `gluegen-rt-natives-windows-amd64.jar`. macOS: `jogl-all-natives-macosx-universal.jar`, `gluegen-rt-natives-macosx-universal.jar`. (The textbook writes “macosc”; the official file name is `macosx`.) Linux: `*-natives-linux-amd64.jar`. |
| JOML | 1.10.0 | `joml-1.10.0.jar` from [Maven Central](https://repo1.maven.org/maven2/org/joml/joml/1.10.0/joml-1.10.0.jar) or the JOML GitHub releases page. |
| OpenGL | 4.3 on Windows, 4.1 on Mac | You do not install OpenGL itself. Windows needs a driver that supports at least 4.3 (Appendix A). macOS tops out at 4.1 (Appendix B). |

Set an environment variable `CSC405_LIB` to the folder that holds the JOGL jars (and, if you keep JOML somewhere else on Windows, `CSC405_JOML` as well). Appendix A suggests a folder such as `C:\JOGL`; Appendix B says the old `/System/Library/Java/Extensions` mechanism is deprecated, so use any folder you like and put it on `CLASSPATH`.

### Programs

| Class | What it does |
| --- | --- |
| `JdkJomlCheck` | Headless check. Prints `java.version`, vendor, and OS, then runs JOML `Matrix4f` / `Vector3f` identity, translation, and Z-rotation tests with expected output. |
| `Program21` | Modeled on Program 2.1: `JFrame` + `GLCanvas` + `GLEventListener` (`init` / `display` / `reshape` / `dispose`). Clears the window to red and prints `GL_VENDOR`, `GL_RENDERER`, `GL_VERSION`, `GL_SHADING_LANGUAGE_VERSION`, and the JOGL version. |
| `Program22` | Modeled on Program 2.2, with Program 2.3 error checking and Program 2.4 file-loaded shaders. Compiles and links a vertex and fragment shader, reports compile status, link status, info logs, and `checkOpenGLError`, then draws a point. Pass `-triangle` to draw the Program 2.5 triangle instead. |
| `RotatingTriangle` | Optional. Program 2.5 triangle spun about Z by a JOML `Matrix4f`, driven by JOGL's `FPSAnimator` at 60 fps (Program 2.6 describes both `Animator` and `FPSAnimator`). |
| `Utils` | Shared `createShaderProgram`, `readShaderSource`, `checkOpenGLError`, `printShaderLog`, `printProgramLog`, plus GL-profile and GLSL-version selection. |

GLSL sources live in `shaders/430` (`#version 430`, Windows / Linux) and `shaders/410` (`#version 410`, macOS). The programs pick the folder from `os.name`. You can override that, or the GL profile, on the command line:

```text
-Dglsl.version=410          # or 430
-Dgl.profile=maxcore        # Appendix B: GLProfile.getMaxProgrammableCore(true)
-Dgl.profile=gl4            # OpenGL 4.x core (book default on Windows is 4.3)
-autoClose 2500             # close the window after 2.5 seconds (useful for a quick smoke test)
```

On macOS the code always uses `GLProfile.getMaxProgrammableCore(true)` unless you override it, which is the change Appendix B makes to every `new GLCanvas()` in the book.

### Build and run (Windows)

`CLASSPATH` uses a semicolon. From the repo root in `cmd.exe`:

```bat
set CSC405_LIB=C:\JOGL
set CSC405_JOML=C:\JOML
set CP=%CSC405_LIB%\jogl-all.jar;%CSC405_LIB%\gluegen-rt.jar;%CSC405_LIB%\jogl-all-natives-windows-amd64.jar;%CSC405_LIB%\gluegen-rt-natives-windows-amd64.jar;%CSC405_JOML%\joml-1.10.0.jar

mkdir out
javac --release 11 -d out -cp %CP% src\*.java

java -cp out;%CP% JdkJomlCheck
java -Dsun.java2d.d3d=false -Dsun.java2d.uiScale=1 -cp out;%CP% Program21
java -Dsun.java2d.d3d=false -Dsun.java2d.uiScale=1 -cp out;%CP% Program22
java -Dsun.java2d.d3d=false -Dsun.java2d.uiScale=1 -cp out;%CP% Program22 -triangle
java -Dsun.java2d.d3d=false -Dsun.java2d.uiScale=1 -cp out;%CP% RotatingTriangle
```

The `-Dsun.java2d.d3d=false -Dsun.java2d.uiScale=1` flags are the ones the textbook recommends after Program 2.1 on Windows (disable Direct3D acceleration and UI scaling). Always run these commands from the repo root so the relative `shaders\430\...` paths resolve. Or use the script: `run.bat compile`, `run.bat jdk`, `run.bat info`, `run.bat shader`, `run.bat rotate`.

### Build and run (macOS)

`CLASSPATH` uses a colon. From the repo root in Terminal:

```sh
export CSC405_LIB=$HOME/jogl
export CP="$CSC405_LIB/jogl-all.jar:$CSC405_LIB/gluegen-rt.jar:$CSC405_LIB/jogl-all-natives-macosx-universal.jar:$CSC405_LIB/gluegen-rt-natives-macosx-universal.jar:$CSC405_LIB/joml-1.10.0.jar"

mkdir -p out
javac --release 11 -d out -cp "$CP" src/*.java

java -cp "out:$CP" JdkJomlCheck
java -cp "out:$CP" Program21
java -cp "out:$CP" Program22
java -cp "out:$CP" Program22 -triangle
java -cp "out:$CP" RotatingTriangle
```

Appendix B compiles with `javac code/*.java` and runs with `java code.Code` because the book's examples live in a `code` package. These tests use the default package and an `out/` directory, so the `-d out` / `-cp out:$CP` form above is the one that matches this repo.

`-XstartOnFirstThread` is **not** needed for JOGL. That flag is for LWJGL / GLFW. JOGL draws into an AWT `GLCanvas` inside a Swing `JFrame`, and AWT already starts on the AppKit thread on a Mac. Appendix B's run command is just `java code.Code`.

Mac-specific caveats from Appendix B that these tests already account for, or that will matter later in the course:

- Force a modern core profile with `GLProfile.getMaxProgrammableCore(true)` (done automatically on macOS).
- Change `#version 430` to `#version 410` (the `shaders/410` copies; selected automatically on macOS).
- OpenGL 4.1 does not support `layout(binding=...)` qualifiers on texture samplers. That first appears in Chapter 5, not in these tests. Appendix B shows the replacement `glUniform1i(gl.glGetUniformLocation(...), unit)` call.
- Compute shaders (Chapter 16) will not run on macOS; they arrived in OpenGL 4.3.
- On a Retina display, `gl_FragCoord` can report a pixel count twice the window size (Appendix B.2.4). These tests do not depend on fragment coordinates.

Or use the script: `./run.sh compile`, `./run.sh jdk`, `./run.sh info`, `./run.sh shader`, `./run.sh rotate`.

### Expected results

- `JdkJomlCheck` should print `JDK / JOML check: PASS`. The three JOML results are `(1, 2, 3)`, `(3, 4, 5)`, and `(0, 1, 0)`.
- `Program21` should open a red window and print vendor, renderer, version, GLSL version, and JOGL 2.4.x to the console.
- `Program22` should print `vertex compilation succeeded`, `fragment compilation succeeded`, `program link succeeded`, and `Shader compile/link test: PASS`, and show a blue point (or, with `-triangle`, a blue triangle).
- `RotatingTriangle` should show a cyan triangle spinning about the center of a black window.

If a shader fails to compile or link, the Program 2.3 helpers print the GLSL info log and `checkOpenGLError` output instead of failing silently — GLSL errors do not stop the Java process on their own, which is why the book insists on these checks.
