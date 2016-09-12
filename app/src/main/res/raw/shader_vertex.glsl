uniform mat4 u_Matrix;
uniform vec4 u_color;

attribute vec3 a_position;
attribute vec3 a_normal;

varying vec3 v_vertex;
varying vec3 v_normal;
varying vec4 v_color;

void main()
{
    v_color = u_color;
    v_vertex = a_position;

    vec3 norm = normalize(a_normal);
    v_normal = norm;

    gl_Position = u_Matrix * vec4(a_position, 1.0);
}