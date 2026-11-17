#!/usr/bin/env bash
# Build and run the CSC405 Module 1 environment tests on macOS or Linux.
# CLASSPATH uses the Unix/macOS colon separator.
#
# Usage:
#   export CSC405_LIB=/path/to/folder/with/jogl-and-joml-jars
#   ./run.sh compile
#   ./run.sh jdk
#   ./run.sh info          # Program 2.1 GL info window
#   ./run.sh shader        # Program 2.2 point (add -- -triangle for Program 2.5)
#   ./run.sh rotate        # FPSAnimator rotating triangle
#   ./run.sh jdk|info|shader|rotate -autoClose [millis]
#
# -XstartOnFirstThread is an LWJGL / GLFW flag. It is NOT needed for JOGL.
# JOGL uses AWT/Swing (GLCanvas inside a JFrame), which already starts on
# the AppKit thread on macOS.

set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"
cd "$ROOT"

# Folder that contains jogl-all.jar, gluegen-rt.jar, joml-1.10.0.jar, and the
# matching natives jars. Override with CSC405_LIB. The default below is only a
# convenience for the shared lab machine; on your own Mac put the jars wherever
# Appendix B describes and point CSC405_LIB at that folder.
if [[ -z "${CSC405_LIB:-}" ]]; then
  if [[ -d "$ROOT/../../libs" ]]; then
    CSC405_LIB="$ROOT/../../libs"
  else
    echo "Set CSC405_LIB to the folder that holds jogl-all.jar, gluegen-rt.jar," >&2
    echo "joml-1.10.0.jar, and the platform natives jars, then re-run." >&2
    exit 1
  fi
fi

# Natives jars should sit next to the Java jars (JOGL finds them automatically)
# and are also listed on the CLASSPATH so a split layout still works.
# Official 2.4 names use "macosx-universal" (the textbook's "macosc" is a typo).
CP="$CSC405_LIB/jogl-all.jar:$CSC405_LIB/gluegen-rt.jar:$CSC405_LIB/joml-1.10.0.jar"
for natives in \
    jogl-all-natives-linux-amd64.jar \
    gluegen-rt-natives-linux-amd64.jar \
    jogl-all-natives-linux-aarch64.jar \
    gluegen-rt-natives-linux-aarch64.jar \
    jogl-all-natives-macosx-universal.jar \
    gluegen-rt-natives-macosx-universal.jar
do
  if [[ -f "$CSC405_LIB/$natives" ]]; then
    CP="$CP:$CSC405_LIB/$natives"
  fi
done

# Prefer JAVA_HOME if it points at a JDK 11+, otherwise whatever javac is on PATH.
if [[ -n "${JAVA_HOME:-}" && -x "$JAVA_HOME/bin/javac" ]]; then
  JAVAC="$JAVA_HOME/bin/javac"
  JAVA="$JAVA_HOME/bin/java"
else
  JAVAC="$(command -v javac)"
  JAVA="$(command -v java)"
fi

if [[ -z "${JAVAC:-}" || -z "${JAVA:-}" ]]; then
  echo "javac/java not found. Install Java SE 11 (Appendix A / B) and put it on PATH." >&2
  exit 1
fi

CMD="${1:-compile}"
shift || true

compile() {
  mkdir -p out
  echo "Compiling with $JAVAC (--release 11)"
  "$JAVAC" --release 11 -d out -cp "$CP" src/*.java
  echo "Compiled classes are in $ROOT/out (gitignored)."
}

run() {
  local main="$1"; shift || true
  # Run from the repo root so shaders/430 and shaders/410 resolve.
  "$JAVA" -cp "out:$CP" "$main" "$@"
}

case "$CMD" in
  compile) compile ;;
  jdk)     compile; run JdkJomlCheck "$@" ;;
  info)    compile; run Program21 "$@" ;;
  shader)  compile; run Program22 "$@" ;;
  rotate)  compile; run RotatingTriangle "$@" ;;
  *)
    echo "Usage: $0 compile|jdk|info|shader|rotate [-autoClose [millis]] [-triangle]" >&2
    exit 2
    ;;
esac
