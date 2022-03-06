package io.pleo.antaeus.core.exceptions.validations

open class CronJobExecutionException (var trigger: String, message:String, var errorCode:String) : Exception("$errorCode : $message : '$trigger' failed."){


}