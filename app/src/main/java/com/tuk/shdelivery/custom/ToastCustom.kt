package com.tuk.shdelivery.custom

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

public class ToastCustom {
    public companion object {

        // Activity context를 사용해서 토스트 메시지를 보여주는 함수입니다. 토스트 메시지는 0.5초 후에 사라집니다.
        fun toast(context : Activity, message: String): Toast {
            val toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
            toast.show()
            Handler().postDelayed(
                {
                    toast.cancel()
                }, 500
            )
            return toast
        }

        // Context를 사용해서 토스트 메시지를 보여주는 함수입니다. 토스트 메시지는 0.5초 후에 사라집니다.
        fun toast(context: Context,message: String): Toast {
            val toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
            toast.show()
            Handler().postDelayed(
                {
                    toast.cancel()
                }, 500
            )
            return toast
        }

        // FragmentActivity context를 사용해서 토스트 메시지를 보여주는 함수입니다. 토스트 메시지는 0.5초 후에 사라집니다.
        fun toast(context: FragmentActivity,message: String): Toast {
            val toast = Toast.makeText(context as Activity, message, Toast.LENGTH_SHORT)
            toast.show()
            Handler().postDelayed(
                {
                    toast.cancel()
                }, 500
            )
            return toast
        }
    }
}
