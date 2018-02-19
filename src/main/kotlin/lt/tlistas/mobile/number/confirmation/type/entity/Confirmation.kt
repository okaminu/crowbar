package lt.tlistas.mobile.number.confirmation.type.entity

import lt.tlistas.core.type.entity.Collaborator
import org.springframework.data.mongodb.core.mapping.DBRef

class Confirmation(

        @DBRef(lazy = true)
        var collaborator: Collaborator = Collaborator(),

        var code: String = "",

        var id: String? = collaborator.id
)