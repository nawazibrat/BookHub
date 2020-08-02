package com.nawaz.bookhub.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.nawaz.bookhub.R
import com.nawaz.bookhub.database.BookDatabase
import com.nawaz.bookhub.database.BookEntity
import com.nawaz.bookhub.util.ConnectionManager
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject

class DescriptionActivity : AppCompatActivity() {

    lateinit var txtBookName: TextView
    lateinit var txtBookAuthor: TextView
    lateinit var txtBookPrice: TextView
    lateinit var txtBookRating: TextView
    lateinit var txtBookDesc: TextView
    lateinit var imgBookImage: ImageView
    lateinit var btnAddToFav: Button
    lateinit var progressBar: ProgressBar
    lateinit var progreesLayout: RelativeLayout
    lateinit var toolbar: Toolbar

    var bookId: String? = "100"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description)

        txtBookName = findViewById(R.id.txtBookName)
        txtBookAuthor = findViewById(R.id.txtBookAuthor)
        txtBookPrice = findViewById(R.id.txtBookPrice)
        txtBookRating = findViewById(R.id.txtBookRating)
        txtBookDesc = findViewById(R.id.txtBookDesc)
        imgBookImage = findViewById(R.id.imgBookImage)
        btnAddToFav = findViewById(R.id.btnAddToFav)
        progreesLayout = findViewById(R.id.progressLayout)
        progreesLayout.visibility = View.VISIBLE
        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.VISIBLE
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Book Details"

        if (intent != null) {
            bookId = intent.getStringExtra("book_id")
            if (intent.getStringExtra("activity_name") == "fav recycler") {
                bookId =intent.getIntExtra("bookID",100).toString()
            }
        } else {
            finish()
            Toast.makeText(this, "Some unexpected error occurred", Toast.LENGTH_LONG).show()
        }

        if (bookId == "100") {
            finish()
            Toast.makeText(this, "Some unexpected error occurred book id", Toast.LENGTH_LONG).show()
        }

        val queue = Volley.newRequestQueue(this)
        val url = "http://13.235.250.119/v1/book/get_book/"
        val jsonParams = JSONObject()
        jsonParams.put("book_id", bookId)

        if (ConnectionManager().checkConnectivity(this)) {

            val jsonObjectRequest = object :
                JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {

                    try {

                        val success = it.getBoolean("success")
                        if (success) {
                            val bookJsonObject = it.getJSONObject("book_data")
                            progreesLayout.visibility = View.GONE
                            var bookImageUrl = bookJsonObject.getString("image")
                            Picasso.get().load(bookJsonObject.getString("image"))
                                .error(R.drawable.default_book_cover).into(imgBookImage)
                            txtBookName.text = bookJsonObject.getString("name")
                            txtBookAuthor.text = bookJsonObject.getString("author")
                            txtBookPrice.text = bookJsonObject.getString("price")
                            txtBookRating.text = bookJsonObject.getString("rating")
                            txtBookDesc.text = bookJsonObject.getString("description")

                            val bookEntity = BookEntity(
                                bookId?.toInt() as Int,
                                txtBookName.text.toString(),
                                txtBookAuthor.text.toString(),
                                txtBookPrice.text.toString(),
                                txtBookRating.text.toString(),
                                bookImageUrl,
                                txtBookDesc.text.toString()

                            )

                            val checkFav = DBAsyncTask(applicationContext, bookEntity, 1).execute()
                            val isFav = checkFav.get()
                            if (isFav) {
                                btnAddToFav.text = "Remove from Fvaoutite"
                                val favColor =
                                    ContextCompat.getColor(applicationContext, R.color.colorBlue)
                                btnAddToFav.setBackgroundColor(favColor)
                                btnAddToFav.setTextColor(Color.parseColor("#FFFFFF"))
                            } else {
                                btnAddToFav.text = "Add to Fvaoutite"
                                val noFavColor =
                                    ContextCompat.getColor(applicationContext, R.color.colorPrimary)
                                btnAddToFav.setBackgroundColor(noFavColor)
                            }

                            btnAddToFav.setOnClickListener {
                                if (DBAsyncTask(applicationContext, bookEntity, 1).execute()
                                        .get()
                                ) {
                                    val async =
                                        DBAsyncTask(applicationContext, bookEntity, 3).execute()
                                    val result = async.get()
                                    if (result) {
                                        Toast.makeText(
                                            this,
                                            "Book removed from Favurites",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        btnAddToFav.text = "Add to Fvaoutite"
                                        val noFavColor = ContextCompat.getColor(
                                            applicationContext,
                                            R.color.colorPrimary
                                        )
                                        btnAddToFav.setBackgroundColor(noFavColor)
                                    } else {
                                        Toast.makeText(
                                            this,
                                            "Database remove error",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                } else {
                                    val async =
                                        DBAsyncTask(applicationContext, bookEntity, 2).execute()
                                    val result = async.get()
                                    if (result) {
                                        Toast.makeText(
                                            this,
                                            "Book added to Favurites",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        btnAddToFav.text = "Remove from Fvaoutite"
                                        val favColor = ContextCompat.getColor(
                                            applicationContext,
                                            R.color.colorBlue
                                        )
                                        btnAddToFav.setBackgroundColor(favColor)
                                        btnAddToFav.setTextColor(Color.parseColor("#FFFFFF"))
                                    } else {
                                        Toast.makeText(
                                            this,
                                            "Database insert error",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }

                        } else {
                            Toast.makeText(
                                this,
                                "Some unexpected api error occurred",
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }

                    } catch (e: JSONException) {
                        Toast.makeText(
                            this,
                            "Some unexpected json error occurred",
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }
                }, Response.ErrorListener {
                    Toast.makeText(this, "Some unexpected volley error occurred", Toast.LENGTH_LONG)
                        .show()
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "f0d9a5efb8d9d8"
                    return headers
                }
            }
            queue.add(jsonObjectRequest)
        } else {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection not Found")
            dialog.setPositiveButton("open settings") { text, listner ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                finish()
            }
            dialog.setNegativeButton("exit") { text, listner ->
                ActivityCompat.finishAffinity(this)
            }
            dialog.create()
            dialog.show()
        }

    }

    class DBAsyncTask(val context: Context, val bookEntity: BookEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {

        val db = Room.databaseBuilder(context, BookDatabase::class.java, "books-db").build()

        override fun doInBackground(vararg p0: Void?): Boolean {
            when (mode) {
                1 -> {
                    val book: BookEntity? = db.bookDao().getBookById(bookEntity.book_id.toString())
                    db.close()
                    return book != null

                }

                2 -> {
                    db.bookDao().insertBook(bookEntity)
                    db.close()
                    return true

                }

                3 -> {
                    db.bookDao().deleteBook(bookEntity)
                    db.close()
                    return true

                }
            }
            return false
        }
    }
}