package sample

import org.jetbrains.skia.RuntimeEffect
import org.jetbrains.skia.RuntimeShaderBuilder

class SigmoidalContrast {

    companion object {
        private const val sksl = """
            uniform shader content;
            uniform shader iImage;
            uniform vec3 palette0;
            uniform vec3 palette1;
            uniform vec3 palette2;
            uniform vec3 palette3;
            uniform vec3 palette4;
            uniform vec3 palette5;
            uniform vec3 palette6;
            uniform vec3 palette7;
            const int paletteSize = 8;
            const mat4 bayerMat = mat4( 0,  8,  2, 10,
                                       12,  4, 14,  6,
                                        3, 11,  1,  9,
                                       15,  7, 13,  5) / 16.0;
            
            float scurve(float value, float amount, float correction) {
              float curve = 1.0; 
                if (value < 0.5) {
                  curve = pow(value, amount) * pow(2.0, amount) * 0.5; 
                } else {
                  curve = 1.0 - pow(1.0 - value, amount) * pow(2.0, amount) * 0.5;
                }
                return pow(curve, correction);
            }
            
            mat4 saturationMatrix(float saturation)
            {
                vec3 luminance = vec3( 0.3086, 0.6094, 0.0820 );
                float oneMinusSat = 1.0 - saturation;
                vec3 red = vec3( luminance.x * oneMinusSat );
                red+= vec3( saturation, 0, 0 );
                vec3 green = vec3( luminance.y * oneMinusSat );
                green += vec3( 0, saturation, 0 );
                vec3 blue = vec3( luminance.z * oneMinusSat );
                blue += vec3( 0, 0, saturation );
                return mat4( red,     0,
                             green,   0,
                             blue,    0,
                             0, 0, 0, 1 );
            }

            float indexValue(vec2 coord) {
                int bayerIndex = int(mod(coord.x, 4) + mod(coord.y, 4) * 4.);
                if(bayerIndex == 0) return bayerMat[0][0];
                if(bayerIndex == 1) return bayerMat[0][1];
                if(bayerIndex == 2) return bayerMat[0][2];
                if(bayerIndex == 3) return bayerMat[0][3];
                if(bayerIndex == 4) return bayerMat[1][0];
                if(bayerIndex == 5) return bayerMat[1][1];
                if(bayerIndex == 6) return bayerMat[1][2];
                if(bayerIndex == 7) return bayerMat[1][3];
                if(bayerIndex == 8) return bayerMat[2][0];
                if(bayerIndex == 9) return bayerMat[2][1];
                if(bayerIndex == 10) return bayerMat[2][2];
                if(bayerIndex == 11) return bayerMat[2][3];
                if(bayerIndex == 12) return bayerMat[3][0];
                if(bayerIndex == 13) return bayerMat[3][1];
                if(bayerIndex == 14) return bayerMat[3][2];
                if(bayerIndex == 15) return bayerMat[3][3];
                return 10.;
            }

            float hueDistance(float h1, float h2) {
                float diff = abs((h1 - h2));
                return min(abs((1.0 - diff)), diff);
            }                   
                      
            vec3 getPalette(int index) {
               if (index == 0) return palette0;
               if (index == 1) return palette1;
               if (index == 2) return palette2;
               if (index == 3) return palette3;
               if (index == 4) return palette4;
               if (index == 5) return palette5;
               if (index == 6) return palette6;
               return palette7;
            }
            
            vec3 hslToRgb(vec3 c ) {
               vec3 rgb = clamp( abs(mod(c.x*6.0+vec3(0.0,4.0,2.0),6.0)-3.0)-1.0, 0.0, 1.0 );
               return c.z + c.y * (rgb-0.5)*(1.0-abs(2.0*c.z-1.0));
            }
            
            vec3 rgbToHsl(vec3 c ){
                float h = 0.0;
                float s = 0.0;
                float l = 0.0;
                float r = c.r;
                float g = c.g;
                float b = c.b;
                float cMin = min( r, min( g, b ) );
                float cMax = max( r, max( g, b ) );
            
                l = ( cMax + cMin ) / 2.0;
                if ( cMax > cMin ) {
                    float cDelta = cMax - cMin;                   
                    s = l < .0 ? cDelta / ( cMax + cMin ) : cDelta / ( 2.0 - ( cMax + cMin ) );
                    
                    if ( r == cMax ) {
                        h = ( g - b ) / cDelta;
                    } else if ( g == cMax ) {
                        h = 2.0 + ( b - r ) / cDelta;
                    } else {
                        h = 4.0 + ( r - g ) / cDelta;
                    }
            
                    if ( h < 0.0) {
                        h += 6.0;
                    }
                    h = h / 6.0;
                }
                return vec3( h, s, l );
            }

            mat3 closestColors(float hue) {               
                vec3 closest = vec3(-2, 0, 0);
                vec3 secondClosest = vec3(-2, 0, 0);
                vec3 temp;
                for (int i = 0; i < paletteSize; ++i) {
                    temp = rgbToHsl(getPalette(i));                    
                    float tempDistance = hueDistance(temp.x, hue);                   
                    if (tempDistance < hueDistance(closest.x, hue)) {
                        secondClosest = closest;
                        closest = temp;
                    } else {
                        if (tempDistance < hueDistance(secondClosest.x, hue)) {
                            secondClosest = temp;
                        }
                    }
                }
                return mat3( closest,       
                             secondClosest,
                             0, 0, 0);
            }            

            vec3 dither(vec3 color, vec2 coord) {
                vec3 hsl = rgbToHsl(color);
                mat3 colors = closestColors(hsl.x);
                vec3 closestColor = vec3(colors[0][0], colors[0][1], colors[0][2]);
                vec3 secondClosestColor = vec3(colors[1][0], colors[1][1], colors[1][2]);
                float d = indexValue(coord);
                float hueDiff = hueDistance(hsl.x, closestColor.x) /
                                hueDistance(secondClosestColor.x, closestColor.x);
                return hslToRgb(hueDiff < d ? closestColor : secondClosestColor);
            }

            vec4 main(vec2 coord) {
              vec4 o = iImage.eval(coord);
              o = vec4(scurve(o.r, 8.0, 0.5), scurve(o.g, 8.0, 0.5), scurve(o.b, 8.0, 0.5), 1.0);
              o = saturationMatrix(3.0) * o;
              return vec4(dither(o.rgb, coord), 1);
            }
            """

        private val runtimeEffect = RuntimeEffect.makeForShader(sksl)
        val shaderBuilder = RuntimeShaderBuilder(runtimeEffect)
    }
}
