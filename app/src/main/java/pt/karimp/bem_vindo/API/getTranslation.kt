package pt.karimp.bem_vindo.API


fun getTranslations(language: String): Map<String, String> {
    return when (language) {
        "pt" -> mapOf(
            "progress_title" to "Meu Progresso",
            "tutor_title" to "Meu Tutor",
            "daily_phrase_title" to "Frase do Dia",
            "daily_phrase" to "A união faz a força.",
            "profile_title" to "Meu Perfil",
            "full_name_label" to "Nome Completo",
            "address_label" to "Endereço",
            "postal_code_label" to "Código Postal",
            "city_label" to "Cidade",
            "nif_label" to "Número de Identificação Fiscal",
            "nss_label" to "Número de Segurança Social",
            "phone_number_label" to "Número de Telefone",
            "email_label" to "Email",
            "edit_button" to "Editar",
            "save_button" to "Salvar",
            "cancel_button" to "Cancelar",
            "password_recovery_button" to "Recuperar Senha",
            "delete_account_button" to "Excluir Conta",
            "confirm_label" to "Confirmar a eliminação",
            "confirm_deletion_phrase" to "Tem certeza de que deseja eliminar sua conta?",
            "confirm_button" to "Confirmar",
            "uploading_audio" to "A Fazer o upload do audio...",
            "horario" to "Selecione um horário",
            "horario_titulo" to "Horário Preferido: ",
            "calendario" to "Adicionar ao calendário",
            "hoje" to "Hoje",
            "aula" to "Aula ás",
            "nivel" to "Nível",
            "basico" to "Básico",
            "iniciante" to "Iniciante",
            "avancado" to "Avançado",
            "niv_mundial" to "Nível Mundial",
            "professional" to "Profissional",
            "desconhecido" to "Desconhecido",
            "apagar_user" to "Apagar a minha conta",
            "sem_prof" to "Sem Professor",
            "sem_prof_desc" to "Você não tem um professor. Aguarde que um lhe seja atribuido para poder continuar para esta página.",
            "login_button" to "Entrar",
            "register_button" to "Registrar-se",
            "password" to "Palavra-passe",
            "password_mail" to "Email enviado com sucesso!",
            "jogo_complete" to "Complete a frase: ",
            "nivel_completed" to "Você concluiu o nível. Deseja voltar para os níveis?",
            "parabens" to "Parabéns!!!",
            "ir_niveis" to "Ir para Níveis",
            "nivel" to "Nível",
            "reset_response_button" to "Repor Resposta",
            "regras" to "Regras",
            "regulamento_registro" to "NSS : tem de ter exatamente 11 digitos, NIF: tem de ter exatamente 9 digitos, Telefone tem de ter exatamente 9 digitos, O codigo Postal tem de começar por 4 digitos e ter um - mais 3 digitos, O email deve ser um email válido",
            "professor_label" to "É Professor?"

        )
        "fr" -> mapOf(
            "progress_title" to "Mon Progrès",
            "tutor_title" to "Mon Tuteur",
            "daily_phrase_title" to "Expression du jour",
            "daily_phrase" to "L'union fait la force.",
            "profile_title" to "Profil",
            "full_name_label" to "Nom Complet",
            "address_label" to "Adresse",
            "postal_code_label" to "Code Postal",
            "city_label" to "Ville",
            "nif_label" to "Numéro D'identification Fiscal",
            "nss_label" to "Numéro de Securité Sociale",
            "phone_number_label" to "Numéro de Téléphone",
            "email_label" to "Email",
            "edit_button" to "Éditer",
            "save_button" to "Sauvegarder",
            "cancel_button" to "Annuler",
            "password_recovery_button" to "Recuperer Mot de Passe",
            "delete_account_button" to "Supprimer le Compte",
            "confirm_label" to "Confirmer l'élimination",
            "confirm_deletion_phrase" to "Êtes-vous certain de vouloir éliminer votre compte ?",
            "confirm_button" to "Confirmer",
            "uploading_audio" to "Chargement de l'audio en cours...",
            "horario" to "Sélectionnez un horaire",
            "horario_titulo" to "Horaire Préféré: ",
            "calendario" to "Ajouter au calendrier",
            "hoje" to "Aujourd'hui",
            "aula" to "Cour à ",
            "nivel" to "Niveau",
            "basico" to "Basique",
            "iniciante" to "Débutant",
            "avancado" to "Avancé",
            "niv_mundial" to "Niveau Mondial",
            "professional" to "Professionnel",
            "desconhecido" to "Inconnu",
            "apagar_user" to "Supprimer mon compte",
            "sem_prof" to "Sans professeur",
            "sem_prof_desc" to "Vous n'avez pas de professeur. Veuillez attendre qu'un vous soit attribué pour pouvoir continuer sur cette page.",
            "login_button" to "Se Connecter",
            "register_button" to "S'inscrire",
            "password" to "Mot de passe",
            "password_mail" to "Email envoyé avec succès!",
            "jogo_complete" to "Completer la phrase: ",
            "nivel_completed" to "Vous avez terminé le niveau. Voulez-vous revenir aux niveaux ?",
            "parabens" to "Félicitations !!!",
            "ir_niveis" to "Aller aux niveaux",
            "nivel" to "Niveau",
            "reset_response_button" to "Réinitialiser la réponse",
            "regras" to "Regles",
            "regulamento_registro" to "NSS : doit contenir exactement 11 chiffres\n" +
                    "NIF : doit contenir exactement 9 chiffres\n" +
                    "Téléphone : doit contenir exactement 9 chiffres\n" +
                    "Code Postal : doit commencer par 4 chiffres et contenir un \"-\" suivi de 3 chiffres\n" +
                    "Email : doit être une adresse email valide",
            "professor_label" to "Professeur?"
        )
        "en" -> mapOf(
            "progress_title" to "My Progress",
            "tutor_title" to "My Tutor",
            "daily_phrase_title" to "Phrase of the Day",
            "daily_phrase" to "Unity is strength.",
            "profile_title" to "Profile",
            "full_name_label" to "Full Name",
            "address_label" to "Address",
            "postal_code_label" to "Postal Code",
            "city_label" to "City",
            "nif_label" to "Tax Identification Number",
            "nss_label" to "Social Security Number",
            "phone_number_label" to "Phone Number",
            "email_label" to "Email",
            "edit_button" to "Edit",
            "save_button" to "Save",
            "cancel_button" to "Cancel",
            "password_recovery_button" to "Recover Password",
            "delete_account_button" to "Delete Account",
            "confirm_label" to "Confirm deletion",
            "confirm_deletion_phrase" to "Are you sure you want to delete your account?",
            "confirm_button" to "Confirm",
            "uploading_audio" to "Uploading Audio...",
            "horario" to "Select a schedule",
            "horario_titulo" to "Prefered Schedule: ",
            "calendario" to "Add to calendar",
            "hoje" to "Today",
            "aula" to "Class at",
            "nivel" to "Level",
            "basico" to "Basic",
            "iniciante" to "Beginner",
            "avancado" to "Advanced",
            "niv_mundial" to "World Level",
            "professional" to "Professional",
            "desconhecido" to "Unknown",
            "apagar_user" to "Delete my account",
            "sem_prof" to "Without Professor",
            "sem_prof_desc" to "You don't have a teacher. Please wait for one to be assigned to you in order to proceed to this page.",
            "login_button" to "Login",
            "register_button" to "Register",
            "password" to "Password",
            "password_mail" to "Password sent successfully!",
            "jogo_complete" to "Complete the phrase: ",
            "nivel_completed" to "You have completed the level. Do you want to go back to levels?",
            "parabens" to "Congratulations!!!",
            "ir_niveis" to "Go to Levels",
            "nivel" to "Level",
            "reset_response_button" to "Reset Response",
            "regras" to "Rules",
            "regulamento_registro" to "NSS: must have exactly 11 digits\n" +
                    "NIF: must have exactly 9 digits\n" +
                    "Phone: must have exactly 9 digits\n" +
                    "Postal Code: must start with 4 digits and contain a \"-\" followed by 3 digits\n" +
                    "Email: must be a valid email address",
            "professor_label" to "Are you a Teacher?"
        )
        "es" -> mapOf(
            "progress_title" to "Mi Progreso",
            "tutor_title" to "Mi Tutor",
            "daily_phrase_title" to "Frase del Día",
            "daily_phrase" to "La unión hace la fuerza.",
            "profile_title" to "Perfil",
            "full_name_label" to "Nombre Completo",
            "address_label" to "Dirección",
            "postal_code_label" to "Código Postal",
            "city_label" to "Ciudad",
            "nif_label" to "Número de Identificación Fiscal",
            "nss_label" to "Número de Seguridad Social",
            "phone_number_label" to "Número de Teléfono",
            "email_label" to "Correo Electrónico",
            "edit_button" to "Editar",
            "save_button" to "Guardar",
            "cancel_button" to "Cancelar",
            "password_recovery_button" to "Recuperar Contraseña",
            "delete_account_button" to "Eliminar Cuenta",
            "confirm_label" to "Confirmar eliminación",
            "confirm_deletion_phrase" to "¿Está seguro de que desea eliminar su cuenta?",
            "confirm_button" to "Confirmar",
            "uploading_audio" to "Carregando el audio..",
            "horario" to "Seleccione un horario",
            "horario_titulo" to "Preferred Time: ",
            "calendario" to "Adicionar al calendario",
            "hoje" to "Hoy",
            "aula" to "Aula a las",
            "nivel" to "Nivel",
            "basico" to "Básico",
            "iniciante" to "Principiante",
            "avancado" to "Avanzado",
            "niv_mundial" to "Nivel Mundial",
            "professional" to "Profesional",
            "desconhecido" to "Desconocido",
            "apagar_user" to "Eliminar mi cuenta",
            "sem_prof" to "sin profesor",
            "sem_prof_desc" to "No tienes un profesor. Por favor, espera a que se te asigne uno para poder continuar a esta página.",
            "login_button" to "Iniciar Sesión",
            "register_button" to "Registrarse",
            "password" to "Contraseña",
            "password_mail" to "Correo electrónico enviado con éxito!",
            "jogo_complete" to "Completa la frase: ",
            "nivel_completed" to "Has completado el nivel. ¿Quieres volver a los niveles?",
            "parabens" to "Felicitaciones!!!",
            "ir_niveis" to "Ir para Níveis",
            "nivel" to "Nivel",
            "reset_response_button" to "Reiniciar la Respuesta",
            "regras" to "Regras",
            "regulamento_registro" to "NSS: debe tener exactamente 11 dígitos\n" +
                    "NIF: debe tener exactamente 9 dígitos\n" +
                    "Teléfono: debe tener exactamente 9 dígitos\n" +
                    "Código Postal: debe comenzar con 4 dígitos y contener un \"-\" seguido de 3 dígitos\n" +
                    "Correo Electrónico: debe ser una dirección de correo electrónico válida",
            "professor_label" to "Eres un Professor?"
        )
        else -> emptyMap()
    }
}