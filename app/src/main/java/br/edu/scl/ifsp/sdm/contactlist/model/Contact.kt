package br.edu.scl.ifsp.sdm.contactlist.model

import android.os.Parcelable
import br.edu.scl.ifsp.sdm.contactlist.model.Constant.INVALID_CONTACT_ID
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Contact(
    var id: Int? = INVALID_CONTACT_ID,
    var name: String = "",
    var address: String = "",
    var phone: String = "",
    var email: String = ""
): Parcelable
