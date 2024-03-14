package com.advantal.shieldcrypt.utils_pkg

/**
 * Created by Sonam on 29-06-2022 17:48.
 */
open class BaseFileModel{
    var id = Int
    var name = String
    var path = String




    constructor()

    constructor(name: String.Companion) {
        this.name = name
    }

    constructor(id: Int.Companion, path: String.Companion) {
        this.id = id
        this.path = path
    }



}
//    (var id:Int,val name:String,val path:String)