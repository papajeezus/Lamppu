#include <fcntl.h>
#include <sys/ioctl.h>
#include <jni.h>

int led_status;
int dev;
int ioctlResult = 1;

JNIEXPORT jboolean JNICALL Java_com_vmt_lamppu_LamppuService_openDev(JNIEnv* env, jobject thiz)
{
    dev = open("/dev/msm_camera/config0", O_RDWR);
    if(dev != -1)
	return JNI_TRUE;
    else
	return JNI_FALSE;
}

JNIEXPORT jboolean JNICALL Java_com_vmt_lamppu_LamppuService_ledLow(JNIEnv* env, jobject thiz)
{
    led_status = 1;
    if(ioctlResult = ioctl(dev, _IOW('m', 22, unsigned *), &led_status))
	return JNI_TRUE;
    else
	return JNI_FALSE;
}

JNIEXPORT jboolean JNICALL Java_com_vmt_lamppu_LamppuService_ledHigh(JNIEnv* env, jobject thiz)
{
    led_status = 2;
    if(ioctlResult = ioctl(dev, _IOW('m', 22, unsigned *), &led_status))
	return JNI_TRUE;
    else
	return JNI_FALSE;

}

JNIEXPORT jboolean JNICALL Java_com_vmt_lamppu_LamppuService_ledOff(JNIEnv* env, jobject thiz)
{
    led_status = 0;
    if(ioctlResult = ioctl(dev, _IOW('m', 22, unsigned *), &led_status))
	return JNI_TRUE;
    else
	return JNI_FALSE;

}

JNIEXPORT jboolean JNICALL Java_com_vmt_lamppu_LamppuService_closeDev(JNIEnv* env, jobject thiz)
{
    if (dev > 0) {
	close(dev);
	return JNI_TRUE;
    }
    else
	return JNI_FALSE;
}
