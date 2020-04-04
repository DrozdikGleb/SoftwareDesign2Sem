package com.drozdov.software_development.fitness_center.query

import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class SubscriptionHistory(
        @Id val id: UUID,
        val subscriptionId:UUID,
        val timestamp: Instant,
        var action: Action
)

enum class Action {
    ENTER, EXIT
}

interface SubscriptionHistoryRepository : JpaRepository<SubscriptionHistory, UUID>