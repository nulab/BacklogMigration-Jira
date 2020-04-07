package com.nulabinc.backlog.j2b.core

import com.nulabinc.backlog.migration.common.errors.{MappingFileError => InnerMappingFileError}

sealed trait AppError extends RuntimeException

case class MappingFileError(inner: InnerMappingFileError) extends AppError

