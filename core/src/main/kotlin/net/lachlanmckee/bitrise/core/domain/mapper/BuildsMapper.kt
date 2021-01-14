package net.lachlanmckee.bitrise.core.domain.mapper

import net.lachlanmckee.bitrise.core.data.entity.BuildsData
import net.lachlanmckee.bitrise.core.data.entity.generic.BuildDataResponse

interface BuildsMapper {
  fun mapBuilds(data: List<BuildDataResponse>): BuildsData
}
