package io.pleo.antaeus.core.services.utility

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

class DatabaseConnectionHelper {

    /**
     * Returns db
     */
    fun getDb(url: String, driver: String, user: String, password: String):Database {

            val db = Database
                    .connect(url = url,
                            driver = driver,
                            user =user,
                            password = password)
                    .also {
                        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
                        transaction(it) {
                            addLogger(StdOutSqlLogger)
                        }
                    }

            return db
    }

    /**
     * Initial Database setup of tables
     */
    fun setupInitialDB(url: String, driver: String, user: String, password: String, tables:Array<Table>):Database {


        val db=Database
                .connect(url = url,
                        driver = driver,
                        user = user,
                        password = password)
                .also {
                    TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
                    transaction(it) {
                        addLogger(StdOutSqlLogger)
                        // Drop all existing tables to ensure a clean slate on each run
                        SchemaUtils.drop(*tables)
                        // Create all tables
                        SchemaUtils.create(*tables)
                    }
                }
        return db

    }


}