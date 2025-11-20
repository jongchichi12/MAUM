package com.example.myapplication.ui

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.example.myapplication.ui.counsel.CounselActivity
import com.example.myapplication.ui.root.RootActivity

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. 전체 배경 레이아웃
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#F2EBFF"))       // 연보라
            gravity = Gravity.CENTER                              // 세로·가로 모두 가운데
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        // 2. 제목 MAUM
        val title = TextView(this).apply {
            text = "MAUM"
            textSize = 44f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#7B61FF"))
            gravity = Gravity.CENTER

            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, dp(24))
            layoutParams = params
        }
        root.addView(title)

        // 3. 슬로건
        val subtitle = TextView(this).apply {
            text = "당신의 마음을\n듣고 있습니다."
            textSize = 26f
            setTextColor(Color.parseColor("#7B61FF"))
            gravity = Gravity.CENTER

            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, dp(48))
            layoutParams = params
        }
        root.addView(subtitle)

        // 4. 버튼 가로 레이아웃
        val buttonRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }

        val purple = Color.parseColor("#7B61FF")
        val buttonWidth = dp(150)

        // 5. 뿌리찾기 버튼
        val btnRoot = Button(this).apply {
            text = "뿌리찾기"
            textSize = 18f
            setTextColor(Color.WHITE)
            background = roundButtonBackground(purple)
            setPadding(dp(24), dp(12), dp(24), dp(12))

            val params = LinearLayout.LayoutParams(
                buttonWidth,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, dp(16), 0)    // 오른쪽 간격
            layoutParams = params
        }

        // 6. 상담 버튼
        val btnCounsel = Button(this).apply {
            text = "상담"
            textSize = 18f
            setTextColor(Color.WHITE)
            background = roundButtonBackground(purple)
            setPadding(dp(24), dp(12), dp(24), dp(12))

            val params = LinearLayout.LayoutParams(
                buttonWidth,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(dp(16), 0, 0, 0)    // 왼쪽 간격
            layoutParams = params
        }

        // 7. 버튼들을 레이아웃에 추가
        buttonRow.addView(btnRoot)
        buttonRow.addView(btnCounsel)
        root.addView(buttonRow)

        // 8. 버튼 클릭 → 페이지 이동
        btnRoot.setOnClickListener {
            // MAUM 뿌리찾기 화면으로 이동
            startActivity(Intent(this, RootActivity::class.java))
        }

        btnCounsel.setOnClickListener {
            // MAUM 상담 화면으로 이동
            startActivity(Intent(this, CounselActivity::class.java))
        }

        // 9. 화면에 보여주기
        setContentView(root)
    }

    // dp → px 변환
    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    // 둥근 알약 모양 버튼 배경
    private fun roundButtonBackground(color: Int): GradientDrawable =
        GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = dp(999).toFloat()   // 크게 줘서 완전 둥글게
            setColor(color)
        }
}
