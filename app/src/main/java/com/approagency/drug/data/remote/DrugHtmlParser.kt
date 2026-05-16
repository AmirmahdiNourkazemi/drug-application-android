package com.approagency.drug.data.remote

import com.approagency.drug.domain.model.DrugSearchResult
import okio.IOException
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class DrugHtmlParser {

    fun parseSearchResults(html: String): List<DrugSearchResult> {
        val document: Document = Jsoup.parse(html)
        val results = mutableListOf<DrugSearchResult>()

        // Select the table body that contains the drug rows
        val rows = document.select("#tbody_DrugList tr")

        if (rows.isEmpty()) {
            // Handle case where no results are found
            return emptyList()
        }

        for (row in rows) {
            try {
                // --- Extract Persian Name and Detail Page URL ---
                val nameCell = row.selectFirst("td:eq(0)") // First td
                val drugLink = nameCell?.selectFirst("a.ahref_Generic")
                val persianName = drugLink?.text()?.trim() ?: continue // Skip if no name found
                val detailPageRelativeUrl = drugLink?.attr("href") ?: continue
                val detailPageUrl = "https://www.darooyab.ir$detailPageRelativeUrl"

                // --- Extract Generic ID from URL ---
                // URL format: /G-2556/Casanthranol or /G-2556/
                val genericIdRegex = "/G-(\\d+)/?".toRegex()
                val genericId = genericIdRegex.find(detailPageRelativeUrl)?.groupValues?.get(1) ?: ""

                // --- Extract English Name ---
                // The last cell contains the English name link
                val lastCell = row.select("td").last()
                val englishLink = lastCell?.selectFirst("a.ahref_Generic")
                val englishName = englishLink?.text()?.trim()

                val drugResult = DrugSearchResult(
                    genericId = genericId,
                    persianName = persianName,
                    englishName = englishName,
                    detailPageUrl = detailPageUrl
                )
                results.add(drugResult)

            } catch (e: Exception) {
                // Log error for a specific row but continue with others
                println("Error parsing row: ${e.message}")
            }
        }

        return results
    }
}