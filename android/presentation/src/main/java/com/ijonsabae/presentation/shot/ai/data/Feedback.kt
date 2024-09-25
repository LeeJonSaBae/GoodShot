package com.ijonsabae.presentation.shot.ai.data

open class Feedback()

class NiceFeedback(val compliment: String) : Feedback()

class BadFeedback(val problem: String, val solution: String) : Feedback()
