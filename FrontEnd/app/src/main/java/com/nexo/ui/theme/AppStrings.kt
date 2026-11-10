package com.nexo.ui.theme

object AppStrings {
    private val isEn: Boolean
        get() = AppPreferencesState.language == "en-US"

    // Navegação (Bottom Bar)
    val navHome get() = if (isEn) "Home" else "Início"
    val navProfile get() = if (isEn) "Profile" else "Perfil"
    val navSettings get() = if (isEn) "Settings" else "Ajustes"

    // Configurações (SettingsScreen)
    val settingsTitle get() = if (isEn) "Settings" else "Configurações"
    val learningAdjustments get() = if (isEn) "Learning Settings" else "Ajustes de Aprendizado"
    val learningSubtitle get() = if (isEn) "Customize the pace and preferences of your account." else "Personalize o ritmo e preferências da sua conta."
    val sectionRoutine get() = if (isEn) "Routine" else "Rotina"
    val dailyNotifications get() = if (isEn) "Daily Notifications" else "Notificações Diárias"
    val dailyNotificationsSub get() = if (isEn) "Receive friendly study reminders" else "Receber lembretes amigáveis para estudar"
    val studyWeekends get() = if (isEn) "Study on Weekends" else "Estudar nos Finais de Semana"
    val studyWeekendsSub get() = if (isEn) "Include Saturdays and Sundays in your goals" else "Incluir sábados e domingos nas suas metas"
    val sectionPreferences get() = if (isEn) "Preferences" else "Preferências"
    val appTheme get() = if (isEn) "App Theme" else "Tema do Aplicativo"
    val darkMode get() = if (isEn) "Dark Mode" else "Modo Escuro"
    val lightMode get() = if (isEn) "Light Mode" else "Modo Claro"
    val languageLabel get() = if (isEn) "Language" else "Idioma"
    val sectionAccount get() = if (isEn) "Account" else "Conta"
    val registeredEmail get() = if (isEn) "Registered e-mail" else "E-mail cadastrado"
    val notInformed get() = if (isEn) "Not informed" else "Não informado"
    val logout get() = if (isEn) "Log Out" else "Sair da Conta"
    val changeEmailTitle get() = if (isEn) "Change E-mail" else "Alterar E-mail"
    val newEmailLabel get() = if (isEn) "New e-mail" else "Novo e-mail"
    val save get() = if (isEn) "Save" else "Salvar"
    val cancel get() = if (isEn) "Cancel" else "Cancelar"

    // Perfil & Home
    val myProfile get() = if (isEn) "My Profile" else "Meu Perfil"
    val achievements get() = if (isEn) "Achievements" else "Conquistas"
    val level get() = if (isEn) "Level" else "Nível"
    val activeTrails get() = if (isEn) "My Active Trails" else "Minhas Trilhas Ativas"
    val tryAgain get() = if (isEn) "Try Again" else "Tentar Novamente"
    val back get() = if (isEn) "Back" else "Voltar"
}