package fgori.ft_hanguots

class Message {

    val direction: MsgDir
    val sender: Number
    val content: String
    val timeStamp: Long


    constructor(inOrOut: MsgDir, content: String, timeStamp: Long) {
        this.direction = inOrOut;
        this.content = content;
        this.timeStamp = timeStamp;
        this.sender = 0;
    }
    constructor(inOrOut: MsgDir, content: String, sender: Number,  timeStamp: Long) {
        this.direction = inOrOut;
        this.content = content;
        this.timeStamp = timeStamp;
        this.sender = sender;
    }


}