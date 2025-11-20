package com.example.myapplication.ui.counsel

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.ComponentActivity

class CounselActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bgColor = Color.parseColor("#F2EBFF")
        val mainPurple = Color.parseColor("#7B61FF")
        val textPurple = Color.parseColor("#5A42A6")
        val cardBg = Color.parseColor("#F9F2FF")
        val iconBg = Color.parseColor("#E1D4FF")
        val tabUnselected = Color.parseColor("#9C8BD9")

        val scrollView = ScrollView(this).apply {
            setBackgroundColor(bgColor)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        scrollView.addView(root)

        val horizontalPadding = dp(24)

        // 상단 MAUM + 탭
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(horizontalPadding, dp(24), horizontalPadding, dp(12))
        }

        val title = TextView(this).apply {
            text = "MAUM"
            textSize = 28f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(mainPurple)
        }
        header.addView(title)

        val tabRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.START
            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, dp(16), 0, 0)
            layoutParams = params
        }

        fun makeTab(text: String, selected: Boolean): LinearLayout {
            val container = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                val p = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                p.setMargins(0, 0, dp(24), 0)
                layoutParams = p
            }

            val tv = TextView(this).apply {
                this.text = text
                textSize = 16f
                setTextColor(if (selected) mainPurple else tabUnselected)
            }

            val underline = View(this).apply {
                val h = dp(2)
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    h
                ).also { lp ->
                    lp.setMargins(0, dp(4), 0, 0)
                }
                setBackgroundColor(if (selected) mainPurple else Color.TRANSPARENT)
            }

            container.addView(tv)
            container.addView(underline)
            return container
        }

        // 필요하면 탭 추가해서 쓰면 됨
        // val tab1 = makeTab("상담", true)
        // tabRow.addView(tab1)

        header.addView(tabRow)
        root.addView(header)

        // 안내 문구
        val description = TextView(this).apply {
            text = "전문 상담사와 함께 당신의 마음을 들어드립니다."
            textSize = 16f
            setTextColor(textPurple)
            setPadding(horizontalPadding, dp(8), horizontalPadding, dp(16))
        }
        root.addView(description)

        // 카드 1 : 지금의 마음을 남겨보세요
        val card1 = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            background = roundedRect(cardBg, dp(20))
            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(horizontalPadding, dp(8), horizontalPadding, dp(12))
            layoutParams = params
            setPadding(dp(16), dp(16), dp(16), dp(16))
            gravity = Gravity.CENTER_VERTICAL
        }

        val iconBox1 = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            background = roundedRect(iconBg, dp(16))
            val size = dp(52)
            layoutParams = LinearLayout.LayoutParams(size, size)
        }

        val icon1 = TextView(this).apply {
            text = "💬"
            textSize = 24f
            gravity = Gravity.CENTER
        }
        iconBox1.addView(icon1)

        val card1TextArea = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            val params = LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            )
            params.setMargins(dp(12), 0, 0, 0)
            layoutParams = params
        }

        val card1Title = TextView(this).apply {
            text = "지금의 마음을 남겨보세요"
            textSize = 18f
            setTextColor(textPurple)
            typeface = Typeface.DEFAULT_BOLD
        }

        val card1Subtitle = TextView(this).apply {
            text = ""
            textSize = 14f
            setTextColor(textPurple)
        }

        // 🤖 AI에게 털어놓기
        val aiButton = Button(this).apply {
            text = "🤖 AI에게 털어놓기"
            textSize = 15f
            setTextColor(Color.WHITE)
            background = roundedRect(mainPurple, dp(999))
            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, dp(12), 0, 0)
            layoutParams = params
            setPadding(0, dp(10), 0, dp(10))
        }

        aiButton.setOnClickListener {
            val intent = Intent(this, AICounselingActivity::class.java)
            startActivity(intent)
        }

        // 👥 상담사 연결하기
        val counselorButton = Button(this).apply {
            text = "상담사 연결하기"
            textSize = 15f
            setTextColor(mainPurple)
            background = borderedButton(mainPurple, bgColor)
            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, dp(8), 0, 0)
            layoutParams = params
            setPadding(0, dp(10), 0, dp(10))
        }

        counselorButton.setOnClickListener {
            // 사람 상담사 연결 페이지로 이동
            val intent = Intent(this, HumanCounselActivity::class.java)
            startActivity(intent)
        }

        card1TextArea.addView(card1Title)
        card1TextArea.addView(card1Subtitle)
        card1TextArea.addView(aiButton)
        card1TextArea.addView(counselorButton)

        card1.addView(iconBox1)
        card1.addView(card1TextArea)
        root.addView(card1)

        // 카드 2 : 나의 상담 기록 보기
        val card2 = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            background = roundedRect(cardBg, dp(20))
            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(horizontalPadding, dp(4), horizontalPadding, dp(16))
            layoutParams = params
            setPadding(dp(16), dp(16), dp(16), dp(16))
            gravity = Gravity.CENTER_VERTICAL
        }

        val iconBox2 = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            background = roundedRect(iconBg, dp(16))
            val size = dp(52)
            layoutParams = LinearLayout.LayoutParams(size, size)
        }

        val icon2 = TextView(this).apply {
            text = "📝"
            textSize = 24f
            gravity = Gravity.CENTER
        }
        iconBox2.addView(icon2)

        val card2TextArea = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            val params = LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            )
            params.setMargins(dp(12), 0, 0, 0)
            layoutParams = params
        }

        val card2Title = TextView(this).apply {
            text = "나의 상담 기록 보기"
            textSize = 18f
            setTextColor(textPurple)
            typeface = Typeface.DEFAULT_BOLD
        }

        val card2Button = Button(this).apply {
            text = "나의 상담 기록 보기"
            textSize = 15f
            setTextColor(mainPurple)
            background = borderedButton(mainPurple, bgColor)
            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, dp(12), 0, 0)
            layoutParams = params
            setPadding(0, dp(10), 0, dp(10))
        }

        card2Button.setOnClickListener {
            // 나의 상담 기록 페이지로 이동
            val intent = Intent(this, MyCounselLogActivity::class.java)
            startActivity(intent)
        }

        card2TextArea.addView(card2Title)
        card2TextArea.addView(card2Button)

        card2.addView(iconBox2)
        card2.addView(card2TextArea)
        root.addView(card2)

        // 하단 문구
        val bottomText = TextView(this).apply {
            text = "작은 이야기 괜찮아요.\nMAUM은 언제나 당신의 편이에요. 🌱"
            textSize = 13f
            setTextColor(textPurple)
            gravity = Gravity.CENTER
            setPadding(horizontalPadding, dp(4), horizontalPadding, dp(12))
        }
        root.addView(bottomText)

        // 맨 아래 MAUM 스타일 뒤로가기 버튼
        val backBtn = Button(this).apply {
            text = "←  뒤로가기"
            textSize = 16f
            setTextColor(Color.WHITE)
            background = roundedRect(mainPurple, dp(999))
            setPadding(dp(24), dp(12), dp(24), dp(12))
            setOnClickListener { finish() }
        }

        val backRow = LinearLayout(this).apply {
            gravity = Gravity.CENTER
            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, dp(4), 0, dp(32))
            layoutParams = params
        }
        backRow.addView(backBtn)
        root.addView(backRow)

        setContentView(scrollView)
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    private fun roundedRect(color: Int, radiusDp: Int): GradientDrawable =
        GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = dp(radiusDp).toFloat()
            setColor(color)
        }

    private fun borderedButton(strokeColor: Int, fillColor: Int): GradientDrawable =
        GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = dp(999).toFloat()
            setColor(fillColor)
            setStroke(dp(1), strokeColor)
        }
}
