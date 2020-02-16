package trains

object TaskEventType extends Enumeration {
  type TaskEventType = Value

  val TRAINING_STATS_SCALAR = Value("training_stats_scalar")
  val TRAINING_DEBUG_IMAGE = Value("training_debug_image")
  val TRAINING_STATS_VECTOR = Value("training_stats_vector")
  val PLOT = Value("plot")
  val LOG = Value("log")
}
