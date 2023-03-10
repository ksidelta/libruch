package com.ksidelta.libruch.modules.organisation

import com.ksidelta.libruch.modules.kernel.Party
import com.ksidelta.libruch.modules.organisation.Role.*
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.extensions.kotlin.applyEvent
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.spring.stereotype.Aggregate
import java.util.*

@Aggregate
class OrganisationAggregate() {
    @AggregateIdentifier
    lateinit var organisationId: UUID


    lateinit var organisationName: String
    val members: MutableList<Member> = mutableListOf()

    @CommandHandler
    constructor(createOrganisation: CreateOrganisation) : this() {
        applyEvent(OrganisationCreated(UUID.randomUUID(), createOrganisation.name))
        applyEvent(MemberAdded(createOrganisation.creator, OWNER, createOrganisation.name))
    }

    @CommandHandler
    fun addMember(addMember: AddMember) {
        addMember.run {
            forUser(actor).let {
                when (it.role) {
                    OWNER, ADMIN -> applyEvent(MemberAdded(newMember, role, organisationName))
                    MEMBER -> throw NotEnoughPriviliges()
                }
            }
        }
    }

    @EventSourcingHandler
    fun organisationCreated(organisationCreated: OrganisationCreated) {
        organisationId = organisationCreated.id
        organisationName = organisationCreated.name
    }

    @EventSourcingHandler
    fun memberAdded(memberAdded: MemberAdded) {
        memberAdded.run { members.add(Member(memberAdded.user, memberAdded.role)) }
    }

    private fun forUser(user: Party.User) =
        members.find { it.user == user } ?: throw NoSuchMemberException()
}

data class CreateOrganisation(val name: String, val creator: Party.User)
data class AddMember(val actor: Party.User, val newMember: Party.User, val role: Role)

data class OrganisationCreated(val id: UUID, val name: String)
data class MemberAdded(val user: Party.User, val role: Role, val organisationName: String)

data class Member(val user: Party.User, val role: Role)
enum class Role {
    OWNER, ADMIN, MEMBER
}

class NoSuchMemberException : Exception("You are not in this group")
class NotEnoughPriviliges : Exception("Your Power Level is Too low")
