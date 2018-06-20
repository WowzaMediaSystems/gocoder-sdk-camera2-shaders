
![GoCoder SDK Camera2 Shaders Example](https://raw.githubusercontent.com/WowzaMediaSystems/gocoder-sdk-camera2-shaders/master/gocoder-sdk-shaders/src/main/res/mipmap-xxhdpi/ic_launcher.png)

### Live streaming with real-time video filters using the Wowza GoCoder SDK

This sample code is based on Google's [shadercam](https://github.com/googlecreativelab/shadercam) library and extends the [shadercam-example](https://github.com/googlecreativelab/shadercam/tree/master/shadercam-example) app with live streaming support using the [Wowza GoCoder™ SDK](https://www.wowza.com/products/gocoder). The camera preview display is based on Android's [camera2](https://developer.android.com/reference/android/hardware/camera2/package-summary.html) API with real-time filtering using the [OpenGL Shading Language (GLSL)](https://developer.android.com/guide/topics/graphics/opengl.html).

The shadercam-example app was updated to support live streaming by adding a StreamingRenderer class that uses the WZGLBroadcaster API class and the WZRenderAPI.VideoFrameRenderer interface in the GoCoder SDK to encode each frame for the video stream while a live broadcast is active.

## Prerequisites

- [Wowza GoCoder SDK v1.0.1.340](https://www.wowza.com/products/gocoder) or later

     **NOTE:** The GoCoder SDK library necessary to build this app isn't provided with this sample code. To get a free trial of the SDK, complete the **[GoCoder SDK trial sign-up form](https://www.wowza.com/products/gocoder/sdk/trial)** to be sent a link where you can download the SDK along with a free trial license key.

- [Android SDK v4.4.2](https://developer.android.com/studio/index.html) or later.
- [Android Studio v1.2.0](https://developer.android.com/studio/index.html) or later.
- A [Wowza Streaming Engine](https://www.wowza.com/products/streaming-engine)™ media server or a [Wowza Streaming Cloud](https://www.wowza.com/products/streaming-cloud)™ account. You can request a Wowza Streaming Engine trial by completing the [Wowza Streaming Engine trial sign-up form](https://www.wowza.com/pricing/trial) or sign up for a Wowza Streaming Cloud trial by completing the [Wowza Streaming Cloud trial sig-nup form](https://www.wowza.com/pricing/cloud-free-trial).

## Build and run the app for the first time

1. Copy the GoCoder SDK aar library file (**com.wowza.gocoder.sdk.aar**) to the **gocoder-sdk-shaders/libs** folder.

2. Edit the following code, which is from the **StreamingShaderActivity** class, with the configuration settings used by your Wowza Streaming Engine server or your Wowza Streaming Cloud live stream:

```
    mWZBroadcastConfig.setHostAddress("192.168.1.246");
    mWZBroadcastConfig.setPortNumber(1935);
    mWZBroadcastConfig.setApplicationName("live");
    mWZBroadcastConfig.setStreamName("myStream");
    mWZBroadcastConfig.setUsername(null);
    mWZBroadcastConfig.setPassword(null);
```

## More resources
* [Wowza GoCoder SDK Technical Articles](https://www.wowza.com/docs/wowza-gocoder-sdk)
* [Wowza GoCoder SDK for Android Reference Docs](https://www.wowza.com/resources/gocodersdk/docs/1.0/api-reference-android/)
* [Wowza GoCoder SDK for Android Release Notes](https://www.wowza.com/docs/wowza-gocoder-sdk-release-notes-for-android)
* [Wowza GoCoder SDK Community Forum](https://www.wowza.com/community/spaces/36/wowza-gocoder-sdk.html)

Wowza Media Systems™ provides developers with a platform to create streaming applications and solutions. See [Wowza Developer Tools](https://www.wowza.com/resources/developers) to learn more about our APIs and SDK.

#### Contact
[Wowza Media Systems, LLC](https://www.wowza.com/contact)

## License
This code is distributed under the [Wowza Public License](https://github.com/WowzaMediaSystems/gocoder-sdk-camera2-shaders/blob/master/LICENSE).
