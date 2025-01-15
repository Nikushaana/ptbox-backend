package com.ptboxchallenge.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.ptboxchallenge.models.Domain
import com.ptboxchallenge.models.DomainDraft
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.time.LocalDate
import java.time.LocalDateTime

class InMemoryDomainService : DomainService {
    private val domains = mutableListOf<Domain>()
    private var idCounter = 1
    private val filePath = "domains.json"
    private val objectMapper = jacksonObjectMapper()

    init {
        loadDomainsFromFile()
    }

    private fun loadDomainsFromFile() {
        try {
            val file = File(filePath)
            if (file.exists()) {
                val json = file.readText()
                val savedDomains: List<Domain> = objectMapper.readValue(json)
                domains.addAll(savedDomains)
                idCounter = domains.maxOfOrNull { it.id }?.plus(1) ?: 1
            }
        } catch (e: IOException) {
            println("Error loading domains from file: ${e.message}")
        }
    }

    private fun saveDomainsToFile() {
        try {
            val json = objectMapper.writeValueAsString(domains)
            File(filePath).writeText(json)
        } catch (e: IOException) {
            println("Error saving domains to file: ${e.message}")
        }
    }

    private fun runTheHarvester(domainName: String): Domain {
        return try {
            val processBuilder = ProcessBuilder(
                "python",
                "C:\\theHarvester-master\\theHarvester.py",
                "-d", domainName,
                "-b", "bing"
            )
            processBuilder.directory(File("C:\\theHarvester-master"))
            processBuilder.redirectErrorStream(true)

            val process = processBuilder.start()
            val reader = BufferedReader(InputStreamReader(process.inputStream))

            val output = StringBuilder()
            var line: String? = reader.readLine()
            while (line != null) {
                output.append(line).append("\n")
                line = reader.readLine()
            }

            // Parse the output and create a Domain object
            parseTheHarvesterOutput(output.toString(), domainName)

        } catch (e: Exception) {
            throw RuntimeException("Error running TheHarvester: ${e.message}")
        }
    }

    fun parseTheHarvesterOutput(output: String, domainName: String): Domain {
        val ips = mutableListOf<String>()
        val emails = mutableListOf<String>()
        val hosts = mutableListOf<String>()

        // Regex patterns for matching IPs, emails, and hosts
        val ipPattern = Regex("\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b")
        val emailPattern = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
        val hostPattern = Regex("\\S+\\.$domainName")

        // Extract matches from output
        ips.addAll(ipPattern.findAll(output).map { it.value })
        emails.addAll(emailPattern.findAll(output).map { it.value })
        hosts.addAll(hostPattern.findAll(output).map { it.value })

        return Domain(
            id = idCounter++,
            name = domainName,
            ips = ips,
            emails = emails,
            hosts = hosts,
            startTime = LocalDateTime.now().toString(),
            endTime = LocalDateTime.now().toString()
        )
    }

    override fun getAllDomain(): List<Domain> {
        return domains
    }

    override fun getOneDomain(id: Int): Domain? {
        return domains.firstOrNull { it.id == id }
    }

    override fun createDomain(draft: DomainDraft): Domain {
        val domain = runTheHarvester(draft.name)

        domains.add(domain)
        saveDomainsToFile()
        return domain
    }

    override fun removeDomain(id: Int): Boolean {
        val removed = domains.removeIf { it.id == id }
        if (removed) {
            saveDomainsToFile()
        }
        return removed
    }

    override fun updateDomain(id: Int, draft: DomainDraft): Boolean {
        val domain = domains.firstOrNull { it.id == id }
            ?: return false

        domain.name = draft.name
        domain.endTime = LocalDateTime.now().toString()

        saveDomainsToFile()

        return true
    }


}