# CSC405 Module 1 — Textbook Example Programs

Example code from Gordon, V. S., & Clevenger, J. (2021). Computer graphics programming in OpenGL with Java (3rd ed.). Mercury Learning and Information. Used for coursework study.

Official companion files were downloaded from the author’s page,
[Computer Graphics Programming in OpenGL with Java, 3rd Edition](https://athena.ecs.csus.edu/~gordonvs/textJ3E.html)
(`J3E_CompanionFiles.zip`). Each program folder is self-contained:
its `.java` sources, `.glsl` shaders, utility classes (`Utils.java`,
`Sphere.java`, `Torus.java`, `ImportedModel.java` as needed), and any
textures or models that program loads are copied into that folder.

**Assigned reading:** Chapter 1: Getting Started (no numbered example programs); Chapter 2, Sections 2.1–2.2.

## Programs in this module

### Program2_1 — First Java/JOGL Application

- **Section:** 2.1.1 Java/JOGL Application
- **Official folder:** `Chapter_02_OpenGLPipeline/Prog2_1_background`

### Program2_2 — Shaders, Drawing a POINT

- **Section:** 2.1.2 Vertex and Fragment Shaders
- **Official folder:** `Chapter_02_OpenGLPipeline/Prog2_2_point`

### Program2_3 — Modules to Catch GLSL Errors

- **Section:** 2.2 Detecting OpenGL and GLSL Errors
- **Official folder:** `Chapter_02_OpenGLPipeline/Prog2_3_errorChecking`
- **Note:** Official companion includes two versions: CodedVersion (the book’s custom error-check modules) and UsingJOGLerrorChecking (the JOGL built-in alternative discussed in the chapter).

### supplemental_queryingOpenGL — Querying OpenGL (supplemental, not a numbered book program)

- **Section:** Chapter 2 companion supplemental
- **Official folder:** `Chapter_02_OpenGLPipeline/supplemental_queryingOpenGL`
- **Note:** Not a numbered Program X.Y listing. Included because it ships with the Chapter 2 companion files and is useful for checking the OpenGL/GLSL version, which Module 1 also covers.

## Assigned sections without a numbered program

- Chapter 1 is installation and configuration only; there is no Program 1.x.
- Sections 2.1.3–2.1.7 describe later pipeline stages (tessellation, geometry shader, rasterization, fragment shader, pixel operations) and do not have their own numbered example programs in this chapter.

## Compile and run

These programs use the official companion layout: Java sources and shaders live in a `code/` package (`package code;`), and textures or `.obj` models sit in the program folder (the working directory when you run). Shaders are loaded with paths such as `code/vertShader.glsl`.

You need **JDK 11**, **JOGL 2.4** (`jogl-all.jar`, `gluegen-rt.jar`, plus the native jars for your OS), and **JOML 1.10** (`joml-1.10.0.jar`) on the classpath. The textbook’s Appendix A (Windows) and Appendix B (macOS) cover installation. On Linux, put the jars in a `lib` directory (or any folder you prefer) and run the following from a program folder, for example `Program2_5`:

```bash
# Adjust these paths to where you keep the jars
CP="lib/jogl-all.jar:lib/jogl-all-natives-linux-amd64.jar:lib/gluegen-rt.jar:lib/gluegen-rt-natives-linux-amd64.jar:lib/joml-1.10.0.jar"

# Compile (from the program folder that contains the code/ directory)
javac -cp "$CP" code/*.java

# Run (also from that program folder, so shader and texture paths resolve)
java -cp "$CP:." \
  --add-exports java.base/java.lang=ALL-UNNAMED \
  --add-exports java.desktop/sun.awt=ALL-UNNAMED \
  --add-exports java.desktop/sun.java2d=ALL-UNNAMED \
  -Dsun.java2d.d3d=false -Dsun.java2d.uiScale=1 \
  code.Code
```

On Windows, use `;` instead of `:` in the classpath, and the official `compile.bat` / `run.bat` in each folder assume the JOGL and JOML jars are already on your global `CLASSPATH` (as the book’s setup appendix describes):

```bat
javac code/Code.java
java --add-exports java.base/java.lang=ALL-UNNAMED --add-exports java.desktop/sun.awt=ALL-UNNAMED --add-exports java.desktop/sun.java2d=ALL-UNNAMED -Dsun.java2d.d3d=false -Dsun.java2d.uiScale=1 code.Code
```

Programs that open a window need a display; compiling does not.


This `textbook-examples` folder contains **16** files.
