package com.simple.cache

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column


data class User(val id: Int, val login: String, val password: String, val phoneNumber: String)


object Users : IntIdTable() {
    val login: Column<String> = varchar("login", 50)
    val password: Column<String> = varchar("password", 50)
    val phoneNumber: Column<String> = varchar("phoneNumber", 20)
}

object Tokens : IntIdTable() {
    val login: Column<String> = varchar("login", 50)
    val token: Column<String> = varchar("token", 50)
}
