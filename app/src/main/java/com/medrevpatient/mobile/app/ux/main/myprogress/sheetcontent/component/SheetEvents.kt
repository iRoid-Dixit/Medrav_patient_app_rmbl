package com.medrevpatient.mobile.app.ux.main.myprogress.sheetcontent.component

import com.medrevpatient.mobile.app.navigation.NavRoute
import java.io.File

typealias SheetEvent = (SheetEvents) -> Unit

sealed interface SheetEvents {
    data class AddLogToSpecific(
        val element: String, val value: String,
        val images: List<File> = emptyList(),
        val value2: String = "",
    ) : SheetEvents

    data class AddElementLog(
        val element: String,
        val value: String,
        val minute: String
    ) : SheetEvents

    data class CreateGoal(
        val element: String = "Calories",
        val value: String = "",
        val value2: String = "",
        val images: List<File> = emptyList(),
        val elementToDisable: List<String> = emptyList(),
    ) : SheetEvents

    data class EditGoal(
        val id: String,
        val element: String,
        val value: String,
        val value2: String,
        val images: List<File> = emptyList()
    ) : SheetEvents

    data class DeleteGoal(val id: String, val element: String, val value: String) : SheetEvents
    data class OptionMenu(val sheetType: SheetType) : SheetEvents
    object Negative : SheetEvents
    data class Positive(val sheetType: SheetType) : SheetEvents
    data class NavigateTo(val route: NavRoute) : SheetEvents
    data class Success(val sheetType: SheetType) : SheetEvents
}

data class SheetUiState(
    val shouldShowSuccessSheet: Boolean = false,
    val createGoal: CreateGoal = CreateGoal(),
    val editGoal: EditGoal = EditGoal(),
    val deleteGoal: DeleteGoal = DeleteGoal(),
    val addLogToSpecific: AddLogToSpecific = AddLogToSpecific(),
    val addElementLog: AddElementLog = AddElementLog(),
) {

    data class CreateGoal(
        val isLoading: Boolean = false,
        val isSuccess: String? = null,
        val isError: String? = null,
        val elementToDisable: List<String> = emptyList(),
        val element: String = "Calories",
        val value: String = "",
        val value2: String = "",
        val images: List<File> = emptyList()
    )

    data class EditGoal(
        val isLoading: Boolean = false,
        val isSuccess: Boolean = false, //TODO:remove if not require
        val isError: String? = null,
        val id: String = "",
        val element: String = "Calories",
        val value: String = "",
        val value2: String = "",
        val info: String = "Update Your log",
        val images: List<File> = emptyList()
    )

    data class DeleteGoal(
        val isLoading: Boolean = false,
        val isSuccess: String? = null,
        val isError: String? = null,
        val id: String = "",
        val element: String = "Calories",
        val value: String = "",
    )

    data class AddLogToSpecific(
        val isLoading: Boolean = false,
        val isSuccess: String? = null,
        val isError: String? = null,
        val element: String = "Calories",
        val value: String = "",
        val value2: String = "",
        val images: List<File> = emptyList(),
        val textFieldTitle: String? = null,
        val textFieldPlaceHolder: String = "0",
        val textFieldDescription: String = "",
        val info: String = "Add Log",
    )

    data class AddElementLog(
        val isLoading: Boolean = false,
        val isSuccess: String? = null,
        val isError: String? = null,
        val tabs: List<Int> = emptyList(),
        val element: String = "Calories",
        val value: String = "",
        val minute: String = ""
    )

}


enum class SheetType {
    CREATE_GOAL,
    EDIT_GOAL,
    DELETE_GOAL,
    OPTION_MENU,
    ADD_LOG_TO_SPECIFIC_GOAL,
    ADD_ELEMENT_LOG
}