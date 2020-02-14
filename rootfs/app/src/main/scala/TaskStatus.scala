package trains

object TaskStatus extends Enumeration {
  type TaskStatus = Value

  val CREATED = Value("created")
  val STARTED = Value("started")
  val IN_PROGRESS = Value("in_progress")
  val FAILED = Value("failed")
  val STOPPED = Value("stopped")
  val COMPLETED = Value("completed")
  val PUBLISHING = Value("publishing")
  val PUBLISHED = Value("published")
  val CLOSED = Value("closed")
  val UNKNOWN = Value("unknown")
}
