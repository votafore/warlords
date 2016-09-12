precision mediump float;

uniform vec3 u_camera;
uniform vec3 u_lightPosition;

varying vec3 v_vertex;
varying vec3 v_normal;
varying vec4 v_color;

void main() {

    vec3 n_normal=normalize(v_normal);
    vec3 lightvector = normalize(u_lightPosition - v_vertex);
    vec3 lookvector = normalize(u_camera - v_vertex);

    float ambient=0.5;
    float k_diffuse=0.8;
    float diffuse = k_diffuse * max(dot(n_normal, lightvector), 0.0);

    //float k_specular=0.4;
    //vec3 reflectvector = reflect(-lightvector, n_normal);
    //float specular = k_specular * pow( max(dot(lookvector,reflectvector),0.0), 40.0 );

    gl_FragColor = (ambient + diffuse) * v_color;
}