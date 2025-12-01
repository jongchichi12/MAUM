package com.example.myapplication.ui.root

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RootAdminActivity : ComponentActivity() {

    // 🔹 Firestore 인스턴스
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bgColor = Color.parseColor("#F2EBFF")
        val titlePurple = Color.parseColor("#2F285A")
        val borderPurple = Color.parseColor("#C8B6FF")

        // 전체를 감싸는 스크롤뷰
        val scroll = ScrollView(this).apply {
            setBackgroundColor(bgColor)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        // 실제 내용이 들어갈 루트 레이아웃
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(24), dp(32), dp(24), dp(32))
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        scroll.addView(root)

        // 제목
        val title = TextView(this).apply {
            text = "뿌리찾기 신청 목록 (관리자)"
            textSize = 22f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(titlePurple)
        }
        root.addView(title)

        // 설명
        val desc = TextView(this).apply {
            text = "Firebase에 저장된 뿌리찾기 신청서를 보여줍니다."
            textSize = 14f
            setTextColor(titlePurple)
            setPadding(0, dp(8), 0, dp(16))
        }
        root.addView(desc)

        // 🔹 카드들이 쌓일 컨테이너
        val listContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        root.addView(listContainer)

        // 화면에 보여주기
        setContentView(scroll)

        // 🔥 Firestore에서 신청서 목록 불러오기
        db.collection("rootRequests")
            .get()
            .addOnSuccessListener { snapshot ->
                listContainer.removeAllViews()

                if (snapshot.isEmpty) {
                    listContainer.addView(
                        TextView(this).apply {
                            text = "신청서가 아직 없습니다."
                            textSize = 15f
                            setTextColor(titlePurple)
                            gravity = Gravity.CENTER
                            setPadding(0, dp(24), 0, 0)
                        }
                    )
                    return@addOnSuccessListener
                }

                for (doc in snapshot.documents) {
                    // RootRequest 데이터 클래스로 매핑
                    val req = doc.toObject(RootRequest::class.java)
                    if (req != null) {
                        listContainer.addView(
                            makeRequestCard(
                                req = req,
                                titlePurple = titlePurple,
                                borderPurple = borderPurple
                            )
                        )
                    }
                }
            }
            .addOnFailureListener { e ->
                listContainer.removeAllViews()
                listContainer.addView(
                    TextView(this).apply {
                        text = "데이터 불러오기 실패: ${e.message}"
                        textSize = 14f
                        setTextColor(Color.RED)
                        setPadding(0, dp(24), 0, 0)
                    }
                )
            }
    }

    // 🔹 신청서 하나를 카드로 만드는 함수
    private fun makeRequestCard(
        req: RootRequest,
        titlePurple: Int,
        borderPurple: Int
    ): View {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(12), dp(16), dp(12))
            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = dp(16).toFloat()
                setColor(Color.WHITE)
                setStroke(dp(1), borderPurple)
            }
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, dp(8), 0, dp(8))
            }
        }

        val line1 = TextView(this).apply {
            text = "이름: ${req.name}   /   입양 시기: ${req.adoptionDate}"
            textSize = 15f
            setTextColor(titlePurple)
            typeface = Typeface.DEFAULT_BOLD
        }

        val line2 = TextView(this).apply {
            text = "생년월일: ${req.birth}   /   연락처: ${req.phone}"
            textSize = 14f
            setTextColor(titlePurple)
        }

        val line3 = TextView(this).apply {
            text = "이메일: ${req.email}\n가족 정보: ${req.familyInfo}"
            textSize = 14f
            setTextColor(titlePurple)
        }

        card.addView(line1)
        card.addView(line2)
        card.addView(line3)

        return card
    }

    // dp -> px
    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()
}
