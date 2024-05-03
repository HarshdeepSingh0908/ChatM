package com.harsh.chatm.DataClasses

import com.google.firebase.Timestamp


class Message{
     var message : String ?= null
     var senderId : String ?= null
    var timeStamp : Long ?= 0



     constructor(){}
     constructor(message : String,senderId : String,currentTime : Long){
         this.message = message
         this.senderId = senderId
         this.timeStamp = currentTime

     }


 }

