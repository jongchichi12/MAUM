package com.example.myapplication.ui.counsel

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
import android.widget.Toast
import androidx.activity.ComponentActivity

class MyCounselLogActivity : ComponentActivity() {

    data class CounselLog(
        val type: String,      // "AI 감정 상담" / "상담사와의 상담"
        val date: String,      // "2024.04.06"
        val preview: String,   // 내용 한 줄 요약
        val isAi: Boolean      // 아이콘 모양 다르게 주려고
    )

    private val bgColor = Color.parseColor("#F2EBFF")
    private val mainPurple = Color.parseColor("#7B61FF")
    private val titlePurple = Color.parseColor("#241A5F")
    private val textPurple = Color.parseColor("#4B3B8F")
    private val cardBg = Color.WHITE
    private val iconBg = Color.parseColor("#E6D9FF")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 루트
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bgColor)
            setPadding(dp(24), dp(40), dp(24), dp(32))
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        // 상단 제목
        val titleView = TextView(this).apply {
            text = "상담 기록함"
            textSize = 28f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(titlePurple)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(24)
            }
        }
        root.addView(titleView)

        // 스크롤 영역
        val scrollView = ScrollView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        }

        val listContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        // 샘플 상담 기록 2개 (이미지처럼)
        val logs = listOf(
            CounselLog(
                type = "AI 감정 상담",
                date = "2024.04.06",
                preview = "요즘 부쩍 불안해진 것 같아요...",
                isAi = true
            ),
            CounselLog(
                type = "상담사와의 상담",
                date = "2024.03.19",
                preview = "입양 관련 고민이 있습니다",
                isAi = false
            )
        )

        logs.forEach { log ->
            listContainer.addView(createLogCard(log))
        }

        scrollView.addView(listContainer)
        root.addView(scrollView)

        // 하단 뒤로가기 버튼 (MAUM 공통 스타일)
        val backBtn = Button(this).apply {
            text = "←  뒤로가기"
            textSize = 16f
            setTextColor(Color.WHITE)
            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = dp(999).toFloat()
                setColor(mainPurple)
            }
            setPadding(dp(32), dp(12), dp(32), dp(12))
            setOnClickListener { finish() }
        }

        val backRow = LinearLayout(this).apply {
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(16)
            }
            addView(backBtn)
        }

        root.addView(backRow)

        setContentView(root)
    }

    // 카드 하나 만드는 함수
    private fun createLogCard(log: CounselLog): View {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(16)
            }

            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = dp(20).toFloat()
                setColor(cardBg)
            }
        }

        // 왼쪽 아이콘 동그라미
        val iconCircle = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                dp(52),
                dp(52)
            ).apply {
                rightMargin = dp(12)
            }
            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(iconBg)
            }
        }

        val iconText = TextView(this).apply {
            text = if (log.isAi) "🤖" else "👤"
            textSize = 24f
            gravity = Gravity.CENTER
        }
        iconCircle.addView(iconText)

        // 가운데 텍스트 영역
        val textArea = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        }

        val typeView = TextView(this).apply {
            text = log.type
            textSize = 18f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(textPurple)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val dateView = TextView(this).apply {
            text = log.date
            textSize = 13f
            setTextColor(Color.parseColor("#7A70B4"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(2)
                bottomMargin = dp(8)
            }
        }

        val previewView = TextView(this).apply {
            text = log.preview
            textSize = 14f
            setTextColor(Color.parseColor("#4B3B8F"))
        }

        textArea.addView(typeView)
        textArea.addView(dateView)
        textArea.addView(previewView)

        // 오른쪽 "상세보기 >"
        val detailView = TextView(this).apply {
            text = "상세보기  >"
            textSize = 14f
            setTextColor(textPurple)
        }

        val detailContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.BOTTOM
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            addView(detailView)
        }

        // 카드 클릭 시 (지금은 토스트만)
        val onClickListener = View.OnClickListener {
            Toast.makeText(
                this,
                "「${log.type}」 기록 상세보기(추후 구현 예정)",
                Toast.LENGTH_SHORT
            ).show()
        }

        card.setOnClickListener(onClickListener)
        detailView.setOnClickListener(onClickListener)

        card.addView(iconCircle)
        card.addView(textArea)
        card.addView(detailContainer)

        return card
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()
}
