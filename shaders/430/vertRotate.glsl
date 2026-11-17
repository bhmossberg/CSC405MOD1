#version 430

uniform mat4 mv_matrix;
void main(void)
{
	vec4 p;
	if (gl_VertexID == 0) p = vec4( 0.25, -0.25, 0.0, 1.0);
	else if (gl_VertexID == 1) p = vec4(-0.25, -0.25, 0.0, 1.0);
	else p = vec4( 0.25, 0.25, 0.0, 1.0);
	gl_Position = mv_matrix * p;
}
