package com.nexo.ui.profile

enum class QuestionType {
    SINGLE_CHOICE,
    MULTIPLE_CHOICE,
    OPEN_TEXT
}

data class QuizQuestion(
    val id: String,
    val stepTitle: String,
    val question: String,
    val subtitle: String? = null,
    val type: QuestionType,
    val options: List<String> = emptyList(),
    val placeholder: String = ""
)

data class LearningQuizUiState(
    val currentStep: Int = 0,
    val questions: List<QuizQuestion> = defaultProfileQuizQuestions(),
    val singleAnswers: Map<String, String> = emptyMap(),
    val multipleAnswers: Map<String, Set<String>> = emptyMap(),
    val textAnswers: Map<String, String> = emptyMap(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isCompleted: Boolean = false
) {
    val totalSteps: Int get() = questions.size
    val currentQuestion: QuizQuestion get() = questions[currentStep]
    val progress: Float get() = (currentStep + 1).toFloat() / totalSteps.toFloat()

    fun isCurrentStepValid(): Boolean {
        val q = currentQuestion
        if (q.id == "golden_rules") return true // Última pergunta de texto é opcional
        return when (q.type) {
            QuestionType.SINGLE_CHOICE -> !singleAnswers[q.id].isNullOrBlank()
            QuestionType.MULTIPLE_CHOICE -> !multipleAnswers[q.id].isNullOrEmpty()
            QuestionType.OPEN_TEXT -> !textAnswers[q.id].isNullOrBlank()
        }
    }

    fun buildConsolidatedProfileRules(): String = buildString {
        appendLine("• Neurodivergência / Perfil Cognitivo: ${multipleAnswers["neurodivergence"]?.joinToString(", ") ?: "Nenhuma específica"}")
        appendLine("• Arquitetura Preferida dos Roteiros: ${singleAnswers["structure_preference"] ?: "Equilibrada"}")
        appendLine("• Duração Ideal por Sessão de Foco: ${singleAnswers["session_duration"] ?: "30 a 50 minutos"}")
        val notes = textAnswers["golden_rules"]
        if (!notes.isNullOrBlank()) {
            appendLine("• Regras Específicas do Aluno: $notes")
        }
    }
}

fun defaultProfileQuizQuestions(): List<QuizQuestion> = listOf(
    QuizQuestion(
        id = "neurodivergence",
        stepTitle = "Perfil Cognitivo",
        question = "Você possui alguma neurodivergência ou característica de atenção?",
        subtitle = "Selecione uma ou mais opções. Isso define como a IA vai quebrar e organizar os tópicos de qualquer trilha.",
        type = QuestionType.MULTIPLE_CHOICE,
        options = listOf(
            "TDAH (prefiro micro-tópicos curtos, metas rápidas e alta clareza de progresso)",
            "TEA / Autismo (prefiro estrutura previsível, literalidade e ordem lógica rígida)",
            "Dislexia (prefiro títulos objetivos, frases curtas e menos sobrecarga textual)",
            "Ansiedade / Sobrecarga (prefiro passos pequenos para evitar paralisia)",
            "Nenhuma / Aprendizado típico"
        )
    ),
    QuizQuestion(
        id = "structure_preference",
        stepTitle = "Estilo de Roteiro",
        question = "Como você prefere que os roteiros de estudo sejam organizados?",
        subtitle = "Será aplicado como padrão em todas as trilhas que você criar.",
        type = QuestionType.SINGLE_CHOICE,
        options = listOf(
            "Micro-passos (muitos tópicos pequenos e granulares, fáceis de concluir)",
            "Mão na massa (foco em prática e projetos desde os primeiros tópicos)",
            "Teoria estruturada (base conceitual sólida antes de ir para a prática)",
            "Visão geral para o detalhe (mapa macro primeiro, aprofundando aos poucos)"
        )
    ),
    QuizQuestion(
        id = "session_duration",
        stepTitle = "Janela de Foco",
        question = "Quanto tempo dura uma sessão ideal de estudos para você?",
        subtitle = "Ajuda a calibrar o tamanho de cada etapa do roteiro.",
        type = QuestionType.SINGLE_CHOICE,
        options = listOf(
            "15 a 25 minutos (Blocos curtos / Estilo Pomodoro)",
            "30 a 50 minutos (Sessões moderadas)",
            "1 hora ou mais (Imersão profunda)"
        )
    ),
    QuizQuestion(
        id = "golden_rules",
        stepTitle = "Regras de Ouro",
        question = "Existe alguma preferência permanente que a IA deve sempre seguir?",
        subtitle = "Opcional. Escreva livremente qualquer instrução que valha para todos os seus estudos.",
        type = QuestionType.OPEN_TEXT,
        placeholder = "Ex: Sempre incluir um tópico de revisão a cada 4 módulos; evitar termos em inglês sem explicação..."
    )
)