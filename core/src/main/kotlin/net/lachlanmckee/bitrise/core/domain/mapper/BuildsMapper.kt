package net.lachlanmckee.bitrise.core.domain.mapper

import net.lachlanmckee.bitrise.core.data.entity.BuildDataResponse
import net.lachlanmckee.bitrise.core.data.entity.BuildsData
import net.lachlanmckee.bitrise.core.data.entity.MultipleBuildsResponse

interface BuildsMapper {
    fun mapBuilds(data: List<BuildDataResponse>): BuildsData
}
