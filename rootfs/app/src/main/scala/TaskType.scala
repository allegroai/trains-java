package trains

object TaskType extends Enumeration {
  type TaskType = Value

  val TRAINING = Value("training")
  val TESTING = Value("testing")
}
