package com.nawaz.bookhub.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")                //by default tableName is same as Class name here, BookEntity if tableName is not defined
data class BookEntity(
    @PrimaryKey val book_id: Int,
    @ColumnInfo(name = "book_name") val bookName: String,     //Note: either u can give @ColumnInfo or directly provide column name using '_'
    @ColumnInfo(name = "book_author") val bookAuthor: String,                                                     //ex- bookName as book_name
    @ColumnInfo(name = "book_rating") val bookRating: String,
    @ColumnInfo(name = "book_price") val bookPrice: String,
    @ColumnInfo(name = "book_image") val bookImage: String,
    @ColumnInfo(name = "book_desc") val bookDesc: String
)