package com.nexo.ui.theme
import com.nexo.dto.UserProfileResponse
object AppStrings {
    private val isEn: Boolean
        get() = AppPreferencesState.language == "en-US"

    // Common / Navigation
    val navHome get() = if (isEn) "Home" else "Início"
    val navProfile get() = if (isEn) "Profile" else "Perfil"
    val navSettings get() = if (isEn) "Settings" else "Ajustes"
    val btnBack get() = if (isEn) "Back" else "Voltar"
    val btnHelp get() = if (isEn) "Help" else "Ajuda"
    val btnTryAgain get() = if (isEn) "Try Again" else "Tentar Novamente"
    val labelEmail get() = if (isEn) "E-mail" else "E-mail"
    val labelPassword get() = if (isEn) "Password" else "Senha"
    val labelUsername get() = if (isEn) "Username" else "Nome de usuário"
    val statusCompleted get() = if (isEn) "Completed" else "Concluído"
    val statusInProgress get() = if (isEn) "In Progress" else "Em Andamento"
    val statusPending get() = if (isEn) "Pending" else "Pendente"

    // Splash Screen
    val splashProjectAcademy get() = if (isEn) "PROJECT ACADEMY" else "PROJECT ACADEMY"
    val splashSlogan get() = if (isEn) "Learning today,\ntransforming tomorrow." else "Aprender hoje,\ntransformar o amanhã."

    // Login & Register Screens
    val loginGreetingTitle get() = if (isEn) "Hello, welcome back!" else "Olá, bem-vindo de volta!"
    val loginGreetingSubtitle get() = if (isEn) "Great to see you here." else "Que bom te ver por aqui."
    val loginEmailLabel get() = if (isEn) "Email" else "E-mail"
    val loginEmailPlaceholder get() = if (isEn) "your@email.com" else "seu@email.com"
    val loginPasswordPlaceholder get() = if (isEn) "••••••••" else "••••••••"
    val loginForgotPassword get() = if (isEn) "Forgot your password?" else "Esqueceu sua senha?"
    val errEmailBlankLogin get() = if (isEn) "Please enter your email." else "Por favor, informe seu e-mail."
    val errPasswordBlankLogin get() = if (isEn) "Please enter your password." else "Por favor, informe sua senha."
    val loginBtnSubmit get() = if (isEn) "Sign In" else "Entrar"
    val loginBtnGoogle get() = if (isEn) "Sign in with Google" else "Entrar com Google"
    val loginNoAccountPrompt get() = if (isEn) "Don't have an account? " else "Não tem uma conta? "
    val loginCreateAccountLink get() = if (isEn) "Create account" else "Criar conta"
    val registerTitle get() = if (isEn) "Create Account" else "Criar Conta"
    val registerSubtitle get() = if (isEn) "Join Nexo Academy and start your learning journey!" else "Junte-se ao Nexo Academy e comece sua jornada de aprendizado!"
    val registerConfirmPasswordLabel get() = if (isEn) "Confirm Password" else "Confirmar Senha"
    val registerBtnSubmit get() = if (isEn) "Register" else "Cadastrar"
    val registerAlreadyHaveAccountLink get() = if (isEn) "Already have an account? Log in" else "Já tem uma conta? Faça login"

    // Home Screen
    val homeAppTitle get() = if (isEn) "Nexo Academy" else "Nexo Academy"
    val homeActionProfile get() = if (isEn) "Profile" else "Perfil"
    val homeActionSettings get() = if (isEn) "Settings" else "Ajustes"
    val homeActionLogout get() = if (isEn) "Log out" else "Sair"
    val homeNewTrailFab get() = if (isEn) "New Trail" else "Nova Trilha"

    fun homeWelcomeTitle(name: String) = if (isEn) "Hello, $name!" else "Olá, $name!"
    val homeWelcomeSubtitle get() = if (isEn) "Continue your learning\njourney." else "Continue sua jornada\nde aprendizado."
    val homeLevelLabel get() = if (isEn) "Level 3" else "Nível 3"
    val homeActiveTrailsHeader get() = if (isEn) "My Active Trails" else "Minhas Trilhas Ativas"
    val homeEmptyStateTitle get() = if (isEn) "No activities here yet." else "Ainda não há atividades por aqui."
    val homeEmptyStateSubtitle get() = if (isEn) "How about starting a new mission?" else "Que tal começar uma nova missão?"
    val homeEmptyStateButton get() = if (isEn) "Explore activities" else "Explorar atividades"

    // Create Trail Screen
    val createTrailTitle get() = if (isEn) "New AI Trail" else "Nova Trilha com IA"
    val createTrailHeader get() = if (isEn) "What would you like to learn?" else "O que você gostaria de aprender?"
    val createTrailDescription get() = if (isEn) "Describe the topic and your goal. Gemini will generate a complete study plan with steps and deadlines." else "Descreva o assunto e o objetivo. O Gemini irá gerar um plano de estudos completo com etapas e prazos."
    val createTrailInputPlaceholder get() = if (isEn) "Ex: I want to learn Spring Boot with Kotlin..." else "Ex: Quero aprender Spring Boot com Kotlin..."
    val createTrailDateLabel get() = if (isEn) "Completion deadline (YYYY-MM-DD)" else "Data limite de conclusão (YYYY-MM-DD)"
    val createTrailSelectDate get() = if (isEn) "Select Date" else "Selecionar Data"
    val errSubjectLength get() = if (isEn) "Subject must be at least 3 characters long." else "O assunto deve ter pelo menos 3 caracteres."
    val createTrailBtnSubmit get() = if (isEn) "Generate Trail with AI" else "Gerar Trilha com IA"
    val createTrailLoadingText get() = if (isEn) "Gemini is creating your trail..." else "O Gemini está criando sua trilha..."

    // Trail Details Screen & Modal
    val trailDetailsTitle get() = if (isEn) "Trail Details" else "Detalhes da Trilha"
    val trailDetailsDeadlineLabel get() = if (isEn) "Deadline: " else "Data Limite: "
    val trailDetailsOverallProgress get() = if (isEn) "Overall Progress" else "Progresso Geral"
    val trailDetailsTopicsHeader get() = if (isEn) "Study Topics" else "Tópicos de Estudo"
    val trailDetailsExpandSubtopics get() = if (isEn) "Expand Subtopics" else "Expandir Subtópicos"
    val trailDetailsViewDetails get() = if (isEn) "View Details" else "Ver Detalhes"
    val modalClose get() = if (isEn) "Close Modal" else "Fechar Modal"
    val modalWhatYouWillLearn get() = if (isEn) "What you will learn:" else "O que você vai aprender:"
    val modalDefaultDescription get() = if (isEn) "Explore the core concepts of this topic to advance in your study trail." else "Explore os conceitos fundamentais deste tópico para avançar na sua trilha de estudos."
    val modalTargetDateLabel get() = if (isEn) "Completion goal: " else "Meta de conclusão: "
    val modalTopicCompletedBtn get() = if (isEn) "Topic Completed!" else "Tópico Concluído!"
    val modalCompleteTopicBtn get() = if (isEn) "Complete Topic (+50 XP)" else "Concluir Tópico (+50 XP)"

    // Topic Detail Modal Specific Properties
    val topicCall get() = modalWhatYouWillLearn
    val topicErr get() = modalDefaultDescription
    val topicDate get() = modalTargetDateLabel
    val topicComplete get() = modalCompleteTopicBtn
    val topicCompleted get() = modalTopicCompletedBtn

    // Profile & Settings Screens
    val profileTitle get() = if (isEn) "My Profile" else "Meu Perfil"
    val profileLevelLabel get() = if (isEn) "Level " else "Nível "
    val profileAchievementsHeader get() = if (isEn) "Achievements" else "Conquistas"
    val settingsTitle get() = if (isEn) "Settings" else "Configurações"
    val settingsHeaderTitle get() = if (isEn) "Learning Settings" else "Ajustes de Aprendizado"
    val settingsHeaderSubtitle get() = if (isEn) "Customize the pace and reminders of your routine." else "Personalize o ritmo e lembretes da sua rotina."
    val settingsThemeTitle get() = if (isEn) "Dark Mode" else "Modo Escuro"
    val settingsThemeSubtitle get() = if (isEn) "Use dark theme for the interface" else "Usar tema escuro na interface"
    val settingsLanguageTitle get() = if (isEn) "English Language" else "Idioma em Inglês"
    val settingsLanguageSubtitle get() = if (isEn) "Use English as the app language" else "Usar inglês como idioma do aplicativo"
    val settingsNotificationsTitle get() = if (isEn) "Daily Notifications" else "Notificações Diárias"
    val settingsNotificationsSubtitle get() = if (isEn) "Receive friendly study reminders" else "Receber lembretes amigáveis para estudar"
    val settingsWeekendsTitle get() = if (isEn) "Study on Weekends" else "Estudar nos Finais de Semana"
    val settingsWeekendsSubtitle get() = if (isEn) "Include Saturdays and Sundays in your goals" else "Incluir sábados e domingos nas suas metas"
    val editProfileTitle get() = if (isEn) "Edit Profile" else "Editar Perfil"
    val editProfileHeader get() = if (isEn) "Update your account details" else "Atualize os dados da sua conta"
    val editProfileSuccessMsg get() = if (isEn) "Profile updated successfully!" else "Perfil atualizado com sucesso!"
    val editProfileBtnSubmit get() = if (isEn) "Save Changes" else "Salvar Alterações"

    // Error Messages & Alerts
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

    // Specific register validations
    val errUsernameBlank get() = if (isEn) "Please enter a username." else "Por favor, informe um nome de usuário."
    val errUsernameLength get() = if (isEn) "Username must be at least 3 characters long." else "O nome de usuário deve ter pelo menos 3 caracteres."
    val errEmailBlank get() = if (isEn) "Email field cannot be empty." else "O campo de e-mail não pode estar vazio."
    val errEmailInvalid get() = if (isEn) "The email address appears to be invalid." else "O e-mail informado parece inválido."
    val errPasswordBlank get() = if (isEn) "Please create a password." else "Por favor, crie uma senha de acesso."
    val errPasswordLength get() = if (isEn) "Password is too short. It must be at least 6 characters." else "A senha é muito curta. Ela precisa ter pelo menos 6 caracteres."
    val errConfirmPasswordBlank get() = if (isEn) "Please confirm your password." else "Por favor, confirme a sua senha digitando-a novamente."
    val labelConfirmPassword get() = if (isEn) "Confirm Password" else "Confirmar Senha"
    val errPasswordMismatchSpecific get() = if (isEn) "Passwords do not match. Please check." else "As senhas não coincidem. Confira os campos de senha."

    // Content Descriptions (Accessibility)
    val cdMascotDefault get() = if (isEn) "Nexo Mascot" else "Mascote Nexo"
    val cdMascotStudying get() = if (isEn) "Mascot Studying" else "Mascote Estudando"
    val cdMascotSupport get() = if (isEn) "Support Mascot" else "Mascote de Apoio"
    val cdMascotCalm get() = if (isEn) "Calm Mascot" else "Mascote Calma"
    val cdTarget get() = if (isEn) "Focus Target" else "Alvo de Foco"
    val cdMission get() = if (isEn) "Mission" else "Missão"
    val cdXP get() = if (isEn) "Experience Points" else "Pontos de Experiência"
    val cdTopicStatus get() = if (isEn) "Topic Status" else "Status do Tópico"
}