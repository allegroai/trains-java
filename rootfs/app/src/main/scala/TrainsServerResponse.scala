package trains

object TrainsServerResponse {
  class Response(val meta: Meta, val data: Option[Any])

  case class Endpoint(name: String, requested_version: String, actual_version: String)
  case class Meta(id: String, trx: String, endpoint: Endpoint, result_code: Int, result_subcode: Int, result_msg: String, error_stack: String)

  case class CreateProjectData(id: String)
  case class CreateProjectResponse(override val meta: Meta, override val data: Option[CreateProjectData]) extends Response(meta, data)

  case class CreateTaskExecution(parameters: Map[String, String], model: String, framework: String)
  case class CreateTaskRequestBody(name: String, `type`: String, project: String, execution: CreateTaskExecution)
  case class CreateTaskData(id: String)
  case class CreateTaskResponse(override val meta: Meta, override val data: Option[CreateTaskData]) extends Response(meta, data)

  case class ChangeTaskStatusDataFields(status: String, status_reason: String, status_message: String, status_changed: String, last_update: String, completed: Option[String])
  case class ChangeTaskStatusData(fields: ChangeTaskStatusDataFields, updated: Int, started: Option[Int])
  case class ChangeTaskStatusResponse(override val meta: Meta, override val data: Option[ChangeTaskStatusData]) extends Response(meta, data)

  case class AddEventData(added: Int, errors: Int)
  case class AddEventResponse(override val meta: Meta, override val data: Option[AddEventData]) extends Response(meta, data)
}
