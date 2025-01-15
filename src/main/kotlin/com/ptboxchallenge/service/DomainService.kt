package com.ptboxchallenge.service

import com.ptboxchallenge.models.Domain
import com.ptboxchallenge.models.DomainDraft

interface DomainService {
    fun getAllDomain(): List<Domain>

    fun getOneDomain(id: Int): Domain?

    fun createDomain(domain: DomainDraft): Domain

    fun removeDomain(id: Int): Boolean

    fun updateDomain(id: Int, domain: DomainDraft): Boolean
}