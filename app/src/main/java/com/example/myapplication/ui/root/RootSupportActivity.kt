package com.example.myapplication.ui.root

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.db.MaumDatabase
import com.example.myapplication.db.SupportApplication
import com.example.myapplication.db.SupportApplicationRepository
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class RootSupportActivity : ComponentActivity() {

    // Firestore 인스턴스
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    // 로컬 DB 저장용 리포지토리
    private val supportRepository: SupportApplicationRepository by lazy {
        SupportApplicationRepository(
            MaumDatabase.getInstance(applicationContext).supportApplicationDao()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // MAUM 톤 컬러
        val bgColor = Color.parseColor("#F2EBFF")      // 연보라 배경
        val mainPurple = Color.parseColor("#7B61FF")   // 진보라 버튼
        val borderPurple = Color.parseColor("#C8B6FF") // 입력창 테두리
        val titlePurple = Color.parseColor("#2F285A")  // 진한 보라 텍스트

        val horizontalPadding = dp(24)

        // 전체 스크롤
        val scrollView = ScrollView(this).apply {
            setBackgroundColor(bgColor)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        // 루트 레이아웃
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(horizontalPadding, dp(32), horizontalPadding, dp(32))
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        scrollView.addView(root)

        // 제목
        val title = TextView(this).apply {
            text = "뿌리 찾기 지원 신청"
            textSize = 26f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(titlePurple)
        }
        root.addView(title)

        // 안내 문구
        val subtitle = TextView(this).apply {
            text = "친부모 찾기 서비스 신청을 위한 정보를 입력해주세요.\n모든 정보는 안전하게 보호됩니다."
            textSize = 14f
            setTextColor(titlePurple)
            setPadding(0, dp(12), 0, dp(24))
        }
        root.addView(subtitle)

        // 폼 컨테이너
        val formContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        root.addView(formContainer)

        // 공통 입력 필드 생성 함수
        fun makeField(
            hintText: String,
            multiline: Boolean = false,
            inputType: Int? = null
        ): EditText {
            return EditText(this).apply {
                hint = hintText
                textSize = 15f
                setHintTextColor(borderPurple)
                setTextColor(titlePurple)
                setPadding(dp(16), dp(12), dp(16), dp(12))

                isEnabled = true
                isFocusable = true
                isFocusableInTouchMode = true

                background = roundedRectStroke(
                    bgColor = Color.TRANSPARENT,
                    strokeColor = borderPurple,
                    radiusDp = 20,
                    strokeWidthDp = 1
                )

                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, dp(8), 0, dp(8))
                }

                if (multiline) {
                    minLines = 4
                    gravity = Gravity.TOP or Gravity.START
                    setSingleLine(false)
                    this.inputType = InputType.TYPE_CLASS_TEXT or
                            InputType.TYPE_TEXT_FLAG_MULTI_LINE
                } else {
                    setSingleLine(true)
                    inputType?.let { this.inputType = it }
                }
            }
        }

        // 입력 항목들
        val nameField = makeField("이름:김나라")
        val birthField = makeField("생년월일:2000.12.09")
        val contactField = makeField(
            "현재 연락처:010-0000-0000",
            inputType = InputType.TYPE_CLASS_PHONE
        )
        val emailField = makeField(
            "이메일-maum@naver.com",
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        )
        val adoptionTimeField = makeField("입양 시기:2025.11.27")
        val familyInfoField = makeField("찾고 싶은 가족 정보:엄마", multiline = true)

        formContainer.addView(nameField)
        formContainer.addView(birthField)
        formContainer.addView(contactField)
        formContainer.addView(emailField)
        formContainer.addView(adoptionTimeField)
        formContainer.addView(familyInfoField)

        // 약간 여백
        formContainer.addView(makeSpace(16))

        // 신청서 제출하기 버튼
        val submitBtn = Button(this).apply {
            text = "신청서 제출하기"
            textSize = 16f
            setTextColor(Color.WHITE)
            background = roundedRect(mainPurple, 24)
            setPadding(0, dp(12), 0, dp(12))

            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, dp(8), 0, 0)
            }

            setOnClickListener {
                // 1) 입력값 읽기 + trim
                val name = nameField.text.toString().trim()
                val birth = birthField.text.toString().trim()
                val phone = contactField.text.toString().trim()
                val email = emailField.text.toString().trim()
                val adoptionDate = adoptionTimeField.text.toString().trim()
                val familyInfo = familyInfoField.text.toString().trim()

                // 2) 필수값 검사 (빈칸 있으면 제출 막기)
                if (name.isEmpty() || birth.isEmpty() || phone.isEmpty() ||
                    email.isEmpty() || adoptionDate.isEmpty() || familyInfo.isEmpty()
                ) {
                    Toast.makeText(
                        this@RootSupportActivity,
                        "필수 항목을 모두 입력해 주세요.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                isEnabled = false

                // 3) RootRequest 객체 만들기
                val request = RootRequest(
                    name = name,
                    birth = birth,
                    phone = phone,
                    email = email,
                    adoptionDate = adoptionDate,
                    familyInfo = familyInfo
                )

                val application = SupportApplication(
                    name = name,
                    birthDate = birth,
                    contact = phone,
                    email = email,
                    adoptionTime = adoptionDate.ifBlank { null },
                    familyInfo = familyInfo
                )

                lifecycleScope.launch {
                    val localResult = supportRepository.submit(application)
                    localResult.onFailure { err ->
                        Toast.makeText(
                            this@RootSupportActivity,
                            "로컬 저장 실패: ${err.message ?: "알 수 없는 오류"}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    // 4) Firestore에 저장 + createdAt(서버 시간) 필드 추가
                    db.collection("rootRequests")
                        .add(request)
                        .addOnSuccessListener { docRef ->
                            docRef.update("createdAt", FieldValue.serverTimestamp())

                            Toast.makeText(
                                this@RootSupportActivity,
                                "신청서가 정상적으로 제출되었어요.",
                                Toast.LENGTH_SHORT
                            ).show()

                            // 입력칸 비우기
                            nameField.text.clear()
                            birthField.text.clear()
                            contactField.text.clear()
                            emailField.text.clear()
                            adoptionTimeField.text.clear()
                            familyInfoField.text.clear()
                            isEnabled = true
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                this@RootSupportActivity,
                                "제출 중 오류가 발생했어요: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                            isEnabled = true
                        }
                }
            }
        }
        root.addView(submitBtn)

        // 🔹 맨 아래 뒤로가기 버튼
        val backBtn = Button(this).apply {
            text = "←  뒤로가기"
            textSize = 16f
            setTextColor(Color.WHITE)
            background = roundedRect(mainPurple, 24)
            setPadding(dp(24), dp(12), dp(24), dp(12))
            setOnClickListener { finish() }   // 이전 화면으로
        }

        val backRow = LinearLayout(this).apply {
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, dp(16), 0, dp(24))
            }
        }
        backRow.addView(backBtn)
        root.addView(backRow)

        // 마지막으로 스크롤뷰 보여주기
        setContentView(scrollView)
    }

    // ---- 공용 유틸 함수들 ----

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    private fun roundedRect(color: Int, radiusDp: Int): GradientDrawable =
        GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = dp(radiusDp).toFloat()
            setColor(color)
        }

    private fun roundedRectStroke(
        bgColor: Int,
        strokeColor: Int,
        radiusDp: Int,
        strokeWidthDp: Int
    ): GradientDrawable =
        GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = dp(radiusDp).toFloat()
            setColor(bgColor)
            setStroke(dp(strokeWidthDp), strokeColor)
        }

    private fun makeSpace(heightDp: Int): View =
        View(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(heightDp)
            )
        }
}
