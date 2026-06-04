package com.approagency.drug.data.remote
import com.approagency.drug.domain.model.PharmacyItem
import org.jsoup.Jsoup

object PharmacyHtmlParser {

    fun parse(html: String): List<PharmacyItem> {
        val items = mutableListOf<PharmacyItem>()
        val doc = Jsoup.parse(html)
        val rows = doc.select("table#TBL_patientReferral tbody tr")

        for (row in rows) {
            val cells = row.select("td")
            if (cells.size < 3) continue

            // ستون 0: اطلاعات برند
            val brandCell = cells[0]
            val brandLink = brandCell.select("a").first()
            val brandName = brandLink?.text()?.trim() ?: ""
            val brandUrl = brandLink?.attr("href")?.trim() ?: ""
            val brandId = extractIdFromUrl(brandUrl, "/B-(\\d+)/?")

            // ستون 1: اطلاعات داروخانه
            val pharmacyCell = cells[1]
            val pharmacyLink = pharmacyCell.select("a").first()
            val pharmacyName = pharmacyLink?.text()?.trim() ?: ""
            val pharmacyUrl = pharmacyLink?.attr("href")?.trim() ?: ""
            val pharmacyId = extractIdFromUrl(pharmacyUrl, "/ph-(\\d+)/?")

            // ستون 2: موقعیت
            val locationText = cells[2].text().trim()
            val parts = locationText.split("شهر")
            val province = parts.getOrNull(0)?.replace("استان", "")?.trim() ?: ""
            val city = parts.getOrNull(1)?.trim() ?: ""

            if (brandName.isNotBlank() && pharmacyName.isNotBlank()) {
                items.add(
                    PharmacyItem(
                        brandName = brandName,
                        brandUrl = brandUrl,
                        brandId = brandId,
                        pharmacyName = pharmacyName,
                        pharmacyUrl = pharmacyUrl,
                        pharmacyId = pharmacyId,
                        province = province,
                        city = city
                    )
                )
            }
        }

        return items
    }

    private fun extractIdFromUrl(url: String, pattern: String): String {
        val regex = pattern.toRegex()
        return regex.find(url)?.groupValues?.get(1) ?: ""
    }
}