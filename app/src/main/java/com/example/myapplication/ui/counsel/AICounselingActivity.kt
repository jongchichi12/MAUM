package com.example.myapplication.ui.counsel

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.InputType
import android.text.Editable
import android.text.TextWatcher
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
import com.example.myapplication.BuildConfig
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.data.llm.ChatMessage
import com.example.myapplication.data.llm.ChatRole
import com.example.myapplication.data.llm.ProxyChatClient
import com.example.myapplication.data.llm.ProxyChatStub
import com.example.myapplication.db.ChatMessageRepository
import com.example.myapplication.db.MaumDatabase
import com.example.myapplication.ui.counsel.chat.AICounselingController
import kotlinx.coroutines.launch

class AICounselingActivity : ComponentActivity() {

    private lateinit var controller: AICounselingController
    private var isSending: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // MAUM 톤 컬러
        val bgColor = Color.parseColor("#F2EBFF")      // 전체 배경 연보라
        val mainPurple = Color.parseColor("#7B61FF")   // 메인 버튼/말풍선
        val subPurple = Color.parseColor("#5A42A6")    // 제목/서브 텍스트
        val bubbleBg = Color.parseColor("#FFFFFF")     // 말풍선 배경
        val iconBg = Color.parseColor("#E1D4FF")       // AI 아이콘 배경
        val borderPurple = Color.parseColor("#C8B6FF") // 입력창 테두리

        val horizontalPadding = dp(24)

        // 스크롤 가능한 전체 레이아웃
        val scrollView = ScrollView(this).apply {
            setBackgroundColor(bgColor)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(horizontalPadding, dp(32), horizontalPadding, dp(24))
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        scrollView.addView(root)

        // ───── 헤더 영역 ─────
        val headerTitle = TextView(this).apply {
            text = "지금의 마음을 털어놓아 보세요"
            textSize = 24f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(subPurple)
        }
        root.addView(headerTitle)

        val headerSubtitle = TextView(this).apply {
            text = "안녕하세요, 당신의 이야기를 듣고 있는\nMAUM AI입니다"
            textSize = 14f
            setTextColor(subPurple)
            setPadding(0, dp(8), 0, dp(20))
        }
        root.addView(headerSubtitle)

        // ───── 메시지 영역 컨테이너 (나중에 GPT 붙일 때 여기만 쓰면 됨) ─────
        val messageContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        root.addView(messageContainer)

        // AI 첫 메시지 카드
        messageContainer.addView(
            makeAIBubble(
                iconBg = iconBg,
                bubbleBg = bubbleBg,
                textColor = subPurple,
                message = "어떤 이야기라도 괜찮아요.\n편하게 말씀해 주세요.",
                timestamp = "1분 전"
            )
        )

        // 가운데 새싹 아이콘 (치유/시작 상징)
        val sprout = TextView(this).apply {
            text = "🌱"
            textSize = 24f
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, dp(16), 0, dp(16))
            }
        }
        root.addView(sprout)

        // ───── 하단 입력창 + 전송 버튼 ─────
        val inputRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        val inputField = EditText(this).apply {
            hint = "메시지를 입력하세요..."
            textSize = 15f
            setHintTextColor(borderPurple)
            setTextColor(subPurple)
            setPadding(dp(16), dp(10), dp(16), dp(10))
            background = roundedRectStroke(
                bgColor = Color.WHITE,
                strokeColor = borderPurple,
                radiusDp = 30,
                strokeWidthDp = 1
            )
            inputType = InputType.TYPE_CLASS_TEXT
            isSingleLine = true

            layoutParams = LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            ).apply {
                setMargins(0, 0, dp(8), 0)
            }
        }

        val sendButton = Button(this).apply {
            text = "➤"
            textSize = 18f
            setTextColor(Color.WHITE)
            background = roundedRect(mainPurple, 30)
            setPadding(dp(18), dp(10), dp(18), dp(10))
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            isEnabled = false // 입력이 있을 때만 활성화
        }

        inputRow.addView(inputField)
        inputRow.addView(sendButton)

        root.addView(inputRow)

        // ───── 뒤로가기 버튼 ─────
        val backBtn = Button(this).apply {
            text = "←  뒤로가기"
            textSize = 16f
            setTextColor(Color.WHITE)
            background = roundedRect(mainPurple, 999)
            setPadding(dp(24), dp(12), dp(24), dp(12))
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER_HORIZONTAL
                setMargins(0, dp(24), 0, dp(8))
            }
            setOnClickListener { finish() }
        }
        root.addView(backBtn)

        setContentView(scrollView)

        // ---- 프록시 기반 LLM 컨트롤러 연결 ----
        val proxyBaseUrl = BuildConfig.PROXY_BASE_URL
        val proxyApiKey = BuildConfig.PROXY_API_KEY.takeIf { it.isNotBlank() }
        val primaryClient = if (proxyBaseUrl.isNotBlank()) {
            ProxyChatClient(baseUrl = proxyBaseUrl, apiKey = proxyApiKey)
        } else {
            null
        }
        val fallbackClient = ProxyChatStub()

        val chatRepo = ChatMessageRepository(
            MaumDatabase.getInstance(applicationContext).chatMessageDao()
        )

        controller = AICounselingController(
            primaryClient = primaryClient,
            fallbackClient = fallbackClient
        ).apply {
            onNewMessage = { msg ->
                val bubble = if (msg.role == ChatRole.USER) {
                    makeUserBubble(message = msg.content, bubbleColor = mainPurple)
                } else {
                    makeAIBubble(
                        iconBg = iconBg,
                        bubbleBg = bubbleBg,
                        textColor = subPurple,
                        message = msg.content,
                        timestamp = "방금 전"
                    )
                }
                messageContainer.addView(bubble)
                scrollView.post { scrollView.fullScroll(View.FOCUS_DOWN) }
                if (msg.role == ChatRole.ASSISTANT) {
                    isSending = false
                    sendButton.isEnabled = inputField.text.isNotBlank()
                }
                // 저장
                lifecycleScope.launch {
                    chatRepo.add(role = msg.role.name, content = msg.content)
                }
            }
            onError = { err ->
                Toast.makeText(
                    this@AICounselingActivity,
                    "전송 실패: ${err.message ?: "알 수 없는 오류"}",
                    Toast.LENGTH_SHORT
                ).show()
                isSending = false
                sendButton.isEnabled = inputField.text.isNotBlank()
            }
        }

        // 입력 변화에 따라 전송 버튼 활성/비활성
        inputField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (!isSending) {
                    sendButton.isEnabled = !s.isNullOrBlank()
                }
            }
        })

        // 이전 대화 불러오기
        lifecycleScope.launch {
            val history = chatRepo.getAll().map {
                ChatMessage(
                    role = if (it.role.equals("assistant", ignoreCase = true)) ChatRole.ASSISTANT else ChatRole.USER,
                    content = it.content
                )
            }
            controller.seedHistory(history)
            history.forEach { msg ->
                val bubble = if (msg.role == ChatRole.USER) {
                    makeUserBubble(message = msg.content, bubbleColor = mainPurple)
                } else {
                    makeAIBubble(
                        iconBg = iconBg,
                        bubbleBg = bubbleBg,
                        textColor = subPurple,
                        message = msg.content,
                        timestamp = "이전 대화"
                    )
                }
                messageContainer.addView(bubble)
            }
            scrollView.post { scrollView.fullScroll(View.FOCUS_DOWN) }
        }

        // 전송 버튼 클릭 → GPT로 전송
        sendButton.setOnClickListener {
            val text = inputField.text.toString().trim()
            if (text.isNotEmpty()) {
                isSending = true
                sendButton.isEnabled = false
                controller.sendUserMessage(text)
                inputField.setText("")
            }
        }
    }

    // ───── 말풍선 UI 만드는 함수들 ─────

    // AI 말풍선 (왼쪽 정렬 + 아이콘)
    private fun makeAIBubble(
        iconBg: Int,
        bubbleBg: Int,
        textColor: Int,
        message: String,
        timestamp: String
    ): View {
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.START
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, dp(8), 0, dp(8))
            }
        }

        val iconCircle = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            background = roundedRect(iconBg, 999)
            val size = dp(40)
            layoutParams = LinearLayout.LayoutParams(size, size)
        }

        val icon = TextView(this).apply {
            text = "🤖"
            textSize = 20f
            gravity = Gravity.CENTER
        }
        iconCircle.addView(icon)

        val bubbleLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = roundedRect(bubbleBg, 20)
            setPadding(dp(16), dp(12), dp(16), dp(12))
            layoutParams = LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            ).apply {
                setMargins(dp(8), 0, 0, 0)
            }
        }

        val msgText = TextView(this).apply {
            text = message
            textSize = 15f
            setTextColor(textColor)
        }

        val timeText = TextView(this).apply {
            text = timestamp
            textSize = 11f
            setTextColor(Color.parseColor("#9C8BD9"))
            setPadding(0, dp(4), 0, 0)
        }

        bubbleLayout.addView(msgText)
        bubbleLayout.addView(timeText)

        container.addView(iconCircle)
        container.addView(bubbleLayout)

        return container
    }

    // 사용자 말풍선 (오른쪽 정렬)
    private fun makeUserBubble(
        message: String,
        bubbleColor: Int
    ): View {
        val outer = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.END
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, dp(8), 0, dp(8))
            }
        }

        val bubble = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = roundedRect(bubbleColor, 20)
            setPadding(dp(16), dp(10), dp(16), dp(10))
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        val msgText = TextView(this).apply {
            text = message
            textSize = 15f
            setTextColor(Color.WHITE)
        }

        bubble.addView(msgText)
        outer.addView(bubble)

        return outer
    }

    // ───── 공용 유틸 함수 ─────

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
}
