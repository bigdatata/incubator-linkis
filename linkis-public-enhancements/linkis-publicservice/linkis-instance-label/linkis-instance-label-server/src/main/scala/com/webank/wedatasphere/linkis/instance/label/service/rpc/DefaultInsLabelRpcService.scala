/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.wedatasphere.linkis.instance.label.service.rpc

import java.util

import com.webank.wedatasphere.linkis.common.exception.ErrorException
import com.webank.wedatasphere.linkis.common.utils.Logging
import com.webank.wedatasphere.linkis.instance.label.service.{InsLabelRpcService, InsLabelServiceAdapter}
import com.webank.wedatasphere.linkis.manager.label.builder.factory.LabelBuilderFactoryContext
import com.webank.wedatasphere.linkis.manager.label.entity.Label
import com.webank.wedatasphere.linkis.message.annotation.Receiver
import com.webank.wedatasphere.linkis.message.builder.ServiceMethodContext
import com.webank.wedatasphere.linkis.protocol.label.{InsLabelAttachRequest, InsLabelRefreshRequest, InsLabelRemoveRequest}
import javax.annotation.{PostConstruct, Resource}
import org.springframework.stereotype.Service

import scala.collection.JavaConversions._

@Service
class DefaultInsLabelRpcService extends InsLabelRpcService with Logging {
  @Resource
  private var insLabelService: InsLabelServiceAdapter = _


  @PostConstruct
  def init(): Unit = {
    info("Use the default implement of rpc service: DefaultInsLabelRpcService")
  }

  @Receiver
  override def attachLabelsToInstance(context: ServiceMethodContext, insLabelAttachRequest: InsLabelAttachRequest): Unit = {
    val labelMap = Option(insLabelAttachRequest.getLabels)
    val instance = Option(insLabelAttachRequest.getServiceInstance).getOrElse(
      throw new ErrorException(-1, "field 'serviceInstance' in attachRequest cannot be blank")
    )
    val labels = getLabels(labelMap).filter(_ != null)
    info(s"Start to attach labels[$labels] to instance[$instance]")
    insLabelService.attachLabelsToInstance(labels, instance)
    info(s"Success to attach labels[$labels] to instance[$instance]")
  }

  private def getLabels(labelMap: Option[util.Map[String, Object]]): util.List[Label[_]] = {
    if(labelMap.isDefined) {
      LabelBuilderFactoryContext.getLabelBuilderFactory.getLabels(labelMap.get)
    }else{
      new util.ArrayList[Label[_]]
    }
  }

  @Receiver
  override def refreshLabelsToInstance(context: ServiceMethodContext, insLabelRefreshRequest: InsLabelRefreshRequest): Unit = {
    val labelMap = Option(insLabelRefreshRequest.getLabels)
    val instance = Option(insLabelRefreshRequest.getServiceInstance).getOrElse(
      throw new ErrorException(-1, "field 'serviceInstance' in refreshRequest cannot be blank")
    )
    val labels = getLabels(labelMap)
    info(s"Start to refresh labels[$labels] to instance[$instance]")
    insLabelService.refreshLabelsToInstance(labels, instance)
    info(s"Success to refresh labels[$labels] to instance[$instance]")
  }

  @Receiver
  override def removeLabelsFromInstance(context: ServiceMethodContext, insLabelRemoveRequest: InsLabelRemoveRequest): Unit = {
    val instance = Option(insLabelRemoveRequest.getServiceInstance).getOrElse(
      throw new ErrorException(-1, "field 'serviceInstance' in removeRequest cannot be blank")
    )
    info(s"Start to remove labels from instance[$instance]")
    insLabelService.removeLabelsFromInstance(instance)
    info(s"Success to remove labels from instance[$instance]")
  }
}
