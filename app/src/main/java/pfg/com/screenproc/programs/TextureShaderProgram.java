package pfg.com.screenproc.programs;

import android.content.Context;
import android.opengl.GLES30;

/**
 * Created by FPENG3 on 2018/7/24.
 */

public class TextureShaderProgram extends ShaderProgram {
    // Uniform locations
    private final int uMatrixLocation;
    private final int uTextureUnitLocation;

    // Attribute locations
    private final int aPositionLocation;
    private final int aTextureCoordinatesLocation;


    public TextureShaderProgram(Context context, String vertexShader, String fragmentShader) {
        super(context, vertexShader, fragmentShader);
        uMatrixLocation = GLES30.glGetUniformLocation(program, U_MATRIX);
        uTextureUnitLocation = GLES30.glGetUniformLocation(program, U_TEXTURE_UNIT);

        aPositionLocation = GLES30.glGetAttribLocation(program, A_POSITION);
        aTextureCoordinatesLocation = GLES30.glGetAttribLocation(program, A_TEXTURE_COORDINATES);
    }

    public void setUniform(float[] matrix, int textureId) {
        GLES30.glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);

        // Set the active texture unit to texture unit 0
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);
    }

    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }

    public int getTextureCoordinatesAttributeLocation() {
        return aTextureCoordinatesLocation;
    }
}
