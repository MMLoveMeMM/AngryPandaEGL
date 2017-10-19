#include <jni.h>
#include "Shape.h"
#include "glm/mat4x4.hpp"
#include "glm/ext.hpp"
Shape mShape;
glm::mat4 projection;
glm::mat4 view;
//glm::mat4 module;
/*
* glm 是数学库,如矩阵运算
*/
extern "C" {
JNIEXPORT void JNICALL
Java_com_panda_org_glwrapper_GLRendererExt_nativeOnSurfaceCreated(JNIEnv *env, jclass type,
                                                                 jstring vertexShaderCode_,
                                                                 jstring fragmentShaderCode_) {
    const char *vertexShaderCode = env->GetStringUTFChars(vertexShaderCode_, 0);
    const char *fragmentShaderCode = env->GetStringUTFChars(fragmentShaderCode_, 0);

    // TODO

    mShape.initGL(vertexShaderCode, fragmentShaderCode);

    env->ReleaseStringUTFChars(vertexShaderCode_, vertexShaderCode);
    env->ReleaseStringUTFChars(fragmentShaderCode_, fragmentShaderCode);
    glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
    glDisable(GL_DEPTH_TEST);
}

JNIEXPORT void JNICALL
Java_com_panda_org_glwrapper_GLRendererExt_nativeOnDrawFrame(JNIEnv *env, jclass type,
                                                                 jfloat angleX, jfloat angleY) {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    /*
    module = glm::rotate(module, angleX, glm::vec3(1,0,0));
    module = glm::rotate(module, angleY, glm::vec3(0,1,0));
     */
    glm::mat4 mvpMatrix = projection * view/* * module*/;
    float *mvp = (float *) glm::value_ptr(mvpMatrix);
    // TODO
    mShape.draw(mvp);

}
JNIEXPORT void JNICALL
Java_com_panda_org_glwrapper_GLRendererExt_nativeOnSurfaceChanged(JNIEnv *env, jclass type,
                                                                           jint width,
                                                                           jint height) {
    /*
    * 采用正交透视方式
    */
    projection = glm::ortho(-1.0f, 1.0f, -(float) height / width, (float) height / width, 5.0f,
                            7.0f);
//    projection = glm::perspective(glm::radians(50.0f), (float)width/height, 5.0f ,7.0f);
    view = glm::lookAt(glm::vec3(0.0f, 0.0f, 6.0f),
                       glm::vec3(0.0f, 0.0f, 0.0f),
                       glm::vec3(0.0f, 1.0f, 0.0f));
    glViewport(0, 0, width, height);
}
}