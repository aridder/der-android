package org.aridder.der.repository

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import org.aridder.der.model.StationInfo


@androidx.room.Database(entities = [StationInfo::class], version = 1)
abstract class Database: RoomDatabase() {
    abstract fun StationDao(): StationDao

    companion object {
        var INSTANCE:  Database? = null

        fun getDatabase(context: Context): Database? {
            if (INSTANCE == null){
                synchronized(this){
                    INSTANCE =
                        Room.databaseBuilder(
                            context.applicationContext,
                            Database::class.java, "derDB1")
                            .build()
                }
            }
            return INSTANCE
        }

        fun destroyDatabase(){
            INSTANCE = null
        }
    }
}