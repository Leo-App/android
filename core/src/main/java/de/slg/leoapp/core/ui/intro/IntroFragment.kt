package de.slg.leoapp.core.ui.intro

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import de.slg.leoapp.annotation.PreferenceKey
import de.slg.leoapp.core.preferences.PreferenceManager

abstract class IntroFragment : Fragment() {

    @PreferenceKey
    val preferenceKey: String = getFragmentTag()

    lateinit var listener: View.OnClickListener

    abstract fun getContentView(): Int

    abstract fun getFragmentTag(): String

    abstract fun canContinue(): Boolean

    abstract fun getErrorMessage(): String

    abstract fun getNextButton(): Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(getContentView(), container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<View>(getNextButton())?.setOnClickListener(listener)
    }

    @CallSuper
    open fun complete() {
        PreferenceManager.edit(context!!) {
            putBoolean(preferenceKey, true)
        }
    }

    fun isCompleted(context: Context): Boolean {
        var b = false
        PreferenceManager.read(context) {
            b = getBoolean(preferenceKey)
        }
        return b
    }

}