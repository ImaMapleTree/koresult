package com.sylvona.koresult.example.model.entity

/**
 * Represents an [Entity](https://docs.oracle.com/cd/E17277_02/html/collections/tutorial/Entity.html)
 * mapped to a table in a database.
 */
data class CustomerEntity(
    val id: com.sylvona.koresult.example.model.entity.CustomerId,
    val firstName: String,
    val lastName: String,
    val email: String,
)
