package io.pleo.antaeus.models


enum class JobType {
    SCHEDULED,
    SIMPLE;

    companion object {
        fun getJobTypes(): List<String> {
            return values().map {
                it.toString()
            }
        }
    }


}



