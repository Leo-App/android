package de.slg.leoapp.ui.profile

import android.graphics.Bitmap

interface IProfilePresenter {
    fun onBackPressed()
    fun onEditStarted()
    fun onEditFinished()
    fun onImageInteraction()
    fun onImageSelected(bitmap: Bitmap)
}