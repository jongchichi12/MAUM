package com.example.myapplication.ui

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.myapplication.ui.counsel.CounselActivity
import com.example.myapplication.ui.root.RootActivity
import com.example.myapplication.ui.root.RootAdminActivity   // ✅ 관리자 화면 import

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#F2EBFF"))
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        val title = TextView(this).apply {
            text = "MAUM"
            textSize = 44f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#7B61FF"))
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, dp(24))
            }
        }
        root.addView(title)

        val subtitle = TextView(this).apply {
            text = "당신의 마음을\n듣고 있습니다."
            textSize = 26f
            setTextColor(Color.parseColor("#7B61FF"))
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, dp(48))
            }
        }
        root.addView(subtitle)

        val buttonRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }

        val purple = Color.parseColor("#7B61FF")
        val buttonWidth = dp(150)

        val btnRoot = Button(this).apply {
            text = "뿌리찾기"
            textSize = 18f
            setTextColor(Color.WHITE)
            background = roundButtonBackground(purple)
            setPadding(dp(24), dp(12), dp(24), dp(12))
            layoutParams = LinearLayout.LayoutParams(
                buttonWidth,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, dp(16), 0)
            }
        }

        val btnCounsel = Button(this).apply {
            text = "상담"
            textSize = 18f
            setTextColor(Color.WHITE)
            background = roundButtonBackground(purple)
            setPadding(dp(24), dp(12), dp(24), dp(12))
            layoutParams = LinearLayout.LayoutParams(
                buttonWidth,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(dp(16), 0, 0, 0)
            }
        }

        buttonRow.addView(btnRoot)
        buttonRow.addView(btnCounsel)
        root.addView(buttonRow)

        // 🔹 관리자 버튼
        val adminBtn = Button(this).apply {
            text = "관리자용 신청 목록 보기"
            textSize = 14f
            setTextColor(Color.WHITE)
            background = roundButtonBackground(Color.parseColor("#9C88FF"))
            setPadding(dp(20), dp(8), dp(20), dp(8))
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER
                setMargins(0, dp(32), 0, 0)
            }
        }
        root.addView(adminBtn)

        // 👉 화면 이동
        btnRoot.setOnClickListener {
            startActivity(Intent(this, RootActivity::class.java))
        }

        btnCounsel.setOnClickListener {
            startActivity(Intent(this, CounselActivity::class.java))
        }

        adminBtn.setOnClickListener {
            // 🔐 관리자 코드 입력 후 이동
            showAdminCodeDialog()
        }

        setContentView(root)
    }

    // --------- 유틸 함수들 ---------

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    private fun roundButtonBackground(color: Int): GradientDrawable =
        GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = dp(999).toFloat()
            setColor(color)
        }

    // 🔐 관리자 코드 다이얼로그
    private fun showAdminCodeDialog() {
        val editText = EditText(this).apply {
            hint = "관리자 코드를 입력하세요"
            inputType = InputType.TYPE_CLASS_NUMBER
            setPadding(dp(16), dp(8), dp(16), dp(8))
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle("관리자 인증")
            .setMessage("관리자만 접근 가능한 화면입니다.")
            .setView(editText)
            .setPositiveButton("확인") { d, _ ->
                val input = editText.text.toString().trim()
                val ADMIN_CODE = "1004"   // 👉 너만 아는 코드로 바꿔도 됨

                if (input == ADMIN_CODE) {
                    startActivity(Intent(this, RootAdminActivity::class.java))
                } else {
                    Toast.makeText(
                        this,
                        "관리자 코드가 올바르지 않습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                d.dismiss()
            }
            .setNegativeButton("취소") { d, _ -> d.dismiss() }
            .create()

        dialog.show()
    }
}
