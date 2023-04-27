package com.example.capstone_giftcon.ui.viewModel

import androidx.lifecycle.ViewModel
import com.example.capstone_giftcon.repository.BoardRepository
import com.google.gson.JsonObject

class BoardViewModel: ViewModel() {

    private val repository: BoardRepository by lazy {
        BoardRepository()
    }

    val setBoardResult get() = repository.setBoardResult
    val boardAddResult get() = repository.boardAddResult
    val boardDetailResult get() = repository.boardDetailResult
    val boardDeleteResult get() = repository.boardDeleteResult
    val boardUpdateResult get() = repository.boardUpdateResult
    val wishAddResult get() = repository.wishAddResult
    val wishDeleteResult get() = repository.wishDeleteResult

    val boardList get() = repository.boardList
    val specificBoardList get() = repository.specificBoardList
    val boardDetail get() = repository.boardDetail

    fun setSpecificBoard(wishData: Boolean, postData: Boolean){
        repository.setSpecificBoard(wishData, postData)
    }

    fun setBoard(){
        repository.setBoard()
    }

    fun boardAdd(url: String, title: String, content: String){
        repository.boardAdd(url, title, content)
    }

    fun boardDetail(id: Int){
        repository.boardDetail(id)
    }

    fun boardDelete(postId: Int){
        repository.boardDelete(postId)
    }

    fun boardUpdate(postId: Int, data: JsonObject){
        repository.boardUpdate(postId, data)
    }

    fun wishListAdd(postId: Int){
        repository.wishListAdd(postId)
    }

    fun wishListDelete(postId: Int) {
        repository.wishListDelete(postId)
    }
}
