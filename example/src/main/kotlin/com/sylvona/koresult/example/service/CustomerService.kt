package com.sylvona.koresult.example.service

import com.sylvona.koresult.Err
import com.sylvona.koresult.Ok
import com.sylvona.koresult.KoResult
import com.sylvona.koresult.andThen
import com.sylvona.koresult.example.model.domain.Created
import com.sylvona.koresult.example.model.domain.Customer
import com.sylvona.koresult.example.model.domain.CustomerIdMustBePositive
import com.sylvona.koresult.example.model.domain.CustomerNotFound
import com.sylvona.koresult.example.model.domain.CustomerRequired
import com.sylvona.koresult.example.model.domain.DatabaseError
import com.sylvona.koresult.example.model.domain.DatabaseTimeout
import com.sylvona.koresult.example.model.domain.DomainMessage
import com.sylvona.koresult.example.model.domain.EmailAddressChanged
import com.sylvona.koresult.example.model.domain.Event
import com.sylvona.koresult.example.model.domain.FirstNameChanged
import com.sylvona.koresult.example.model.domain.LastNameChanged
import com.sylvona.koresult.example.model.dto.CustomerDto
import com.sylvona.koresult.example.model.entity.CustomerEntity
import com.sylvona.koresult.example.model.entity.CustomerId
import com.sylvona.koresult.example.repository.CustomerRepository
import com.sylvona.koresult.getOrElse
import com.sylvona.koresult.map
import com.sylvona.koresult.mapError
import com.sylvona.koresult.runCatching
import com.sylvona.koresult.toResultOr
import com.sylvona.koresult.zip
import java.sql.SQLTimeoutException

class CustomerService(
    private val repository: CustomerRepository,
) {

    fun getById(id: Long): KoResult<CustomerDto, DomainMessage> {
        return parseCustomerId(id)
            .andThen(::findById)
            .map(::entityToDto)
    }

    fun save(id: Long, dto: CustomerDto): KoResult<Event?, DomainMessage> {
        return parseCustomerId(id)
            .andThen { upsert(it, dto) }
    }

    private fun parseCustomerId(id: Long?) = when {
        id == null -> Err(CustomerRequired)
        id < 1 -> Err(CustomerIdMustBePositive)
        else -> Ok(com.sylvona.koresult.example.model.entity.CustomerId(id))
    }

    private fun entityToDto(entity: CustomerEntity): CustomerDto {
        return CustomerDto(
            firstName = entity.firstName,
            lastName = entity.lastName,
            email = entity.email
        )
    }

    private fun findById(id: com.sylvona.koresult.example.model.entity.CustomerId): KoResult<CustomerEntity, CustomerNotFound> {
        return repository.findById(id)
            .toResultOr { CustomerNotFound }
    }

    private fun upsert(id: com.sylvona.koresult.example.model.entity.CustomerId, dto: CustomerDto): KoResult<Event?, DomainMessage> {
        val existingCustomer = repository.findById(id)

        return if (existingCustomer != null) {
            update(existingCustomer, dto)
        } else {
            insert(id, dto)
        }
    }

    private fun update(entity: CustomerEntity, dto: CustomerDto): KoResult<Event?, DomainMessage> {
        val validated = validate(dto).getOrElse { return Err(it) }

        val updated = entity.copy(
            firstName = validated.name.first,
            lastName = validated.name.last,
            email = validated.email.address
        )

        return runCatching { repository.save(updated) }
            .map { compare(entity, updated) }
            .mapError(::exceptionToDomainMessage)
    }

    private fun insert(id: com.sylvona.koresult.example.model.entity.CustomerId, dto: CustomerDto): KoResult<Created, DomainMessage> {
        val entity = createEntity(id, dto).getOrElse { return Err(it) }

        return runCatching { repository.save(entity) }
            .map { Created }
            .mapError(::exceptionToDomainMessage)
    }

    private fun validate(dto: CustomerDto): KoResult<Customer, DomainMessage> {
        return zip(
            { PersonalNameParser.parse(dto.firstName, dto.lastName) },
            { EmailAddressParser.parse(dto.email) },
            ::Customer
        )
    }

    private fun createEntity(id: com.sylvona.koresult.example.model.entity.CustomerId, dto: CustomerDto): KoResult<CustomerEntity, DomainMessage> {
        return zip(
            { PersonalNameParser.parse(dto.firstName, dto.lastName) },
            { EmailAddressParser.parse(dto.email) },
            { (first, last), (address) -> CustomerEntity(id, first, last, address) }
        )
    }

    private fun exceptionToDomainMessage(t: Throwable) = when (t) {
        is SQLTimeoutException -> DatabaseTimeout
        else -> DatabaseError(t.message)
    }

    private fun compare(old: CustomerEntity, new: CustomerEntity): Event? {
        return when {
            new.firstName != old.firstName -> FirstNameChanged(old.firstName, new.firstName)
            new.lastName != old.lastName -> LastNameChanged(old.lastName, new.lastName)
            new.email != old.email -> EmailAddressChanged(old.email, new.email)
            else -> null
        }
    }
}
