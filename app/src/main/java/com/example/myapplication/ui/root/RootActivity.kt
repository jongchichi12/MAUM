package com.example.myapplication.ui.root

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

class RootActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bgColor = Color.parseColor("#F2EBFF")   // 전체 배경 연보라
        val mainPurple = Color.parseColor("#7B61FF")
        val textPurple = Color.parseColor("#5A42A6")
        val cardBg = Color.parseColor("#F9F2FF")
        val iconBg = Color.parseColor("#E1D4FF")
        val tabUnselected = Color.parseColor("#9C8BD9")

        // 1. 스크롤 가능한 전체 레이아웃
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

        // 2. 상단 MAUM + 탭 메뉴
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

        // 3. 안내 문구
        val description = TextView(this).apply {
            text = "친부모 찾기 프로그램 지원 및 \n행정적 연계를 도와드립니다."
            textSize = 16f
            setTextColor(textPurple)
            setPadding(horizontalPadding, dp(8), horizontalPadding, dp(16))

            gravity = Gravity.CENTER
            textAlignment = View.TEXT_ALIGNMENT_CENTER

            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        root.addView(description)

        // 4. 카드 1 : 뿌리 찾기 지원
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
            text = "🔍"
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
            text = "뿌리 찾기 지원"
            textSize = 18f
            setTextColor(textPurple)
            typeface = Typeface.DEFAULT_BOLD
        }

        val card1Subtitle = TextView(this).apply {
            text = "신청서를 작성하고 친부모 찾기 절차를 안내받을 수 있어요"
            textSize = 14f
            setTextColor(textPurple)
        }

        val card1Button = Button(this).apply {
            text = "뿌리 찾기 지원"
            textSize = 15f
            setTextColor(Color.WHITE)
            background = roundedRect(mainPurple, dp(999))
            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, dp(12), 0, 0)
            layoutParams = params
            setPadding(dp(20), dp(8), dp(20), dp(8))
        }

        card1TextArea.addView(card1Title)
        card1TextArea.addView(card1Subtitle)
        card1TextArea.addView(card1Button)

        card1.addView(iconBox1)
        card1.addView(card1TextArea)
        root.addView(card1)

        // ▶ 여기서 뿌리 찾기 지원 상세 화면으로 이동
        card1Button.setOnClickListener {
            val intent = Intent(this, RootSupportActivity::class.java)
            startActivity(intent)
        }

        // 5. 카드 2 : 기관 알아보기
        val card2 = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            background = roundedRect(cardBg, dp(20))
            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(horizontalPadding, dp(4), horizontalPadding, dp(24))
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
            text = "🏛️"
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
            text = "기관 알아보기"
            textSize = 18f
            setTextColor(textPurple)
            typeface = Typeface.DEFAULT_BOLD
        }

        val card2Subtitle = TextView(this).apply {
            text = "중앙입양원, 홀트, 대한사회복지회 등\n관련 기관 정보를 확인할 수 있어요"
            textSize = 14f
            setTextColor(textPurple)
        }

        val card2Button = Button(this).apply {
            text = "기관 알아보기"
            textSize = 15f
            setTextColor(Color.WHITE)
            background = roundedRect(mainPurple, dp(999))
            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, dp(12), 0, 0)
            layoutParams = params
            setPadding(dp(20), dp(8), dp(20), dp(8))
        }

        card2TextArea.addView(card2Title)
        card2TextArea.addView(card2Subtitle)
        card2TextArea.addView(card2Button)

        card2.addView(iconBox2)
        card2.addView(card2TextArea)
        root.addView(card2)

        // ▶ 여기서 기관 알아보기 화면으로 이동
        card2Button.setOnClickListener {
            val intent = Intent(this, AgencyInfoActivity::class.java)
            startActivity(intent)
        }

        // 6. 맨 아래 MAUM 스타일 뒤로가기 버튼
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
            params.setMargins(0, dp(8), 0, dp(32))
            layoutParams = params
        }
        backRow.addView(backBtn)
        root.addView(backRow)

        // 끝
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
}
