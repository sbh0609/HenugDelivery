package com.tuk.shdelivery.Activity

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient
import com.tuk.shdelivery.databinding.ActivityLoginBinding


class LoginActivity : AppCompatActivity() {
    // 바인딩 객체 생성
    lateinit var bd: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 카톡에서 네이티브키 복사 후 붙혀넣기
        KakaoSdk.init(this, "네이티브키")
        bd = ActivityLoginBinding.inflate(layoutInflater)

//        Log.d(TAG, "keyhash : ${Utility.getKeyHash(this)}")
//         이걸로 Logcat 가서 해시 코드 카톡에 올려주세요
        setContentView(bd.root)

        bd.login.setOnClickListener {

            // 카카오톡 설치 확인
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                // 카카오톡 로그인
                UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                    // 로그인 실패 부분
                    if (error != null) {
                        Log.e(TAG, "로그인 실패 $error")
                        // 사용자가 취소
                        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                            return@loginWithKakaoTalk
                        }
                        // 다른 오류
                        else {
                            UserApiClient.instance.loginWithKakaoAccount(
                                this,
                                callback = mCallback
                            ) // 카카오 이메일 로그인
                        }
                    }
                    // 로그인 성공 부분
                    else if (token != null) {
                        Log.e(TAG, "로그인 성공 ${token.accessToken}")
                        fetch_UserData()
                    }
                }
            } else {
                UserApiClient.instance.loginWithKakaoAccount(
                    this,
                    callback = mCallback
                ) // 카카오 이메일 로그인
            }
            finish()
        }
        bd.aboutLogin.setOnClickListener {
            var t1 = Toast.makeText(this, "현재 카카오 로그인만 지원됩니다.", Toast.LENGTH_SHORT)
            t1.show()
        }
    }

    private fun fetch_UserData() {
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e(TAG, "사용자 정보 요청 실패 $error")
            } else if (user != null) {
                Log.e(TAG, "사용자 정보 요청 성공 : $user")
                val intent = Intent(this, HomeActivity::class.java)
                intent.putExtra("userName", user.kakaoAccount?.profile?.nickname)
                intent.putExtra("userid",user.id.toString())
                startActivity(intent)
                finish()
            }
        }
    }

    // 이메일 로그인 콜백
    private val mCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Log.e(TAG, "로그인 실패 $error")
        } else if (token != null) {
            Log.e(TAG, "로그인 성공 ${token.accessToken}")
            fetch_UserData()
        }
    }
}
