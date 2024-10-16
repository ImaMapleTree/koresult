package com.sylvona.koresult.example

import com.fasterxml.jackson.databind.SerializationFeature
import com.sylvona.koresult.KoResult
import com.sylvona.koresult.andThen
import com.sylvona.koresult.example.model.domain.Created
import com.sylvona.koresult.example.model.domain.CustomerIdMustBePositive
import com.sylvona.koresult.example.model.domain.CustomerNotFound
import com.sylvona.koresult.example.model.domain.CustomerRequired
import com.sylvona.koresult.example.model.domain.DatabaseError
import com.sylvona.koresult.example.model.domain.DatabaseTimeout
import com.sylvona.koresult.example.model.domain.DomainMessage
import com.sylvona.koresult.example.model.domain.EmailAddressChanged
import com.sylvona.koresult.example.model.domain.EmailInvalid
import com.sylvona.koresult.example.model.domain.EmailRequired
import com.sylvona.koresult.example.model.domain.EmailTooLong
import com.sylvona.koresult.example.model.domain.Event
import com.sylvona.koresult.example.model.domain.FirstNameChanged
import com.sylvona.koresult.example.model.domain.FirstNameRequired
import com.sylvona.koresult.example.model.domain.FirstNameTooLong
import com.sylvona.koresult.example.model.domain.LastNameChanged
import com.sylvona.koresult.example.model.domain.LastNameRequired
import com.sylvona.koresult.example.model.domain.LastNameTooLong
import com.sylvona.koresult.example.model.domain.SqlCustomerInvalid
import com.sylvona.koresult.example.model.dto.CustomerDto
import com.sylvona.koresult.example.model.entity.CustomerEntity
import com.sylvona.koresult.example.model.entity.CustomerId
import com.sylvona.koresult.example.repository.InMemoryCustomerRepository
import com.sylvona.koresult.example.service.CustomerService
import com.sylvona.koresult.mapBoth
import com.sylvona.koresult.toResultOr
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing

fun main() {
    embeddedServer(
        factory = Netty,
        port = 8080,
        host = "0.0.0.0",
        module = Application::exampleModule
    ).start(wait = true)
}

fun Application.exampleModule() {
    configureSerialization()
    configureRouting()
}

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }
}

fun Application.configureRouting() {
    val customers = setOf(
        CustomerEntity(com.sylvona.koresult.example.model.entity.CustomerId(1L), "Michael", "Bull", "michael@example.com"),
        CustomerEntity(com.sylvona.koresult.example.model.entity.CustomerId(2L), "Kevin", "Herron", "kevin@example.com"),
        CustomerEntity(com.sylvona.koresult.example.model.entity.CustomerId(3L), "Markus", "Padourek", "markus@example.com"),
        CustomerEntity(com.sylvona.koresult.example.model.entity.CustomerId(4L), "Tristan", "Hamilton", "tristan@example.com"),
    )

    val customersById = customers.associateBy(CustomerEntity::id).toMutableMap()
    val customerRepository = InMemoryCustomerRepository(customersById)
    val customerService = CustomerService(customerRepository)

    routing {
        get("/customers/{id}") {
            val (status, message) = call.parameters
                .readId()
                .andThen(customerService::getById)
                .mapBoth(::customerToResponse, ::messageToResponse)

            call.respond(status, message)
        }

        post("/customers/{id}") {
            val (status, message) = call.parameters
                .readId()
                .andThen { customerService.save(it, call.receive()) }
                .mapBoth(::eventToResponse, ::messageToResponse)

            if (message != null) {
                call.respond(status, message)
            } else {
                call.respond(status)
            }
        }
    }
}

private fun Parameters.readId(): KoResult<Long, DomainMessage> {
    return get("id")?.toLongOrNull().toResultOr { CustomerRequired }
}

private fun customerToResponse(customer: CustomerDto) = HttpStatusCode.OK to customer

private fun messageToResponse(message: DomainMessage) = when (message) {
    CustomerRequired,
    CustomerIdMustBePositive,
    FirstNameRequired,
    FirstNameTooLong,
    LastNameRequired,
    LastNameTooLong,
    EmailRequired,
    EmailTooLong,
    EmailInvalid,
    ->
        HttpStatusCode.BadRequest to "There is an error in your request"

// exposed errors
    CustomerNotFound ->
        HttpStatusCode.NotFound to "Unknown customer"

// internal errors
    SqlCustomerInvalid,
    DatabaseTimeout,
    is DatabaseError,
    ->
        HttpStatusCode.InternalServerError to "Internal server error occurred"
}

private fun eventToResponse(event: Event?) = when (event) {
    null ->
        HttpStatusCode.NotModified to null

    Created ->
        HttpStatusCode.Created to "Customer created"

    is FirstNameChanged ->
        HttpStatusCode.OK to "First name changed from ${event.old} to ${event.new}"

    is LastNameChanged ->
        HttpStatusCode.OK to "Last name changed from ${event.old} to ${event.new}"

    is EmailAddressChanged ->
        HttpStatusCode.OK to "Email address changed from ${event.old} to ${event.new}"
}
