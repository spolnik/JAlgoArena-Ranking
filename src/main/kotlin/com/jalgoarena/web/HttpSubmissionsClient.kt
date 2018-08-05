package com.jalgoarena.web

import com.jalgoarena.domain.Submission
import org.slf4j.LoggerFactory
import org.springframework.web.client.RestOperations

interface SubmissionsClient {
    fun findAll(): List<Submission>
    fun findByProblemId(problemId: String): List<Submission>
    fun findBySubmissionTimeLessThan(tillDate: String): List<Submission>
}

class HttpSubmissionsClient(
        private val restTemplate: RestOperations,
        private val jalgoarenaApiUrl: String
) : SubmissionsClient {

    override fun findAll() =
            handleExceptions(returnOnException = emptyList()) {
                restTemplate.getForObject(
                        "$jalgoarenaApiUrl/submissions/api/submissions", Array<Submission>::class.java)!!.asList()
            }

    override fun findByProblemId(problemId: String) =
            handleExceptions(returnOnException = emptyList()) {
                restTemplate.getForObject(
                        "$jalgoarenaApiUrl/submissions/api/submissions/problem/$problemId", Array<Submission>::class.java)!!.asList()
            }

    override fun findBySubmissionTimeLessThan(tillDate: String) =
            handleExceptions(returnOnException = emptyList()) {
                restTemplate.getForObject(
                        "$jalgoarenaApiUrl/submissions/api/submissions/date/$tillDate", Array<Submission>::class.java)!!.asList()
            }

    private fun <T> handleExceptions(returnOnException: T, body: () -> T) = try {
        body()
    } catch (e: Exception) {
        LOG.error("Error in querying jalgoarena auth service", e)
        returnOnException
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(HttpSubmissionsClient::class.java)
    }
}
