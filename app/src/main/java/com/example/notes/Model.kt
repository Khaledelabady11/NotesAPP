package com.example.notes

class Note{
    var id: String?=null
    var title:String?=null
    var note:String?=null
    var time: String?=null
    var check: Boolean=false
    constructor(){

    }
    constructor(id: String?, title: String?, note: String?, time: String?) {
        this.id = id
        this.title = title
        this.note = note
        this.time = time

    }


}