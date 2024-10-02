package com.ijonsabae.presentation.shot.ai.data

open class Comment(val type: String, val content: String)

class BadComment(content: String) : Comment("BAD", content)

class NiceComment(content: String) : Comment("NICE", content)
