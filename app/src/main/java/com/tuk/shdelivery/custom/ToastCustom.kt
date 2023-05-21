package com.tuk.shdelivery.custom

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

public class ToastCustom {
    public companion object {
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