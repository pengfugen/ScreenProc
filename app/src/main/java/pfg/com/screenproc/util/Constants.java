package pfg.com.screenproc.util;

/**
 * Created by FPENG3 on 2018/7/24.
 */

public class Constants {

    public static final int BYTES_PER_FLOAT = 4;

    public static final String texture_vertex_shader =
            "uniform mat4 u_Matrix;" +
                    "attribute vec4 a_Position;" +
                    "attribute vec2 a_TextureCoordinates;" +
                    "varying vec2 v_TextureCoordinates;" +
                    "void main() {" +
                    "  v_TextureCoordinates = a_TextureCoordinates;" +
                    "  gl_Position = u_Matrix * a_Position;" +
                    "}";

    public static final String texture_fragment_shader =
            "precision mediump float;" +
                    "uniform sampler2D u_TextureUnit;" +
                    "varying vec2 v_TextureCoordinates;" +
                    "void main() {" +
                    "  gl_FragColor = texture2D(u_TextureUnit, v_TextureCoordinates);" +
                    "}";
}
