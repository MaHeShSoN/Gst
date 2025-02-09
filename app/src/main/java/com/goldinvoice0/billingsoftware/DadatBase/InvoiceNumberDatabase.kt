package com.goldinvoice0.billingsoftware.DadatBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.goldinvoice0.billingsoftware.Dao.InvoiceNumberDao
import com.goldinvoice0.billingsoftware.Model.InvoiceNumber

@Database(entities = [InvoiceNumber::class], version = 1, exportSchema = false)
abstract class InvoiceNumberDatabase : RoomDatabase() {
    abstract fun invoiceNumberDao(): InvoiceNumberDao

    companion object {
        @Volatile
        private var INSTANCE: InvoiceNumberDatabase? = null

        fun getDatabase(context: Context): InvoiceNumberDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    InvoiceNumberDatabase::class.java,
                    "invoice_number_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}