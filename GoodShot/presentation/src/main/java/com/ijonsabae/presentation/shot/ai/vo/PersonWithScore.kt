package com.ijonsabae.presentation.shot.ai.vo

import com.ijonsabae.presentation.shot.ai.data.Person

// 사람의 관절 좌표와 8개 자세 간의 유사도
class PersonWithScore(val person: Person, val scores: Array<Float>)