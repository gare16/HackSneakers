package com.apps.hacksneakers.uitel

import android.content.Context
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.apps.hacksneakers.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

fun getProgessDrawable(c: Context): CircularProgressDrawable {
    return CircularProgressDrawable(c).apply {
        strokeWidth = 5f
        centerRadius = 40f
        start()
    }

}

/**set Images*/
fun ImageView.loadImage(uri:String?, progressDawable:CircularProgressDrawable){

    val option = RequestOptions().placeholder(progressDawable)
        .error(R.drawable.user_demo)

    Glide.with(context)
        .setDefaultRequestOptions(option)
        .load(uri)
        .into(this)

}

@BindingAdapter("android:imageUrl")
fun loadImage(view: ImageView, url:String){
    view.loadImage(url, getProgessDrawable(view.context))
}