#version 150
in vec3 vertColor;
in vec4 depthTexCoord;
in vec2 texCoord;
in vec3 normal;
in vec3 light;
in vec3 viewDirection;
in vec3 NdotL;

out vec4 outColor;

uniform sampler2D textureID;
uniform sampler2D depthTexture;

void main() {
	vec3 halfVector = normalize(normalize(light) + normalize(viewDirection));
	float NdotH = dot(normalize(normal), halfVector);

	vec4 ambient = vec4(0.5, 0.0, 0.0, 1.0);
	vec4 diffuse = vec4(normalize(NdotL) * vec3(0.0, 1.0, 0.0), 1.0);
	vec4 specular = vec4(pow(NdotH, 16) * vec4(0.0, 0.0, 1.0, 1.0));
	vec4 color = ambient + diffuse + specular;

	vec4 texColor = texture(textureID, texCoord);

	float z1 = texture(depthTexture, depthTexCoord.xy / depthTexCoord.w).r;
	float z2 = depthTexCoord.z / depthTexCoord.w;
	if (z1 < z2 - 0.0001) {
		outColor = texColor + ambient;
	} else {
		outColor = texColor + color;
	}
} 
