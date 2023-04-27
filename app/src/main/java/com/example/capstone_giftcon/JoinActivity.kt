package com.example.capstone_giftcon
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.capstone_giftcon.databinding.ActivityJoinBinding
import com.example.capstone_giftcon.dto.JoinData
import com.example.capstone_giftcon.ui.viewModel.SignViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult

class JoinActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJoinBinding
    private lateinit var token: String

    private val viewModel: SignViewModel by lazy {
        ViewModelProvider(this)[SignViewModel::class.java]
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJoinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.myToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.idDup.setOnClickListener(View.OnClickListener {
            val id: String = binding.joinId.text.toString()
            checkDuplicate("id", id)
        })
        binding.nameDup.setOnClickListener(View.OnClickListener {
            val name: String = binding.joinName.text.toString()
            checkDuplicate("nickname", name)
        })
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(object : OnCompleteListener<InstanceIdResult> {
                override fun onComplete(task: Task<InstanceIdResult>) {
                    if (!task.isSuccessful) {
                        Log.w("TAG_getTokenFailed", "getInstanceId failed", task.exception)
                        return
                    }
                    // Get new Instance ID token
                    token = task.result!!.token
                }
            })
        binding.joinButton.setOnClickListener(View.OnClickListener { attemptJoin() })
    }

    private fun attemptJoin() {
        binding.joinName.error = null
        binding.joinId.error = null
        binding.joinPwd.error = null
        val name: String = binding.joinName.text.toString()
        val email: String = binding.joinId.text.toString()
        val password: String = binding.joinPwd.text.toString()
        val passwordCheck: String = binding.joinCheckpwd.text.toString()
        var cancel = false
        var focusView: View? = null

        // 패스워드의 유효성 검사
        if (password.isEmpty()) {
            binding.joinId.error = "비밀번호를 입력해주세요."
            focusView = binding.joinId
            cancel = true
        } else if (!isPasswordValid(password)) { // 비밀번호 조건 수정
            binding.pwdWarn.visibility = View.VISIBLE
            binding.pwdWarn.text = "비밀번호 조건에 부합하지 않습니다. 다시 입력해주세요."
            focusView = binding.joinPwd
            cancel = true
        }
        if (password != passwordCheck) {
            binding.pwdCheckWarn.visibility = View.VISIBLE
            binding.pwdCheckWarn.text = "비밀번호가 일치하지 않습니다."
        }

        // 이메일의 유효성 검사
        if (email.isEmpty()) {
            binding.joinId.error = "이메일을 입력해주세요."
            focusView = binding.joinId
            cancel = true
        } else if (!isEmailValid(email)) { // 아이디 유효조건 수정
            //mEmailView.setError("@를 포함한 유효한 이메일을 입력해주세요.");
            binding.idWarn.visibility = View.VISIBLE
            binding.idWarn.text = "아이디 조건에 부합하지 않습니다. 다시 입력해주세요."
            focusView = binding.joinId
            cancel = true
        }

        // 이름의 유효성 검사
        if (name.isEmpty()) {
            binding.joinName.error = "이름을 입력해주세요."
            focusView = binding.joinName
            cancel = true
        }
        if (cancel) {
            focusView!!.requestFocus()
        } else {
            Log.d("join", "check")
            // token non-null 선언함 -> token이 null일 경우는 어캄쇼~
            startJoin(JoinData(name, email, password, token))
        }
    }

    private fun checkDuplicate(property: String, data: String) {
        if (property == "id") {
            viewModel.checkIdDuplicate(data)
            binding.idWarn.visibility = View.VISIBLE
            viewModel.duplicateIdResult.observe(this){
                when(it){
                    NetworkResult.SUCCESS -> {
                        binding.idWarn.setTextColor(Color.parseColor("#5876ed"))
                        binding.idWarn.text = "사용 가능한 아이디 입니다."
                    }
                    NetworkResult.FAIL -> {
                        binding.idWarn.setTextColor(Color.parseColor("#f25f30"))
                        binding.idWarn.text = "네트워크 연결에 실패하였습니다. 다시 시도해주세요."
                    }
                    else -> {
                        binding.idWarn.setTextColor(Color.parseColor("#f25f30"))
                        binding.idWarn.text = "중복된 아이디 입니다."
                    }
                }
            }
        } else {
            viewModel.checkNameDuplicate(data)
            binding.nameWarn.visibility = View.VISIBLE
            viewModel.duplicateNameResult.observe(this){
                when(it){
                    NetworkResult.SUCCESS -> {
                        binding.nameWarn.setTextColor(Color.parseColor("#5876ed"))
                        binding.nameWarn.text = "사용 가능한 닉네임 입니다."
                    }
                    NetworkResult.FAIL -> {
                        binding.nameWarn.setTextColor(Color.parseColor("#f25f30"))
                        binding.nameWarn.text = "네트워크 연결에 실패하였습니다. 다시 시도해주세요."
                    }
                    else -> {
                        binding.nameWarn.setTextColor(Color.parseColor("#f25f30"))
                        binding.nameWarn.text = "중복된 닉네임 입니다."
                    }
                }
            }
        }
    }

    private fun startJoin(data: JoinData) {
        viewModel.userJoin(data)
        viewModel.userJoinResult.observe(this){
            when (it) {
                NetworkResult.SUCCESS -> {
                    Toast.makeText(this@JoinActivity, "회원가입에 성공하였습니다!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                NetworkResult.NOT_SUCCESS -> {
                    Toast.makeText(
                        this@JoinActivity,
                        "회원가입에 실패하였습니다. 다시 입력해 주세요.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    Toast.makeText(this@JoinActivity, "회원가입 에러 발생", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun isEmailValid(id: String): Boolean {
        // 아이디(6-12자 이내 영문, 숫자 사용가능)
        return id.length >= 5 && id.length <= 12
    }

    private fun isPasswordValid(password: String): Boolean {
        //비밀번호(8자 이상, 문자/숫자/기호 사용 가능)
        return password.length >= 5
    }
}