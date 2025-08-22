package com.medrevpatient.mobile.app.domain.validation

import android.content.Context
import android.util.Patterns
import com.medrevpatient.mobile.app.R
import javax.inject.Inject

class ValidationUseCase @Inject constructor() {

    fun emptyFieldValidation(fieldValue: String, errorMsg: String): ValidationResult {
        return ValidationResult(
            isSuccess = fieldValue.isNotBlank(),
            errorMsg = if (fieldValue.isBlank()) {
                errorMsg
            } else {
                null
            }
        )
    }
    fun emailValidation(emailAddress: String,context: Context): ValidationResult {
        val isEmailMatched = Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()
        return ValidationResult(
            isSuccess = isEmailMatched,
            errorMsg = if (emailAddress.isBlank()) context.getString(R.string.please_enter_an_email_address)
            else if (!isEmailMatched) "Please enter a valid email address" else null
        )
    }
    fun passwordValidation(password: String, context: Context): ValidationResult {
        val uppercasePattern = Regex("[A-Z]")
        val specialCharPattern = Regex("[!@#\$%^&*(),.?\":{}|<>]")
        val digitPattern = Regex("\\d")

        val errorMessage = when {
            password.isBlank() -> context.getString(R.string.error_enter_password)
            password.length < 8 -> context.getString(R.string.password_text) // Update this string in resources
            !uppercasePattern.containsMatchIn(password) -> context.getString(R.string.password_text)
            !specialCharPattern.containsMatchIn(password) -> context.getString(R.string.password_text)
            !digitPattern.containsMatchIn(password) -> context.getString(R.string.password_text)
            else -> null
        }

        return ValidationResult(
            isSuccess = errorMessage == null,
            errorMsg = errorMessage
        )
    }

}

data class ValidationResult(
    val isSuccess: Boolean = false,
    val errorMsg: String? = null
)