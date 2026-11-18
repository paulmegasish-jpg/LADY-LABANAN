package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.AddressEntity
import com.example.data.model.AppSettingsEntity
import com.example.data.model.BannerEntity
import com.example.data.model.CartItemEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.CouponEntity
import com.example.data.model.NotificationEntity
import com.example.data.model.OrderEntity
import com.example.data.model.ProductEntity
import com.example.data.model.ReviewEntity
import com.example.data.model.SupportTicketEntity
import com.example.data.model.UserEntity
import com.example.data.model.WishlistItemEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        CategoryEntity::class,
        ProductEntity::class,
        BannerEntity::class,
        CartItemEntity::class,
        WishlistItemEntity::class,
        AddressEntity::class,
        OrderEntity::class,
        CouponEntity::class,
        ReviewEntity::class,
        SupportTicketEntity::class,
        NotificationEntity::class,
        AppSettingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun catalogDao(): CatalogDao
    abstract fun shoppingDao(): ShoppingDao
    abstract fun engagementDao(): OrderAndEngagementDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context.applicationContext).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            lateinit var db: AppDatabase
            db = Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "lady_lebanan_store.db"
            )
                .addCallback(object : Callback() {
                    override fun onCreate(sqliteDb: SupportSQLiteDatabase) {
                        super.onCreate(sqliteDb)
                        CoroutineScope(Dispatchers.IO).launch {
                            seedInitialData(db)
                        }
                    }
                })
                .build()
            return db
        }

        suspend fun seedIfEmpty(db: AppDatabase) {
            if (db.catalogDao().getCategoriesOnce().isEmpty()) {
                seedInitialData(db)
            }
        }

        private suspend fun seedInitialData(db: AppDatabase) {
            val userDao = db.userDao()
            val catalogDao = db.catalogDao()
            val shoppingDao = db.shoppingDao()
            val engagementDao = db.engagementDao()

            SeedData.defaultUsers.forEach { userDao.upsertUser(it) }
            catalogDao.upsertCategories(SeedData.defaultCategories)
            catalogDao.upsertBanners(SeedData.defaultBanners)
            catalogDao.upsertProducts(SeedData.defaultProducts)
            engagementDao.upsertCoupons(SeedData.defaultCoupons)
            engagementDao.upsertReviews(SeedData.defaultReviews)
            SeedData.defaultAddresses.forEach { shoppingDao.upsertAddress(it) }
            SeedData.defaultOrders.forEach { engagementDao.upsertOrder(it) }
            engagementDao.upsertNotifications(SeedData.defaultNotifications)
            engagementDao.upsertSettings(SeedData.defaultSettings)
        }
    }
}
