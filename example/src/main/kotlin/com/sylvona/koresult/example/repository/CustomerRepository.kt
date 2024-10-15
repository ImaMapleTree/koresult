package com.sylvona.koresult.example.repository

import com.sylvona.koresult.example.model.entity.CustomerEntity
import com.sylvona.koresult.example.model.entity.CustomerId

/**
 * A repository that stores a [CustomerEntity] identified by a [CustomerId].
 */
interface CustomerRepository : Repository<CustomerEntity, com.sylvona.koresult.example.model.entity.CustomerId>
