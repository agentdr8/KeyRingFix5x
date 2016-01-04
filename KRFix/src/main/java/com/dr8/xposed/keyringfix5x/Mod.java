package com.dr8.xposed.keyringfix5x;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getBooleanField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

public class Mod implements IXposedHookLoadPackage {

    private static final String TAG = "KR5xFix";
    private static boolean DEBUG = true;
    private static Activity acta;
    private static Activity actb;
    private static Activity actc;

    private static void log(String msg) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        String formattedDate = df.format(c.getTime());
        XposedBridge.log("[" + formattedDate + "] " + TAG + ": " + msg);
    }

    @SuppressWarnings("deprecation")
    public static void setCameraDisplayOrientation(Activity activity, int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    @SuppressWarnings("deprecation")
    public static Camera.Parameters setCameraParamRotation(Camera c) {
        Camera.Parameters cp = c.getParameters();
        cp.setRotation(180);
        return cp;
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {

        String targetpkg = "com.froogloid.kring.google.zxing.client.android";
        String camcls = "com.threegvision.products.inigma_sdk_pro.sdk_pro.CCamera";
        String actcls = "com.keyring.add_card.ScannerActivity";
        String piccls = "com.keyring.picture.temp.TakeCardPhotoActivity";
        String listcls = "com.keyring.shoppinglists.PictureActivity";
        String cmgrcls = "com.froogloid.kring.google.zxing.client.android.camera.CameraManager";
        String capcls = "com.keyring.picture.CaptureActivity2";

        if (loadPackageParam.packageName.equals(targetpkg)) {

            findAndHookMethod(actcls, loadPackageParam.classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                    if (DEBUG) log("hooked Key Ring ScannerActivity class");

                    acta = (Activity) param.thisObject;
                }
            });

            findAndHookMethod(listcls, loadPackageParam.classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                    if (DEBUG) log("hooked Key Ring PictureActivity class");

                    actb = (Activity) param.thisObject;
                }
            });

            findAndHookMethod(capcls, loadPackageParam.classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                    if (DEBUG) log("hooked Key Ring PictureActivity class");

                    actc = (Activity) param.thisObject;
                }
            });

            findAndHookMethod(camcls, loadPackageParam.classLoader, "Start", new XC_MethodHook() {
                @Override
                @SuppressWarnings("deprecation")
                protected void afterHookedMethod(XC_MethodHook.MethodHookParam mparam) throws Throwable {

                    if (DEBUG) log("hooked Key Ring SDK class");

                    Boolean openPending = getBooleanField(mparam.thisObject, "m_bOpenPending");
                    Camera c = (Camera) getObjectField(mparam.thisObject, "m_Camera");

                    if (c != null && !openPending) {
                        if (DEBUG) log("fixing camera preview rotation");
                        setCameraDisplayOrientation(acta, 0, c);
                        c.setParameters(setCameraParamRotation(c));
                    } else {
                        if (DEBUG) log("camera already closed or not open");
                    }
                }
            });

            findAndHookMethod(piccls, loadPackageParam.classLoader, "initCamera", SurfaceHolder.class, new XC_MethodHook() {
                @Override
                @SuppressWarnings("deprecation")
                protected void afterHookedMethod(XC_MethodHook.MethodHookParam mparam) throws Throwable {

                    if (DEBUG) log("hooked Key Ring picture.temp class");

                    Camera c = (Camera) getObjectField(mparam.thisObject, "mCamera");

                    if (c != null) {
                        if (DEBUG) log("fixing camera preview rotation");
                        setCameraDisplayOrientation(acta, 0, c);
                        c.setParameters(setCameraParamRotation(c));
                    } else {
                        if (DEBUG) log("camera already closed or not open");
                    }
                }
            });

            findAndHookMethod(listcls, loadPackageParam.classLoader, "initCamera", SurfaceHolder.class, new XC_MethodHook() {
                @Override
                @SuppressWarnings("deprecation")
                protected void afterHookedMethod(XC_MethodHook.MethodHookParam mparam) throws Throwable {

                    if (DEBUG) log("hooked Key Ring shoppinglist class");

                    Camera c = (Camera) getObjectField(mparam.thisObject, "mCamera");

                    if (c != null) {
                        if (DEBUG) log("fixing camera preview rotation");
                        setCameraDisplayOrientation(actb, 0, c);
                        c.setParameters(setCameraParamRotation(c));
                    } else {
                        if (DEBUG) log("camera already closed or not open");
                    }
                }
            });

            findAndHookMethod(capcls, loadPackageParam.classLoader, "initCamera", SurfaceHolder.class, new XC_MethodHook() {
                @Override
                @SuppressWarnings("deprecation")
                protected void afterHookedMethod(XC_MethodHook.MethodHookParam mparam) throws Throwable {

                    if (DEBUG) log("hooked Key Ring CaptureActivity2 class");

                    Camera c = (Camera) getObjectField(mparam.thisObject, "mCamera");

                    if (c != null) {
                        if (DEBUG) log("fixing camera preview rotation");
                        setCameraDisplayOrientation(actc, 0, c);
                        c.setParameters(setCameraParamRotation(c));
                    } else {
                        if (DEBUG) log("camera already closed or not open");
                    }
                }
            });

            findAndHookMethod(cmgrcls, loadPackageParam.classLoader, "openDriver", SurfaceHolder.class, new XC_MethodHook() {
                @Override
                @SuppressWarnings("deprecation")
                protected void afterHookedMethod(XC_MethodHook.MethodHookParam mparam) throws Throwable {

                    if (DEBUG) log("hooked Key Ring CamManager class");

                    Camera c = (Camera) getObjectField(mparam.thisObject, "camera");

                    if (c != null) {
                        if (DEBUG) log("fixing camera preview rotation");
                        setCameraDisplayOrientation(acta, 0, c);
                        c.setParameters(setCameraParamRotation(c));
                    } else {
                        if (DEBUG) log("camera already closed or not open");
                    }
                }
            });

        }
    }
}
