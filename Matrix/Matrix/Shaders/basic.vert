#version 330

uniform mat3 projection;

in vec2 position;
in vec4 color;

out vec4 vertColor;

void main() {
    vec3 projected = projection * vec3(position, 1);
	gl_Position = vec4(projected.xy, 0.0, 1.0);
	vertColor = color;
}
