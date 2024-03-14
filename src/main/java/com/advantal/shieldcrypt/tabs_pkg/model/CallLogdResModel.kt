package com.advantal.shieldcrypt.tabs_pkg.model

import java.io.Serializable

/**
 * Created by Sonam on 18-10-2022 14:38.
 */
class CallLogdResModel():Serializable {

    var content=listOf<ContentDataModel>()
//var pageable:PageableDataModel?=null
//
//class PageableDataModel {
//var pageNumber:Int=0
//var pageSize:Int=0
//}
class ContentDataModel():Serializable {
    var calldate: String? = ""
    var src: String? = ""
    var dst: String? = ""
    var duration: String? = ""
    var disposition: String? = ""
    var cnam: String? = ""
    var dst_cnam: String? = ""
}


}