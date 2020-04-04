package com.drozdov.software_development.fitness_center.query

import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class SubscriptionInfo(
        @Id val subscriptionId: UUID, 
        val owner:String,
        val startDate: Instant,
        var endDate:Instant
            
)

interface SubscriptionInfoRepository : JpaRepository<SubscriptionInfo, UUID>