package com.demo.kotlin_coil

import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import coil.*
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.transform.BlurTransformation
import coil.transform.CircleCropTransformation
import coil.transform.GrayscaleTransformation
import coil.transform.RoundedCornersTransformation
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlin.reflect.KProperty

class MainActivity : AppCompatActivity() {

    //复用ImageLoader
    val iImageLoader: ImageLoader by lazy {
        ImageLoader.Builder(this).crossfade(true).crossfade(3000).placeholder(R.drawable.ic_launcher_background)
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //网络图
        iv1.load("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=233301930,3031623456&fm=11&gp=0.jpg") {
            allowHardware(false)  //禁用硬件位图
        }
        //drawable
        val decoder = iv2.load(R.drawable.me)
        //decoder.isDisposed //图片加载是否完成状态
        //decoder.dispose()  //图片取消加载
        //本地图片加载
        //iv2.load(File("/../"))
        //Gif图
        val gifImageLoader = ImageLoader.Builder(this)
            .componentRegistry {
                if (SDK_INT >= 28) {
                    add(ImageDecoderDecoder(this@MainActivity))
                } else {
                    add(GifDecoder())
                }
            }
            .build()
        iv3.load(
            "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201611%2F04%2F20161104110413_" +
                    "XzVAk.thumb.700_0.gif&refer=http%3A%2F%2Fb-ssl.duitang.com&app=2002&size=" +
                    "f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1623217025&t=8f28c7f3640eea441348c9d44ba762f8",
            gifImageLoader
        ) {
            placeholder(R.drawable.loading)//占位图
            crossfade(true)   //淡出动画
            crossfade(3000) //动画时间
        }
        iv4.load("") {
            error(R.drawable.ic_launcher_foreground) //异常图
        }
        iv5.load(R.drawable.me) {
            transformations(CircleCropTransformation())  //圆形图
        }
        iv6.load(R.drawable.me) {
            transformations(BlurTransformation(radius = 5f, context = this@MainActivity))  //模糊
        }
        iv7.load(R.drawable.me) {
            transformations(GrayscaleTransformation())  //灰度图
        }
        iv8.load(R.drawable.me) {
            transformations(RoundedCornersTransformation(radius = 100f))  //圆角图
        }
        iv9.load("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=233301930,3031623456&fm=11&gp=0.jpg") {
            placeholderMemoryCacheKey(iv1.metadata?.memoryCacheKey)  //复用iv1的缓存key占位
        }

        //背景线程
        GlobalScope.launch {
            val drawable =
                requestImage("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=233301930,3031623456&fm=11&gp=0.jpg")
            iv10.load(drawable, iImageLoader)
        }
        //非视图目标
        nonViewTarget("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=233301930,3031623456&fm=11&gp=0.jpg")

    }

    /**
     * 预加载图片
     */
    suspend inline fun requestImage(url: String): Drawable? {
        val request = ImageRequest.Builder(this).data(url).build()
        return imageLoader.execute(request).drawable
    }

    /**
     * 非视图目标
     */
    inline fun nonViewTarget(url: String) {
        val build = ImageRequest.Builder(this).data(url).target(
            onStart = {
                //开始加载
                iv11.load(R.drawable.loading)
            }, onError = {
                //加载异常
                iv11.load(R.drawable.ic_launcher_foreground)
            },
            onSuccess = {
                //加载成功
                iv11.load(it, iImageLoader)
            }).build()
        imageLoader.enqueue(build)
    }

}


