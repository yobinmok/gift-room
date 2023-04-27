package com.example.capstone_giftcon.dto

data class CommentData(
     val id:String,
     val author: String,
     val text: String,
     val created_at: String,
) {
     val my_comment: Boolean = false
     val author_img: String = ""
//    constructor(id: String, author:String, text: String, created_at: String) : this(id, author, text, created_at, )
}

//data class CommentData(// 댓글은 아이디로 이어야되겠지?
//    @field: SerializedName("id") // 게시글 아이디
//    var id: String, var author:String, text:String, created_at:String)
//{
//
//    @SerializedName("author")// 작성자
//    var author: String
//
//    @SerializedName("text")// 댓글 내용
//    var text:String
//
//    @SerializedName("created_at")// 작성시간
//    var created_at: String
//
//    @SerializedName("my_comment") // 내 댓글인지
//    val my_comment:Boolean? = null
//
//    @SerializedName("author_image")  // 작성자 프사
//    val author_img:String? = null
//
//    init {
//        this.id = id
//        this.author = author
//        this.text = text
//        this.created_at = created_at
//    }
//}

//public class CommentData {
//
//    @SerializedName("id") // 게시글 아이디
//    String post_id;
//
//    @SerializedName("author") // 작성자
//    String author;
//
//    @SerializedName("text") // 댓글내용
//    String text;
//
//    @SerializedName("created_at") // 작성시간
//    String created_at;
//
//    @SerializedName("my_comment") // 작성시간
//    Boolean my_comment;
//
//    @SerializedName("author_image") // 작성시간
//    String author_img;
//
//    // 댓글은 아이디로 이어야되겠지?
//
//    public String getPost_id(){ return post_id; }
//    public String getAuthor() { return author; }
//    public String getText() { return text; }
//    public String getCreated_at() { return created_at; }
//    public Boolean getMy_comment() { return my_comment; }
//    public String getAuthor_img() { return author_img; }
//    public CommentData(String post_id, String author, String text, String created_at) {
//        this.post_id = post_id;
//        this.author = author;
//        this.text = text;
//        this.created_at = created_at;
//    }
//}