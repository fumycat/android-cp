precision mediump float;
varying vec4 vPosition;
void main() {
    float uR = 0.6;//The radius of the ball
    vec4 color;
    float n = 8.0;//divided into n layers, n columns and n rows
    float span = 2.0*uR/n;//square length

    //Calculate the number of ranks
    int i = int((vPosition.x + uR)/span);//number of rows
    int j = int((vPosition.y + uR)/span);//number of layers
    int k = int((vPosition.z + uR)/span);//Number of columns
        int colorType = int(mod(float(i+j+k),2.0));

    if(colorType == 1) {//green when odd number
            color = vec4(0.2,1.0,0.129,0);
    } else {
    //White when even number
        color = vec4(1.0,1.0,1.0,0);//white
    }
    // Give the calculated color to this piece
    gl_FragColor = color;
 }