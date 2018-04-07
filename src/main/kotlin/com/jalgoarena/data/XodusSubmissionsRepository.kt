package com.jalgoarena.data

import com.jalgoarena.domain.Constants
import com.jalgoarena.domain.Constants.SUBMISSION_ENTITY_TYPE
import com.jalgoarena.domain.Submission
import jetbrains.exodus.entitystore.Entity
import jetbrains.exodus.entitystore.EntityRemovedInDatabaseException
import jetbrains.exodus.entitystore.PersistentEntityId
import jetbrains.exodus.entitystore.PersistentStoreTransaction
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository

@Repository
open class XodusSubmissionsRepository(db: Db) : SubmissionsRepository {
    private val store = db.store

    private val logger = LoggerFactory.getLogger(this.javaClass)
    override fun findAll(): List<Submission> {
        return readonly {
            it.getAll(SUBMISSION_ENTITY_TYPE).map { Submission.from(it) }
        }
    }

    override fun findAllAccepted(): List<Submission> {
        return readonly {
            it.find(
                    Constants.SUBMISSION_ENTITY_TYPE,
                    Constants.statusCode,
                    Constants.ACCEPTED
            ).map { Submission.from(it) }
        }
    }

    override fun findByUserId(userId: String): List<Submission> {
        return readonly {
            it.find(
                    SUBMISSION_ENTITY_TYPE,
                    Constants.userId,
                    userId
            ).map { Submission.from(it) }
        }
    }

    override fun findBySubmissionId(submissionId: String): Submission {
        return readonly {
            val submissionResult = it.find(
                    SUBMISSION_ENTITY_TYPE,
                    Constants.submissionId,
                    submissionId
            ).firstOrNull()

            if (submissionResult == null) {
                Submission.notFound(submissionId)
            } else {
                Submission.from(submissionResult)
            }
        }
    }

    override fun findById(id: String): Submission? {
        return try {
            readonly {
                val entityId = PersistentEntityId.toEntityId(id)
                Submission.from(it.getEntity(entityId))
            }
        } catch(e: EntityRemovedInDatabaseException) {
            null
        }
    }

    override fun findByProblemId(problemId: String): List<Submission> {
        return readonly {
            it.find(
                    SUBMISSION_ENTITY_TYPE,
                    Constants.problemId,
                    problemId
            ).map { Submission.from(it) }
        }
    }

    override fun addOrUpdate(submission: Submission): Submission {
        return transactional {

            val existingEntity = it
                    .find(Constants.SUBMISSION_ENTITY_TYPE, Constants.userId, submission.userId)
                    .intersect(it.find(Constants.SUBMISSION_ENTITY_TYPE, Constants.problemId, submission.problemId))
                    .firstOrNull()

            if (existingEntity != null) {
                val elapsedTime = existingEntity.getProperty(Constants.elapsedTime) as Double
                if (elapsedTime <= submission.elapsedTime) {
                    logger.info("Ignoring as new submission time {} is worse than existing {}",
                            submission.elapsedTime, elapsedTime
                    )
                    Submission.from(existingEntity)
                } else {
                    updateEntity(existingEntity, submission)
                }
            } else {
                addNewSubmission(it, submission)
            }
        }
    }

    private fun addNewSubmission(it: PersistentStoreTransaction, submission: Submission) =
            updateEntity(it.newEntity(SUBMISSION_ENTITY_TYPE), submission)

    private fun updateEntity(entity: Entity, submission: Submission): Submission {
        entity.apply {
            setProperty(Constants.problemId, submission.problemId)
            setProperty(Constants.elapsedTime, submission.elapsedTime)
            setProperty(Constants.sourceCode, submission.sourceCode)
            setProperty(Constants.statusCode, submission.statusCode)
            setProperty(Constants.userId, submission.userId)
            setProperty(Constants.language, submission.language)
            setProperty(Constants.submissionId, submission.submissionId)
            setProperty(Constants.submissionTime, submission.submissionTime)
            setProperty(Constants.consumedMemory, submission.consumedMemory)
            setProperty(Constants.errorMessage, submission.errorMessage ?: "")
            setProperty(Constants.passedTestCases, submission.passedTestCases ?: 0)
            setProperty(Constants.failedTestCases, submission.failedTestCases ?: 0)
        }

        return Submission.from(entity)
    }

    override fun delete(id: String): List<Submission> {
        val entityId = PersistentEntityId.toEntityId(id)

        transactional {
            val entity = it.getEntity(entityId)
            entity.delete()
        }

        return findAll()
    }

    override fun destroy() {
        var proceed = true
        var count = 1
        while (proceed && count <= 10) {
            try {
                logger.info("trying to close persistent store. attempt {}", count)
                store.close()
                proceed = false
                logger.info("persistent store closed")
            } catch (e: RuntimeException) {
                logger.error("error closing persistent store", e)
                count++
            }
        }
    }

    private fun <T> transactional(call: (PersistentStoreTransaction) -> T): T {
        return store.computeInTransaction { call(it as PersistentStoreTransaction) }
    }

    private fun <T> readonly(call: (PersistentStoreTransaction) -> T): T {
        return store.computeInReadonlyTransaction { call(it as PersistentStoreTransaction) }
    }
}
