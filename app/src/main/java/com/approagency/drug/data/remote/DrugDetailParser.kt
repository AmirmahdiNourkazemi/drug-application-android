package com.approagency.drug.data.remote

import com.approagency.drug.domain.model.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Evaluator

class DrugDetailParser {

    fun parseDrugDetail(html: String): DrugDetail {
        val document = Jsoup.parse(html)

        // تشخیص نوع صفحه: Generic (G) یا Brand (B)
        val isGenericPage = detectPageType(document)

        return if (isGenericPage) {
            parseGenericPage(document)
        } else {
            parseBrandPage(document)
        }
    }

    /**
     * تشخیص نوع صفحه با بررسی URL canonical و ساختار DOM
     */
    private fun detectPageType(document: Document): Boolean {
        // روش 1: بررسی URL canonical
        val canonicalUrl = document.select("link[rel=canonical]").attr("href")
        if (canonicalUrl.contains("/G-")) {
            return true
        }
        if (canonicalUrl.contains("/B-")) {
            return false
        }

        // روش 2: بررسی ساختار DOM (Fallback)
        val hasGenericLayout = document.select("#UL_GenericTabInfo").isNotEmpty()
        val hasBrandLayout = document.select("#BrandInfoContainer").isNotEmpty()

        return hasGenericLayout || !hasBrandLayout // Default to generic if ambiguous
    }

    // ============================================================
    // Generic Page Parsing
    // ============================================================
    private fun parseGenericPage(document: Document): DrugDetail {
        val genericId = extractGenericId(document)
        val persianName = extractPersianName(document)
        val englishName = extractEnglishName(document)
        val sections = extractSectionsFromGeneric(document)

        return DrugDetail(
            genericId = genericId,
            persianName = persianName,
            englishName = englishName,
            drugClass = extractDrugClass(document),
            therapeuticClass = extractTherapeuticClass(document),
            usage = sections["usage"],
            mechanism = sections["mechanism"],
            pharmacokinetics = sections["pharmacokinetics"],
            contraindications = sections["contraindications"],
            sideEffects = sections["sideEffects"],
            interactions = sections["interactions"],
            warnings = sections["warnings"],
            recommendations = sections["recommendations"],
            pregnancyCategory = extractPregnancyCategory(document),
            pregnancyDescription = extractPregnancyDescription(document),
            dosageForms = extractDosageForms(document),
            brandNames = extractBrandNames(document),
            similarDrugs = extractSimilarDrugs(document),
            categories = extractCategories(document),
            comments = extractComments(document),
            manufacturer = null,
            isGeneric = true,
            generalInfo = extractGeneralInfoGeneric(document),
            specializedInfo = extractSpecializedInfoGeneric(document),
        )
    }

    private fun extractGenericId(document: Document): String {
        val url = document.select("link[rel=canonical]").attr("href")
        val regex = "/G-(\\d+)/".toRegex()
        return regex.find(url)?.groupValues?.get(1) ?: ""
    }

    private fun extractPersianName(document: Document): String {
        val titleElement = document.select("h1.EnglishNumericFont").first()
        val fullText = titleElement?.text() ?: ""
        // Remove the suffix "چیست و برای چه مواردی استفاده می شود؟"
        return fullText.replace(Regex("\\s*چیست و برای چه مواردی استفاده می شود\\?\\s*"), "").trim()
    }

    private fun extractEnglishName(document: Document): String {
        return document.select("label.EnglishTopLabel").text().trim()
    }

    private fun extractDrugClass(document: Document): String? {
        return document.select("#divExtraInfo > div:first-child a.ahref_Generic").first()?.text()?.trim()
    }

    private fun extractTherapeuticClass(document: Document): String? {
        // The therapeutic class can be a chain of links
        val classLinks = document.select("#divExtraInfo > div:last-child a.ahref_Generic")
        return if (classLinks.isNotEmpty()) classLinks.joinToString(" > ") { it.text().trim() } else null
    }

    /**
     * Generic pages store content in divs with IDs inside #EtelaatTakhasosiContent
     * The titles are h2.h2_TabTitle, and content is everything until the next h2.
     */
    private fun extractSectionsFromGeneric(document: Document): Map<String, String> {
        val sections = mutableMapOf<String, String>()
        val specializedContentDiv = document.getElementById("EtelaatTakhasosiContent") ?: return sections

        // Mapping of section title keywords to our data class keys
        val titleToKey = mapOf(
            "موارد مصرف" to "usage",
            "مکانیسم اثر" to "mechanism",
            "فارماکوکینتیک" to "pharmacokinetics",
            "منع مصرف" to "contraindications",
            "عوارض جانبی" to "sideEffects",
            "تداخلات دارویی" to "interactions",
            "هشدار" to "warnings",
            "توصیه های دارویی" to "recommendations"
        )

        val sectionHeaders = specializedContentDiv.select("h2.h2_TabTitle")

        for (header in sectionHeaders) {
            val headerText = header.text().trim()
            val sectionKey = titleToKey.entries.find { headerText.contains(it.key) }?.value

            if (sectionKey != null) {
                var contentElement = header.nextElementSibling()
                val contentBuilder = StringBuilder()

                while (contentElement != null && !contentElement.select("h2.h2_TabTitle").hasText()) {
                    // Extract clean text from various elements
                    val text = cleanText(contentElement)
                    if (text.isNotBlank()) {
                        contentBuilder.append(text).append("\n\n")
                    }
                    contentElement = contentElement.nextElementSibling()
                }

                val content = contentBuilder.toString().trim()
                if (content.isNotEmpty()) {
                    sections[sectionKey] = content
                }
            }
        }

        return sections
    }

    /**
     * General info for generic pages is inside #EtelaatOmomiVaTakhasosi > #EtelaatOmomi
     */
    private fun extractGeneralInfoGeneric(document: Document): String? {
        val generalDiv = document.select("#EtelaatOmomiVaTakhasosi #EtelaatOmomi").first() ?: return null
        // Clone the element to avoid modifying the original document
        val clone = generalDiv.clone()

        // Remove the accordion (table of contents) as it's not part of the main content
        clone.select(".accordion").remove()
        // Remove navigation boxes if any
        clone.select("#nav_box_general").remove()

        return cleanText(clone).trim().takeIf { it.isNotEmpty() }
    }

    /**
     * Specialized info for generic pages is inside #EtelaatTakhasosiContent
     */
    private fun extractSpecializedInfoGeneric(document: Document): String? {
        val specializedDiv = document.getElementById("EtelaatTakhasosiContent") ?: return null
        val clone = specializedDiv.clone()

        // Remove the table of contents
        clone.select(".accordion").remove()
        // Remove the "دارو های هم گروه" section and sources if you don't want them in specializedInfo
        clone.select("#Teammate").remove()
        clone.select("#externalLinks").remove()

        return cleanText(clone).trim().takeIf { it.isNotEmpty() }
    }

    // ============================================================
    // Brand Page Parsing
    // ============================================================
    private fun parseBrandPage(document: Document): DrugDetail {
        val brandId = extractBrandId(document)
        val persianName = extractBrandPersianName(document)
        val englishName = extractBrandEnglishName(document)
        val manufacturer = extractManufacturer(document)
        val genericInfo = extractGenericInfo(document)

        // Extract all sections from .brandAttrPersDesc
        val (introText, sections) = extractBrandSections(document)

        return DrugDetail(
            genericId = genericInfo?.genericId ?: "",
            persianName = persianName,
            englishName = englishName,
            drugClass = genericInfo?.persianName, // Drug class is essentially the generic name
            therapeuticClass = extractBrandTherapeuticClass(document),
            usage = sections["usage"] ?: introText,
            mechanism = sections["mechanism"],
            pharmacokinetics = null,
            contraindications = sections["contraindications"],
            sideEffects = sections["sideEffects"],
            interactions = sections["interactions"],
            warnings = sections["warnings"],
            recommendations = sections["recommendations"],
            pregnancyCategory = null,
            pregnancyDescription = null,
            dosageForms = extractOtherBrandForms(document),
            brandNames = listOf(), // This page itself is a brand
            similarDrugs = emptyList(),
            categories = null,
            comments = extractComments(document),
            manufacturer = manufacturer,
            genericInfo = genericInfo,
            otherBrandForms = extractOtherBrandForms(document),
            isGeneric = false
        )
    }

    private fun extractBrandId(document: Document): String {
        val url = document.select("link[rel=canonical]").attr("href")
        val regex = "/B-(\\d+)/".toRegex()
        return regex.find(url)?.groupValues?.get(1) ?: ""
    }

    private fun extractBrandPersianName(document: Document): String {
        return document.select("#h1PersianName").text().trim()
    }

    private fun extractBrandEnglishName(document: Document): String {
        return document.select("#h2EnglishName").text().trim()
    }

    private fun extractManufacturer(document: Document): String? {
        val manufacturerLink = document.select("#divProducer a.ahref_Generic").first()
        return manufacturerLink?.text()?.trim()
    }

    private fun extractGenericInfo(document: Document): GenericInfo? {
        val genericLink = document.select("#divAjzaContent a.ahref_Generic").first() ?: return null
        val href = genericLink.attr("href")
        val genericIdRegex = "/G-(\\d+)/".toRegex()
        val genericId = genericIdRegex.find(href)?.groupValues?.get(1) ?: ""

        return GenericInfo(
            genericId = genericId,
            persianName = genericLink.text().trim(),
            detailUrl = "https://www.darooyab.ir$href"
        )
    }

    private fun extractBrandTherapeuticClass(document: Document): String? {
        val therapeuticContainer = document.select("#brand_desc > div:last-child").first()
        val text = therapeuticContainer?.text() ?: ""
        val regex = "طبقه بندی درمانی :\\s*(.+?)(?:\$|\\n)".toRegex()
        return regex.find(text)?.groupValues?.get(1)?.trim()
    }

    /**
     * Extract all content from .brandAttrPersDesc.
     * Returns a Pair: first is the introductory text (before any h2), second is a map of section title to content.
     */
    private fun extractBrandSections(document: Document): Pair<String?, Map<String, String>> {
        val container = document.select(".brandAttrPersDesc").first() ?: return Pair(null, emptyMap())
        val sections = mutableMapOf<String, String>()
        val titleToKey = mapOf(
            "موارد مصرف" to "usage",
            "مکانیسم اثر" to "mechanism",
            "منع مصرف" to "contraindications",
            "عوارض جانبی" to "sideEffects",
            "تداخلات دارویی" to "interactions",
            "هشدار" to "warnings",
            "توصیه های دارویی" to "recommendations"
        )

        var introText: String? = null
        var currentSectionKey: String? = null
        val contentBuilder = StringBuilder()

        for (child in container.children()) {
            if (child.tagName() == "h2" || child.tagName() == "h3") {
                // If we were building a previous section, save it
                if (currentSectionKey != null && contentBuilder.isNotEmpty()) {
                    sections[currentSectionKey] = contentBuilder.toString().trim()
                    contentBuilder.clear()
                }

                // Start a new section
                val headerText = child.text().trim()
                currentSectionKey = titleToKey.entries.find { headerText.contains(it.key) }?.value
            } else {
                val text = cleanText(child)
                if (text.isNotBlank()) {
                    if (currentSectionKey == null) {
                        // This is introductory text before any h2
                        introText = introText?.let { "$it\n\n$text" } ?: text
                    } else {
                        contentBuilder.append(text).append("\n\n")
                    }
                }
            }
        }

        // Save the last section if any
        if (currentSectionKey != null && contentBuilder.isNotEmpty()) {
            sections[currentSectionKey] = contentBuilder.toString().trim()
        }

        return Pair(introText, sections)
    }

    private fun extractOtherBrandForms(document: Document): List<DosageForm> {
        val forms = mutableListOf<DosageForm>()
        val brandmateDiv = document.select(".brandmate").first() ?: return forms

        val links = brandmateDiv.select("a.ahref_Brand")
        for (link in links) {
            val persianName = link.text().trim()
            val detailUrl = "https://www.darooyab.ir${link.attr("href")}"

            forms.add(
                DosageForm(
                    code = "",
                    persianName = persianName,
                    englishName = "",
                    isHighRisk = false,
                    temperature = null,
                    isVital = false,
                    warningLabel = null,
                    detailUrl = detailUrl
                )
            )
        }
        return forms
    }

    // ============================================================
    // Common Extractors (for both page types)
    // ============================================================

    private fun extractPregnancyCategory(document: Document): String? {
        // On generic pages only
        val categoryElement = document.select("#UseInPregnancy .EnglishNumericFont, #UseInPregnancy > div.EnglishNumericFont").first()
        return categoryElement?.text()?.trim()
    }

    private fun extractPregnancyDescription(document: Document): String? {
        // On generic pages only
        val descElement = document.select("#UseInPregnancy p, #UseInPregnancy .alert").first()
        val text = descElement?.text()?.trim()
        return if (text.isNullOrBlank() || text.contains("ثبت نشده")) null else text
    }

    private fun extractDosageForms(document: Document): List<DosageForm> {
        val forms = mutableListOf<DosageForm>()
        val table = document.select("#TBL_AshkalDarooyi").first() ?: return forms

        val rows = table.select("tbody tr").filter { !it.hasClass("showMoreRow") && it.id() != "showMoreRow" }

        for (row in rows) {
            try {
                val cells = row.select("td")
                if (cells.size >= 2) {
                    val persianNameElement = cells[1].select("h3").first()
                    val persianName = persianNameElement?.text()?.trim() ?: continue

                    forms.add(
                        DosageForm(
                            code = cells[0].text().trim(),
                            persianName = persianName,
                            englishName = cells[1].select("label.EnglishNumericFont").first()?.text()?.trim() ?: "",
                            isHighRisk = cells.getOrNull(2)?.hasText() == true,
                            temperature = cells.getOrNull(3)?.text()?.takeIf { it.isNotBlank() },
                            isVital = cells.getOrNull(4)?.hasText() == true,
                            warningLabel = cells.getOrNull(5)?.text()?.takeIf { it.isNotBlank() }
                        )
                    )
                }
            } catch (e: Exception) {
                println("Error parsing dosage form: ${e.message}")
            }
        }
        return forms
    }

    private fun extractBrandNames(document: Document): List<BrandName> {
        val brands = mutableListOf<BrandName>()
        val persianRows = document.select("#PersCommertialDrugs .tableCommertial tbody tr.tr_persian")

        if (persianRows.isEmpty()) return brands

        for (row in persianRows) {
            try {
                val linkElement = row.select("td:first-child a.ahref_Generic").first()
                val persianName = linkElement?.text()?.trim() ?: continue
                val detailUrl = "https://www.darooyab.ir${linkElement.attr("href")}"
                val manufacturerElement = row.select("td:eq(1) a.ahref_Generic").first()
                val manufacturer = manufacturerElement?.text()?.trim()
                val importerElement = row.select("td:eq(2) a.ahref_Generic").first()
                val importer = importerElement?.text()?.trim()

                brands.add(
                    BrandName(
                        persianName = persianName,
                        englishName = "",
                        manufacturer = manufacturer,
                        importer = importer,
                        detailUrl = detailUrl
                    )
                )
            } catch (e: Exception) {
                println("Error parsing brand name: ${e.message}")
            }
        }
        return brands
    }

    private fun extractSimilarDrugs(document: Document): List<SimilarDrug> {
        val drugs = mutableListOf<SimilarDrug>()
        val table = document.select("table.tableGroups").first() ?: return drugs

        val rows = table.select("tbody tr").filter {
            !it.hasClass("hidden-row") && it.select("a#toggleButton").isEmpty()
        }

        for (row in rows) {
            try {
                for (cell in row.select("td")) {
                    val link = cell.select("a.ahref_Generic").first()
                    if (link != null && link.text().isNotBlank()) {
                        val href = link.attr("href")
                        val genericIdRegex = "/G-(\\d+)/".toRegex()
                        val genericId = genericIdRegex.find(href)?.groupValues?.get(1) ?: ""

                        drugs.add(
                            SimilarDrug(
                                persianName = link.text().trim(),
                                englishName = null,
                                genericId = genericId,
                                detailUrl = "https://www.darooyab.ir$href"
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                println("Error parsing similar drug: ${e.message}")
            }
        }
        return drugs
    }

    private fun extractCategories(document: Document): DrugCategories? {
        val martindaleLink = document.select("#divExtraInfo > div:first-child a.ahref_Generic").first()
        val martindale = martindaleLink?.text()?.trim()
        val martindaleUrl = martindaleLink?.attr("href")?.let { "https://www.darooyab.ir$it" }

        val therapeuticLinks = document.select("#divExtraInfo > div:last-child a.ahref_Generic")
        val therapeutic = therapeuticLinks.mapNotNull { it.text().trim().takeIf { text ->
            text != "بدون طبقه بندی درمانی" && text.isNotBlank()
        } }
        val therapeuticUrls = therapeuticLinks.map { "https://www.darooyab.ir${it.attr("href")}" }

        return if (martindale != null || therapeutic.isNotEmpty()) {
            DrugCategories(
                martindale = martindale,
                martindaleUrl = martindaleUrl,
                therapeutic = therapeutic.ifEmpty { null },
                therapeuticUrls = therapeuticUrls.ifEmpty { null }
            )
        } else null
    }

    private fun extractComments(document: Document): List<Comment> {
        val comments = mutableListOf<Comment>()
        val commentElements = document.select("#CommentContent .comment")

        for (element in commentElements) {
            try {
                val authorElement = element.select("span").first()
                val author = authorElement?.text()?.replace("(", "")?.replace(")", "")?.trim() ?: "ناشناس"
                val date = authorElement?.text()?.let {
                    val regex = "\\((\\d{4}/\\d{1,2}/\\d{1,2})\\)".toRegex()
                    regex.find(it)?.groupValues?.get(1) ?: ""
                } ?: ""
                val textElement = element.select("p.commentText").first()
                val text = textElement?.text()?.trim() ?: ""
                if (text.isBlank()) continue

                val responseElement = element.select(".responseComment").first()
                val response = responseElement?.let { parseCommentResponse(it) }

                comments.add(
                    Comment(
                        author = author,
                        date = date,
                        text = text,
                        response = response
                    )
                )
            } catch (e: Exception) {
                println("Error parsing comment: ${e.message}")
            }
        }
        return comments
    }

    private fun parseCommentResponse(element: Element): CommentResponse {
        val doctorLink = element.select("a").first()
        val doctorName = doctorLink?.text()?.trim() ?: ""
        val doctorUrl = doctorLink?.attr("href")?.let { "https://www.darooyab.ir$it" }
        val doctorText = element.select("span").first()?.text()?.trim() ?: ""
        val doctorTitle = doctorText.substringAfter(" - ").takeIf { it.isNotBlank() } ?: ""
        val responseText = element.select("p.commentText").last()?.text()?.trim() ?: ""

        return CommentResponse(
            doctorName = doctorName,
            doctorTitle = doctorTitle,
            text = responseText,
            doctorUrl = doctorUrl
        )
    }

    /**
     * Helper function to clean and normalize text from an HTML element.
     * Converts block elements to newlines for better readability.
     */
    private fun cleanText(element: Element): String {
        // Clone to avoid affecting the original
        val clone = element.clone()

        // Replace block-level elements with newlines for structure
        clone.select("p, div, h2, h3, h4, li, br").before("\n")

        // Get the text and clean it up
        var text = clone.text()
            .replace(Regex("\\n\\s*\\n+"), "\n\n") // Collapse multiple newlines
            .replace(Regex("[ \\t]+"), " ")        // Collapse spaces/tabs
            .trim()

        // Ensure lists look decent
        if (element.tagName() == "li") {
            text = "• $text"
        }

        return text
    }
}