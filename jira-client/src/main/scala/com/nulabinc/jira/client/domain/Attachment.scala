package com.nulabinc.jira.client.domain

import java.util.Date

case class Attachment(
    id: Long,
    fileName: String,
    author: User,
    createdAt: Date,
    size: Long,
    mimeType: String,
    content: String
)
