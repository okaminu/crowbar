package lt.tlistas.mobile.number.confirmation.type.entity

import lt.tlistas.core.type.entity.Collaborator
import org.springframework.data.mongodb.core.mapping.DBRef

class Authentication(

        @DBRef(lazy = true)
        var collaborator: Collaborator = Collaborator(),

        var token: String = "",

        var id: String? = collaborator.id
)