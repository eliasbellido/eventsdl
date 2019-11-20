package com.beyondthecode.shared.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CodelabFtsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(codelabs: List<CodelabFtsEntity>)

    @Query("SELECT codelabId FROM codelabsFts WHERE codelabsFts MATCH :query")
    fun searchAllCodelabs(query: String): List<String>
}