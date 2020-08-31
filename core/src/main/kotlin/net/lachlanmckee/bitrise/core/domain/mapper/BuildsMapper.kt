package net.lachlanmckee.bitrise.core.domain.mapper

import net.lachlanmckee.bitrise.core.data.entity.BuildsData
import net.lachlanmckee.bitrise.core.data.entity.BuildsResponse

interface BuildsMapper {
    fun mapBuilds(data: List<BuildsResponse.BuildData>): BuildsData
}
