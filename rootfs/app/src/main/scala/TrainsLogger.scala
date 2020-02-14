package trains

import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.jackson.Serialization.write
import org.json4s.jackson.Serialization
import sttp.client._


class TrainsLogger(val apiHost: String, val userKey: String, val userSecret: String) {
  import trains.TaskStatus._
  import trains.TaskType._
  import trains.TaskEventType._
  import trains.LogLevel._
  import trains.TrainsServerResponse._

  implicit val backend = HttpURLConnectionBackend()

  private val requestUri = (path: String) => uri"${apiHost}/${path}"

  private val authRequest = () => {
    basicRequest
      .auth
      .basic(userKey, userSecret)
  }

  private def catchRequestError[T](f: () => Either[String, T]): Either[String, T] = {
    try {
      f()
    } catch {
      case ex: Exception => {
        Left(ex.getMessage)
      }
    }
  }


  def parseResponse[T](body: Either[String, String])(implicit m: Manifest[T]): Either[String, T] = {
    implicit val formats = DefaultFormats
    body match {
      case Left(error) => {
        try {
          val response = parse(error).extract[Response]
          Left(response.meta.result_msg)
        } catch {
          case ex: Exception => {
            Left(s"parse response error (${error}): ${ex.getMessage}")
          }
        }
      }
      case Right(body) => {
        Right(parse(body).extract[T])
      }
    }
  }

  def createProject(name: String, desc: String): Either[String, String]= {
    val endpoint = requestUri("/projects.create")
    val createProjectRequest = (name: String, desc: String) => {
      authRequest()
        .post(endpoint)
        .body(Map("name"->name, "description"->desc))
    }

    catchRequestError(() => {
      val res = createProjectRequest(name, desc).send()
      parseResponse[CreateProjectResponse](res.body) match {
        case Left(error) => Left(error)
        case Right(res) => Right(res.data.map(_.id).getOrElse("unknown"))
      }
    })
  }

  def createTaskForProject(projectId: String, name: String, taskType: TaskType=TaskType.TRAINING,
                           parameters: Map[String, String]=Map(), model: String="",
                           framework: String="Angel"
                          ): Either[String, String] = {

    implicit val formats = Serialization.formats(NoTypeHints)

    implicit val personSerializer: BodySerializer[CreateTaskRequestBody] = { r: CreateTaskRequestBody =>
      val serialized = write(r)
      StringBody(serialized, "UTF-8", Some("application/json"))
    }

    val createTaskRequest = (projectId: String, name: String, taskType: TaskType, parameters: Map[String, String], model: String, framework: String) => {
      authRequest()
        .post(requestUri("/tasks.create"))
        .body(CreateTaskRequestBody( name, taskType.toString, projectId, CreateTaskExecution( parameters, model, framework ) ))
    }

    catchRequestError(() => {
      val res = createTaskRequest(projectId, name, taskType, parameters, model, framework).send()
      parseResponse[CreateTaskResponse](res.body) match {
        case Left(error) => Left(error)
        case Right(res) => Right(res.data.map(_.id).getOrElse("unknown"))
      }
    })
  }

  def reportMetricForTask(taskId: String, title: String, series: String, value: Double, iteration: Int): Either[String, Int] = {
    addEventForTask(taskId, Map(
      "type"->TaskEventType.TRAINING_STATS_SCALAR.toString,
      "metric"->title,
      "variant"->series,
      "value"->value.toString,
      "iter"->iteration.toString
    ))
  }

  def logForTask(taskId: String, msg: String, level: LogLevel = LogLevel.INFO): Either[String, Int] = {
    addEventForTask(taskId, Map(
      "type"->TaskEventType.LOG.toString,
      "msg"->msg,
      "level"->level.toString
    ))
  }

  private def addEventForTask(taskId: String, payload: Map[String, String]): Either[String, Int] = {
    val addEventRequest = (taskId: String, payload: Map[String, String]) => {
      authRequest()
        .post(requestUri("/events.add"))
        .body(payload + ("task"->taskId))
    }

    catchRequestError(() => {
      val res = addEventRequest(taskId, payload).send()
      parseResponse[AddEventResponse](res.body) match {
        case Left(error) => Left(error)
        case Right(res) => Right(res.data.map(_.added).getOrElse(0))
      }
    })
  }

  def setTaskStatusAs(taskId: String, status: TaskStatus): Either[String, String] = {
    val setTaskStoppedRequest = (taskId: String) => {
      authRequest()
        .post(requestUri("/tasks.stopped"))
        .body(Map("task"->taskId))
    }

    val setTaskStartedRequest = (taskId: String) => {
      authRequest()
        .post(requestUri("/tasks.started"))
        .body(Map("task"->taskId))
    }

    val setTaskFailedRequest = (taskId: String) => {
      authRequest()
        .post(requestUri("/tasks.failed"))
        .body(Map("task"->taskId))
    }

    val setTaskClosedRequest = (taskId: String) => {
      authRequest()
        .post(requestUri("/tasks.close"))
        .body(Map("task"->taskId))
    }

    val req = status match {
      case TaskStatus.STARTED => setTaskStartedRequest(taskId)
      case TaskStatus.FAILED => setTaskFailedRequest(taskId)
      case TaskStatus.STOPPED => setTaskStoppedRequest(taskId)
      case TaskStatus.CLOSED => setTaskClosedRequest(taskId)
      case _ => return Left("not supported action")
    }

    catchRequestError(() => {
      val res = req.send()
      parseResponse[ChangeTaskStatusResponse](res.body) match {
        case Left(error) => Left(error)
        case Right(res) => Right(res.data.map(_.fields.status).getOrElse(TaskStatus.UNKNOWN.toString))
      }
    })
  }

}

object TrainsLogger {
}
