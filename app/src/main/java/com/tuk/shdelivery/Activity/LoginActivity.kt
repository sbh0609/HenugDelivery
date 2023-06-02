package com.tuk.shdelivery.Activity

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.common.model.KakaoSdkError
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient
import com.tuk.shdelivery.Data.User
import com.tuk.shdelivery.UserDao
import com.tuk.shdelivery.databinding.ActivityLoginBinding
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


class LoginActivity : AppCompatActivity(), CoroutineScope {
    private var job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    val Udao = UserDao()

    // 바인딩 객체 생성
    // 카톡에서 네이티브키 복사 후 붙혀넣기

    val bd by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 카톡에서 네이티브키 복사 후 붙혀넣기
        KakaoSdk.init(this, "네이티브키")
        //        Log.d(TAG, "keyhash : ${Utility.getKeyHash(this)}")
        //         이걸로 Logcat 가서 해시 코드 카톡에 올려주세요
        setContentView(bd.root)

        bd.login.setOnClickListener {
            login()
        }

        bd.aboutLogin.setOnClickListener {
            var t1 = Toast.makeText(this, "현재 카카오 로그인만 지원됩니다.", Toast.LENGTH_SHORT)
            t1.show()
        }


        //로그인 기록있음
        if (AuthApiClient.instance.hasToken()) {
            UserApiClient.instance.accessTokenInfo { _, error ->
                if (error != null) {
                    if (error is KakaoSdkError && error.isInvalidTokenError() == true) {
                        //로그인 필요
                        login()
                    } else {
                        //기타 에러
                    }
                } else {
                    //토큰 유효성 체크 성공(필요 시 토큰 갱신됨)
                    fetch_UserData()
                }
            }
        }
    }

    private fun login() {
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
    }

    private fun fetch_UserData() {
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e(TAG, "사용자 정보 요청 실패 $error")
            } else if (user != null) {
                Log.e(TAG, "사용자 정보 요청 성공 : $user")
                val intent = Intent(this, HomeActivity::class.java)
                Udao.getUser(user.id.toString()){
                    var result = it
                    //새로운 유저라면
                    if (result == null) {
                        val newUser = User(
                            userId = user.id.toString(),
                            userName = user.kakaoAccount?.profile?.nickname!!,
                        )
                        intent.putExtra("user", newUser)
                        Udao.addUser(newUser)
                    } else {
                        //이미 있는 유저라면 intent에 넣기
                        intent.putExtra("user", result)
                    }
                    startActivity(intent)
                    finish()
                }
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
