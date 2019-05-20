[github]:            https://github.com/ologe/canaree-music-player
[paypal-url]:        https://paypal.me/nextmusicplayer
[googleplay-url]:    https://play.google.com/store/apps/details?id=dev.olog.msc

[platform-badge]:   https://img.shields.io/badge/Platform-Android-F3745F.svg
[paypal-badge]:     https://img.shields.io/badge/Donate-Paypal-F3745F.svg
[googleplay-badge]: https://img.shields.io/badge/Google_Play-Demo-F3745F.svg
[minsdk-badge]:     https://img.shields.io/badge/minSdkVersion-21-F3745F.svg

<!------------------------------------------------------------------------------------------------------->


Canaree (Music Player)
=

[![platform-badge]][github]
[![minsdk-badge]][github]
[![paypal-badge]][paypal-url]
[![googleplay-badge]][googleplay-url]

Complete music player published in the Play Store. Heavily relies on Dagger, RxJava and Clean architecture.

## Screenshots
<div style="dispaly:flex">
    <img src="https://github.com/ologe/canaree-music-player/blob/master/images/device-2018-10-28-235818.png" width="32%">
    <img src="https://github.com/ologe/canaree-music-player/blob/master/images/device-2018-10-29-001417.png" width="32%">
    <img src="https://github.com/ologe/canaree-music-player/blob/master/images/device-2018-10-29-002256.png" width="32%">
</div>

## Build
Compilation can be done in 2 ways. Using the first method will prevent you from supporting FFMPEG, FLAC and OPUS formats.  
#### Method 1 (Fast)
In `build.gradle` app module.
* Uncomment
```gradle
implementation 'com.google.android.exoplayer:exoplayer-core:$latest_exoplayer_version
```
* Comment 
```gradle
implementation project(':exoplayer-library-core')
implementation project(':exoplayer-extension-flac')
implementation project(':exoplayer-extension-opus')
implementation project(':exoplayer-extension-ffmpeg')
```


#### Method 2
* Clone [ExoPlayer](https://github.com/google/ExoPlayer)
* In `settings.gradle`:
  - Update `gradle.ext.exoplayerRoot` to match your ExoPlayer repo path
  - Change <br> 
      ```gradle 
      apply from: new File(gradle.ext.exoplayerRoot, 'core_settings_min.gradle')
      ``` 
      with</br>
      ```gradle 
      apply from: new File(gradle.ext.exoplayerRoot, 'core_settings.gradle')
      ```

## Extensions (Linux or macOS recommended)
To support **FLAC**, **FFMPEG** and **OPUS** formats to you need to compile manually the corresponding 
ExoPlayer extensions using <b>NDK-r15c</b> or older, newer version of NDK are not supported. 
* [**FFMPEG**](https://github.com/google/ExoPlayer/tree/release-v2/extensions/ffmpeg)
* [**FLAC**](https://github.com/google/ExoPlayer/tree/release-v2/extensions/flac)
* [**OPUS**](https://github.com/google/ExoPlayer/tree/release-v2/extensions/opus)

## Issues
If you find any problems, please feel free to file an [issue](https://github.com/ologe/canaree-music-player/issues).

## Open-source libraries
* [**ExoPlayer**](https://github.com/google/ExoPlayer)
* [**Dagger**](https://github.com/google/dagger)
* [**RxJava**](https://github.com/ReactiveX/RxJava)
* [**rx-preferences**](https://github.com/f2prateek/rx-preferences)
* [**RxKotlin**](https://github.com/ReactiveX/RxKotlin)
* [**Glide**](https://github.com/bumptech/glide)
* [**AndroidSlidingUpPanel**](https://github.com/umano/AndroidSlidingUpPanel)
* [**Lottie**](https://github.com/airbnb/lottie-android)
* [**Retrofit**](https://github.com/square/retrofit)
* [**OkHttp**](https://github.com/square/okhttp)
* [**Gson**](https://github.com/google/gson)
* [**LastFm bindings**](https://github.com/jkovacs/lastfm-java)
* [**java-aes-crypto**](https://github.com/tozny/java-aes-crypto)
* [**Custom tabs**](https://github.com/saschpe/android-customtabs)
* [**Material dialogs**](https://github.com/afollestad/material-dialogs)
* [**fuzzywuzzy**](https://github.com/xdrop/fuzzywuzzy)
* [**Leak canary**](https://github.com/square/leakcanary)
* [**Stetho**](http://facebook.github.io/stetho/)

## Download
[<img src="https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png" alt="" height="100">](https://play.google.com/store/apps/details?id=dev.olog.msc)
