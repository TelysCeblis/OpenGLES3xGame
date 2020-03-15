package com.bn.Sample7_4;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES30;

public class TextureRect {

    int mProgram;//自定义渲染管线程序id
    int muMVPMatrixHandle;//总变换矩阵引用id   
    int muMMatrixHandle;//位置、旋转变换矩阵
    int uTexHandle;//外观纹理属性引用id

    int maCameraHandle; //摄像机位置属性引用id  
    int maPositionHandle; //顶点位置属性引用id  
    int maTexCoorHandle; //顶点纹理坐标属性引用id  

    String mVertexShader;//顶点着色器    	 
    String mFragmentShader;//片元着色器

    private FloatBuffer mVertexBuffer;//顶点坐标数据缓冲
    private FloatBuffer mTextureBuffer;//顶点着色数据缓冲
    int vCount;

    public TextureRect(final float UNIT_SIZE) {
        //初始化顶点坐标与着色数据
        initVertexData(UNIT_SIZE);
    }

    public void initVertexData(final float UNIT_SIZE) {
        //顶点坐标数据的初始化================begin============================
        vCount = 6;
        float vertices[] = new float[]
                {
                        -1 * UNIT_SIZE, 1 * UNIT_SIZE, 0,
                        -1 * UNIT_SIZE, -1 * UNIT_SIZE, 0,
                        1 * UNIT_SIZE, 1 * UNIT_SIZE, 0,

                        -1 * UNIT_SIZE, -1 * UNIT_SIZE, 0,
                        1 * UNIT_SIZE, -1 * UNIT_SIZE, 0,
                        1 * UNIT_SIZE, 1 * UNIT_SIZE, 0
                };

        //创建顶点坐标数据缓冲
        //vertices.length*4是因为一个整数四个字节
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mVertexBuffer = vbb.asFloatBuffer();//转换为int型缓冲
        mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
        mVertexBuffer.position(0);//设置缓冲区起始位置
        //特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        //转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
        //顶点坐标数据的初始化================end============================
        //顶点纹理数据的初始化================begin============================
        float textures[] = new float[]
                {
                        0, 1, 0, 0, 1, 1,
                        0, 0, 1, 0, 1, 1
                };
        //创建顶点纹理数据缓冲
        ByteBuffer tbb = ByteBuffer.allocateDirect(textures.length * 4);
        tbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mTextureBuffer = tbb.asFloatBuffer();//转换为Float型缓冲
        mTextureBuffer.put(textures);//向缓冲区中放入顶点着色数据
        mTextureBuffer.position(0);//设置缓冲区起始位置
        //特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        //转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
        //顶点纹理数据的初始化================end============================
    }

    //初始化shader
    public void intShader(MySurfaceView mv, int mProgram) {
        this.mProgram = mProgram;
        //获取程序中顶点位置属性引用id  
        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中顶点经纬度属性引用id   
        maTexCoorHandle = GLES30.glGetAttribLocation(mProgram, "aTexCoor");
        //获取程序中总变换矩阵引用id 
        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");
        //获取位置、旋转变换矩阵引用id
        muMMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMMatrix");
        //获取程序中摄像机位置引用id
        maCameraHandle = GLES30.glGetUniformLocation(mProgram, "uCamera");
        uTexHandle = GLES30.glGetUniformLocation(mProgram, "sTexture");
    }

    public void drawSelf(int texId) {
        //制定使用某套shader程序
        GLES30.glUseProgram(mProgram);
        //将最终变换矩阵传入shader程序
        GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
        //将位置、旋转变换矩阵传入shader程序
        GLES30.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.getMMatrix(), 0);
        //将摄像机位置传入shader程序
        GLES30.glUniform3fv(maCameraHandle, 1, MatrixState.cameraFB);

        //为画笔指定顶点位置数据
        GLES30.glVertexAttribPointer
                (
                        maPositionHandle,
                        3,
                        GLES30.GL_FLOAT,
                        false,
                        3 * 4,
                        mVertexBuffer
                );
        //为画笔指定顶点纹理坐标数据
        GLES30.glVertexAttribPointer
                (
                        maTexCoorHandle,
                        2,
                        GLES30.GL_FLOAT,
                        false,
                        2 * 4,
                        mTextureBuffer
                );
        //允许顶点位置数据数组
        GLES30.glEnableVertexAttribArray(maPositionHandle);
        GLES30.glEnableVertexAttribArray(maTexCoorHandle);
        //绑定纹理
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texId);
        GLES30.glUniform1i(uTexHandle, 0);

        //绘制三角形
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount);
    }
}
