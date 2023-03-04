package com.ksidelta.libruch.modules.kernel

import java.util.UUID

// This should be a party archetype, but it sucks :)
sealed class Party(val partyId: UUID) {

    data class User(val id: UUID): Party(partyId = id)
    data class Organisation(val id: UUID) : Party(partyId = id)
}
