package com.ijonsabae.presentation.mapper

import com.ijonsabae.domain.model.TotalReport
import com.ijonsabae.presentation.model.TotalReportParcelable

object TotalReportMapper {
    fun mapperTotalReportParcelable(totalReport: TotalReport): TotalReportParcelable{
        return TotalReportParcelable(
            name = totalReport.name,
            similarity = totalReport.similarity,
            content = totalReport.content,
            score = totalReport.score,
            problems = totalReport.problems
        )
    }
}