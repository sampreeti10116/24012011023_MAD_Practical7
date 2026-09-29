package com.example.a24012011023_mad_practical7

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var personAdapter: PersonAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmptyState: TextView
    private lateinit var fabRefresh: ImageButton

    private val apiUrl =
        "https://api.json-generator.com/templates/5rDXHcbgpo93/data"

    private val apiToken =
        "d7wrtfqywyhu7y2bcbsz3cgjpbfisuhnmbibvgvf"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { v, insets ->

            val systemBars =
                insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                )

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                0
            )

            insets
        }

        dbHelper = DatabaseHelper(this)

        initViews()

        setupRecyclerView()

        loadData()
    }

    private fun initViews() {

        recyclerView =
            findViewById(R.id.recyclerView)

        progressBar =
            findViewById(R.id.progressBar)

        tvEmptyState =
            findViewById(R.id.tvEmptyState)

        fabRefresh =
            findViewById(R.id.fabRefresh)

        fabRefresh.setOnClickListener {

            fetchFromApiAndStoreInDb(
                isManualRefresh = true
            )
        }
    }

    private fun setupRecyclerView() {

        personAdapter = PersonAdapter { person, position ->

            deletePersonRecord(
                person.id,
                position
            )
        }

        recyclerView.layoutManager =
            LinearLayoutManager(this)

        recyclerView.adapter =
            personAdapter
    }

    private fun loadData() {

        val existingPersons =
            dbHelper.getAllPersons()

        if (existingPersons.isNotEmpty()) {

            personAdapter.submitList(
                existingPersons
            )

            updateEmptyState()

        } else {

            fetchFromApiAndStoreInDb(
                isManualRefresh = false
            )
        }
    }

    private fun fetchFromApiAndStoreInDb(
        isManualRefresh: Boolean
    ) {

        progressBar.visibility =
            View.VISIBLE

        tvEmptyState.visibility =
            View.GONE

        thread {

            try {

                val data =
                    HttpRequest().makeServiceCall(
                        apiUrl,
                        apiToken
                    )

                val persons =
                    parseJson(data)

                if (persons.isNotEmpty()) {

                    dbHelper.clearAllPersons()

                    dbHelper.insertPersons(persons)
                }

                val dbPersons =
                    dbHelper.getAllPersons()

                runOnUiThread {

                    progressBar.visibility =
                        View.GONE

                    personAdapter.submitList(
                        dbPersons
                    )

                    updateEmptyState()

                    val message =
                        if (isManualRefresh) {
                            "Data fetched and saved to SQLite"
                        } else {
                            "Data loaded successfully"
                        }

                    Toast.makeText(
                        this@MainActivity,
                        message,
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {

                runOnUiThread {

                    progressBar.visibility =
                        View.GONE

                    Toast.makeText(
                        this@MainActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()

                    updateEmptyState()
                }
            }
        }
    }

    private fun parseJson(
        jsonString: String
    ): List<Person> {

        val personList =
            mutableListOf<Person>()

        try {

            val jsonArray =
                JSONArray(jsonString)

            for (i in 0 until jsonArray.length()) {

                val obj =
                    jsonArray.getJSONObject(i)

                val id =
                    obj.optString(
                        "id",
                        ""
                    )

                val emailId =
                    obj.optString(
                        "email",
                        ""
                    )

                val phoneNo =
                    obj.optString(
                        "phone",
                        ""
                    )

                val profile =
                    obj.optJSONObject(
                        "profile"
                    )

                val name =
                    profile?.optString(
                        "name",
                        ""
                    ) ?: ""

                val address =
                    profile?.optString(
                        "address",
                        ""
                    ) ?: ""

                val location =
                    profile?.optJSONObject(
                        "location"
                    )

                val latitude =
                    location?.optDouble(
                        "lat",
                        0.0
                    ) ?: 0.0

                val longitude =
                    location?.optDouble(
                        "long",
                        0.0
                    ) ?: 0.0

                personList.add(
                    Person(
                        id = id,
                        name = name,
                        emailId = emailId,
                        phoneNo = phoneNo,
                        address = address,
                        latitude = latitude,
                        longitude = longitude
                    )
                )
            }

        } catch (e: Exception) {

            e.printStackTrace()
        }

        return personList
    }

    private fun deletePersonRecord(
        id: String,
        position: Int
    ) {

        val success =
            dbHelper.deletePerson(id)

        if (success) {

            personAdapter.removeItemAt(
                position
            )

            updateEmptyState()

            Toast.makeText(
                this,
                getString(R.string.person_deleted),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun updateEmptyState() {

        val count =
            personAdapter.itemCount

        tvEmptyState.visibility =
            if (count == 0) {
                View.VISIBLE
            } else {
                View.GONE
            }

        recyclerView.visibility =
            if (count == 0) {
                View.GONE
            } else {
                View.VISIBLE
            }
    }
}