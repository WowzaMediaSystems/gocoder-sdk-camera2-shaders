
### Live streaming with real-time video filters using the Wowza GoCoder SDK™

This sample code is based on Google's [shadercam](https://github.com/googlecreativelab/shadercam) library and extends the [shadercam-example](https://github.com/googlecreativelab/shadercam/tree/master/shadercam-example) app with live streaming support using the [GoCoder SDK](https://www.wowza.com/products/gocoder). The camera preview display is based on Android's [camera2](https://developer.android.com/reference/android/hardware/camera2/package-summary.html) API with real-time filtering using the [OpenGL Shading Language (GLSL)](https://developer.android.com/guide/topics/graphics/opengl.html).

The [shadercam-example](https://github.com/googlecreativelab/shadercam/tree/master/shadercam-example) app was updated to support live streaming by adding a new renderer class (`StreamingRenderer`) that uses the [`WZGLBroadcaster`](https://www.wowza.com/resources/gocodersdk/docs/1.0/api-docs-android/com/wowza/gocoder/sdk/api/broadcast/WZGLBroadcaster.html) API class and [`WZRenderAPI.VideoFrameRenderer`](https://www.wowza.com/resources/gocodersdk/docs/1.0/api-docs-android/com/wowza/gocoder/sdk/api/render/WZRenderAPI.VideoFrameRenderer.html) interface from the GoCoder SDK to encode each frame for the video stream while a live broadcast is active.

## Development Requirements

- **[Wowza GoCoder SDK v1.0.1.340](https://www.wowza.com/products/gocoder) or later**

     **PLEASE NOTE:** The GoCoder SDK library necessary to build this app is not provided with this sample code. To receive a free trial copy of the SDK, please fill out the **[GoCoder SDK trial signup form](https://www.wowza.com/products/gocoder/sdk/trial)** to be sent a link where you can download the SDK along with a free trial license key.

- **[Android SDK v4.4.2](https://developer.android.com/studio/index.html)** or later.
- **[Android Studio v1.2.0](https://developer.android.com/studio/index.html)** or later.
- Access to a **[Wowza Streaming Engine](https://www.wowza.com/products/streaming-engine)™** server installation or a **[Wowza Cloud](https://www.wowza.com/products/streaming-cloud)™** hosted service account. You can request a free trial copy of Wowza Streaming Engine software by filling out the **[Wowza Streaming Engine trial signup form](https://www.wowza.com/pricing/trial)** or signup for a Wowza Cloud trial account by filling out the **[Wowza Cloud account signup form](https://www.wowza.com/pricing/cloud-free-trial)**.

#### Building and running the app

Follow the steps below before attempting to build this app for the first time:

1. Update `gocoder-sdk-shaders/build.gradle` and set the value of the `applicationId` property displayed below to match the app id corresponding to the GoCoder SDK license key you were issued. You can request a trial key using the [GoCoder SDK trial signup form](https://www.wowza.com/products/gocoder/sdk/trial).

```
applicationId "com.XXX.XXX.XXX.XXX.XXX"
```

2. Copy the GoCoder SDK aar library file (`com.wowza.gocoder.sdk.aar`) to the `gocoder-sdk-shaders/libs` folder.
3. Edit the line displayed below from the `StreamingShaderActivity` class to match your GoCoder SDK license key.

```
private static final String GOCODER_SDK_LICENSE_KEY = "GOSK-XXXX-XXXX-XXXX-XXXX-XXXX";
```

4. Edit the lines displayed below, also from the `StreamingShaderActivity` class. with the configuration settings specific to your [Wowza Streaming Engine](https://www.wowza.com/products/streaming-engine) installation or [Wowza Cloud](https://www.wowza.com/products/streaming-cloud) account:

```
    mWZBroadcastConfig.setHostAddress("192.168.1.246");
    mWZBroadcastConfig.setPortNumber(1935);
    mWZBroadcastConfig.setApplicationName("live");
    mWZBroadcastConfig.setStreamName("myStream");
    mWZBroadcastConfig.setUsername(null);
    mWZBroadcastConfig.setPassword(null);
```

#### Additional resources
* [GoCoder SDK Developer Documentation](https://www.wowza.com/resources/gocodersdk/docs/1.0/)
* [GoCoder SDK for Android API Reference](https://www.wowza.com/resources/gocodersdk/docs/1.0/api-reference-android/)
* [GoCoder SDK for Android Release Notes](https://www.wowza.com/resources/gocodersdk/docs/1.0/release-notes-android/)
* [GoCoder Product Page](https://www.wowza.com/products/gocoder)

Wowza Media Systems™ provides developers with a platform to create streaming applications and solutions. See [Wowza Developer Tools](https://www.wowza.com/resources/developers) to learn more about our APIs and SDK.

#### Contact
[Wowza Media Systems, LLC](https://www.wowza.com/contact)

# shadercam README

_The contents of the [original README.md from shadercam](https://github.com/googlecreativelab/shadercam/blob/master/README.md) are provided below:_

---

shadercam
=========

Simple OpenGL Shaders with the [camera2](https://developer.android.com/reference/android/hardware/camera2/package-summary.html) apis in Android 5.0+

examples
--------

Check out [`shadercam-example`](https://github.com/googlecreativelab/shadercam/tree/master/shadercam-example) here for basic usage.

Also, **shadercam** was built for usage with a couple Android Experiments:

* [Lip Swap](https://github.com/googlecreativelab/lipswap)
* [Tunnel Vision](https://github.com/googlecreativelab/tunnelvision)

permissions
-----------

**updated 9/14/15**

We've added a [`PermissionsHelper`](https://github.com/googlecreativelab/shadercam/blob/master/shadercam/src/main/java/com/androidexperiments/shadercam/fragments/PermissionsHelper.java)
fragment to make handling Android M's new permissions model a bit easier.

Refer to the example applications [`MainActivity.java`](https://github.com/googlecreativelab/shadercam/blob/master/shadercam-example/src/main/java/com/androidexperiments/shadercam/example/MainActivity.java#L82)
for implementation specifics.

usage
-----

Import **shadercam** in your `build.gradle` file:

```
compile project(':shadercam')
```
or
```
compile 'com.androidexperiments:shadercam:1.1.0'
```

**shadercam** comes with a simple implementation of the camera2 apis called `CameraFragment`, which only
requires that you add a `TextureView` to your layout.

```
private void setCameraFragment() {
    mCameraFragment = CameraFragment.getInstance();
    mCameraFragment.setCameraToUse(CameraFragment.CAMERA_PRIMARY); //or CAMERA_BACK
    mCameraFragment.setTextureView(mTextureView); //the TextureView we added to our layout

    //add fragment to our setup and let it work its magic
    getSupportFragmentManager().beginTransaction()
        .add(mCameraFragment, TAG_CAMERA_FRAGMENT) //any tag is fine if u want to access later
        .commit();
}
```

Once your CameraFragment is setup, we need to wait until our `TextureView` is ready to create
 our `CameraRenderer`.

```
public void onResume() {
    if(!mTextureView.isAvailable())
        mTextureView.setSurfaceTextureListener(mTextureListener);
    else
        setReady(mTextureView.getSurfaceTexture(), mTextureView.getWidth(), mTextureView.getHeight());
}
```

Our texture listener is your normal, every day `TextureView.SurfaceTextureListener` that will also call our `setReady` method that will create our renderer.
Now all you have to do is extend `CameraRenderer` to do anything you want with the video feed!

```
private void setReady(SurfaceTexture surface, int width, int height) {
    mRenderer = new ExampleRenderer(this, surface, mCameraFragment, width, height);
    mRenderer.setOnRendererReadyListener(this);
    mRenderer.start();

    //initial config if needed
    mCameraFragment.configureTransform(width, height);
}
```

Check out `MainActivity` and `ExampleRenderer` in `shadercam-example` for more in depth explanations and details.

more info
---------

If you make something cool with shadercam, let us know by heading over to [Android Experiments](http://www.androidexperiments.com) and submitting your experiment!

Report any issues [here](https://github.com/googlecreativelab/shadercam/issues) - we love pull requests!

license
-------

```
Copyright 2015 Google Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
