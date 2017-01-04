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
 */

package com.wowza.gocoder.sdk.shaders.example;

import java.io.File;
import java.util.Arrays;

import android.Manifest;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import com.androidexperiments.shadercam.fragments.CameraFragment;
import com.androidexperiments.shadercam.fragments.PermissionsHelper;
import com.androidexperiments.shadercam.gl.CameraRenderer;
import com.androidexperiments.shadercam.utils.ShaderUtils;

import com.wowza.gocoder.sdk.api.WowzaGoCoder;
import com.wowza.gocoder.sdk.api.broadcast.WZBroadcastConfig;
import com.wowza.gocoder.sdk.api.logging.WZLog;
import com.wowza.gocoder.sdk.api.status.WZState;
import com.wowza.gocoder.sdk.api.status.WZStatus;
import com.wowza.gocoder.sdk.api.status.WZStatusCallback;

import com.wowza.gocoder.sdk.shaders.example.gl.StreamingRenderer;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * Based on SimpleShaderActivity written by Anthony Tripaldi
 *
 * Implementation of shader camera that can send a live video stream with the GoCoder SDK from Wowza Media Systems.
 */
public class StreamingShaderActivity extends FragmentActivity
        implements  CameraRenderer.OnRendererReadyListener,
                    PermissionsHelper.PermissionsListener,
                    WZStatusCallback
{
    private static final String TAG = StreamingShaderActivity.class.getSimpleName();
    private static final String TAG_CAMERA_FRAGMENT = "tag_camera_frag";

    /*
    *  GoCoder SDK-related properties
    */

    //
    // NOTE: You must update the app specified in gocoder-sdk-shaders/build.gradle
    // and provide a license key corresponding to the app id here
    //
    private static final String GOCODER_SDK_LICENSE_KEY = "GOSK-1243-0101-AB5F-A560-6EC5";
    private static WowzaGoCoder sGoCoderSDK = null;

    private WZBroadcastConfig mWZBroadcastConfig = null;

    /**
     * filename for our test video output
     */
    private static final String TEST_VIDEO_FILE_NAME = "test_video.mp4";

    /**
     * We inject our views from our layout xml here using {@link ButterKnife}
     */
    @InjectView(R.id.texture_view) TextureView mTextureView;
    @InjectView(R.id.sw_target_type) Switch mTargetType;
    @InjectView(R.id.btn_target_control) ImageButtonExt mTargetControl;
    @InjectView(R.id.btn_swap_camera) ImageButtonExt mSwapCamera;

    /**
     * Custom fragment used for encapsulating all the {@link android.hardware.camera2} apis.
     */
    private CameraFragment mCameraFragment;

    /**
     * Our custom renderer for this example, which extends {@link CameraRenderer} and then adds custom
     * shaders, which turns stuff green, which is easy.
     */
    private StreamingRenderer mRenderer;

    /**
     * boolean for triggering restart of camera after completed rendering
     */
    private boolean mRestartCamera = false;

    private PermissionsHelper mPermissionsHelper;
    private boolean mPermissionsSatisfied = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);

        setupCameraFragment();
        setupInteraction();

        // Enable detailed logging from the GoCoder SDK
        WZLog.LOGGING_ENABLED = true;

        // Initialize the GoCoder SDK
        if (sGoCoderSDK == null) {
            sGoCoderSDK = WowzaGoCoder.init(this, GOCODER_SDK_LICENSE_KEY);

            if (sGoCoderSDK == null) {
                WZLog.error(TAG, WowzaGoCoder.getLastError());
                Toast.makeText(this, WowzaGoCoder.getLastError().getErrorDescription(), Toast.LENGTH_LONG).show();
            } else {
                mWZBroadcastConfig  = new WZBroadcastConfig();
                mWZBroadcastConfig.setAudioEnabled(false);

                //
                // NOTE: Provide your specific WSE server settings here
                //
                mWZBroadcastConfig.setHostAddress("192.168.1.246");
                mWZBroadcastConfig.setPortNumber(1935);
                mWZBroadcastConfig.setApplicationName("live");
                mWZBroadcastConfig.setStreamName("myStream");
                mWZBroadcastConfig.setUsername(null);
                mWZBroadcastConfig.setPassword(null);
            }
        }

        //setup permissions for M or start normally
        if(PermissionsHelper.isMorHigher())
            setupPermissions();
    }

    private void setupPermissions() {
        mPermissionsHelper = PermissionsHelper.attach(this);
        mPermissionsHelper.setRequestedPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        );
    }

    /**
     * create the camera fragment responsible for handling camera state and add it to our activity
     */
    private void setupCameraFragment()
    {
        if(mCameraFragment != null && mCameraFragment.isAdded())
            return;

        mSwapCamera.setEnabled(false);
        mTargetType.setEnabled(false);
        mTargetControl.setEnabled(false);

        mCameraFragment = CameraFragment.getInstance();
        mCameraFragment.setCameraToUse(CameraFragment.CAMERA_PRIMARY); //pick which camera u want to use, we default to forward
        mCameraFragment.setTextureView(mTextureView);

        //add fragment to our setup and let it work its magic
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(mCameraFragment, TAG_CAMERA_FRAGMENT);
        transaction.commit();
    }

    /**
     * add a listener for touch on our surface view that will pass raw values to our renderer for
     * use in our shader to control color channels.
     */
    private void setupInteraction() {
        mTextureView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(mRenderer instanceof StreamingRenderer) {
                    ((StreamingRenderer) mRenderer).setTouchPoint(event.getRawX(), event.getRawY());
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * Things are good to go and we can continue on as normal. If this is called after a user
     * sees a dialog, then onResume will be called next, allowing the app to continue as normal.
     */
    @Override
    public void onPermissionsSatisfied() {
        Log.d(TAG, "onPermissionsSatisfied()");
        mPermissionsSatisfied = true;
    }

    /**
     * User did not grant the permissions needed for out app, so we show a quick toast and kill the
     * activity before it can continue onward.
     * @param failedPermissions string array of which permissions were denied
     */
    @Override
    public void onPermissionsFailed(String[] failedPermissions) {
        Log.e(TAG, "onPermissionsFailed()" + Arrays.toString(failedPermissions));
        mPermissionsSatisfied = false;
        Toast.makeText(this, "shadercam needs all permissions to function, please try again.", Toast.LENGTH_LONG).show();
        this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "onResume()");
        mTargetType.setVisibility(sGoCoderSDK != null ? View.VISIBLE : View.GONE);

        ShaderUtils.goFullscreen(this.getWindow());

        /**
         * if we're on M and not satisfied, check for permissions needed
         * {@link PermissionsHelper#checkPermissions()} will also instantly return true if we've
         * checked prior and we have all the correct permissions, allowing us to continue, but if its
         * false, we want to {@code return} here so that the popup will trigger without {@link #setReady(SurfaceTexture, int, int)}
         * being called prematurely
         */
        //
        if(PermissionsHelper.isMorHigher() && !mPermissionsSatisfied) {
            if(!mPermissionsHelper.checkPermissions())
                return;
            else
                mPermissionsSatisfied = true; //extra helper as callback sometimes isnt quick enough for future results
        }

        if(!mTextureView.isAvailable())
            mTextureView.setSurfaceTextureListener(mTextureListener); //set listener to handle when its ready
        else
            setReady(mTextureView.getSurfaceTexture(), mTextureView.getWidth(), mTextureView.getHeight());
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mRenderer != null && mRenderer.isStreaming()) {
            final WZStatus streamStatus = new WZStatus(WZState.STOPPING);

            mRenderer.stopStreaming(new WZStatusCallback() {
                @Override
                public void onWZStatus(WZStatus wzStatus) {
                    if (wzStatus.isIdle())
                        streamStatus.setState(wzStatus.getState());
                }

                @Override
                public void onWZError(WZStatus wzStatus) {

                }
            });

            streamStatus.waitForState(WZState.IDLE);
        }

        shutdownCamera(false);
        mTextureView.setSurfaceTextureListener(null);
    }

    /**
     * {@link ButterKnife} uses annotations to make setting {@link android.view.View.OnClickListener}'s
     * easier than ever with the {@link OnClick} annotation.
     */

    @OnClick(R.id.btn_target_control)
    public void onClickTargetControl()
    {
        mSwapCamera.setEnabled(false);
        mTargetType.setEnabled(false);

        if(mRenderer.isStreaming()) {
            mTargetControl.setImageResource(android.R.drawable.ic_media_play);
            mTargetControl.setEnabled(false);
            mRenderer.stopStreaming(this);
        } else if (mRenderer.isRecording() ){
            mRenderer.stopRecording();
            mTargetControl.setImageResource(android.R.drawable.ic_media_play);

            Toast.makeText(this, "File recording complete: " + getOutputFile().getAbsolutePath(), Toast.LENGTH_LONG).show();

            //restart so surface is recreated
            shutdownCamera(true);
        } else if (mTargetType.isChecked()) {
            // Stream
            mTargetControl.setEnabled(false);
            mRenderer.startStreaming(mWZBroadcastConfig, this);
        } else {
            // MP4
            mTargetControl.setImageResource(android.R.drawable.ic_media_pause);
            mRenderer.startRecording(getOutputFile());
        }
    }

    @OnCheckedChanged(R.id.sw_target_type)
    public void onChangeTarget()
    {
        mTargetType.setText(mTargetType.isChecked() ? R.string.target_stream : R.string.target_mp4);
    }

    @OnClick(R.id.btn_swap_camera)
    public void onClickSwapCamera()
    {
        mCameraFragment.swapCamera();
    }

    /**
     * called whenever surface texture becomes initially available or whenever a camera restarts after
     * completed recording or resuming from onpause
     * @param surface {@link SurfaceTexture} that we'll be drawing into
     * @param width width of the surface texture
     * @param height height of the surface texture
     */
    protected void setReady(SurfaceTexture surface, int width, int height) {
        mRenderer = getRenderer(surface, width, height);

        mRenderer.setCameraFragment(mCameraFragment);
        mRenderer.setOnRendererReadyListener(this);

        mRenderer.start();

        //initial config if needed
        mCameraFragment.configureTransform(width, height);
    }

    /**
     * Override this method for easy usage of stock example setup, allowing for easy
     * recording with any shader.
     */
    protected StreamingRenderer getRenderer(SurfaceTexture surface, int width, int height) {
        return new StreamingRenderer(this, surface, width, height);
    }

    private File getOutputFile()
    {
        return new File(Environment.getExternalStorageDirectory(), TEST_VIDEO_FILE_NAME);
    }

    /**
     * kills the camera in camera fragment and shuts down the render thread
     * @param restart whether or not to restart the camera after shutdown is complete
     */
    private void shutdownCamera(boolean restart)
    {
        //make sure we're here in a working state with proper permissions when we kill the camera
        if(PermissionsHelper.isMorHigher() && !mPermissionsSatisfied) return;

        //check to make sure we've even created the cam and renderer yet
        if(mCameraFragment == null || mRenderer == null) return;

        mSwapCamera.setEnabled(false);
        mTargetControl.setEnabled(false);
        mTargetType.setEnabled(false);

        mCameraFragment.closeCamera();

        mRestartCamera = restart;
        mRenderer.getRenderHandler().sendShutdown();
        mRenderer = null;
    }

    /**
     * Interface overrides from our {@link com.androidexperiments.shadercam.gl.CameraRenderer.OnRendererReadyListener}
     * interface. Since these are being called from inside the CameraRenderer thread, we need to make sure
     * that we call our methods from the {@link #runOnUiThread(Runnable)} method, so that we don't
     * throw any exceptions about touching the UI from non-UI threads.
     *
     * Another way to handle this would be to create a Handler/Message system similar to how our
     * {@link com.androidexperiments.shadercam.gl.CameraRenderer.RenderHandler} works.
     */
    @Override
    public void onRendererReady() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCameraFragment.setPreviewTexture(mRenderer.getPreviewTexture());
                mCameraFragment.openCamera();

                mSwapCamera.setEnabled(true);
                mTargetControl.setEnabled(true);
                mTargetType.setEnabled(true);
            }
        });
    }

    @Override
    public void onRendererFinished() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mRestartCamera) {
                    setReady(mTextureView.getSurfaceTexture(), mTextureView.getWidth(), mTextureView.getHeight());
                    mRestartCamera = false;
                }
            }
        });
    }

    /**
     * {@link android.view.TextureView.SurfaceTextureListener} responsible for setting up the rest of the
     * rendering and recording elements once our TextureView is good to go.
     */
    private TextureView.SurfaceTextureListener mTextureListener = new TextureView.SurfaceTextureListener()
    {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, final int width, final int height) {
            //convenience method since we're calling it from two places
            setReady(surface, width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
            mCameraFragment.configureTransform(width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) { }
    };

    @Override
    public void onWZStatus(final WZStatus wzStatus) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (wzStatus.isIdle()) {
                    Toast.makeText(StreamingShaderActivity.this, "Streaming stopped", Toast.LENGTH_SHORT).show();
                    //restart so surface is recreated
                    shutdownCamera(true);
                } else if (wzStatus.isRunning()) {
                    mTargetControl.setImageResource(android.R.drawable.ic_media_pause);
                    mTargetControl.setEnabled(true);
                    Toast.makeText(StreamingShaderActivity.this, "Streaming started", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onWZError(final WZStatus wzStatus) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Display any errors reported by the GoCoder SDK
                Toast.makeText(StreamingShaderActivity.this, wzStatus.getLastError().getErrorDescription(), Toast.LENGTH_LONG).show();
            }
        });
    }

}
