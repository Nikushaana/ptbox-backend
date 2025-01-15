package com.ptboxchallenge

import com.ptboxchallenge.models.DomainDraft
import com.ptboxchallenge.service.DomainService
import com.ptboxchallenge.service.InMemoryDomainService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        val service: DomainService = InMemoryDomainService()

        get("/") {
            call.respondText("Hello World!")
        }
        get("/domains") {
            val domains = service.getAllDomain()

            if (domains.isEmpty()) {
                call.respond(mapOf("message" to "No domains found"))
            } else {
                call.respond(HttpStatusCode.OK, domains)
            }
        }
        get("/domains/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()

            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Id parameter has to be number")
                return@get
            }

            val domain = service.getOneDomain(id)
            if (domain == null) {
                call.respond(HttpStatusCode.NotFound, "Domain $id not found")
            } else {
                call.respond(domain)
            }
        }
        post("/domains") {
            val domainDraft = call.receive<DomainDraft>()
            val domain = service.createDomain(domainDraft)
            call.respond(domain)
        }
        put("/domains/{id}") {
            val domainDraft = call.receive<DomainDraft>()
            val domainId = call.parameters["id"]?.toIntOrNull()

            if (domainId == null) {
                call.respond(HttpStatusCode.BadRequest, "id parameter has to be number!")
                return@put
            }

            val updated = service.updateDomain(domainId, domainDraft)
            if (updated) {
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.NotFound, "found no domain with the id $domainId!")
            }
        }
        delete("/domains/{id}") {
            val domainId = call.parameters["id"]?.toIntOrNull()

            if (domainId == null) {
                call.respond(HttpStatusCode.BadRequest, "id parameter has to be number!")
                return@delete
            }

            val removed = service.removeDomain(domainId)
            if (removed) {
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.NotFound, "no domain with the id $domainId!")
            }
        }
    }
}
