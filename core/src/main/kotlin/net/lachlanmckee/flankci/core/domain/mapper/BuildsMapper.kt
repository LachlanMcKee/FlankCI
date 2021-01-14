package net.lachlanmckee.flankci.core.domain.mapper

import net.lachlanmckee.flankci.core.data.entity.BuildsData
import net.lachlanmckee.flankci.core.data.entity.generic.BuildDataResponse

interface BuildsMapper {
  fun mapBuilds(data: List<BuildDataResponse>): BuildsData
}
