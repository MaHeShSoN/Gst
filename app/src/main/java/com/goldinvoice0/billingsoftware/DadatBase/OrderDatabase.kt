package com.goldinvoice0.billingsoftware.DadatBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.goldinvoice0.billingsoftware.Dao.OrderDao
import com.goldinvoice0.billingsoftware.Model.Order
import com.goldinvoice0.billingsoftware.convertes.JewelryItemListConverter
import com.goldinvoice0.billingsoftware.convertes.PaymentListConverter

// Database
@Database(entities = [Order::class], version = 1, exportSchema = false)
@TypeConverters(PaymentListConverter::class, JewelryItemListConverter::class) // Important!
abstract class OrderDatabase : RoomDatabase() {
    abstract fun orderDao(): OrderDao

    companion object {
        @Volatile
        private var INSTANCE: OrderDatabase? = null

        fun getDatabase(context: Context): OrderDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    OrderDatabase::class.java,
                    "order_database"
                ).build() // No fallbackToDestructiveMigration() in production!
                INSTANCE = instance
                instance
            }
        }
    }

}
