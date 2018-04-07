package com.jalgoarena.web

import com.jalgoarena.data.ProblemsRepository
import com.jalgoarena.data.SubmissionsRepository
import com.jalgoarena.domain.SolvedRatioEntry
import com.jalgoarena.domain.Submission
import com.jalgoarena.ranking.RankingCalculator
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import javax.inject.Inject

@RestController
class RankingController(
        @Inject private val rankingCalculator: RankingCalculator,
        @Inject private val usersClient: UsersClient,
        @Inject private val problemsRepository: ProblemsRepository,
        @Inject private val submissionsRepository: SubmissionsRepository
) {

    @GetMapping("/ranking", produces = ["application/json"])
    fun ranking() = rankingCalculator.ranking(
            users = usersClient.findAllUsers().filter { it.username != "admin" },
            submissions = submissionsRepository.findAllAccepted(),
            problems = problemsRepository.findAll()
    )

    @GetMapping("/ranking/{problemId}", produces = ["application/json"])
    fun problemRanking(@PathVariable problemId: String) =
            rankingCalculator.problemRanking(
                    problemId = problemId,
                    users = usersClient.findAllUsers(),
                    problems = problemsRepository.findAll())

    @GetMapping("/solved-ratio", produces = ["application/json"])
    fun submissionsSolvedRatio() =
            calculateSubmissionsSolvedRatioAndReturnIt(submissionsRepository.findAll())

    private fun calculateSubmissionsSolvedRatioAndReturnIt(submissions: List<Submission>) =
            submissions
                    .distinctBy { it.userId }
                    .groupBy { it.problemId }
                    .map {
                        SolvedRatioEntry(it.key, it.value.count())
                    }
}