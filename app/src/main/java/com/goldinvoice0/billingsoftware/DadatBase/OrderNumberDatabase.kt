package com.goldinvoice0.billingsoftware.DadatBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.goldinvoice0.billingsoftware.Dao.OrderNumberDao
import com.goldinvoice0.billingsoftware.Model.OrderNumber


@Database(entities = [OrderNumber::class], version = 1, exportSchema = false)
abstract class OrderNumberDatabase : RoomDatabase() {
    abstract fun orderNumberDao(): OrderNumberDao

    companion object {
        @Volatile
        private var INSTANCE: OrderNumberDatabase? = null

        fun getDatabase(context: Context): OrderNumberDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    OrderNumberDatabase::class.java,
                    "order_number_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}