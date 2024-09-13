package com.ijonsabae.presentation.shot.ai.camera

import com.ijonsabae.presentation.shot.ai.data.Person

interface CameraSourceListener {
    fun onDetectedInfo(person: Person)
}