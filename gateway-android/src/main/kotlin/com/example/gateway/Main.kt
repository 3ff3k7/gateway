package com.example.gateway

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("Usage: gateway <command>")
        return
    }

    val result = when (args[0]) {
        "case-status" -> {
            if (args.size < 2) throw IllegalArgumentException("receipt required")
            val client = UscisClient()
            client.checkCaseStatus(args[1])
        }
        "foia-create" -> {
            if (args.size < 2) throw IllegalArgumentException("json data required")
            val client = FoiaClient()
            client.createRequest(args[1])
        }
        "foia-status" -> {
            if (args.size < 2) throw IllegalArgumentException("request id required")
            val client = FoiaClient()
            client.checkStatus(args[1])
        }
        "processing-time" -> {
            if (args.size < 3) throw IllegalArgumentException("form and office required")
            ProcessingTimes.getProcessingTime(args[1], args[2])
        }
        "ceac-status" -> {
            if (args.size < 2) throw IllegalArgumentException("case number required")
            StateDept.checkCeacStatus(args[1])
        }
        "create-ics" -> {
            if (args.size < 4) throw IllegalArgumentException("summary, start, end required")
            CalendarEvent.createEvent(
                args[1],
                args[2],
                args[3],
                location = args.getOrNull(4),
                description = args.getOrNull(5)
            )
        }
        else -> {
            println("Unknown command: ${args[0]}")
            return
        }
    }

    println(Json.encodeToString(result))
}
