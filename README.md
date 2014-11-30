CoverLoading
============

Simulate ios installing app animation. It will progress when app is installing, when icon is clicked, a pause animation will start and progress stops. Also include a stop animation. CoverView is child of ImageView, so you can set a image to it.

![](./slide2.gif)

How to use
==========

Include this to gradle

    compile 'me.biubiubiu.coverloading:library:0.2'

Add view in xml

    <com.bettycc.coverloading.library.CoverView
        android:id="@+id/cover"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_centerInParent="true"
        android:src="@drawable/cover"
        />


Set progress

        mCoverView = (CoverView) findViewById(R.id.cover);
        mCoverView.setProgress(20);

Progress is in 0-100. When you set a progress to CoverView it will perform a animation and move progress. But when in the animation you set another progress, it will save the progress and move again when previous ended.

##Compatibility

Api 8 and above.
 
## TODO

* [x] Pause animation.
* [x] Compatible with api 8.
* [x] Fancy finish animation.
* [x] Upload to maven central.
* [x] Provide customizable options.
* [x] Provide a interface to set progress.
