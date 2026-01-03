package idk.core;

import android.content.Context;
import android.view.Choreographer;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.opengl.GLES30;
import android.opengl.EGL14;
import android.opengl.EGLSurface;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.GLES20;
import java.util.Random;


import androidx.annotation.NonNull;

public class CustomRenderer extends SurfaceView implements SurfaceHolder.Callback, Choreographer.VsyncCallback, Choreographer.FrameCallback {

    private boolean pause = false;
    private int width;
    private int height;
    private boolean surfaceAvailable = true;
    private Choreographer choreographer;

    public CustomRenderer(Context context) {
        super(context);
        init();
        choreographer = Choreographer.getInstance();
        choreographer.postFrameCallback(this);
    }

    private void init() {
        getHolder().addCallback(this);
    }

    private void egl(Object face) throws Exception {
        EGL14.eglBindAPI(EGL14.EGL_OPENGL_ES_API);
        EGLDisplay display = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);

        int[] version = new int[2];
        EGL14.eglInitialize(display, version, 0, version, 1);

        int[] attribList = {
                EGL14.EGL_RED_SIZE, 8,
                EGL14.EGL_GREEN_SIZE, 8,
                EGL14.EGL_BLUE_SIZE, 8,
                EGL14.EGL_ALPHA_SIZE, 8,
                EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                EGL14.EGL_NONE
        };
        EGLConfig[] configs = new EGLConfig[1];
        int[] numConfigs = new int[1];
        EGL14.eglChooseConfig(display, attribList, 0, configs, 0, 1, numConfigs, 0);

        int[] surfaceAttribs = {
                EGL14.EGL_NONE
        };
        EGLSurface surface = null;// EGL14.eglCreatePbufferSurface(display, configs[0], surfaceAttribs, 0);
        surface = EGL14.eglCreateWindowSurface(display, configs[0], face, surfaceAttribs, 0);

        int[] contextAttribs = {
                EGL14.EGL_CONTEXT_CLIENT_VERSION, 3,
                EGL14.EGL_NONE
        };
        EGLContext context = EGL14.eglCreateContext(display, configs[0], EGL14.EGL_NO_CONTEXT, contextAttribs, 0);


        EGL14.eglMakeCurrent(display, surface, surface, context);
        long l = System.currentTimeMillis();
        int i = 0;
        while (surfaceAvailable) {

            long ll = System.currentTimeMillis();
            if(ll - l > 1000){
                l = ll;
                System.out.println("FPS:"+i);
                i = 0 ;
            }
            while (pause && surfaceAvailable) {

            }
            if (!surfaceAvailable) {
                return;
            }
            EGL14.eglSwapBuffers(display, surface);
            swap--;
            GLES30.glClearColor(0, 0, (float) new Random().nextGaussian(), 1);
            GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
            GLES30.glFinish();
        }


    }

    public void pause() {
        pause = true;
    }

    public void resume() {
        pause = false;
    }
    private long swap;
    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        new Thread() {
            public void run() {
                try {
                    egl(surfaceHolder);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        width = i1;
        height = i2;
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        surfaceAvailable = false;
    }


    @Override
    public void onVsync(@NonNull Choreographer.FrameData frameData) {
        swap++;
    }

    @Override
    public void doFrame(long l) {
        swap++;
        choreographer.postFrameCallback(this);
    }
}