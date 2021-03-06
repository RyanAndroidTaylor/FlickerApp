package com.izeni.rapidosqlite

import android.annotation.SuppressLint
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.izeni.rapidosqlite.item_builder.ItemBuilder
import com.izeni.rapidosqlite.query.Query
import com.izeni.rapidosqlite.query.RawQuery
import com.izeni.rapidosqlite.table.Column
import com.izeni.rapidosqlite.table.DataTable
import com.izeni.rapidosqlite.table.ParentDataTable
import java.util.*

/**
 * Created by ryantaylor on 9/22/16.
 */
class DataConnection(val database: SQLiteDatabase) {

    companion object {
        private lateinit var sqliteOpenHelper: SQLiteOpenHelper

        fun init(databaseHelper: SQLiteOpenHelper) {
            this.sqliteOpenHelper = databaseHelper
        }

        fun <T> getAndClose(block: (DataConnection) -> T): T {
            val connection = DataConnection(sqliteOpenHelper.writableDatabase)

            val items = block(connection)

            return items
        }

        fun doAndClose(block: (DataConnection) -> Unit) {
            val connection = DataConnection(sqliteOpenHelper.writableDatabase)

            block(connection)
        }

        fun openConnection(): DataConnection {
            return DataConnection(sqliteOpenHelper.writableDatabase)
        }
    }

    var conflictAlgorithm = SQLiteDatabase.CONFLICT_REPLACE

    fun close() {
        database.close()
    }

    fun save(item: DataTable) {
        database.transaction { save(item, it) }
    }

    fun saveAll(items: List<DataTable>) {
        database.transaction { database -> items.forEach { save(it, database) } }
    }

    private fun save(item: DataTable, database: SQLiteDatabase) {
        if (item is ParentDataTable) {
            item.getChildren()?.forEach { save(it, database) }

            item.getJunctionTables()?.forEach { save(it, database) }
        }

        database.insertWithOnConflict(item.tableName(), null, item.contentValues(), conflictAlgorithm)
    }

    /**
     * If column is not null update based on the column and value. If column is null
     * update based on the id.
     */
    fun updateWithColumn(item: DataTable, column: Column, value: Any) {
        val columnValue = when (value) {
            is String -> value
            is Int, is Long -> value.toString()
            is Boolean -> if (value) "1" else "0"
            else -> throw IllegalArgumentException("String, Int, Long and Boolean are the only supported types. You passed ${value.javaClass}")
        }

        database.transaction { it.update(item.tableName(), item.contentValues(), "${column.name}=?", arrayOf(columnValue)) }
    }

    fun updateWithId(item: DataTable) {
        database.transaction { it.update(item.tableName(), item.contentValues(), Column.ID.name, arrayOf()) }
    }

    /**
     * If column is not null delete based on the column and value. If column is null
     * delete based on the id.
     */
    fun delete(tableName: String, column: Column, value: Any) {
        val columnValue = when (value) {
            is String -> value
            is Int, is Long -> value.toString()
            is Boolean -> if (value) "1" else "0"
            else -> throw IllegalArgumentException("String, Int, Long and Boolean are the only supported types. You passed ${value.javaClass}")
        }
        database.transaction { it.delete(tableName, "${column.name}=?", arrayOf(columnValue)) }
    }

    /**
     * Delete everything for the table name that matches the column and value
     */
    fun deleteAll(tableName: String) {
        database.transaction { it.delete(tableName, null, null) }
    }

    fun <T> findFirst(builder: ItemBuilder<T>, query: Query): T? {
        var item: T? = null

        database.transaction {
            val cursor = getCursor(database, query)

            if (cursor.moveToFirst())
                item = builder.buildItem(cursor, this)

            cursor.close()
        }

        return item
    }

    fun <T> findAll(builder: ItemBuilder<T>, query: Query): List<T> {
        val items = ArrayList<T>()

        database.transaction {
            val cursor = getCursor(database, query)

            while (cursor.moveToNext()) {
                items.add(builder.buildItem(cursor, this))
            }

            cursor.close()
        }

        return items
    }

    fun getCursor(query: Query): Cursor? {
        database.transaction {
            return getCursor(it, query)
        }

        return null
    }

    // Cursor should be closed in the block that calls this method
    @SuppressLint("Recycle")
    private fun getCursor(database: SQLiteDatabase, query: Query): Cursor {
        if (query is RawQuery) {
            Log.i("DataConnection", "Raw query cursor \n ${query.query} \n ${query.selectionArgs?.get(0)}")
            val cursor = database.rawQuery(query.query, query.selectionArgs)

            return cursor
        } else {
            return database.query(query.tableName, query.columns, query.selection, query.selectionArgs, null, null, query.order, query.limit)
        }
    }
}