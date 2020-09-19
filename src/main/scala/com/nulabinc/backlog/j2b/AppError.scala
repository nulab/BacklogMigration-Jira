package com.nulabinc.backlog.j2b

import com.nulabinc.backlog.j2b.conf.ConfigValidateResult
import com.nulabinc.backlog.migration.common.errors.MappingFileError

sealed trait AppError

case class ParameterError(errors: Seq[ConfigValidateResult]) extends AppError
case class MappingError(inner: MappingFileError)             extends AppError
case object ConfirmCanceled                                  extends AppError
