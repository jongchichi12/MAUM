package com.example.myapplication.ui.root

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.ComponentActivity

class AgencyInfoActivity : ComponentActivity() {

    data class Agency(
        val nameKo: String,
        val nameEn: String,
        val desc: String,
        val url: String
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // MAUM 톤 컬러
        val bgColor = Color.parseColor("#F2EBFF")      // 연보라 배경
        val cardBg = Color.parseColor("#F9F2FF")       // 카드 배경
        val mainPurple = Color.parseColor("#7B61FF")   // 버튼/포인트
        val textPurple = Color.parseColor("#2F285A")   // 진한 텍스트
        val subTextPurple = Color.parseColor("#6E5C9E")

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

        // 상단 제목
        val title = TextView(this).apply {
            text = "기관 알아보기"
            textSize = 26f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(textPurple)
        }
        root.addView(title)

        // 안내 문구
        val subtitle = TextView(this).apply {
            text = "뿌리 찾기 관련 기관 정보를 확인해 보세요."
            textSize = 14f
            setTextColor(subTextPurple)
            setPadding(0, dp(8), 0, dp(24))
        }
        root.addView(subtitle)

        // 기관 리스트 데이터
        val agencies = listOf(
            Agency(
                nameKo = "아동권리보장원",
                nameEn = "Korea Adoption Services",
                desc = "입양 기록 조회, 친가족 찾기 지원 등",
                url = "https://www.kadoption.or.kr" // TODO: 실제 기관 사이트로 변경
            ),
            Agency(
                nameKo = "홀트아동복지회",
                nameEn = "Holt Children’s Services",
                desc = "국내·해외 입양, 가족찾기 지원 등",
                url = "https://love.holt.or.kr/?utm_source=naver&utm_medium=PCBSA&utm_campaign=PChlink&utm_term=홀트아동복지회&utm_content=GROUP&NaPm=ct%3Dmi6yafea%7Cci%3DER18b10547-c5cc-11f0-bfcd-721e731eaafb%7Ctr%3Dbrnd%7Chk%3D57fc368321dabd2a446acd766aaaf684aaeb26dd%7Cnacn%3DliHgBgwxhjwU"
            ),
            Agency(
                nameKo = "대한사회복지회",
                nameEn = "Korean Social Welfare Society",
                desc = "입양 상담, 입양정보 조회 등",
                url = "https://kws.or.kr"
            ),
            Agency(
                nameKo = "동방사회복지회",
                nameEn = "Eastern Social Welfare Society",
                desc = "국내·해외 입양, 친가족 찾기 상담 등",
                url = "https://eastern.or.kr/?utm_source=naver&utm_medium=PC_BS&utm_campaign=main&n_media=27758&n_query=동방사회복지회&n_rank=1&n_ad_group=grp-a001-04-000000041595045&n_ad=nad-a001-04-000000421332461&n_keyword_id=nkw-a001-04-000006163607013&n_keyword=동방사회복지회&n_campaign_type=4&n_contract=tct-a001-04-000000001181423&n_ad_group_type=5"
            ),
            Agency(
                nameKo = "성가정입양원",
                nameEn = "Catholic Family Adoption Center",
                desc = "입양 및 친가족 찾기 관련 상담 등",
                url = "http://www.holyfcac.or.kr"
            )
        )

        // 기관 카드들 추가
        agencies.forEach { agency ->
            root.addView(
                makeAgencyCard(
                    agency = agency,
                    cardBg = cardBg,
                    textPurple = textPurple,
                    subTextPurple = subTextPurple,
                    mainPurple = mainPurple
                )
            )
        }

        // agencies.forEach { ... root.addView(makeAgencyCard(...)) } 까지 끝난 뒤

// 🔹 맨 아래 뒤로가기 버튼
        val backBtn = Button(this).apply {
            text = "←  뒤로가기"
            textSize = 16f
            setTextColor(Color.WHITE)
            background = roundedRect(mainPurple, 999)
            setPadding(dp(24), dp(12), dp(24), dp(12))
            setOnClickListener { finish() }
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

// 🔚 마지막
        setContentView(scrollView)
    }

    // 기관 카드 하나 만드는 함수
    private fun makeAgencyCard(
        agency: Agency,
        cardBg: Int,
        textPurple: Int,
        subTextPurple: Int,
        mainPurple: Int
    ): View {
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            background = roundedRect(cardBg, 20)
            setPadding(dp(16), dp(16), dp(16), dp(16))
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, dp(6), 0, dp(6))
            }
        }

        // 왼쪽 텍스트 영역
        val textArea = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            )
        }

        val nameKoView = TextView(this).apply {
            text = agency.nameKo
            textSize = 18f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(textPurple)
        }

        val nameEnView = TextView(this).apply {
            text = agency.nameEn
            textSize = 13f
            setTextColor(subTextPurple)
            setPadding(0, dp(2), 0, dp(4))
        }

        val descView = TextView(this).apply {
            text = agency.desc
            textSize = 13f
            setTextColor(subTextPurple)
        }

        textArea.addView(nameKoView)
        textArea.addView(nameEnView)
        textArea.addView(descView)

        // 오른쪽 "기관 사이트" 버튼
        val siteButton = Button(this).apply {
            text = "기관 사이트"
            textSize = 14f
            setTextColor(Color.WHITE)
            background = roundedRect(mainPurple, 999)

            setPadding(dp(16), dp(8), dp(16), dp(8))

            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            setOnClickListener {
                // 사이트로 이동 (브라우저 열기)
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(agency.url))
                    startActivity(intent)
                } catch (e: Exception) {
                    // URL이 비어있거나 잘못된 경우를 대비한 예외 처리
                    e.printStackTrace()
                }
            }
        }

        container.addView(textArea)
        container.addView(siteButton)

        return container
    }

    // 공용 dp, 배경 함수들
    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    private fun roundedRect(color: Int, radiusDp: Int): GradientDrawable =
        GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = dp(radiusDp).toFloat()
            setColor(color)
        }
}
