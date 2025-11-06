package fgori.ft_hanguots

class Message {

    val direction: MsgDir
    val content: String
    val timeStamp: Long
    val other: Long


    constructor(inOrOut: MsgDir, content: String, other: Long) {
        this.direction = inOrOut;
        this.content = content;
        this.timeStamp = System.currentTimeMillis();
        this.other = other;
    }

    constructor(inOrOut: MsgDir, content: String, other: Long, timeStamp: Long) {
        this.direction = inOrOut;
        this.content = content;
        this.timeStamp = timeStamp;
        this.other = other;
    }
}