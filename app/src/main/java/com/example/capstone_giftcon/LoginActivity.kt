package com.example.capstone_giftcon
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.capstone_giftcon.databinding.ActivityLoginBinding
import com.example.capstone_giftcon.dto.LoginData
import com.example.capstone_giftcon.network.RetrofitApi
import com.example.capstone_giftcon.network.ServiceApi
import com.example.capstone_giftcon.ui.viewModel.SignViewModel
import com.google.gson.JsonObject
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private var service: ServiceApi = RetrofitApi.retrofitService

    private lateinit var fcmToken: String

    private val signViewModel: SignViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        Utils.init(applicationContext)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textFindPwd.setOnClickListener(View.OnClickListener {
            val intent = Intent(applicationContext, FindPwdActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        })

        // 카카오가 설치되어 있는지 확인 하는 메서드 또한 카카오에서 제공 콜백 객체를 이용함
        val callback: Function2<OAuthToken?, Throwable?, Unit> =
            { oAuthToken: OAuthToken?, throwable: Throwable? ->
                // 이때 토큰이 전달이 되면 로그인이 성공한 것이고 토큰이 전달되지 않았다면 로그인 실패
                if (oAuthToken != null) {  //로그인 성공
                    Log.d("token", "" + oAuthToken)
                    kakaoToken(oAuthToken.accessToken)
                    //Utils.setAccessToken(fcm_token);
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                }
                if (throwable != null) {  //로그인 실패
                    Log.e("로그인 실패!!!!", throwable.toString())
                }
                null
            }

        // 카카오 로그인 버튼
        binding.kakaoLoginButton.setOnClickListener(View.OnClickListener { view: View? ->
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this@LoginActivity)) {
                Log.d("kakao_login1", "" + binding.kakaoLoginButton)
                UserApiClient.instance.loginWithKakaoTalk(this@LoginActivity, callback = callback)
            } else {
                UserApiClient.instance.loginWithKakaoAccount(this@LoginActivity, callback = callback)
            }
        })

        // 로그아웃 버튼
        binding.kakaoLoginButton.setOnClickListener(View.OnClickListener { view: View? ->
            UserApiClient.instance.logout { throwable: Throwable? -> null }
        })

        //기존 로그인 버튼
        binding.buttonLogin.setOnClickListener(View.OnClickListener {
            attemptLogin()
        })

        binding.textJoin.setOnClickListener(View.OnClickListener {
            val intent = Intent(applicationContext, JoinActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        })
    }

    private fun kakaoToken(token: String) {
        service.kakaoToken(token, fcmToken).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    Log.d("kakao_token", "성공")
                } else {
                    Log.d("kakao_token", "not successful")
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e("kakao_token 실패", t.message!!)
                t.printStackTrace()
            }
        })
    }

    private fun updateKakaoLoginUi() {
        UserApiClient.instance.me { user: User?, throwable: Throwable? ->
            // 로그인이 되어있으면
            if (user != null) {
                // 유저의 아이디
                Log.d("id", "invoke: id" + user.id)
                // 유저의 어카운트정보에 이메일
                Log.d("email", "invoke: email" + user.kakaoAccount!!.email)
                //kakao_login.setVisibility(View.GONE);
                //kakao_logout.setVisibility(View.VISIBLE);
            } else {
                //kakao_login.setVisibility(View.VISIBLE);
                //kakao_logout.setVisibility(View.GONE);
            }
            null
        }
    }

    private fun attemptLogin() {
        binding.editId.error = null
        binding.editPwd.error = null
        val id: String = binding.editId.text.toString()
        val password: String = binding.editId.text.toString()
        var cancel = false
        var focusView: View? = null

        // 패스워드의 유효성 검사
        if (password.isEmpty()) {
            binding.editPwd.error = "비밀번호를 입력해주세요."
            focusView = binding.editPwd
            cancel = true
        } else if (!isPasswordValid(password)) {
            binding.editPwd.error = "6자 이상의 비밀번호를 입력해주세요."
            focusView = binding.editPwd
            cancel = true
        }

        // 이메일의 유효성 검사
        if (id.isEmpty()) {
            binding.editId.error = "아이디를 입력해주세요."
            focusView = binding.editId
            cancel = true
        } else if (!isIdValid(id)) { // 유효조건 생각
            binding.editId.error = "8자리의 학번을 입력해주세요"
            focusView = binding.editId
            cancel = true
        }
        if (cancel) {
            focusView!!.requestFocus()
        } else {
            Log.d("check_login", "check")
            startLogin(LoginData(id, password))
        }

    }

    private fun startLogin(data: LoginData) {
        signViewModel.userLogin(data)
        signViewModel.userLoginResult.observe(this) {
            when (it) {
                NetworkResult.SUCCESS -> {
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                }
                NetworkResult.NOT_SUCCESS -> {
                    Toast.makeText(
                        this@LoginActivity,
                        "로그인에 실패하였습니다. 다시 입력해 주세요.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    Toast.makeText(this@LoginActivity, "로그인 에러 발생", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onBackPressed() { //뒤로가기 했을 때
        ActivityCompat.finishAffinity(this@LoginActivity)
    }

    private fun isIdValid(Id: String): Boolean {
        return Id.length >= 5
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length >= 5
    }
}