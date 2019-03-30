#version 130
in vec2 inPosition; // input from the vertex buffer
in vec3 inColor; // input from the vertex buffer
out vec3 vertColor; // output from this shader to the next pipeline stage
//uniform float time; // variable constant for all vertices in a single draw

uniform mat4 proj;
uniform mat4 view;

void main() {
	//vec2 position = inPosition;
	//position.x += 0.1;
	//position.y += cos(position.x + time);
	gl_Position = proj * view * vec4(inPosition, 0.0, 1.0);
	vertColor = inColor;
} 
