package com.example.tmdbRatingApp.ui.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel(){

    val queryText : MutableLiveData<String> = MutableLiveData()
}