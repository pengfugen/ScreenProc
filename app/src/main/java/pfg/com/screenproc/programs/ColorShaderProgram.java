package pfg.com.screenproc.programs;

import android.content.Context;
import android.opengl.GLES30;

/**
 * Created by FPENG3 on 2018/7/25.
 */

public class ColorShaderProgram extends ShaderProgram {

    private final int uMatrixLocation;

    // Attribute locations
    private final int aPositionLocation;
    private final int aColorLocation;

    public ColorShaderProgram(Context context, String vertexShader, String fragmentShader) {
        super(context, vertexShader, fragmentShader);
        uMatrixLocation = GLES30.glGetUniformLocation(program, U_MATRIX);

        aPositionLocation = GLES30.glGetAttribLocation(program, A_POSITION);
        aColorLocation = GLES30.glGetAttribLocation(program, A_COLOR);
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

    public int getColorAttributeLocation() {
        return aColorLocation;
    }

}
