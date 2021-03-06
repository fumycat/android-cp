precision mediump float;       	// Set the default precision to medium. We don't need as high of a
// precision in the fragment shader.
uniform sampler2D u_Texture;    // The input texture.
uniform vec4 u_Color;          	// This is the color from the vertex shader interpolated across the
// triangle per fragment.
varying vec2 v_TexCoordinate;   // Interpolated texture coordinate per fragment.

// The entry point for our fragment shader.
void main()
{
    // Multiply the color by the diffuse illumination level and texture value to get final output color.
    gl_FragColor = u_Color * texture2D(u_Texture, v_TexCoordinate);
}
