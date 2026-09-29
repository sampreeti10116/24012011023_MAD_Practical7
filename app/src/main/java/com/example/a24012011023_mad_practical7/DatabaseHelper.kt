package com.example.a24012011023_mad_practical7

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {

        private const val DATABASE_NAME = "persons.db"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {

        db.execSQL(PersonDbTableData.CREATE_TABLE)
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {

        db.execSQL(
            "DROP TABLE IF EXISTS ${PersonDbTableData.TABLE_NAME}"
        )

        onCreate(db)
    }

    fun insertPerson(person: Person): Long {

        val db = writableDatabase

        val values = ContentValues().apply {

            put(PersonDbTableData.COLUMN_ID, person.id)
            put(PersonDbTableData.COLUMN_PERSON_NAME, person.name)
            put(PersonDbTableData.COLUMN_PERSON_EMAIL_ID, person.emailId)
            put(PersonDbTableData.COLUMN_PERSON_PHONE_NO, person.phoneNo)
            put(PersonDbTableData.COLUMN_PERSON_ADDRESS, person.address)
            put(PersonDbTableData.COLUMN_PERSON_GPS_LAT, person.latitude)
            put(PersonDbTableData.COLUMN_PERSON_GPS_LONG, person.longitude)
        }

        return db.insert(
            PersonDbTableData.TABLE_NAME,
            null,
            values
        )
    }

    fun insertPersons(persons: List<Person>) {

        val db = writableDatabase

        db.beginTransaction()

        try {

            for (person in persons) {

                val values = ContentValues().apply {

                    put(PersonDbTableData.COLUMN_ID, person.id)
                    put(PersonDbTableData.COLUMN_PERSON_NAME, person.name)
                    put(PersonDbTableData.COLUMN_PERSON_EMAIL_ID, person.emailId)
                    put(PersonDbTableData.COLUMN_PERSON_PHONE_NO, person.phoneNo)
                    put(PersonDbTableData.COLUMN_PERSON_ADDRESS, person.address)
                    put(PersonDbTableData.COLUMN_PERSON_GPS_LAT, person.latitude)
                    put(PersonDbTableData.COLUMN_PERSON_GPS_LONG, person.longitude)
                }

                db.insert(
                    PersonDbTableData.TABLE_NAME,
                    null,
                    values
                )
            }

            db.setTransactionSuccessful()

        } finally {

            db.endTransaction()
        }
    }

    fun getAllPersons(): List<Person> {

        val personList = mutableListOf<Person>()

        val db = readableDatabase

        val cursor: Cursor = db.query(
            PersonDbTableData.TABLE_NAME,
            arrayOf(
                PersonDbTableData.COLUMN_ID,
                PersonDbTableData.COLUMN_PERSON_NAME,
                PersonDbTableData.COLUMN_PERSON_EMAIL_ID,
                PersonDbTableData.COLUMN_PERSON_PHONE_NO,
                PersonDbTableData.COLUMN_PERSON_ADDRESS,
                PersonDbTableData.COLUMN_PERSON_GPS_LAT,
                PersonDbTableData.COLUMN_PERSON_GPS_LONG
            ),
            null,
            null,
            null,
            null,
            PersonDbTableData.COLUMN_ID + " ASC"
        )

        cursor.use {

            if (it.moveToFirst()) {

                val idIndex =
                    it.getColumnIndexOrThrow(
                        PersonDbTableData.COLUMN_ID
                    )

                val nameIndex =
                    it.getColumnIndexOrThrow(
                        PersonDbTableData.COLUMN_PERSON_NAME
                    )

                val emailIndex =
                    it.getColumnIndexOrThrow(
                        PersonDbTableData.COLUMN_PERSON_EMAIL_ID
                    )

                val phoneIndex =
                    it.getColumnIndexOrThrow(
                        PersonDbTableData.COLUMN_PERSON_PHONE_NO
                    )

                val addressIndex =
                    it.getColumnIndexOrThrow(
                        PersonDbTableData.COLUMN_PERSON_ADDRESS
                    )

                val latitudeIndex =
                    it.getColumnIndexOrThrow(
                        PersonDbTableData.COLUMN_PERSON_GPS_LAT
                    )

                val longitudeIndex =
                    it.getColumnIndexOrThrow(
                        PersonDbTableData.COLUMN_PERSON_GPS_LONG
                    )

                do {

                    val id =
                        it.getString(idIndex) ?: ""

                    val name =
                        it.getString(nameIndex) ?: ""

                    val emailId =
                        it.getString(emailIndex) ?: ""

                    val phoneNo =
                        it.getString(phoneIndex) ?: ""

                    val address =
                        it.getString(addressIndex) ?: ""

                    val latitude =
                        it.getDouble(latitudeIndex)

                    val longitude =
                        it.getDouble(longitudeIndex)

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

                } while (it.moveToNext())
            }
        }

        return personList
    }

    fun deletePerson(id: String): Boolean {

        val db = writableDatabase

        val rowsDeleted = db.delete(
            PersonDbTableData.TABLE_NAME,
            PersonDbTableData.COLUMN_ID + " = ?",
            arrayOf(id)
        )

        return rowsDeleted > 0
    }

    fun clearAllPersons() {

        val db = writableDatabase

        db.delete(
            PersonDbTableData.TABLE_NAME,
            null,
            null
        )
    }

    fun getPersonCount(): Int {

        val db = readableDatabase

        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM ${PersonDbTableData.TABLE_NAME}",
            null
        )

        var count = 0

        cursor.use {

            if (it.moveToFirst()) {
                count = it.getInt(0)
            }
        }

        return count
    }
}