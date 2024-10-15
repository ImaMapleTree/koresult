package com.sylvona.koresult.example.service

import com.sylvona.koresult.Err
import com.sylvona.koresult.Ok
import com.sylvona.koresult.KoResult
import com.sylvona.koresult.example.model.domain.DomainMessage
import com.sylvona.koresult.example.model.domain.FirstNameRequired
import com.sylvona.koresult.example.model.domain.FirstNameTooLong
import com.sylvona.koresult.example.model.domain.LastNameRequired
import com.sylvona.koresult.example.model.domain.LastNameTooLong
import com.sylvona.koresult.example.model.domain.PersonalName

object PersonalNameParser {

    private const val MAX_LENGTH = 10

    fun parse(first: String?, last: String?): KoResult<PersonalName, DomainMessage> {
        return when {
            first.isNullOrBlank() -> Err(FirstNameRequired)
            last.isNullOrBlank() -> Err(LastNameRequired)
            first.length > MAX_LENGTH -> Err(FirstNameTooLong)
            last.length > MAX_LENGTH -> Err(LastNameTooLong)
            else -> Ok(PersonalName(first, last))
        }
    }
}
