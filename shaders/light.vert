#version 150
in vec2 inPosition;// input from the vertex buffer

uniform float lightTime;
uniform mat4 lightView;
uniform mat4 lightProj;
uniform int lightSetting;

const float PI = 3.1415;

vec3 getModifiedSphere(vec2 xy) {
    float r = 3/2;
    float s = (PI * 10 - PI * xy.y) - 2;
    float t = 10 * PI * xy.x;

    float x = r * cos(t) * cos(s);
    float y = r * cos(t) * sin(s) * sin(t);
    float z = r * sin(t);
    return vec3(x+2, y+0.5, z);
}

vec3 getWall(vec2 xy) {
    return vec3(xy * 2, -2.0);
}

vec3 getParametric(vec2 xy) {
    float t = xy.x;
    float s = xy.y * (2 * PI);

    float x = t * cos(s);
    float y = t * sin(s);
    float z = t;
    return vec3(x, y, z);
}

vec3 getParametric2(vec2 xy) {
    float t = 2 * PI * xy.y;
    float s = PI * 0.5 - PI * xy.x *2;

    float x = t * cos(s);
    float y = t * sin(s);
    float z = 2 * sin(t);
    return vec3(x, y, z) / 2;
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

vec3 getSpheric2(vec2 xy) {
    float az = xy.x * PI;
    float ze = xy.y * PI/2;
    float r = 1;

    float x = cos(az) * cos(ze) * r;
    float y = 2 * sin(az) * cos(ze) * r;
    float z = 0.5 * sin(ze) * r;
    return vec3(x, y, z);
}

vec3 getCylindric(vec2 xy) {
    float r = 1;
    float theta = xy.y * (2 * PI);
    float z = xy.x * 2;

    float x = r * cos(theta);
    float y = r * sin(theta);
    return vec3(x, y, z);
}

vec3 getCylindric2(vec2 xy) {
    float r = 5;
    float theta = xy.y * PI * cos(5);
    float z = xy.x;

    float x = r * cos(theta);
    float y = r * sin(theta);
    return vec3(x, y, z);
}

void main() {
    vec2 timePosition = inPosition * 2 - 1;
    vec2 position = inPosition * 2 - 1;
    timePosition.x += cos(timePosition.x + (lightTime / 2));
    vec3 finalPos;
    switch (lightSetting) {
        case 7:
            finalPos = getWall(position);
            break;
        case 8:
            finalPos = getModifiedSphere(position);
            break;
        case 1:
            finalPos = getParametric(timePosition);
            break;
        case 2:
            finalPos = getParametric2(timePosition);
            break;
        case 3:
            finalPos = getSpheric(timePosition);
            break;
        case 4:
            finalPos = getSpheric2(timePosition);
            break;
        case 5:
            finalPos = getCylindric(timePosition);
            break;
        case 6:
            finalPos = getCylindric2(timePosition);
            break;
    }

    gl_Position = lightProj * lightView * vec4(finalPos, 1.0);
}