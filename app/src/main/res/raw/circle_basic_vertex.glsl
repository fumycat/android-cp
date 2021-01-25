uniform mat4 u_MVPMatrix;//The final transformation matrix
attribute vec4 a_Position;//Vertex position
varying vec4 vPosition;//The vertex position used to pass to the fragment shader
void main() {
     gl_Position = u_MVPMatrix * vec4(a_Position.x, a_Position.y, a_Position.z*0.5, a_Position.w);
     vPosition = a_Position;
}