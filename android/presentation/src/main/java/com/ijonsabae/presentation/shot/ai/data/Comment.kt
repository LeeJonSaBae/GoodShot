package com.ijonsabae.presentation.shot.ai.data

open class Comment(val type : String ,val detail: String)

class BadComment(detail: String) : Comment("BAD" ,detail)

class NiceComment(detail: String) : Comment("NICE" ,detail)
