LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_SRC_FILES := pbufferwrapper.cpp
LOCAL_C_INCLUDES :=./
LOCAL_LDLIBS := -llog -lGLESv2 -landroid -ljnigraphics

LOCAL_MODULE    := wrapper

include $(BUILD_SHARED_LIBRARY)