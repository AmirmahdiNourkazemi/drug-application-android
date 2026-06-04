package com.approagency.drug.data.remote

import com.approagency.drug.domain.model.PharmacyItem
import org.jsoup.Jsoup

object PharmacyHtmlParser {

    fun parse(html: String): List<PharmacyItem> {
        val items = mutableListOf<PharmacyItem>()
        val doc = Jsoup.parse(html)
        val table = doc.select("table#TBL_patientReferral").first() ?: return emptyList()

        // تشخیص ساختار بر اساس سربرگ جدول (یک بار برای کل جدول)
        val hasGuaranteeColumn = hasGuaranteeColumnInHeader(table)

        val rows = table.select("tbody tr")

        for (row in rows) {
            val cells = row.select("td")
            if (cells.size < 3) continue

            // ایندکس ستون‌ها بر اساس وجود ستون تضمین
            val brandIndex = if (hasGuaranteeColumn) 1 else 0
            val pharmacyIndex = if (hasGuaranteeColumn) 2 else 1
            val locationIndex = if (hasGuaranteeColumn) 3 else 2

            // ستون برند
            val brandCell = cells[brandIndex]
            val brandLink = brandCell.select("a").first()
            val brandName = brandLink?.text()?.trim() ?: ""
            val brandUrl = brandLink?.attr("href")?.trim() ?: ""
            val brandId = extractIdFromUrl(brandUrl, "/B-(\\d+)/?")

            // ستون داروخانه
            val pharmacyCell = cells[pharmacyIndex]
            val pharmacyLinks = pharmacyCell.select("a")

            // پیدا کردن لینک داروخانه (لینکی که "اطلاعات تماس" نباشد)
            val pharmacyLink = pharmacyLinks.firstOrNull {
                !it.text().contains("اطلاعات تماس")
            }
            val pharmacyName = pharmacyLink?.text()?.trim() ?: ""
            val pharmacyUrl = pharmacyLink?.attr("href")?.trim() ?: ""
            val pharmacyId = extractIdFromUrl(pharmacyUrl, "/ph-(\\d+)/?")

            // ستون موقعیت
            val locationCell = cells[locationIndex]
            val locationText = locationCell.text().trim()

            // استخراج استان و شهر
            val province = extractProvince(locationText)
            val city = extractCity(locationText)

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

    /**
     * تشخیص وجود ستون تضمین موجودی با بررسی سربرگ جدول
     */
    private fun hasGuaranteeColumnInHeader(table: org.jsoup.nodes.Element): Boolean {
        val headers = table.select("thead th")
        if (headers.isEmpty()) return false

        // بررسی می‌کنیم که آیا اولین ستون حاوی متن "تضمین" است
        val firstHeaderText = headers.firstOrNull()?.text()?.trim() ?: ""
        return firstHeaderText.contains("تضمین") || firstHeaderText.contains("موجودی")
    }

    private fun extractProvince(locationText: String): String {
        // الگو: "استان تهران" یا "استان اصفهان"
        val provincePattern = "استان\\s*(\\S+)".toRegex()
        val match = provincePattern.find(locationText)
        return match?.groupValues?.get(1)?.trim() ?: ""
    }

    private fun extractCity(locationText: String): String {
        // الگو: "شهر تهران" یا "شهر اصفهان" یا "شهر پردیس"
        val cityPattern = "شهر\\s*(.+)".toRegex()
        val match = cityPattern.find(locationText)
        return match?.groupValues?.get(1)?.trim() ?: ""
    }

    private fun extractIdFromUrl(url: String, pattern: String): String {
        val regex = pattern.toRegex()
        return regex.find(url)?.groupValues?.get(1) ?: ""
    }
}