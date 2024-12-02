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
            "confirm_button" to "Confirmar"
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
            "confirm_button" to "Confirmer"
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
            "confirm_button" to "Confirm"
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
            "confirm_button" to "Confirmar"
        )
        else -> emptyMap()
    }
}