package sample

import org.jetbrains.skia.RuntimeEffect
import org.jetbrains.skia.RuntimeShaderBuilder

class RetroGameShader {

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
            uniform float saturation;
            uniform float contrast;
            uniform float midPoint;
                     
            const int paletteSize = 8;
            const mat4 bayerMat = mat4( 1,  9,  3, 11,
                                       13,  5, 15,  7,
                                        4, 12,  2, 10,
                                       16,  8, 15,  6) / 16.;
            const float resolutionDivisor = 2.;                                              
            
            float sigmoidal_contrast(float value) {              
                return (1.0 / ( 1.0 + exp(contrast * (midPoint - value))) - 0.006693) * 1.013567;
            }
            
            mat4 saturationMatrix()
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
                int bayerIndex = int(mod(coord.x, 4)) + int(mod(coord.y, 4)) * 4;
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

            vec3 dither(vec3 color, vec2 coord) {
                vec2 bayerCoord = mod(floor(coord / resolutionDivisor), 4.);                               
                vec3 quantizationPeriod = vec3(1. / (8. - 1.));              
                float d = indexValue(bayerCoord);
                color += (d - 0.5) * quantizationPeriod;
                return color;
            }
            
            vec3 closestColor(vec3 color) {               
                vec3 closest = vec3(-2, 0, 0);                
                vec3 temp;
                for (int i = 0; i < paletteSize; i++) {
                    temp = getPalette(i);                    
                    float tempDistance = distance(temp, color);                   
                    if (tempDistance < distance(closest, color)) {
                        closest = temp;
                    }
                }
                return closest;
            }

            vec4 main(vec2 coord) {
              vec2 uv = floor(coord / resolutionDivisor) * resolutionDivisor;            
              vec4 o = iImage.eval(uv);
              o = vec4(sigmoidal_contrast(o.r), sigmoidal_contrast(o.g), sigmoidal_contrast(o.b), 1.);
              o = saturationMatrix() * o;           
              o = vec4(dither(o.rgb, coord), 1.);
              o = vec4(closestColor(o.rgb), 1.);
              return o;
            }
            """

        private val runtimeEffect = RuntimeEffect.makeForShader(sksl)
        val shaderBuilder = RuntimeShaderBuilder(runtimeEffect)
    }
}
