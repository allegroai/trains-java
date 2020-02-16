package trains

object LogLevel extends Enumeration {
  type LogLevel = Value

  val NOTSET = Value("notset")
  val DEBUG = Value("debug")
  val VERBOSE = Value("verbose")
  val INFO = Value("info")
  val WARN = Value("warn")
  val WARNING = Value("warning")
  val ERROR = Value("error")
  val FATAL = Value("fatal")
  val CRITICAL = Value("critical")
}
