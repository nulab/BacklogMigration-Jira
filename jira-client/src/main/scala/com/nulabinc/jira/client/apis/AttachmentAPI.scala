package com.nulabinc.jira.client.apis

import com.nulabinc.jira.client.HttpClient
import com.nulabinc.jira.client.domain.Attachment

class AttachmentAPI(httpClient: HttpClient) {

  def download(attachment: Attachment, to: String): Unit =
    httpClient.download(attachment.content, to)

}
