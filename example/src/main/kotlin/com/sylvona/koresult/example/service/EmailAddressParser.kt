package com.sylvona.koresult.example.service

import com.sylvona.koresult.Err
import com.sylvona.koresult.Ok
import com.sylvona.koresult.KoResult
import com.sylvona.koresult.example.model.domain.DomainMessage
import com.sylvona.koresult.example.model.domain.EmailAddress
import com.sylvona.koresult.example.model.domain.EmailInvalid
import com.sylvona.koresult.example.model.domain.EmailRequired
import com.sylvona.koresult.example.model.domain.EmailTooLong

object EmailAddressParser {

    private const val MAX_LENGTH = 20
    private val PATTERN = ".+@.+\\..+".toRegex() // crude validation

    fun parse(address: String?): KoResult<EmailAddress, DomainMessage> {
        return when {
            address.isNullOrBlank() -> Err(EmailRequired)
            address.length > MAX_LENGTH -> Err(EmailTooLong)
            !address.matches(this.PATTERN) -> Err(EmailInvalid)
            else -> Ok(EmailAddress(address))
        }
    }
}
