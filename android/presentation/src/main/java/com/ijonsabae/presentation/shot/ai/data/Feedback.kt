package com.ijonsabae.presentation.shot.ai.data

open class Feedback(val isRightPose: Boolean, val comment: String)

class NiceFeedback(comment: String) : Feedback(true, comment)

class BadFeedback(comment: String) : Feedback(false, comment)
