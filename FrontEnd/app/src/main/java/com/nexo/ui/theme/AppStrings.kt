package com.nexo.ui.theme

object AppStrings {
    private val isEn: Boolean
        get() = AppPreferencesState.language == "en-US"

    // 1. COMUM, NAVEGAÇÃO & STATUS
    val navHome get() = if (isEn) "Home" else "Início"
    val navProfile get() = if (isEn) "Profile" else "Perfil"
    val navSettings get() = if (isEn) "Settings" else "Ajustes"

    val btnBack get() = if (isEn) "Back" else "Voltar"
    val btnHelp get() = if (isEn) "Help" else "Ajuda"
    val btnCancel get() = if (isEn) "Cancel" else "Cancelar"
    val btnTryAgain get() = if (isEn) "Try Again" else "Tentar Novamente"

    val labelEmail get() = if (isEn) "E-mail" else "E-mail"
    val labelPassword get() = if (isEn) "Password" else "Senha"
    val labelUsername get() = if (isEn) "Username" else "Nome de usuário"
    val labelOldPassword get() = if (isEn) "Old Password" else "Senha Atual"
    val labelNewPassword get() = if (isEn) "New Password" else "Nova Senha"
    val labelConfirmPassword get() = if (isEn) "Confirm Password" else "Confirmar Senha"

    val statusCompleted get() = if (isEn) "Completed" else "Concluído"
    val statusInProgress get() = if (isEn) "In Progress" else "Em Andamento"
    val statusPending get() = if (isEn) "Pending" else "Pendente"

    // 2. SPLASH, LOGIN, CADASTRO & RECUPERAÇÃO DE SENHA
    val splashProjectAcademy get() = "PROJECT ACADEMY"
    val splashSlogan get() = if (isEn) "Learning today,\ntransforming tomorrow." else "Aprender hoje,\ntransformar o amanhã."

    val loginGreetingTitle get() = if (isEn) "Hello, welcome back!" else "Olá, bem-vindo de volta!"
    val loginGreetingSubtitle get() = if (isEn) "Great to see you here." else "Que bom te ver por aqui."
    val loginEmailOrUserLabel get() = if (isEn) "Email or Username" else "E-mail ou Username"
    val loginEmailOrUserPlaceholder get() = if (isEn) "Enter your email or username" else "Digite seu e-mail ou username"
    val loginEmailLabel get() = if (isEn) "Email" else "E-mail"
    val loginEmailPlaceholder get() = if (isEn) "your@email.com" else "seu@email.com"
    val loginPasswordPlaceholder get() = "••••••••"
    val loginForgotPassword get() = if (isEn) "Forgot your password?" else "Esqueceu sua senha?"
    val loginBtnSubmit get() = if (isEn) "Sign In" else "Entrar"
    val loginBtnGoogle get() = if (isEn) "Sign in with Google" else "Entrar com Google"
    val loginNoAccountPrompt get() = if (isEn) "Don't have an account? " else "Não tem uma conta? "
    val loginCreateAccountLink get() = if (isEn) "Create account" else "Criar conta"
    val loginPasswordResetSuccess get() = if (isEn) "Password reset successfully! Sign in with your new password." else "Senha redefinida com sucesso! Faça login com a nova senha."

    val forgotPasswordTitleStep1 get() = if (isEn) "Recover Password" else "Recuperar Senha"
    val forgotPasswordTitleStep2 get() = if (isEn) "Create New Password" else "Criar Nova Senha"
    val forgotPasswordDescStep1 get() = if (isEn) "Enter your account email to receive a 6-digit verification code." else "Digite o e-mail da sua conta para receber um código de verificação de 6 dígitos."
    fun forgotPasswordDescStep2(email: String) = if (isEn) "We sent a code to $email. Enter it below along with your new password." else "Enviamos um código para $email. Insira-o abaixo junto com sua nova senha."
    val forgotPasswordCodeLabel get() = if (isEn) "6-digit code" else "Código de 6 dígitos"
    val forgotPasswordSendBtn get() = if (isEn) "Send Code" else "Enviar Código"
    val forgotPasswordResetBtn get() = if (isEn) "Reset Password" else "Redefinir Senha"
    val forgotPasswordResendLink get() = if (isEn) "Change email or resend code" else "Alterar e-mail ou reenviar código"
    val forgotPasswordErrCodeLength get() = if (isEn) "Enter the 6-digit code" else "Informe o código de 6 dígitos"
    val forgotPasswordErrInvalidCode get() = if (isEn) "Invalid or expired code" else "Código inválido ou expirado"
    fun forgotPasswordErrSendFailed(code: Int) = if (isEn) "Could not send code ($code)" else "Não foi possível enviar o código ($code)"

    val registerTitle get() = if (isEn) "Create Account" else "Criar Conta"
    val registerSubtitle get() = if (isEn) "Join Nexo Academy and start your learning journey!" else "Junte-se ao Nexo Academy e comece sua jornada de aprendizado!"
    val registerConfirmPasswordLabel get() = if (isEn) "Confirm Password" else "Confirmar Senha"
    val registerBtnSubmit get() = if (isEn) "Register" else "Cadastrar"
    val registerAlreadyHaveAccountLink get() = if (isEn) "Already have an account? Log in" else "Já tem uma conta? Faça login"

    // 2.5. ONBOARDING & QUIZ DE PERFIL DE APRENDIZAGEM
    val quizWelcomeTitle get() = if (isEn) "Personalize how you learn" else "Personalize o seu jeito de aprender"
    val quizWelcomeSubtitle get() = if (isEn) "Answer a quick 4-question quiz so Nexo can adapt the structure, pace, and topic depth of all your future study trails." else "Responda a um quiz rápido de 4 perguntas para que o Nexo adapte a estrutura, o ritmo e o tamanho dos tópicos de todas as suas futuras trilhas de estudo."
    val quizWelcomeOptionalNote get() = if (isEn) "This quiz is completely optional. If you prefer to start right away, you can take or update it anytime in Settings." else "Este quiz é totalmente opcional. Se preferir começar agora, você poderá realizá-lo ou alterá-lo a qualquer momento nas Configurações."
    val quizWelcomeStartBtn get() = if (isEn) "Take quiz now" else "Fazer quiz agora"
    val quizWelcomeLaterBtn get() = if (isEn) "Do it later" else "Fazer mais tarde"

    fun quizStepCounter(current: Int, total: Int) = if (isEn) "Step $current of $total" else "Etapa $current de $total"
    val quizAiProfileBadge get() = if (isEn) "AI Profile" else "Perfil IA"
    val quizSavingBtn get() = if (isEn) "Saving profile..." else "Salvando perfil..."
    val quizSaveProfileBtn get() = if (isEn) "Save Learning Profile" else "Salvar Perfil de Aprendizagem"
    val quizContinueBtn get() = if (isEn) "Continue" else "Continuar"
    val quizFinalStepHint get() = if (isEn) "These preferences will be saved to your profile and automatically applied to every trail you create. You can change them anytime in Settings." else "Essas preferências ficarão salvas no seu perfil e serão aplicadas automaticamente em todas as trilhas que você criar. Você pode alterá-las a qualquer momento nas Configurações."

    // 3. HOME SCREEN
    val homeAppTitle get() = "Nexo Academy"
    val homeActionProfile get() = if (isEn) "Profile" else "Perfil"
    val homeActionSettings get() = if (isEn) "Settings" else "Ajustes"
    val homeActionLogout get() = if (isEn) "Log out" else "Sair"
    val homeNewTrailFab get() = if (isEn) "New Trail" else "Nova Trilha"

    fun homeWelcomeTitle(name: String) = if (isEn) "Hello, $name!" else "Olá, $name!"
    val homeWelcomeSubtitle get() = if (isEn) "Continue your learning\njourney." else "Continue sua jornada\nde aprendizado."
    fun homeLevelLabel(level: Int) = if (isEn) "Level $level" else "Nível $level"
    val homeActiveTrailsHeader get() = if (isEn) "My Active Trails" else "Minhas Trilhas Ativas"
    val homeEmptyStateTitle get() = if (isEn) "No activities here yet." else "Ainda não há atividades por aqui."
    val homeEmptyStateSubtitle get() = if (isEn) "How about starting a new mission?" else "Que tal começar uma nova missão?"
    val homeEmptyStateButton get() = if (isEn) "Explore activities" else "Explorar atividades"

    // 4. CRIAÇÃO DE TRILHA (IA)
    val createTrailTitle get() = if (isEn) "New AI Trail" else "Nova Trilha com IA"
    val createTrailHeader get() = if (isEn) "What would you like to learn?" else "O que você gostaria de aprender?"
    val createTrailDescription get() = if (isEn) "Describe the topic and your goal. Gemini will generate a complete study plan with steps and deadlines." else "Descreva o assunto e o objetivo. O Gemini irá gerar um plano de estudos completo com etapas e prazos."
    val createTrailInputPlaceholder get() = if (isEn) "Ex: I want to learn Spring Boot with Kotlin..." else "Ex: Quero aprender Spring Boot com Kotlin..."
    val createTrailDateLabel get() = if (isEn) "Completion deadline (YYYY-MM-DD)" else "Data limite de conclusão (YYYY-MM-DD)"
    val createTrailSelectDate get() = if (isEn) "Select Date" else "Selecionar Data"
    val createTrailBtnSubmit get() = if (isEn) "Generate Trail with AI" else "Gerar Trilha com IA"
    val createTrailLoadingText get() = if (isEn) "Gemini is creating your trail..." else "O Gemini está criando sua trilha..."

    // 5. DETALHES, EDIÇÃO E EXCLUSÃO DA TRILHA
    val trailDetailsTitle get() = if (isEn) "Trail Details" else "Detalhes da Trilha"
    val trailDetailsDeadlineLabel get() = if (isEn) "Deadline: " else "Data Limite: "
    val trailDetailsOverallProgress get() = if (isEn) "Overall Progress" else "Progresso Geral"
    val trailDetailsTopicsHeader get() = if (isEn) "Study Topics" else "Tópicos de Estudo"
    val trailDetailsExpandSubtopics get() = if (isEn) "Expand Subtopics" else "Expandir Subtópicos"
    val trailDetailsViewDetails get() = if (isEn) "View Details" else "Ver Detalhes"

    val editTrailTitle get() = if (isEn) "Edit Study Trail" else "Editar Trilha"
    val editTrailTitleLabel get() = if (isEn) "Trail Title" else "Título da Trilha"
    val editTrailDescLabel get() = if (isEn) "Description" else "Descrição"
    val editTrailSaveBtn get() = if (isEn) "Save Changes" else "Salvar Alterações"
    val editTrailSuccessMsg get() = if (isEn) "Trail updated successfully!" else "Trilha atualizada com sucesso!"
    val editTrailEmptyError get() = if (isEn) "Title and description cannot be empty." else "O título e a descrição não podem ficar em branco."

    val deleteTrailTitle get() = if (isEn) "Delete Trail?" else "Excluir Trilha?"
    val deleteTrailMessage get() = if (isEn) "This action cannot be undone. All topics, progress, and assessments for this trail will be permanently removed." else "Esta ação não pode ser desfeita. Todos os tópicos, progresso e avaliações desta trilha serão removidos permanentemente."
    val deleteTrailConfirmBtn get() = if (isEn) "Delete Trail" else "Excluir Trilha"

    // 6. MODAL DE TÓPICO
    val modalClose get() = if (isEn) "Close Modal" else "Fechar Modal"
    val modalWhatYouWillLearn get() = if (isEn) "What you will learn:" else "O que você vai aprender:"
    val modalDefaultDescription get() = if (isEn) "Explore the core concepts of this topic to advance in your study trail." else "Explore os conceitos fundamentais deste tópico para avançar na sua trilha de estudos."
    val modalTargetDateLabel get() = if (isEn) "Completion goal: " else "Meta de conclusão: "
    val modalTopicCompletedBtn get() = if (isEn) "Topic Completed!" else "Tópico Concluído!"
    val modalCompleteTopicBtn get() = if (isEn) "Complete Topic (+50 XP)" else "Concluir Tópico (+50 XP)"

    val topicCall get() = modalWhatYouWillLearn
    val topicErr get() = modalDefaultDescription
    val topicDate get() = modalTargetDateLabel
    val topicComplete get() = modalCompleteTopicBtn
    val topicCompleted get() = modalTopicCompletedBtn

    val topicUncomplete get() = if (isEn) "Unmark as Completed" else "Desmarcar como Concluído"
    val statusOverdue get() = if (isEn) "Pending (Overdue)" else "Pendente (Prazo Vencido)"
    val topicDeadlineOverdueLabel get() = if (isEn) "Overdue deadline:" else "Prazo vencido:"

    // 7. AVALIAÇÃO FINAL (PROVA) & COMEMORAÇÃO
    val testSectionTitle get() = if (isEn) "Final Assessment" else "Avaliação Final"
    val testSectionBlockedDesc get() = if (isEn) "Complete all topics above to unlock the test." else "Conclua todos os tópicos acima para liberar a prova."
    fun testSectionCompletedDesc(score: Int, passingScore: Int) =
        if (isEn) "Assessment completed! Final score: $score (minimum: $passingScore)."
        else "Avaliação concluída! Nota final: $score (mínimo: $passingScore)."
    fun testSectionOpenDesc(passingScore: Int) =
        if (isEn) "All topics completed! Get at least $passingScore right to finish the trail."
        else "Todos os tópicos concluídos! Acerte pelo menos $passingScore questões para finalizar a trilha."
    val testBtnLocked get() = if (isEn) "Test Locked" else "Prova Bloqueada"
    val testBtnStart get() = if (isEn) "Start Assessment" else "Iniciar Avaliação"
    fun testApprovedChip(score: Int) =
        if (isEn) "Trail Approved • $score correct" else "Trilha Aprovada • $score acertos"

    val testScreenTitle get() = if (isEn) "Trail Assessment" else "Avaliação da Trilha"
    val testHeaderTitle get() = if (isEn) "Challenge Time!" else "Hora do Desafio!"
    val testHeaderSubtitle get() = if (isEn) "Answer all questions to complete your trail." else "Responda todas as questões para concluir sua trilha."
    fun testMinScoreLabel(passingScore: Int) =
        if (isEn) "Minimum: $passingScore correct" else "Mínimo: $passingScore acertos"
    val testSubmitBtn get() = if (isEn) "Submit Test" else "Finalizar Prova"
    val testErrUnanswered get() = if (isEn) "Please answer all questions before submitting." else "Responda todas as questões antes de enviar."
    val testResultApprovedTitle get() = if (isEn) "Trail Completed!" else "Trilha Concluída!"
    val testResultFailedTitle get() = if (isEn) "Almost there!" else "Quase lá!"
    fun testResultSummary(score: Int, total: Int, passingScore: Int) =
        if (isEn) "You got $score out of $total questions right (minimum: $passingScore)."
        else "Você acertou $score de $total questões (mínimo: $passingScore)."
    val testBackToTrailBtn get() = if (isEn) "Back to Trail" else "Voltar para a Trilha"
    val testReviewTrailBtn get() = if (isEn) "Review Trail" else "Revisar Trilha"
    fun testQuestionBadge(index: Int) = if (isEn) "Question $index" else "Questão $index"
    val testCorrectBadge get() = if (isEn) "Correct" else "Correta"
    val testIncorrectBadge get() = if (isEn) "Incorrect" else "Incorreta"

    val rewardTrailCompletedTitle get() = if (isEn) "Trail Completed! 🎉" else "Trilha Concluída! 🎉"
    val rewardTrailCompletedSubtitle get() = if (isEn) "Congratulations! You passed the final assessment and finished this trail." else "Parabéns! Você foi aprovado na avaliação final e finalizou esta trilha com sucesso."
    val rewardTestUnlockedTitle get() = if (isEn) "Topics Completed! Test Unlocked! 📝" else "Tópicos Concluídos! Prova Liberada! 📝"
    val rewardTestUnlockedSubtitle get() = if (isEn) "You completed all topics! Now take the final assessment to earn your approval." else "Você concluiu todos os tópicos da trilha! Agora faça a avaliação final para conquistar sua aprovação."
    fun rewardLevelUpTitle(level: Int) = if (isEn) "You reached Level $level! 🚀" else "Você subiu para o Nível $level! 🚀"
    val rewardAchievementTitle get() = if (isEn) "New Achievement Unlocked! 🏆" else "Nova Conquista Desbloqueada! 🏆"
    val rewardDefaultTitle get() = if (isEn) "Great job! ✨" else "Mandou muito bem! ✨"
    val rewardTakeTestNowBtn get() = if (isEn) "Take Test Now" else "Fazer Prova Agora"
    val rewardDoLaterBtn get() = if (isEn) "Do it later" else "Fazer depois"
    val rewardContinueBtn get() = if (isEn) "Keep Learning" else "Continuar Aprendendo"

    // 8. PERFIL & TELA DE CONQUISTAS
    val profileTitle get() = if (isEn) "My Profile" else "Meu Perfil"
    val profileLevelLabel get() = if (isEn) "Level " else "Nível "
    val profileAchievementsHeader get() = if (isEn) "Achievements" else "Conquistas"
    val profileOverviewHeader get() = if (isEn) "Overview" else "Visão Geral"
    val profileStatStreakTitle get() = if (isEn) "Day Streak" else "Ofensiva Atual"
    val profileStatTotalXpTitle get() = if (isEn) "Total XP" else "XP Total"
    val profileStatBestStreakTitle get() = if (isEn) "Best Streak" else "Recorde de Dias"
    val profileStatTrailsTitle get() = if (isEn) "Completed Trails" else "Trilhas Concluídas"
    fun profileDaysUnit(days: Int) = if (isEn) "$days days" else if (days == 1) "1 dia" else "$days dias"

    val profileAchievementsCardTitle get() = if (isEn) "Achievements & Records" else "Conquistas e Recordes"
    fun profileAchievementsProgressSummary(unlocked: Int, total: Int) =
        if (isEn) "$unlocked of $total unlocked" else "$unlocked de $total desbloqueadas"
    val profileViewAllAchievementsBtn get() = if (isEn) "View All Achievements" else "Ver Todas as Conquistas"

    val achievementsScreenTitle get() = if (isEn) "Achievements" else "Conquistas"
    val achievementsPersonalRecordsHeader get() = if (isEn) "Personal Records" else "Recordes Pessoais"
    val achievementsTriumphsHeader get() = if (isEn) "Triumphs" else "Triunfos"

    val recordLongestStreak get() = if (isEn) "Longest Streak" else "Maior Ofensiva"
    val recordTotalXp get() = if (isEn) "Accumulated XP" else "XP Acumulado"
    val recordTopicsCompleted get() = if (isEn) "Topics Completed" else "Tópicos Concluídos"
    val recordTrailsFinished get() = if (isEn) "Trails Finished" else "Trilhas Finalizadas"

    val filterAll get() = if (isEn) "All" else "Todas"
    val filterUnlocked get() = if (isEn) "Unlocked" else "Desbloqueadas"
    val filterInProgress get() = if (isEn) "In Progress" else "Em Progresso"

    val badgeUnlocked get() = if (isEn) "UNLOCKED" else "DESBLOQUEADA"
    val badgeLocked get() = if (isEn) "LOCKED" else "BLOQUEADA"
    fun achievementProgressRatio(current: Int, target: Int) =
        if (isEn) "$current of $target" else "$current de $target"

    // 8.5. EDIÇÃO DE CONTA & ZONA DE PERIGO
    val editAvatarTitle get() = if (isEn) "Choose your Avatar" else "Escolha o seu Avatar"
    val editProfileTitle get() = if (isEn) "Edit Profile" else "Editar Perfil"
    val editProfileHeader get() = if (isEn) "Update your account details" else "Atualize os dados da sua conta"
    val editProfileSuccessMsg get() = if (isEn) "Profile updated successfully!" else "Dados atualizados com sucesso!"
    val editProfilePartialSavePrefix get() = if (isEn) "Username updated, but: " else "Nome atualizado, porém: "
    val editProfileBtnSubmit get() = if (isEn) "Save Changes" else "Salvar Alterações"

    val alertDangerZoneTitle get() = if (isEn) "Danger Zone" else "Zona de Perigo"
    val alertDangerZone get() = if (isEn) "Sensitive actions related to your account security." else "Ações sensíveis relacionadas à segurança da sua conta."
    val confirmLogoutTitle get() = if (isEn) "Log Out Everywhere" else "Sair de Todas as Contas"
    val confirmLogoutMessage get() = if (isEn) "You will be signed out from all your devices and will need to log in again." else "Você será desconectado de todos os seus dispositivos e precisará fazer login novamente."
    val confirmLogout get() = if (isEn) "Confirm Logout" else "Saída de Todas as Contas"
    val confirmDeleteAccountTitle get() = if (isEn) "Delete Account Permanently" else "Excluir Conta Permanentemente"
    val confirmDeleteAccountMessage get() = if (isEn) "Warning: this action is irreversible! All your trails, achievements, XP, and study progress will be permanently deleted." else "Atenção: esta ação é irreversível! Todas as suas trilhas, conquistas, pontuação de XP e progresso de estudos serão apagados para sempre."
    val confirmDeleteAccount get() = if (isEn) "Confirm Delete Account" else "Confirmar Exclusão da Conta"

    // 9. CONFIGURAÇÕES & NOTIFICAÇÕES
    val settingsTitle get() = if (isEn) "Settings" else "Configurações"
    val settingsHeaderTitle get() = if (isEn) "Learning Settings" else "Ajustes de Aprendizado"
    val settingsHeaderSubtitle get() = if (isEn) "Customize the pace and reminders of your routine." else "Personalize o ritmo e lembretes da sua rotina."
    val settingsLearningQuizTitle get() = if (isEn) "Learning Profile (Quiz)" else "Perfil de Aprendizagem (Quiz)"
    val settingsLearningQuizConfigured get() = if (isEn) "Configured • Tap to retake or adjust your preferences" else "Configurado • Toque para refazer ou ajustar suas preferências"
    val settingsLearningQuizPending get() = if (isEn) "Pending • Personalize how AI structures your trails" else "Pendente • Personalize como a IA estrutura suas trilhas"
    val settingsThemeSubtitle get() = if (isEn) "Use dark theme for the interface" else "Usar tema escuro na interface"
    val settingsThemeSectionTitle get() = if (isEn) "App Theme" else "Tema do Aplicativo"
    val settingsThemeSystem get() = if (isEn) "System Default" else "Padrão"
    val settingsThemeLight get() = if (isEn) "Light" else "Claro"
    val settingsThemeDark get() = if (isEn) "Dark" else "Escuro"

    val settingsLanguageSectionTitle get() = if (isEn) "App Language" else "Idioma do Aplicativo"
    val settingsLanguageSystem get() = if (isEn) "System Default" else "Padrão"
    val settingsLanguagePt get() = "Português"
    val settingsLanguageEn get() = "English"
    val settingsLanguageSubtitle get() = if (isEn) "Use English as the app language" else "Usar inglês como idioma do aplicativo"
    val settingsNotificationsTitle get() = if (isEn) "Daily Notifications" else "Notificações Diárias"
    val settingsNotificationsSubtitle get() = if (isEn) "Receive friendly study reminders" else "Receber lembretes amigáveis para estudar"
    val settingsWeekendsTitle get() = if (isEn) "Study on Weekends" else "Estudar nos Finais de Semana"
    val settingsWeekendsSubtitle get() = if (isEn) "Include Saturdays and Sundays in your goals" else "Incluir sábados e domingos nas suas metas"

    val notificationChannelName get() = if (isEn) "Streak Reminders" else "Lembretes de Ofensiva"
    val notificationStreakTitle get() = if (isEn) "Don't let your streak flame go out! 🔥" else "Não deixe seu foguinho apagar! 🔥"
    val notificationStreakBody get() = if (isEn) "Complete a quick topic today on Nexo Academy to keep your streak alive." else "Conclua um tópico rápido hoje no Nexo Academy para manter sua ofensiva."
    val notificationPermissionDialogTitle get() = if (isEn) "Notifications Disabled" else "Notificações Desativadas"
    val notificationPermissionDialogMessage get() = if (isEn) "To receive study reminders, enable notification permission in your device settings." else "Para receber lembretes de estudo, ative a permissão de notificações nas configurações do aparelho."
    val notificationOpenSettingsBtn get() = if (isEn) "Open Settings" else "Abrir Configurações"

    fun notificationStreakTitle(urgencyLevel: Int): String = when (urgencyLevel) {
        1 -> if (isEn) "Time to study! 📚" else "Hora de estudar! 📚"
        2 -> if (isEn) "Don't forget your streak! 🔥" else "Não esqueça sua ofensiva! 🔥"
        3 -> if (isEn) "Your streak is in danger! ⚠️" else "Sua ofensiva está em perigo! ⚠️"
        else -> if (isEn) "LAST CHANCE BEFORE MIDNIGHT! 🚨🔥" else "ÚLTIMA CHANCE ANTES DA MEIA-NOITE! 🚨🔥"
    }

    fun notificationStreakBody(urgencyLevel: Int): String = when (urgencyLevel) {
        1 -> if (isEn) "A quick topic keeps your Nexo Academy progress moving." else "Um tópico rápido já garante seu progresso hoje no Nexo Academy."
        2 -> if (isEn) "Your flame is still grey today. Complete one topic to light it up!" else "Seu foguinho ainda está cinza hoje. Conclua um tópico para acendê-lo!"
        3 -> if (isEn) "Only 2 hours left! Don't throw away your hard-earned streak." else "Faltam só 2 horas! Não jogue fora os dias de ofensiva que você conquistou."
        else -> if (isEn) "Less than 30 minutes left! The Nexo is begging you to do 1 topic NOW!" else "Faltam menos de 30 minutos! O Nexo está implorando: faça 1 tópico AGORA!"
    }

    // 10. VALIDAÇÕES DE FORMULÁRIO & ERROS DE API
    val errEmailBlankLogin get() = if (isEn) "Please enter your email." else "Por favor, informe seu e-mail."
    val errPasswordBlankLogin get() = if (isEn) "Please enter your password." else "Por favor, informe sua senha."
    val errUsernameBlank get() = if (isEn) "Please enter a username." else "Por favor, informe um nome de usuário."
    val errUsernameLength get() = if (isEn) "Username must be at least 3 characters long." else "O nome de usuário deve ter pelo menos 3 caracteres."
    val errEmailBlank get() = if (isEn) "Email field cannot be empty." else "O campo de e-mail não pode estar vazio."
    val errEmailInvalid get() = if (isEn) "The email address appears to be invalid." else "O e-mail informado parece inválido."
    val errPasswordBlank get() = if (isEn) "Please create a password." else "Por favor, crie uma senha de acesso."
    val errPasswordLength get() = if (isEn) "Password is too short. It must be at least 6 characters." else "A senha é muito curta. Ela precisa ter pelo menos 6 caracteres."
    val errConfirmPasswordBlank get() = if (isEn) "Please confirm your password." else "Por favor, confirme a sua senha digitando-a novamente."
    val errPasswordMismatchSpecific get() = if (isEn) "Passwords do not match. Please check." else "As senhas não coincidem. Confira os campos de senha."
    val errSubjectLength get() = if (isEn) "Subject must be at least 3 characters long." else "O assunto deve ter pelo menos 3 caracteres."

    val errFillFields get() = if (isEn) "Please fill in all fields" else "Preencha todos os campos"
    val errInvalidCredentials get() = if (isEn) "Invalid username or password" else "Usuário ou senha inválidos"
    val errServerConn get() = if (isEn) "Error connecting to the server" else "Erro ao conectar com o servidor"
    val errSessionExpired get() = if (isEn) "Session expired. Please log in again." else "Sessão expirada. Por favor, faça login novamente."
    val errNetwork get() = if (isEn) "Network failure. Check your connection." else "Falha de rede. Verifique sua conexão com a internet."
    val errServer get() = if (isEn) "Server error. Please try again later." else "Erro no servidor. Tente novamente mais tarde."
    val errUnexpected get() = if (isEn) "An unexpected error occurred." else "Ocorreu um erro inesperado."
    val errMissingSubject get() = if (isEn) "Describe the subject you want to study." else "Descreva o assunto que você deseja estudar."
    val errMissingDate get() = if (isEn) "Provide the desired completion date." else "Informe a data de término desejada."
    val errLoadTrails get() = if (isEn) "Error loading trails" else "Erro ao carregar as trilhas"
    val errLoadTrailDetails get() = if (isEn) "Error loading trail details" else "Erro ao carregar os detalhes da trilha"
    val errLoadProfile get() = if (isEn) "Error loading profile" else "Erro ao carregar o perfil"
    val errLoadTopics get() = if (isEn) "Error loading topics" else "Erro ao carregar os tópicos"
    val errConcludeTopic get() = if (isEn) "Error completing topic" else "Erro ao concluir o tópico"
    val errLoadXp get() = if (isEn) "Error loading XP" else "Erro ao carregar XP"
    val errUpdateXp get() = if (isEn) "Error updating XP" else "Erro ao atualizar XP"
    val errDeleteAccount get() = if (isEn) "Error deleting account" else "Erro ao excluir a conta"
    val errUsernameChange get() = if (isEn) "Error changing username" else "Erro ao alterar o nome de usuário"
    val errEmailChange get() = if (isEn) "Error changing email" else "Erro ao alterar o e-mail"
    val errPasswordChange get() = if (isEn) "Error changing password" else "Erro ao alterar a senha"

    // 11. ACESSIBILIDADE & AVATARES (CONTENT DESCRIPTIONS)
    val cdMascotDefault get() = if (isEn) "Nexo Mascot" else "Mascote Nexo"
    val cdMascotStudying get() = if (isEn) "Studying" else "Focado"
    val cdMascotSupport get() = if (isEn) "Support" else "Apoio"
    val cdMascotCalm get() = if (isEn) "Calm" else "Calma"
    val cdMascotChampion get() = if (isEn) "Champion" else "Campeão"
    val cdMascotWelcoming get() = if (isEn) "Welcoming" else "Acolhedor"
    val cdMascotFriendship get() = if (isEn) "Friendship" else "Amizade"
    val cdMascotAdventurer get() = if (isEn) "Adventurer" else "Aventureiro"
    val cdMascotTrailblazer get() = if (isEn) "Trailblazer" else "Desbravador"
    val cdMascotExplorer get() = if (isEn) "Explorer" else "Explorador"
    val cdMascotThoughtful get() = if (isEn) "Thoughtful" else "Pensativo"
    val cdMascotStar get() = if (isEn) "Star" else "Estrela"
    val cdLevel get() = if (isEn) "Level" else "Nível"
    val cdTarget get() = if (isEn) "Focus Target" else "Alvo de Foco"
    val cdMission get() = if (isEn) "Mission" else "Missão"
    val cdXP get() = if (isEn) "Experience Points" else "Pontos de Experiência"
    val cdTopicStatus get() = if (isEn) "Topic Status" else "Status do Tópico"
    val cdSelected get() = if (isEn) "Selected" else "Selecionado"
    val cdDangerZone get() = if (isEn) "Danger Zone" else "Zona de Perigo"
    val cdEditTrail get() = if (isEn) "Edit Trail" else "Editar Trilha"
    val cdDeleteTrail get() = if (isEn) "Delete Trail" else "Excluir Trilha"
}