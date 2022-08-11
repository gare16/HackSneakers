package com.apps.hacksneakers.model

class ShoeModel {
    var id :String? = null
    var uid :String? = null
    var name :String? = null
    var info:String? = null
    var img:String? = null
    var type:String? = null
    var price:String? = null

    constructor(){}

    constructor(
        id:String?,
        uid:String?,
        name:String?,
        info:String?,
        img:String?,
        type:String?,
        price:String?
    ){
        this.id = id
        this.uid = uid
        this.name = name
        this.info = info
        this.img = img
        this.type = type
        this.price = price

    }
}