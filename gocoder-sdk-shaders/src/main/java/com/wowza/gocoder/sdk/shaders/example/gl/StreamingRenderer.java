/**
 *  This is sample code provided by Wowza Media Systems, LLC.  All sample code is intended to be a reference for the
 *  purpose of educating developers, and is not intended to be used in any production environment.
 *
 *  IN NO EVENT SHALL WOWZA MEDIA SYSTEMS, LLC BE LIABLE TO YOU OR ANY PARTY FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL,
 *  OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS, ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION,
 *  EVEN IF WOWZA MEDIA SYSTEMS, LLC HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  WOWZA MEDIA SYSTEMS, LLC SPECIFICALLY DISCLAIMS ANY WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. ALL CODE PROVIDED HEREUNDER IS PROVIDED "AS IS".
 *  WOWZA MEDIA SYSTEMS, LLC HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 *
 * Originally based on the SimpleShaderActivity class from the sharedcam-example app
 * available at https://github.com/googlecreativelab/shadercam
 */

package com.wowza.gocoder.sdk.shaders.example.gl;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.GLES20;

import com.androidexperiments.shadercam.gl.CameraRenderer;

import com.wowza.gocoder.sdk.api.android.opengl.WZGLES;
import com.wowza.gocoder.sdk.api.broadcast.WZBroadcast;
import com.wowza.gocoder.sdk.api.broadcast.WZBroadcastConfig;
import com.wowza.gocoder.sdk.api.broadcast.WZGLBroadcaster;
import com.wowza.gocoder.sdk.api.geometry.WZSize;
import com.wowza.gocoder.sdk.api.render.WZRenderAPI;
import com.wowza.gocoder.sdk.api.status.WZStatusCallback;

/**
 * Example renderer that changes colors and tones of camera feed
 * based on touch position.
 */
public class StreamingRenderer extends CameraRenderer
    implements WZRenderAPI.VideoFrameRenderer
{
    private static final String TAG = StreamingRenderer.class.getSimpleName();

    /**
     * Wowza GoCoder SDK broadcaster and it's configuration properties
     */
    private WZBroadcast mWZBroadcast = null;
    private WZBroadcastConfig mWZBroadcastConfig = null;
    private WZSize mFrameSize = null;
    private WZGLBroadcaster mGLBroadcaster = null;

    private float offsetR = 0.5f;
    private float offsetG = 0.5f;
    private float offsetB = 0.5f;

    /**
     * By not modifying anything, our default shaders will be used in the assets folder of shadercam.
     *
     * Base all shaders off those, since there are some default uniforms/textures that will
     * be passed every time for the camera coordinates and texture coordinates
     */
    public StreamingRenderer(Context context, SurfaceTexture previewSurface, int width, int height)
    {
        super(context, previewSurface, width, height, "touchcolor.frag.glsl", "touchcolor.vert.glsl");

        mGLBroadcaster = new WZGLBroadcaster(EGL14.eglGetCurrentContext());
        mGLBroadcaster.setVideoFrameRenderer(this);

        mWZBroadcast = new WZBroadcast();
        mWZBroadcastConfig = new WZBroadcastConfig();

        mFrameSize = new WZSize(width, height); // invert since this app always runs in portrait
    }

    /**
     * we override {@link CameraRenderer#setUniformsAndAttribs()} and make sure to call the super so we can add
     * our own uniforms to our shaders here. CameraRenderer handles the rest for us automatically
     */
    @Override
    protected void setUniformsAndAttribs()
    {
        super.setUniformsAndAttribs();

        int offsetRLoc = GLES20.glGetUniformLocation(mCameraShaderProgram, "offsetR");
        int offsetGLoc = GLES20.glGetUniformLocation(mCameraShaderProgram, "offsetG");
        int offsetBLoc = GLES20.glGetUniformLocation(mCameraShaderProgram, "offsetB");

        GLES20.glUniform1f(offsetRLoc, offsetR);
        GLES20.glUniform1f(offsetGLoc, offsetG);
        GLES20.glUniform1f(offsetBLoc, offsetB);
    }

    /**
     * take touch points on that TextureView and turn them into multipliers for the color channels
     * of our shader, simple, yet effective way to illustrate how easy it is to integrate app
     * interaction into our glsl shaders
     * @param rawX raw x on screen
     * @param rawY raw y on screen
     */
    public void setTouchPoint(float rawX, float rawY)
    {
        offsetR = rawX / mSurfaceWidth;
        offsetG = rawY / mSurfaceHeight;
        offsetB = offsetR / offsetG;
    }

    public boolean isStreaming() {
        return !mGLBroadcaster.getBroadcasterStatus().isIdle();
    }

    public void startStreaming(final WZBroadcastConfig broadcastConfig,
                               final WZStatusCallback statusCallback) {

        mWZBroadcastConfig.set(broadcastConfig);

        // set the broadcast frame size to the camera renderer's frame size
        mWZBroadcastConfig.setVideoFrameSize(mFrameSize.asPortrait());
        // set the video source's frame size to the same as the camera renderer's frmae size
        mGLBroadcaster.getVideoSourceConfig().setVideoFrameSize(mFrameSize.asPortrait());

        // set the GL broadcaster as the video broadcaster
        mWZBroadcastConfig.setVideoBroadcaster(mGLBroadcaster);

        getRenderHandler().post(new Runnable() {
            @Override
            public void run() {
                // have to run this on the camera renderer's thread to get the current EGL context
                getPreviewSurface().makeCurrent();
                mGLBroadcaster.setEglContext(EGL14.eglGetCurrentContext());
                mWZBroadcast.startBroadcast(mWZBroadcastConfig,statusCallback);
            }
        });
    }

    public void stopStreaming(final WZStatusCallback statusCallback) {
        mWZBroadcast.endBroadcast(statusCallback);
    }

    @Override
    protected void encodeFrame(SurfaceTexture surfaceTexture) {
        if (mGLBroadcaster.getBroadcasterStatus().isRunning())
            mGLBroadcaster.onFrameAvailable(surfaceTexture.getTimestamp());

        // send to parent renderer
        super.encodeFrame(surfaceTexture);
    }

   /* START: WZRenderAPI.VideoFrameRenderer methods ----------------------------------------------------------------------------------------- */

    @Override
    public boolean isWZVideoFrameRendererActive() {
        // should always be true for broadcasters
        return true;
    }

    /**
     * Called at the beginning of a broadcast
     */
    @Override
    public void onWZVideoFrameRendererInit(WZGLES.EglEnv eglEnv) {
        // nothing to do here
    }

    /**
     * The callback invoked in response to an call to {@link WZGLBroadcaster#onFrameAvailable(long)} to render
     * the frame to be encoded
     * @param eglEnv The {@link com.wowza.gocoder.sdk.api.android.opengl.WZGLES.EglEnv} instance describing the OpenGL ES environment used
     *               to encode the video frames for streaming
     * @param frameSize The broadcast video frame size which will remain constant throughout a streaming session
     * @param frameRotation The video source's current orientation as set by a call to {@link WZGLBroadcaster#setFrameRotation(int)}
     */
    @Override
    public void onWZVideoFrameRendererDraw(WZGLES.EglEnv eglEnv, WZSize frameSize, int frameRotation) {
        // set the viewport to the broadcast stream's frame size and render
        setViewport(frameSize.getWidth(), frameSize.getHeight());
        draw();
    }

    /**
     * Called at the end of a broadcast
     */
    @Override
    public void onWZVideoFrameRendererRelease(WZGLES.EglEnv eglEnv) {
        // nothing to do here
    }

    /* END: WZRenderAPI.VideoFrameRenderer methods ----------------------------------------------------------------------------------------- */

}
