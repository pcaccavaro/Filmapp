package com.example.filmapp.ui.common

sealed class BaseActionTest {
    object ActionTestWithoutParameter : BaseActionTest()
    data class ActionTestWithParameter(val parameterTest: Any) : BaseActionTest()
}

sealed class BaseEventTest {
    object OnEventTestWithoutParameterCalled : BaseEventTest()
    data class OnEventTestWithParameterCalled(val parameterTest: Any) : BaseEventTest()
}