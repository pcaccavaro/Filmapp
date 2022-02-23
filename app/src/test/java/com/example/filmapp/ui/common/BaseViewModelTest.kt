package com.example.filmapp.ui.common

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.MockKAnnotations
import io.mockk.mockk
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class BaseViewModelTest {
    private lateinit var baseViewModelForTesting: BaseViewModelForTesting

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        baseViewModelForTesting = BaseViewModelForTesting(mockk())
    }

    @Test
    fun `should get action test with parameter when start event test with parameter`() {
        val parameterMock = mockk<Any>()

        baseViewModelForTesting.startEvent(BaseEventTest.OnEventTestWithParameterCalled(parameterTest = parameterMock))

        Assert.assertEquals(baseViewModelForTesting.action.getOrAwaitValue(), BaseActionTest.ActionTestWithParameter(parameterTest = parameterMock))
    }

    @Test
    fun `should get action test without parameter when start event test without parameter`() {
        baseViewModelForTesting.startEvent(BaseEventTest.OnEventTestWithoutParameterCalled)

        Assert.assertEquals(baseViewModelForTesting.action.getOrAwaitValue(), BaseActionTest.ActionTestWithoutParameter)
    }
}

private class BaseViewModelForTesting(application: Application) : BaseViewModel<BaseEventTest, BaseActionTest>(application) {
    override fun processEvent(event: BaseEventTest) {
        when(event) {
            is BaseEventTest.OnEventTestWithParameterCalled -> setAction(BaseActionTest.ActionTestWithParameter(event.parameterTest))
            BaseEventTest.OnEventTestWithoutParameterCalled -> setAction(BaseActionTest.ActionTestWithoutParameter)
        }
    }
}