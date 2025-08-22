package com.medrevpatient.mobile.app.ui.compose.common.countryCode
import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.ui.theme.White50
import com.medrevpatient.mobile.app.ui.theme.WorkSans
import com.joelkanyi.jcomposecountrycodepicker.component.CountryCodePicker.getCountryPhoneCode
import com.joelkanyi.jcomposecountrycodepicker.component.CountrySelectionDialog
import com.joelkanyi.jcomposecountrycodepicker.component.SelectedCountryComponent
import com.joelkanyi.jcomposecountrycodepicker.data.CountryData

import java.util.Locale


internal var fullNumberState: String by mutableStateOf("")
internal var phoneNumberState: String by mutableStateOf("")
internal var countryCodeState: String by mutableStateOf("")
@Composable
fun KomposeCountryCodePickerNew(
    modifier: Modifier = Modifier,
    text: String = "",
    showOnlyCountryCodePicker: Boolean = false,
    onValueChange: (String) -> Unit = {},
    shape: Shape = MaterialTheme.shapes.medium,
    showCountryCode: Boolean = true,
    showCountryFlag: Boolean = true,
    limitedCountries: List<String> = emptyList(),
    error: Boolean = false,
    placeholder: @Composable ((defaultLang: String) -> Unit) = { defaultLang ->
        DefaultPlaceholder(defaultLang)
    },
    colors: TextFieldColors = TextFieldDefaults.colors(),
    trailingIcon: @Composable (() -> Unit) = {},
    defaultCountryCode: String? = null,
) {
    val context = LocalContext.current
    val localTextInputService = LocalTextInputService.current
    var phoneCode by rememberSaveable {
        mutableStateOf(
            getDefaultPhoneCode(
                context,
            ),
        )
    }
    var selectedLanguageCode by rememberSaveable {
        mutableStateOf(
            defaultCountryCode?.lowercase() ?: getDefaultLangCode(context),
        )
    }
    fullNumberState = phoneCode + text
    phoneNumberState = text
    countryCodeState = selectedLanguageCode

    var openCountrySelectionDialog by remember { mutableStateOf(false) }

    val countryList: List<CountryData> by remember {
        derivedStateOf {
            if (limitedCountries.isEmpty()) {
                allCountries
            } else {
                allCountries.filter { country ->
                    limitedCountries
                        .map { it.lowercase() }
                        .map { it.trim() }
                        .contains(country.countryCode) ||
                            limitedCountries.contains(country.cCountryPhoneNoCode) ||
                            limitedCountries.contains(country.cCountryName)
                }
            }
        }
    }

    var selectedCountry by remember {
        mutableStateOf(
            allCountries.single { it.countryCode == selectedLanguageCode },
        )
    }

    /**
     * [openCountrySelectionDialog] If true, the country selection dialog will be displayed.
     */
    if (openCountrySelectionDialog) {
        CountrySelectionDialog(
            countryList = countryList,
            onDismissRequest = {
                openCountrySelectionDialog = false
            },
            onSelected = { countryItem ->
                phoneCode = countryItem.cCountryPhoneNoCode
                selectedLanguageCode = countryItem.countryCode
                selectedCountry = allCountries.single { it.countryCode == selectedLanguageCode }
                openCountrySelectionDialog = false
            },
        )
    }

    /**
     * if [showOnlyCountryCodePicker] is true, only the country code picker will be displayed.
     */
    if (showOnlyCountryCodePicker) {
        KinshipSelectedCountryComponent(
            selectedCountry = selectedCountry,
            showCountryCode = showCountryCode,
            showFlag = showCountryFlag,
            onClickSelectedCountry = {
                openCountrySelectionDialog = true
            },
        )
    } else {
        OutlinedTextField(
            modifier = modifier,
            shape = shape,
            value = text,
            onValueChange = {
                if (text != it) {
                    onValueChange(it)
                }
            },
            placeholder = {
                placeholder(selectedLanguageCode)
            },
            singleLine = true,
            colors = colors,
            isError = error,
            //visualTransformation = PhoneNumberTransformation(selectedCountry.countryCode.uppercase()),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Phone,
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    localTextInputService?.hideSoftwareKeyboard()
                },
            ),
            leadingIcon = {
                SelectedCountryComponent(
                    selectedCountry = selectedCountry,
                    showCountryCode = showCountryCode,
                    showFlag = showCountryFlag,
                    onClickSelectedCountry = {
                        openCountrySelectionDialog = true
                    },
                )
            },
            trailingIcon = trailingIcon,
        )
    }
}



@Composable
private fun DefaultPlaceholder(defaultLang: String) {
    Text(
        text = stringResource(id = getNumberHint(allCountries.single { it.countryCode == defaultLang }.countryCode.lowercase())),
        style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.ExtraLight,
        ),
    )
}

/**
 * [SelectedCountryComponent] is a composable that displays a dialog with a list of countries.
 * [modifier] Modifier to be applied to the layout.
 * [selectedCountryPadding] The padding to be applied to the selected country.
 * [selectedCountry] The selected country.
 * [showCountryCode] If true, the country code will be shown in the text field.
 * [showFlag] If true, the country flag will be shown in the text field.
 * [showCountryName] If true, the country name will be shown in the text field.
 */
@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun KinshipSelectedCountryComponent(
    modifier: Modifier = Modifier,
    selectedCountry: CountryData,
    selectedCountryPadding: Dp = 8.dp,
    showCountryCode: Boolean = true,
    showFlag: Boolean = true,
    showCountryName: Boolean = false,
    onClickSelectedCountry: () -> Unit,
) {
    Row(
        modifier = modifier
            .padding(selectedCountryPadding)
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = null,
            ) {
                onClickSelectedCountry()
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (showFlag) {
            Image(
                modifier = modifier
                    .width(28.dp)
                    .height(18.dp),
                painter = painterResource(
                    id = getFlags(
                        selectedCountry.countryCode,
                    ),
                ),
                contentDescription = null,
            )
        }
        if (showCountryCode) {
            Text(
                text = selectedCountry.cCountryPhoneNoCode,
                fontWeight = FontWeight.W400,
                modifier = Modifier.padding(start = 4.dp),
                fontSize = 16.sp,
                fontFamily = WorkSans,
                color = White50,
            )
        }
        Spacer(modifier = Modifier.padding(5.dp))
        if (showCountryName) {
            Text(
                text = stringResource(id = getCountryName(selectedCountry.countryCode.lowercase())),
                fontWeight = FontWeight.W400,
                modifier = Modifier.padding(start = 6.dp),
                fontSize = 14.sp,
                color = Color.Black,
            )
        }
        Icon(painterResource(id = R.drawable.ic_app_icon), contentDescription = null, tint = White50)
    }
}

object CountryCodePickerNew {

    /**
     * Returns the selected country language code.
     */
    fun getCountryCodeWithoutPrefix(): String {
        return countryCodeState
    }

    /**
     * Returns the selected country name.
     */
    fun getCountryName(): String {
        return allCountries.single { it.countryCode == countryCodeState }.cCountryName.replaceFirstChar {
            it.uppercase(
                Locale.getDefault(),
            )
        }
    }

    /**
     * Returns the selected country phone code.
     */
    fun getCountryPhoneCodeNew(): String {
        return allCountries.single { it.countryCode == countryCodeState }.cCountryPhoneNoCode
    }

    /**
     * Returns the selected country phone code without the plus sign.
     */
    private fun getCountryPhoneCodeWithoutPrefix(): String {
        return allCountries.single { it.countryCode == countryCodeState }.cCountryPhoneNoCode.removePrefix(
            "+",
        )
    }

    /**
     * Returns phone number without the country phone code.
     */
    fun getPhoneNumber(): String {
        return if (phoneNumberState.startsWith("0")) {
            phoneNumberState.removeSpecialCharacters()
        } else {
            "0${phoneNumberState.removeSpecialCharacters()}"
        }
    }

    /**
     * Returns phone number without the country phone code and without the zero prefix.
     */
    fun getPhoneNumberWithoutPrefix(): String {
        return phoneNumberState.removeSpecialCharacters().removePrefix("0")
    }

    /**
     * Returns phone number with the country phone code without the + prefix.
     */
    fun getFullPhoneNumberWithoutPrefix(): String {
        return getCountryPhoneCodeWithoutPrefix() + phoneNumberState.removeSpecialCharacters()
            .removePrefix("0")
    }

    /**
     * Returns phone number with the country phone code and with the + prefix.
     */
    private fun getFullPhoneNumber(): String {
        return getCountryPhoneCode() + phoneNumberState.removeSpecialCharacters()
    }

    /**
     * Returns if the phone number is valid or not.
     */
    fun isPhoneNumberValid(phoneNumber: String = getFullPhoneNumber()): Boolean {
        return isValid(phoneNumber)
    }
}