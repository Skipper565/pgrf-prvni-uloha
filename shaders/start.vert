#version 150
in vec2 inPosition;

out vec4 depthTexCoord;
out vec2 texCoord;
out vec3 normal;
out vec3 light;
out vec3 viewDirection;
out vec3 NdotL;

uniform float time;
uniform mat4 view;
uniform mat4 proj;
uniform int setting;
uniform mat4 lightVP;
uniform vec3 lightPosition;

const float PI = 3.14;

vec3 getWall(vec2 xy) {
	return vec3(xy * 2, -2.0);
}

vec3 getWallNormal(vec2 xy) {
	vec3 u = getWall(xy + vec2(0.001, 0)) - getWall(xy - vec2(0.001, 0));
	vec3 v = getWall(xy + vec2(0, 0.001)) - getWall(xy - vec2(0, 0.001));
	return cross(u, v);
}

vec3 getModifiedSphere(vec2 xy) {
	float r = 3/2;
	float s = (PI * 10 - PI * xy.y) - 2;
	float t = 10 * PI * xy.x;

	float x = r * cos(t) * cos(s);
	float y = r * cos(t) * sin(s) * sin(t);
	float z = r * sin(t);
	return vec3(x+2, y+0.5, z);
}

vec3 getModifiedSphereNormal(vec2 xy) {
	vec3 u = getModifiedSphere(xy + vec2(0.001, 0)) - getModifiedSphere(xy - vec2(0.001, 0));
	vec3 v = getModifiedSphere(xy + vec2(0, 0.001)) - getModifiedSphere(xy - vec2(0, 0.001));
	return cross(u, v);
}

vec3 getParametric(vec2 xy) {
	float t = xy.x;
	float s = xy.y * (2 * PI);

	float x = t * cos(s);
	float y = t * sin(s);
	float z = t;
	return vec3(x, y, z);
}

vec3 getParametricNormal(vec2 xy) {
	vec3 u = getParametric(xy + vec2(0.001, 0)) - getParametric(xy - vec2(0.001, 0));
	vec3 v = getParametric(xy + vec2(0, 0.001)) - getParametric(xy - vec2(0, 0.001));
	return cross(u, v);
}

vec3 getParametric2(vec2 xy) {
	float t = 2 * PI * xy.y;
	float s = PI * 0.5 - PI * xy.x *2;

	float x = t * cos(s);
	float y = t * sin(s);
	float z = 2 * sin(t);
	return vec3(x, y, z) / 2;
}

vec3 getParametric2Normal(vec2 xy) {
	vec3 u = getParametric2(xy + vec2(0.001, 0)) - getParametric2(xy - vec2(0.001, 0));
	vec3 v = getParametric2(xy + vec2(0, 0.001)) - getParametric2(xy - vec2(0, 0.001));
	return cross(u, v);
}

vec3 getSpheric(vec2 xy) {
	float rho = 1;
	float phi = xy.x * PI;
	float theta = xy.y * (2 * PI);

	float x = rho * sin(phi) * cos(theta);
	float y = rho * sin(phi) * sin(theta);
	float z = rho * cos(phi);
	return vec3(x, y, z);
}

vec3 getSphericNormal(vec2 xy) {
	vec3 u = getSpheric(xy + vec2(0.001, 0)) - getSpheric(xy - vec2(0.001, 0));
	vec3 v = getSpheric(xy + vec2(0, 0.001)) - getSpheric(xy - vec2(0, 0.001));
	return cross(u, v);
}

vec3 getSpheric2(vec2 xy) {
	float az = xy.x * PI;
	float ze = xy.y * PI/2;
	float r = 1;

	float x = cos(az)*cos(ze)*r;
	float y = 2*sin(az)*cos(ze)*r;
	float z = 0.5*sin(ze)*r;
	return vec3(x, y, z);
}

vec3 getSpheric2Normal(vec2 xy) {
	vec3 u = getSpheric2(xy + vec2(0.001, 0)) - getSpheric2(xy - vec2(0.001, 0));
	vec3 v = getSpheric2(xy + vec2(0, 0.001)) - getSpheric2(xy - vec2(0, 0.001));
	return cross(u, v);
}

vec3 getCylindric(vec2 xy) {
	float r = 1;
	float theta = xy.y * (2 * PI);
	float z = xy.x * 2;

	float x = r * cos(theta);
	float y = r * sin(theta);
	return vec3(x, y, z);
}

vec3 getCylindricNormal(vec2 xy) {
	vec3 u = getCylindric(xy + vec2(0.001, 0)) - getCylindric(xy - vec2(0.001, 0));
	vec3 v = getCylindric(xy + vec2(0, 0.001)) - getCylindric(xy - vec2(0, 0.001));
	return cross(u, v);
}

vec3 getCylindric2(vec2 xy) {
	float r = 5;
	float theta = xy.y * PI * cos(5);
	float z = xy.x;

	float x = r * cos(theta);
	float y = r * sin(theta);
	return vec3(x, y, z);
}

vec3 getCylindric2Normal(vec2 xy) {
	vec3 u = getCylindric2(xy + vec2(0.001, 0)) - getCylindric2(xy - vec2(0.001, 0));
	vec3 v = getCylindric2(xy + vec2(0, 0.001)) - getCylindric2(xy - vec2(0, 0.001));
	return cross(u, v);
}

void main() {
	vec2 timePosition = inPosition * 2 - 1;
	vec2 position = inPosition * 2 - 1;
	timePosition.x += cos(timePosition.x + (time / 2));
	vec3 finalPos;
	switch(setting) {
		case 7:
			finalPos = getWall(position);
			normal = getWallNormal(position);
			break;
		case 8:
			finalPos = getModifiedSphere(position);
			normal = getModifiedSphereNormal(position);
			break;
		case 1:
			finalPos = getParametric(timePosition);
			normal = getParametricNormal(timePosition);
			break;
		case 2:
			finalPos = getParametric2(timePosition);
			normal = getParametric2Normal(timePosition);
			break;
		case 3:
			finalPos = getSpheric(timePosition);
			normal = getSphericNormal(timePosition);
			break;
		case 4:
			finalPos = getSpheric2(timePosition);
			normal = getSpheric2Normal(timePosition);
			break;
		case 5:
			finalPos = getCylindric(timePosition);
			normal = getCylindricNormal(timePosition);
			break;
		case 6:
			finalPos = getCylindric2(timePosition);
			normal = getCylindric2Normal(timePosition);
			break;
	}

	gl_Position = proj * view * vec4(finalPos, 1.0);
	light = lightPosition - finalPos;
	NdotL = vec3(dot(normal, light));

	mat4 invView = inverse(view);
	vec3 eyePosition = vec3(invView[3][0], invView[3][1], invView[3][2]);
	viewDirection = eyePosition - finalPos;

	texCoord = inPosition;
	depthTexCoord = lightVP * vec4(finalPos, 1.0);
	depthTexCoord.xyz = (depthTexCoord.xyz + 1) / 2;
} 
