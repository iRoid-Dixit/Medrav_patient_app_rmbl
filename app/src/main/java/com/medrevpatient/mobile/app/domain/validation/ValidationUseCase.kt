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

    fun heightValidation(height: String, context: Context): ValidationResult {
        return when {
            height.isBlank() -> ValidationResult(
                isSuccess = false,
                errorMsg = "Please enter your height"
            )
            !isValidDecimal(height) -> ValidationResult(
                isSuccess = false,
                errorMsg = "Please enter a valid height value"
            )
            height.toDoubleOrNull()?.let { it <= 0 } == true -> ValidationResult(
                isSuccess = false,
                errorMsg = "Height must be greater than 0"
            )
            height.toDoubleOrNull()?.let { it > 300 } == true -> ValidationResult(
                isSuccess = false,
                errorMsg = "Height must be less than 300 cm"
            )
            else -> ValidationResult(
                isSuccess = true,
                errorMsg = null
            )
        }
    }

    fun weightValidation(weight: String, context: Context): ValidationResult {
        return when {
            weight.isBlank() -> ValidationResult(
                isSuccess = false,
                errorMsg = "Please enter your weight"
            )
            !isValidDecimal(weight) -> ValidationResult(
                isSuccess = false,
                errorMsg = "Please enter a valid weight value"
            )
            weight.toDoubleOrNull()?.let { it <= 0 } == true -> ValidationResult(
                isSuccess = false,
                errorMsg = "Weight must be greater than 0"
            )
            weight.toDoubleOrNull()?.let { it > 1000 } == true -> ValidationResult(
                isSuccess = false,
                errorMsg = "Weight must be less than 1000 kg"
            )
            else -> ValidationResult(
                isSuccess = true,
                errorMsg = null
            )
        }
    }

    private fun isValidDecimal(value: String): Boolean {
        return try {
            if (value.isBlank()) return false
            // Check if it's a valid decimal number (supports both integer and decimal formats)
            val regex = Regex("^\\d+(\\.\\d+)?$")
            if (regex.matches(value)) {
                // Additional check: ensure it can be parsed as a valid decimal
                value.toDoubleOrNull() != null
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

}

data class ValidationResult(
    val isSuccess: Boolean = false,
    val errorMsg: String? = null
)