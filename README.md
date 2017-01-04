
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

1. Copy the GoCoder SDK aar library file (`com.wowza.gocoder.sdk.aar`) to the `gocoder-sdk-shaders/libs` folder.

2. Edit the lines displayed below, also from the `StreamingShaderActivity` class. with the configuration settings specific to your [Wowza Streaming Engine](https://www.wowza.com/products/streaming-engine) installation or [Wowza Cloud](https://www.wowza.com/products/streaming-cloud) account:

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

## License
This code is distributed under the [Wowza Public License](https://github.com/WowzaMediaSystems/gocoder-sdk-camera2-shaders/blob/master/LICENSE).
