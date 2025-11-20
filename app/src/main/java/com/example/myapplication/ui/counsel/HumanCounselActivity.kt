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

class HumanCounselActivity : ComponentActivity() {

    data class Counselor(
        val name: String,
        val field: String,
        val styleDesc: String
    )

    private val bgColor = Color.parseColor("#F2EBFF")
    private val mainPurple = Color.parseColor("#7B61FF")
    private val cardBg = Color.WHITE
    private val cardBorder = Color.parseColor("#E0D4FF")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 전체 루트 레이아웃
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bgColor)
            setPadding(40, 60, 40, 60)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        // ---------------------
        // 상단 제목만 (뒤로가기 제거)
        // ---------------------
        val titleView = TextView(this).apply {
            text = "상담사 연결하기"
            textSize = 26f
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            setTextColor(Color.parseColor("#2C225F"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 40
            }
        }
        root.addView(titleView)

        // 설명 문구
        val description = TextView(this).apply {
            text = "연결을 원하는 상담사를 선택하면\n관리 매니저에게 정보가 전달돼요."
            textSize = 14f
            setTextColor(Color.parseColor("#6A5E99"))
            gravity = Gravity.CENTER
            setLineSpacing(4f, 1.1f)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 30
            }
        }
        root.addView(description)

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

        // 상담사 목록
        val counselors = listOf(
            Counselor("김하은 상담사", "분야: 우울, 불안, 청년 정서 상담", "당신의 속도를 존중하는 상담을 합니다."),
            Counselor("박서준 상담사", "분야: 관계 문제, 자기감정 이해", "이해받는 경험이 회복의 시작입니다."),
            Counselor("최다정 상담사", "분야: 입양·뿌리찾기 진로", "뿌리를 찾는 여정, 함께갈게요."),
            Counselor("이규민 상담사", "분야: 트라우마 회복, 불면", "감정의 안전함을 최우선으로 생각합니다."),
            Counselor("정소리 상담사", "분야: 자기존중감, 진로 진학", "작은 변화가 큰 힘이 될 수 있어요.")
        )

        counselors.forEach { counselor ->
            listContainer.addView(createCounselorCard(counselor))
        }

        scrollView.addView(listContainer)
        root.addView(scrollView)

        // -----------------------
        // 하단 뒤로가기 버튼
        // -----------------------
        val bottomBackBtn = Button(this).apply {
            text = "←  뒤로가기"
            textSize = 16f
            setTextColor(Color.WHITE)
            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 80f
                setColor(mainPurple)
            }
            setPadding(60, 24, 60, 24)
            setOnClickListener { finish() }
        }

        val bottomRow = LinearLayout(this).apply {
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 30
            }
            addView(bottomBackBtn)
        }

        root.addView(bottomRow)

        setContentView(root)
    }

    private fun createCounselorCard(counselor: Counselor): View {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(32, 32, 32, 32)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 24
            }
            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 40f
                setColor(cardBg)
                setStroke(2, cardBorder)
            }
        }

        val textBox = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        }

        val nameView = TextView(this).apply {
            text = counselor.name
            textSize = 18f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#2C225F"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 8 }
        }

        val fieldView = TextView(this).apply {
            text = counselor.field
            textSize = 13f
            setTextColor(Color.parseColor("#6A5E99"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 4 }
        }

        val styleView = TextView(this).apply {
            text = counselor.styleDesc
            textSize = 13f
            setTextColor(Color.parseColor("#8C7FC5"))
        }

        textBox.addView(nameView)
        textBox.addView(fieldView)
        textBox.addView(styleView)

        val connectBtn = Button(this).apply {
            text = "연결하기"
            textSize = 14f
            setTextColor(Color.WHITE)
            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 50f
                setColor(mainPurple)
            }
            setPadding(40, 20, 40, 20)
            setOnClickListener {
                onCounselorSelected(counselor)
            }
        }

        card.addView(textBox)
        card.addView(connectBtn)

        return card
    }

    private fun onCounselorSelected(counselor: Counselor) {
        Toast.makeText(
            this,
            "「${counselor.name}」에게 연결 요청을 보냈어요.",
            Toast.LENGTH_SHORT
        ).show()
    }
}
