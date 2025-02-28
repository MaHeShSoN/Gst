package com.goldinvoice0.billingsoftware.DadatBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.goldinvoice0.billingsoftware.Dao.BillsInputDao
import com.goldinvoice0.billingsoftware.Dao.OrderDao
import com.goldinvoice0.billingsoftware.Model.BillInputs
import com.goldinvoice0.billingsoftware.Model.Order
import com.goldinvoice0.billingsoftware.convertes.ItemListConverter
import com.goldinvoice0.billingsoftware.convertes.RecivedPaymentListConverter


@Database(entities = [BillInputs::class], version = 1, exportSchema = false)
@TypeConverters(RecivedPaymentListConverter::class, ItemListConverter::class) // Important!
abstract class BillInputDatabase : RoomDatabase() {
    abstract fun billInputDao(): BillsInputDao

    companion object {
        @Volatile
        private var INSTANCE: BillInputDatabase? = null

        fun getDatabase(context: Context): BillInputDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BillInputDatabase::class.java,
                    "bill_inputs_database"
                ).build() // No fallbackToDestructiveMigration() in production!
                INSTANCE = instance
                instance
            }
        }
    }

}
