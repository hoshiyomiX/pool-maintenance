package com.poolmaintenance.app.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VillaRepository @Inject constructor(
    private val villaDao: VillaDao
) {
    suspend fun insertRecord(record: MaintenanceRecord): Long {
        return villaDao.insertRecord(record)
    }

    suspend fun getRecordsForVilla(villaNumber: Int): List<MaintenanceRecord> {
        return villaDao.getRecordsForVilla(villaNumber)
    }

    fun getRecordsForVillaFlow(villaNumber: Int): Flow<List<MaintenanceRecord>> {
        return villaDao.getRecordsForVillaFlow(villaNumber)
    }

    fun getAllRecordsFlow(): Flow<List<MaintenanceRecord>> {
        return villaDao.getAllRecordsFlow()
    }

    suspend fun getStatsInRange(startMs: Long, endMs: Long): AggregatedStats {
        return villaDao.getStatsInRange(startMs, endMs)
    }

    suspend fun getPerVillaStatsInRange(startMs: Long, endMs: Long): List<VillaStats> {
        return villaDao.getPerVillaStatsInRange(startMs, endMs)
    }

    suspend fun deleteRecord(id: Long) {
        villaDao.deleteRecord(id)
    }

    companion object {
        /**
         * Valid villa numbers: 1 to 63, excluding 2 and 27.
         * Total: 61 villas.
         */
        val VILLA_NUMBERS: List<Int> = (1..63).filter { it != 2 && it != 27 }
    }
}
