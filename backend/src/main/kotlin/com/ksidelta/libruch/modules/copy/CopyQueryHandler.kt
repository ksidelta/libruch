package com.ksidelta.libruch.modules.copy

import com.ksidelta.libruch.modules.kernel.Party
import org.axonframework.queryhandling.QueryHandler
import org.springframework.stereotype.Service
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

@Service
class CopyQueryHandler(val copyReadModelRepository: CopyReadModelRepository) {
    @QueryHandler
    fun query(queryAllCopies: QueryAllCopies) =
        copyReadModelRepository.findAll().toList()

    @QueryHandler
    fun query(queryByOwner: QueryByOwner) =
        copyReadModelRepository.findAllByOwner(owner = queryByOwner.owner).toList()

    @QueryHandler
    fun query(queryByOwners: QueryByOwners) =
        copyReadModelRepository.findAllByOwnerIn(owner = queryByOwners.owners).toList()

    @QueryHandler
    fun query(queryByOwners: QueryByOrganisationAndTitleFragment) =
        copyReadModelRepository.findAllByOrganisationAndTitleContains(
            organisation = queryByOwners.organisationId,
            titleFragment = queryByOwners.titleFragment
        ).toList()

    @OptIn(ExperimentalStdlibApi::class)
    @QueryHandler
    fun query(queryByIsbn: QueryByIsbn) =
        copyReadModelRepository.findByIsbn(isbn = queryByIsbn.isbn).getOrNull()
}

class QueryAllCopies() {}
class QueryByOwner(val owner: Party) {}
class QueryByOwners(val owners: Collection<Party>) {}
class QueryByIsbn(val isbn: String) {}
class QueryByOrganisationAndTitleFragment(val organisationId: UUID, val titleFragment: String) {}
