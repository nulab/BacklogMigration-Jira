package com.nulabinc.backlog.j2b.cli

import com.nulabinc.backlog.j2b.conf.AppConfiguration
import com.nulabinc.backlog.migration.common.conf.BacklogConfiguration
import com.nulabinc.backlog.migration.common.service._
import com.nulabinc.backlog.migration.common.utils.{MixpanelUtil, TrackingData}

import scala.util.Try

trait Tracker extends BacklogConfiguration {

  def tracking(config: AppConfiguration, spaceService: SpaceService, userService: UserService) = {
    Try {
      val environment = spaceService.environment()
      val data = TrackingData(product = mixpanelProduct,
        envname = environment.name,
        spaceId = environment.spaceId,
        userId = userService.myself().id,
        srcUrl = config.jiraConfig.url,
        dstUrl = config.backlogConfig.url,
        srcProjectKey = config.jiraConfig.projectKey,
        dstProjectKey = config.backlogConfig.projectKey,
        srcSpaceCreated = "",
        dstSpaceCreated = spaceService.space().created)
      MixpanelUtil.track(token = mixpanelToken, data = data)
    }
  }
}
