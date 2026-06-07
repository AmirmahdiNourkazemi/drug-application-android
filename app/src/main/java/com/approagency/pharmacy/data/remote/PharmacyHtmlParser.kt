// data/remote/PharmacyHtmlParser.kt
package com.approagency.pharmacy.data.remote

import com.approagency.pharmacy.domain.model.PharmacyDetail
import com.approagency.pharmacy.domain.model.PharmacyItem
import org.jsoup.Jsoup

object PharmacyHtmlParser {

//    private const val BASE_URL = "https://www.darooyab.ir"

    fun parse(html: String): List<PharmacyItem> {
        val items = mutableListOf<PharmacyItem>()
        val doc = Jsoup.parse(html)
        val table = doc.select("table#TBL_patientReferral").first() ?: return emptyList()

        // ساختار جدول متغیر است:
        //  - صفحات ژنریک: [نام برند, نام داروخانه, شهرستان] (گاهی با ستون «تضمین موجودی»)
        //  - صفحات برند:  [نام داروخانه, شهرستان] (بدون ستون برند)
        // بنابراین ایندکس ستون‌ها را از روی متن سربرگ تشخیص می‌دهیم نه موقعیت ثابت
        val headers = table.select("thead th")
        var brandIndex = -1
        var pharmacyIndex = -1
        var locationIndex = -1
        headers.forEachIndexed { index, th ->
            val text = th.text().trim()
            when {
                brandIndex == -1 && text.contains("برند") -> brandIndex = index
                pharmacyIndex == -1 && text.contains("داروخانه") -> pharmacyIndex = index
                locationIndex == -1 && (text.contains("شهرستان") || text.contains("استان")) -> locationIndex = index
            }
        }

        val rows = table.select("tbody tr")

        for (row in rows) {
            val cells = row.select("td")
            if (cells.isEmpty()) continue

            // ستون داروخانه (ضروری) — اگر از سربرگ پیدا نشد، سلولی که لینک ph- دارد
            val pIdx = if (pharmacyIndex in cells.indices) {
                pharmacyIndex
            } else {
                cells.indexOfFirst { cell ->
                    cell.select("a").any { it.attr("href").contains("ph-") }
                }
            }
            if (pIdx < 0) continue

            val pharmacyCell = cells[pIdx]
            // پیدا کردن لینک داروخانه (لینکی که "اطلاعات تماس" نباشد)
            val pharmacyLink = pharmacyCell.select("a").firstOrNull {
                !it.text().contains("اطلاعات تماس")
            }
            val pharmacyName = pharmacyLink?.text()?.trim() ?: ""
            val pharmacyUrl = normalizeUrl(pharmacyLink?.attr("href")?.trim() ?: "")
            val pharmacyId = extractIdFromUrl(pharmacyUrl, "/ph-(\\d+)/?")
            if (pharmacyName.isBlank()) continue

            // ستون برند (در صفحات برند وجود ندارد)
            val brandCell = brandIndex.takeIf { it in cells.indices }?.let { cells[it] }
            val brandLink = brandCell?.select("a")?.first()
            val brandName = brandLink?.text()?.trim() ?: ""
            val brandUrl = normalizeUrl(brandLink?.attr("href")?.trim() ?: "")
            val brandId = extractIdFromUrl(brandUrl, "/B-(\\d+)/?")

            // ستون موقعیت — اگر از سربرگ پیدا نشد، سلولی که «استان» دارد
            val locIdx = if (locationIndex in cells.indices) {
                locationIndex
            } else {
                cells.indexOfFirst { it.text().contains("استان") }
            }
            val locationText = locIdx.takeIf { it in cells.indices }?.let { cells[it].text().trim() } ?: ""
            val province = extractProvince(locationText)
            val city = extractCity(locationText)

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

        return items
    }

    /**
     * نرمال کردن URL: تبدیل مسیرهای نسبی به URL کامل
     * مثال: ~/../../../../../ph-12012/... -> https://www.darooyab.ir/ph-12012/...
     */
    private fun normalizeUrl(url: String): String {
        if (url.isBlank()) return ""

        // اگر absolute بود domain را حذف کن
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return url
                .replace("https://www.darooyab.ir", "")
                .replace("http://www.darooyab.ir", "")
        }

        var cleanUrl = url

        if (cleanUrl.startsWith("~/")) {
            cleanUrl = cleanUrl.removePrefix("~/")
        }

        while (cleanUrl.startsWith("../")) {
            cleanUrl = cleanUrl.removePrefix("../")
        }

        return if (cleanUrl.startsWith("/")) {
            cleanUrl
        } else {
            "/$cleanUrl"
        }
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

    fun parsePharmacyDetail(html: String): PharmacyDetail {
        val doc = Jsoup.parse(html)

        // استخراج نام داروخانه
        val pharmacyTitle = doc.select("h2#pharmacyTitle").first()?.text()?.trim() ?: ""

        // استخراج آدرس
        val addressElement = doc.select("h2#h2Address").first()
        val address = addressElement?.text()?.trim() ?: ""

        // استخراج تلفن
        val phoneElement = doc.select("h3#h3Phone a").first()
        val phone = phoneElement?.text()?.trim() ?: ""

        // استخراج دانشگاه (اختیاری)
        val university = doc.select("div.alert.alert-success > span").first()?.text()?.trim()

        return PharmacyDetail(
            name = pharmacyTitle,
            address = address,
            phone = phone,
            university = university
        )
    }
}