package com.sylvona.koresult.example.repository

import com.sylvona.koresult.example.model.entity.CustomerEntity
import com.sylvona.koresult.example.model.entity.CustomerId
import java.sql.SQLTimeoutException

class InMemoryCustomerRepository(
    private val customers: MutableMap<com.sylvona.koresult.example.model.entity.CustomerId, CustomerEntity>,
) : CustomerRepository {

    override fun findById(id: com.sylvona.koresult.example.model.entity.CustomerId): CustomerEntity? {
        return customers.entries.find { (key) -> key == id }?.value
    }

    override fun save(entity: CustomerEntity) {
        val id = entity.id

        if (id == TIMEOUT_CUSTOMER_ID) {
            throw SQLTimeoutException()
        } else {
            customers[id] = entity
        }
    }

    private companion object {
        private val TIMEOUT_CUSTOMER_ID = com.sylvona.koresult.example.model.entity.CustomerId(42L)
    }
}
