package com.approagency.drug.data.remote

import com.approagency.drug.domain.model.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class DrugDetailParser {

    fun parseDrugDetail(html: String): DrugDetail {
        val document = Jsoup.parse(html)

        // اطلاعات پایه
        val genericId = extractGenericId(document)
        val persianName = extractPersianName(document)
        val englishName = extractEnglishName(document)

        // استخراج تمام بخش‌ها به صورت داینامیک
        val sections = extractAllSections(document)

        return DrugDetail(
            genericId = genericId,
            persianName = persianName,
            englishName = englishName,
            drugClass = extractDrugClass(document),
            therapeuticClass = extractTherapeuticClass(document),
            usage = sections["usage"] ?: extractSectionByText(document, "موارد مصرف"),
            mechanism = sections["mechanism"] ?: extractSectionByText(document, "مکانیسم اثر"),
            pharmacokinetics = sections["pharmacokinetics"] ?: extractSectionByText(document, "فارماکوکینتیک"),
            contraindications = sections["contraindications"] ?: extractSectionByText(document, "منع مصرف"),
            sideEffects = sections["sideEffects"] ?: extractSectionByText(document, "عوارض جانبی"),
            interactions = sections["interactions"] ?: extractSectionByText(document, "تداخلات دارویی"),
            warnings = sections["warnings"] ?: extractSectionByText(document, "هشدار"),
            recommendations = sections["recommendations"] ?: extractSectionByText(document, "توصیه"),
            pregnancyCategory = extractPregnancyCategory(document),
            pregnancyDescription = extractPregnancyDescription(document),
            dosageForms = extractDosageForms(document),
            brandNames = extractBrandNames(document),
            similarDrugs = extractSimilarDrugs(document),
            categories = extractCategories(document),
            comments = extractComments(document)
        )
    }

    private fun extractGenericId(document: Document): String {
        val urlElement = document.select("link[rel=canonical]").first()
        val url = urlElement?.attr("href") ?: ""
        val regex = "/G-(\\d+)/".toRegex()
        return regex.find(url)?.groupValues?.get(1) ?: ""
    }

    private fun extractPersianName(document: Document): String {
        val titleElement = document.select("h1.EnglishNumericFont").first()
        val fullText = titleElement?.text() ?: ""
        return fullText.replace("چیست و برای چه مواردی استفاده می شود؟", "").trim()
    }

    private fun extractEnglishName(document: Document): String {
        val englishLabel = document.select("label.EnglishTopLabel").first()
        return englishLabel?.text()?.trim() ?: ""
    }

    private fun extractDrugClass(document: Document): String? {
        val classElement = document.select("#divExtraInfo > div:first-child a.ahref_Generic").first()
        return classElement?.text()?.trim()
    }

    private fun extractTherapeuticClass(document: Document): String? {
        val therapeuticElement = document.select("#divExtraInfo > div:last-child a.ahref_Generic").first()
        return therapeuticElement?.text()?.trim()
    }

    /**
     * استخراج بخش‌ها با استفاده از ID (روش قبلی)
     */
    private fun extractAllSections(document: Document): Map<String, String> {
        val sections = mutableMapOf<String, String>()

        // نقشه ID به کلید
        val idToKey = mapOf(
            "0" to "usage",
            "1" to "mechanism",
            "2" to "pharmacokinetics",
            "3" to "contraindications",
            "4" to "sideEffects",
            "5" to "interactions",
            "6" to "warnings",
            "7" to "recommendations"
        )

        for ((id, key) in idToKey) {
            val section = extractSectionById(document, id)
            if (!section.isNullOrBlank()) {
                sections[key] = section
            }
        }

        return sections
    }

    private fun extractSectionById(document: Document, sectionId: String): String? {
        val sectionElement = document.select("h2.h2_TabTitle#${sectionId}").first()
        if (sectionElement == null) return null

        val content = StringBuilder()
        var nextElement = sectionElement.nextElementSibling()

        while (nextElement != null && !nextElement.select("h2.h2_TabTitle").hasText()) {
            if (nextElement.tagName() == "p" || nextElement.tagName() == "div") {
                val text = cleanHtmlText(nextElement.text())
                if (text.isNotBlank()) {
                    content.append(text).append("\n\n")
                }
            }
            nextElement = nextElement.nextElementSibling()
        }

        return content.toString().trim().takeIf { it.isNotEmpty() }
    }

    /**
     * استخراج بخش با جستجوی متن عنوان (روش جایگزین)
     */
    private fun extractSectionByText(document: Document, titleKeyword: String): String? {
        // جستجوی هدر حاوی کلمه کلیدی
        val header = document.select("h2.h2_TabTitle, h3").firstOrNull {
            it.text().contains(titleKeyword, ignoreCase = true)
        } ?: return null

        val content = StringBuilder()
        var nextElement = header.nextElementSibling()

        while (nextElement != null && !nextElement.select("h2.h2_TabTitle, h3").hasText()) {
            if (nextElement.tagName() == "p" || nextElement.tagName() == "div") {
                val text = cleanHtmlText(nextElement.text())
                if (text.isNotBlank()) {
                    content.append(text).append("\n\n")
                }
            }
            nextElement = nextElement.nextElementSibling()
        }

        return content.toString().trim().takeIf { it.isNotEmpty() }
    }

    private fun cleanHtmlText(text: String): String {
        return text
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    private fun extractPregnancyCategory(document: Document): String? {
        val categoryElement = document.select("#UseInPregnancy .EnglishNumericFont, #UseInPregnancy > div.EnglishNumericFont").first()
        return categoryElement?.text()?.trim()
    }

    private fun extractPregnancyDescription(document: Document): String? {
        val descElement = document.select("#UseInPregnancy p, #UseInPregnancy .alert").first()
        val text = descElement?.text()?.trim()
        // اگر متن "مصرف در بارداری ثبت نشده است" باشد، null برگردان
        return if (text.isNullOrBlank() || text.contains("ثبت نشده")) null else text
    }

    private fun extractDosageForms(document: Document): List<DosageForm> {
        val forms = mutableListOf<DosageForm>()

        // بررسی وجود جدول اشکال دارویی
        val table = document.select("#TBL_AshkalDarooyi").first()
        if (table == null) {
            println("No dosage forms table found")
            return forms
        }

        val rows = table.select("tbody tr").filter { !it.hasClass("showMoreRow") && it.id() != "showMoreRow" }

        for (row in rows) {
            try {
                val cells = row.select("td")
                if (cells.size >= 2) {
                    val persianNameElement = cells[1].select("h3").first()
                    val englishNameElement = cells[1].select("label.EnglishNumericFont").first()

                    // بررسی اینکه آیا داده معتبر است
                    val persianName = persianNameElement?.text()?.trim()
                    if (persianName.isNullOrBlank()) continue

                    forms.add(
                        DosageForm(
                            code = cells[0].text().trim(),
                            persianName = persianName,
                            englishName = englishNameElement?.text()?.trim() ?: "",
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

        // استخراج از بخش اسامی تجاری فارسی
        val persianRows = document.select("#PersCommertialDrugs .tableCommertial tbody tr.tr_persian")

        // اگر ردیفی وجود نداشت، بررسی کن که آیا پیام "ثبت نشده" وجود دارد
        if (persianRows.isEmpty()) {
            val noDataMessage = document.select("#PersCommertialDrugs .alert")
            if (noDataMessage.isNotEmpty()) {
                println("No brand names available: ${noDataMessage.text()}")
            }
            return brands
        }

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

        // بررسی وجود جدول داروهای هم گروه
        val table = document.select("table.tableGroups").first()
        if (table == null) {
            println("No similar drugs table found")
            return drugs
        }

        val rows = table.select("tbody tr").filter {
            !it.hasClass("hidden-row") && it.select("a#toggleButton").isEmpty()
        }

        for (row in rows) {
            try {
                val cells = row.select("td")
                for (cell in cells) {
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

        // بررسی طبقه بندی درمانی (ممکن است "بدون طبقه بندی درمانی" باشد)
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
}