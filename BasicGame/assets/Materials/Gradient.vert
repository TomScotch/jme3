uniform mat4 g_WorldViewProjectionMatrix;



attribute vec4 inPosition;

varying vec4 pos;



void main() {

pos = inPosition* 2.0 - 1.0;

gl_Position = pos;

}
