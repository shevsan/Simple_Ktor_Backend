package com.simple.plugins

import com.simple.cache.*
import com.simple.features.login.LoginReceivedRemoteModel
import com.simple.features.login.LoginResponseRemoteModel
import com.simple.features.registration.RegistrationReceivedRemoteModel
import com.simple.features.registration.RegistrationResponseRemoteModel
import com.simple.utils.isPhoneNumberValid
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

fun Application.configureRouting() {
    routing {
        get("/greeting") {
            call.respondText("Hello World!")
        }
    }
}


fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json()
    }
}


fun Application.loginRouting() {
    routing {
        post("/login") {
            val model = call.receive(LoginReceivedRemoteModel::class)
            val user = transaction {
                Users.select { Users.login eq model.login }
                    .mapNotNull { row ->
                        User(
                            row[Users.id].value,
                            row[Users.login],
                            row[Users.password],
                            row[Users.phoneNumber]
                        )
                    }
                    .singleOrNull()
            }

            if (user == null)
                call.respond(HttpStatusCode.BadRequest, "User with this login doesn't exist")

            if (user?.password == model.password) {
                val token = UUID.randomUUID().toString()
                transaction {
                    Tokens.insert {
                        it[login] = model.login
                        it[this.token] = token
                    }
                }
                call.respond(LoginResponseRemoteModel(token = token))
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid password")
            }
        }
    }
}

fun Application.registrationRouting() {
    routing {
        post("/registration") {
            val model = call.receive(RegistrationReceivedRemoteModel::class)
            if (!model.phoneNumber.isPhoneNumberValid()) {
                call.respond(HttpStatusCode.BadRequest, "Invalid phone number")
            }
            val existingUser = transaction {
                Users.select { Users.phoneNumber eq model.phoneNumber }
                    .mapNotNull { row ->
                        User(
                            row[Users.id].value,
                            row[Users.login],
                            row[Users.password],
                            row[Users.phoneNumber]
                        )
                    }
                    .singleOrNull()
            }
            if (existingUser != null)
                call.respond(HttpStatusCode.Conflict, "User with the same phone number already exists")

            val token = UUID.randomUUID().toString()
            transaction {
                Tokens.insert {
                    it[login] = model.login
                    it[this.token] = token
                }
            }

            call.respond(RegistrationResponseRemoteModel(token = token))
        }
    }
}

