package com.nawaz.bookhub.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.nawaz.bookhub.R
import com.nawaz.bookhub.adapter.DashboardRecyclerAdapter
import com.nawaz.bookhub.model.Book
import com.nawaz.bookhub.util.ConnectionManager
import org.json.JSONException

class DashboardFragment : Fragment() {

    lateinit var recyclerDashboard: RecyclerView
    lateinit var layoutManager: LinearLayoutManager
    lateinit var recyclerAdapter: DashboardRecyclerAdapter
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar

    var bookInfoList = ArrayList<Book>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)
        progressLayout.visibility = View.VISIBLE

        recyclerDashboard = view!!.findViewById(R.id.recyclerDashboard)
        layoutManager = LinearLayoutManager(activity)

        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v1/book/fetch_books/"

        if (ConnectionManager().checkConnectivity(activity as Context)) {
            
            val jsonObjectRequest = object : JsonObjectRequest(Request.Method.GET, url, null,
                Response.Listener {

                    try {
                        progressLayout.visibility = View.GONE
                        val success = it.getBoolean("success")

                        if (success) {
                            val data = it.getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val bookJsonObject = data.getJSONObject(i)
                                val bookObject = Book(
                                    bookJsonObject.getString("book_id"),
                                    bookJsonObject.getString("name"),
                                    bookJsonObject.getString("author"),
                                    bookJsonObject.getString("rating"),
                                    bookJsonObject.getString("price"),
                                    bookJsonObject.getString("image")
                                )
                                bookInfoList.add(bookObject)

                                recyclerAdapter =
                                    DashboardRecyclerAdapter(activity as Context, bookInfoList)
                                recyclerDashboard.adapter = recyclerAdapter
                                recyclerDashboard.layoutManager = layoutManager
                            }
                        } else {
                            Toast.makeText(
                                activity as Context,
                                "Something went wrong!!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } catch (e: JSONException) {
                        Toast.makeText(
                            activity as Context,
                            "Something went wrong!!!",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                }, Response.ErrorListener {
                    if (activity!=null){
                        Toast.makeText(
                            activity as Context,
                            "Some error occurred !!!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
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

            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("No Internet Connection")
            dialog.setPositiveButton("Open Settings") { text, listner ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit") { text, listner ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }

        return view                                                                 //returning view
    }
}