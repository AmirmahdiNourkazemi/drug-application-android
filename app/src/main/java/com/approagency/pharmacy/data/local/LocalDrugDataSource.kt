package com.approagency.pharmacy.data.local

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.approagency.pharmacy.data.dto.DarmanList
import com.approagency.pharmacy.data.dto.DarmanModel
import com.approagency.pharmacy.data.dto.DrugDetail
import com.approagency.pharmacy.data.dto.DrugListResponse
import com.approagency.pharmacy.data.dto.DrugModels
import com.approagency.pharmacy.data.dto.GorohDaroei
import com.approagency.pharmacy.data.dto.GorohDarmani
import com.approagency.pharmacy.data.dto.Meta
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileOutputStream
import kotlin.math.ceil

/**
 * منبعِ دادهٔ دارو روی پایگاه‌دادهٔ همراهِ اپ (`assets/DrugStore.db`).
 *
 * تنها منبعِ اطلاعاتِ دارو (جست‌وجو، جزئیات، گروه‌های درمانی) است و فقط خواندنی
 * است. از SQLite خام استفاده می‌کند (نه Room) تا اعتبارسنجیِ سخت‌گیرانهٔ شِمای
 * Room روی این دیتابیسِ ثابتِ ازپیش‌ساخته (با کلیدهای خارجی) دردسر نسازد.
 */
class LocalDrugDataSource(private val context: Context) {

    @Volatile
    private var db: SQLiteDatabase? = null

    private fun database(): SQLiteDatabase {
        db?.let { return it }
        return synchronized(this) {
            db ?: openReadOnly().also { db = it }
        }
    }

    private fun openReadOnly(): SQLiteDatabase {
        val out = context.getDatabasePath(DB_NAME)
        if (!out.exists()) {
            out.parentFile?.mkdirs()
            context.assets.open(DB_NAME).use { input ->
                FileOutputStream(out).use { output -> input.copyTo(output) }
            }
        }
        return SQLiteDatabase.openDatabase(out.path, null, SQLiteDatabase.OPEN_READONLY)
    }

    /** جست‌وجوی دارو با فیلترهای نام، گروه دارویی و گروه درمانی. */
    suspend fun searchDrugs(
        query: String?,
        drugGroup: Int?,
        healGroup: Int?,
        perPage: Int
    ): DrugListResponse = withContext(Dispatchers.IO) {
        val where = StringBuilder("1=1")
        val args = mutableListOf<String>()
        if (!query.isNullOrBlank()) {
            where.append(" AND (d.nam_fa LIKE ? OR d.nam_en LIKE ?)")
            args.add("%$query%"); args.add("%$query%")
        }
        if (drugGroup != null) { where.append(" AND d.goroh_daroei_cod = ?"); args.add(drugGroup.toString()) }
        if (healGroup != null) { where.append(" AND d.goroh_darmani_cod = ?"); args.add(healGroup.toString()) }

        val total = database().rawQuery(
            "SELECT COUNT(*) FROM DrugInfo d WHERE $where", args.toTypedArray()
        ).use { c -> if (c.moveToFirst()) c.getInt(0) else 0 }

        val items = database().rawQuery(
            "$SELECT_WITH_RELATIONS WHERE $where ORDER BY d.nam_fa LIMIT ?",
            (args + perPage.toString()).toTypedArray()
        ).use { c -> buildList { while (c.moveToNext()) add(c.toDrugDetail()) } }

        DrugListResponse(
            success = true,
            message = null,
            data = items,
            meta = Meta(
                current_page = 1,
                last_page = if (perPage > 0) ceil(total.toDouble() / perPage).toInt() else 1,
                per_page = perPage,
                total = total
            )
        )
    }

    /** جزئیات یک دارو بر اساس [cod]. اگر یافت نشود استثناء پرتاب می‌شود. */
    suspend fun drugDetail(cod: Int): DrugModels = withContext(Dispatchers.IO) {
        val detail = database().rawQuery(
            "$SELECT_WITH_RELATIONS WHERE d.cod = ?", arrayOf(cod.toString())
        ).use { c -> if (c.moveToFirst()) c.toDrugDetail() else null }
            ?: throw NoSuchElementException("دارویی با شناسهٔ $cod در پایگاه‌دادهٔ محلی یافت نشد.")

        DrugModels(success = true, message = null, data = detail, meta = null)
    }

    /** فهرست گروه‌های درمانی (goroh_darmani). */
    suspend fun darmanGroups(): DarmanModel = withContext(Dispatchers.IO) {
        val groups = database().rawQuery(
            "SELECT cod, nam_fa, nam_en FROM goroh_darmani ORDER BY nam_fa", null
        ).use { c ->
            buildList {
                while (c.moveToNext()) {
                    add(
                        DarmanList(
                            cod = c.getInt(0),
                            nam_fa = c.getStringOrEmpty(1),
                            nam_en = c.getStringOrEmpty(2)
                        )
                    )
                }
            }
        }
        DarmanModel(success = "true", message = "", data = groups)
    }

    private fun Cursor.toDrugDetail(): DrugDetail {
        val daroeiCod = getIntOrNull("goroh_daroei_cod")
        val darmaniCod = getIntOrNull("goroh_darmani_cod")
        return DrugDetail(
            cod = getIntOrNull("cod"),
            goroh_darmani_detail_cod = getIntOrNull("goroh_darmani_detail_cod"),
            goroh_daroei_cod = daroeiCod,
            goroh_farmakologic_cod = getIntOrNull("goroh_farmakologic_cod"),
            goroh_darmani_cod = darmaniCod,
            nam_fa = getStringOrNull("nam_fa"),
            nam_en = getStringOrNull("nam_en"),
            mavaredmasraf = getStringOrNull("mavaredmasraf"),
            meghdarmasraf = getStringOrNull("meghdarmasraf"),
            masrafdarhamelegi = getStringOrNull("masrafdarhamelegi"),
            masrafdarshirdehi = getStringOrNull("masrafdarshirdehi"),
            manemasraf = getStringOrNull("manemasraf"),
            avarez = getStringOrNull("avarez"),
            tadakhol = getStringOrNull("tadakhol"),
            mekanismtasir = getStringOrNull("mekanismtasir"),
            nokte = getStringOrNull("nokte"),
            hoshdar = getStringOrNull("hoshdar"),
            sharayetnegahdari = getStringOrNull("sharayetnegahdari"),
            ashkal_daroei = getStringOrNull("ashkal_daroei"),
            created_at = null,
            updated_at = null,
            goroh_daroei = getStringOrNull("gd_nam")?.let { GorohDaroei(cod = daroeiCod, nam = it, created_at = null, updated_at = null) },
            goroh_darmani = getStringOrNull("gm_nam_fa")?.let {
                GorohDarmani(
                    cod = darmaniCod,
                    nam_fa = it,
                    nam_en = getStringOrNull("gm_nam_en"),
                    giyahi_ya_shimiyaei = getStringOrNull("gm_giyahi"),
                    image = getStringOrNull("gm_image"),
                    created_at = null,
                    updated_at = null
                )
            },
            goroh_darmani_detail = null
        )
    }

    private fun Cursor.getStringOrNull(name: String): String? {
        val i = getColumnIndex(name)
        return if (i >= 0 && !isNull(i)) getString(i) else null
    }

    private fun Cursor.getStringOrEmpty(index: Int): String =
        if (!isNull(index)) getString(index) else ""

    private fun Cursor.getIntOrNull(name: String): Int? {
        val i = getColumnIndex(name)
        return if (i >= 0 && !isNull(i)) getInt(i) else null
    }

    private companion object {
        const val DB_NAME = "DrugStore.db"
        const val SELECT_WITH_RELATIONS = """
            SELECT d.*,
                   gd.nam AS gd_nam,
                   gm.nam_fa AS gm_nam_fa,
                   gm.nam_en AS gm_nam_en,
                   gm.giyahi_ya_shimiyaei AS gm_giyahi,
                   gm.image AS gm_image
            FROM DrugInfo d
            LEFT JOIN goroh_daroei gd ON gd.cod = d.goroh_daroei_cod
            LEFT JOIN goroh_darmani gm ON gm.cod = d.goroh_darmani_cod
        """
    }
}
