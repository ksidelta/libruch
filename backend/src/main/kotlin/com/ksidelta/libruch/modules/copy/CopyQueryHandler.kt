package com.ksidelta.libruch.modules.copy

import com.ksidelta.libruch.modules.kernel.Party
import org.axonframework.queryhandling.QueryHandler
import org.springframework.stereotype.Service

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

}

class QueryAllCopies() {}
class QueryByOwner(val owner: Party) {}
class QueryByOwners(val owners: Collection<Party>) {}
