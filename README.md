
![GoCoder SDK Camera2 Shaders Example](/shadercam/src/main/res/mipmap-xxhdpi/ic_launcher.png)

## Live stream with real-time video filters using the Wowza GoCoder SDK

**Wowza Player, Wowza GoCoder SDK, and the technology powering the Ultra Low Latency (ULL) service in Wowza Streaming Cloud will no longer be available on January 5, 2021. [Learn more](https://info.wowza.com/product-notification-april-2020).**

This sample code is based on Google's [shadercam](https://github.com/googlecreativelab/shadercam) library and extends the [shadercam-example](https://github.com/googlecreativelab/shadercam/tree/master/shadercam-example) app with live streaming support using the [Wowza GoCoder™ SDK](https://www.wowza.com/products/gocoder). The camera preview display is based on Android's [camera2](https://developer.android.com/reference/android/hardware/camera2/package-summary.html) API with real-time filtering using the [OpenGL Shading Language (GLSL)](https://developer.android.com/guide/topics/graphics/opengl.html).

The shadercam example app was updated to support live streaming by adding a **StreamingRenderer** class that uses the **WOWZGLBroadcaster** API class and the **WOWZRenderAPI.VideoFrameRenderer** interface in the GoCoder SDK to encode each frame for the video stream while a live broadcast is active.

## Prerequisites

- [Wowza GoCoder SDK for Android v1.8.0.0463](https://www.wowza.com/products/gocoder) or later

     **NOTE:** The GoCoder SDK library necessary to build this app isn't provided with this sample code. To get the latest version of the free SDK, go to the [GoCoder SDK download page](https://www.wowza.com/pricing/installer#gocodersdk-downloads).

- [Android 5.0](https://developer.android.com/studio/index.html) (API level 21) or later for broadcasting and Android 6.0 (API level 23) or later for playback.
- [Android Studio v3.4.0](https://developer.android.com/studio/index.html) or later.
- A [Wowza Streaming Engine](https://www.wowza.com/products/streaming-engine)™ media server license or a [Wowza Streaming Cloud](https://www.wowza.com/products/streaming-cloud)™ subscription. You can request a Wowza Streaming Engine trial by completing the [Wowza Streaming Engine trial sign-up form](https://www.wowza.com/pricing/trial) or sign up for a Wowza Streaming Cloud trial by completing the [Wowza Streaming Cloud trial sig-nup form](https://www.wowza.com/pricing/cloud-free-trial).

## Build and run the app for the first time

1. Copy the GoCoder SDK **aar** library file (**com.wowza.gocoder.sdk.aar**) to the **gocoder-sdk-shaders/libs** folder.

2. Edit the following code, which is from the **StreamingShaderActivity** class, with the configuration settings used by your Wowza Streaming Engine server or your Wowza Streaming Cloud live stream:

```
    mWOWZBroadcastConfig.setHostAddress("192.168.1.246");
    mWOWZBroadcastConfig.setPortNumber(1935);
    mWOWZBroadcastConfig.setApplicationName("live");
    mWOWZBroadcastConfig.setStreamName("myStream");
    mWOWZBroadcastConfig.setUsername(null);
    mWOWZBroadcastConfig.setPassword(null);
```

## More resources
* [Wowza GoCoder SDK technical articles](https://www.wowza.com/docs/wowza-gocoder-sdk)
* [Wowza GoCoder SDK for Android reference docs](https://www.wowza.com/resources/gocodersdk/docs/api-reference-android/)
* [Wowza GoCoder SDK for Android release notes](https://www.wowza.com/docs/wowza-gocoder-sdk-release-notes-for-android)
* [Wowza GoCoder SDK community forum](https://www.wowza.com/community/spaces/36/wowza-gocoder-sdk.html)

Wowza Media Systems™ provides developers with a platform to create streaming applications and solutions. See [Wowza Developer Tools](https://www.wowza.com/developer) to learn more about our APIs and SDKs.

## Contact
[Wowza Media Systems, LLC](https://www.wowza.com/contact)

## License
This code is distributed under the [Wowza Public License](/LICENSE).
