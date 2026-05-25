package com.approagency.drug.data.remote

import com.approagency.drug.domain.model.DaroYabSearchResult
import com.approagency.drug.domain.model.DrugSearchResult
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class DrugHtmlParser {

    fun parseSearchResultsWithPagination(html: String): DaroYabSearchResult {
        val document: Document = Jsoup.parse(html)
        val drugs = parseDrugRows(document)
        val paginationInfo = extractPaginationInfo(document)

        return DaroYabSearchResult(
            drugs = drugs,
            currentPage = paginationInfo.currentPage,
            totalPages = paginationInfo.totalPages,
            hasNextPage = paginationInfo.currentPage < paginationInfo.totalPages,
            hasPreviousPage = paginationInfo.currentPage > 1
        )
    }

    private fun parseDrugRows(document: Document): List<DrugSearchResult> {
        val results = mutableListOf<DrugSearchResult>()
        val rows = document.select("#tbody_DrugList tr")

        println("Found ${rows.size} rows in the response")

        if (rows.isEmpty()) {
            println("No rows found. HTML snippet: ${document.html().take(500)}")
            return emptyList()
        }

        for (row in rows) {
            try {
                val nameCell = row.selectFirst("td:eq(0)")
                val drugLink = nameCell?.selectFirst("a.ahref_Generic")
                val persianName = drugLink?.text()?.trim() ?: continue
                val detailPageRelativeUrl = drugLink?.attr("href") ?: continue
                val detailPageUrl = "https://www.darooyab.ir$detailPageRelativeUrl"

                val genericIdRegex = "/G-(\\d+)/?".toRegex()
                val genericId = genericIdRegex.find(detailPageRelativeUrl)?.groupValues?.get(1) ?: ""

                val brandIdRegex = "/B-(\\d+)/?".toRegex()
                val brandId = brandIdRegex.find(detailPageRelativeUrl)?.groupValues?.get(1) ?: ""

                val lastCell = row.select("td").last()
                val englishLink = lastCell?.selectFirst("a.ahref_Generic")
                val englishName = englishLink?.text()?.trim()

                val drugResult = DrugSearchResult(
                    genericId = if (genericId.isNotEmpty()) genericId else brandId,
                    persianName = persianName,
                    englishName = englishName,
                    detailPageUrl = detailPageUrl
                )
                results.add(drugResult)
                println("Parsed: $persianName -> $englishName")

            } catch (e: Exception) {
                println("Error parsing row: ${e.message}")
            }
        }
        return results
    }

    private fun extractPaginationInfo(document: Document): PaginationInfo {
        var currentPage = 1
        var totalPages = 1

        try {
            // Get current page from hidden input
            val currentPageInput = document.select("#CurrentPager_Number")
            if (currentPageInput.isNotEmpty()) {
                currentPage = currentPageInput.`val`()?.toIntOrNull() ?: 1
            }

            // Get total pages from pagination links
            val pageLinks = document.select(".pagination li .PagerBtn_DrugName")
            if (pageLinks.isNotEmpty()) {
                val pageNumbers = pageLinks.mapNotNull { it.text().toIntOrNull() }
                if (pageNumbers.isNotEmpty()) {
                    totalPages = pageNumbers.maxOrNull() ?: 1
                }
            }

            println("Pagination: Current page=$currentPage, Total pages=$totalPages")

        } catch (e: Exception) {
            println("Error extracting pagination: ${e.message}")
        }

        return PaginationInfo(currentPage, totalPages)
    }

    private data class PaginationInfo(
        val currentPage: Int,
        val totalPages: Int
    )
}