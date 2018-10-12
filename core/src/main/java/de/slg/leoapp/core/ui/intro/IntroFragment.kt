package de.slg.leoapp.core.ui.intro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

abstract class IntroFragment : Fragment() {

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

}