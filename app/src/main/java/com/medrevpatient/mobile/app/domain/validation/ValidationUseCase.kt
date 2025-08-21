package com.medrevpatient.mobile.app.domain.validation

import android.util.Patterns
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

    fun emailValidation(emailAddress: String): ValidationResult {
        val isEmailMatched = Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()
        return ValidationResult(
            isSuccess = isEmailMatched,
            errorMsg = if (emailAddress.isBlank()) "Please enter your email address"
            else if (!isEmailMatched) "invalid email address" else null
        )
    }

    fun passwordMatchValidation(password: String, confirmPassword: String): ValidationResult {
        return ValidationResult(
            isSuccess = password == confirmPassword,
            errorMsg = if (password != confirmPassword) "Confirm password must be same as password" else null
        )
    }


    fun isStringIntegerValidation(value: String): ValidationResult {
        return ValidationResult(
            isSuccess = value.toIntOrNull() != null,
            errorMsg = if (value.toIntOrNull() == null) "Please enter a valid number" else null
        )
    }

    fun isEmptyStringValidation(value: String, message: String = "require*"): ValidationResult {
        return ValidationResult(
            isSuccess = value.isNotBlank(),
            errorMsg = if (value.isBlank()) message else null
        )
    }


    fun isStringDecimalValidation(value: String): ValidationResult {
        return ValidationResult(
            isSuccess = value.toDoubleOrNull() != null,
            errorMsg = if (value.toDoubleOrNull() == null) "Please enter a valid number" else null
        )
    }

    fun zeroValidation(value: String): ValidationResult {
        val number: Number? = value.toIntOrNull() ?: value.toDoubleOrNull()
        return ValidationResult(
            isSuccess = number != null && (number.toInt() > 0),
            errorMsg = if (number == null || number.toInt() <= 0) "Please provide a non zero value" else null
        )
    }

}

data class ValidationResult(
    val isSuccess: Boolean = false,
    val errorMsg: String? = null
)