#ifndef __H_PBUFFER_WRAPPER_H__
#define __H_PBUFFER_WRAPPER_H__
#include <jni.h>

/*
 * Class:     com_panda_org_pbufferwrapper
 * Method:    getWrapperGLImage
 * Signature: (Landroid/graphics/Bitmap;)V
 */
JNIEXPORT void JNICALL Java_com_panda_org_pbufferwrapper_getWrapperGLImage
  (JNIEnv *, jclass, jobject);

#endif //__H_PBUFFER_WRAPPER_H__