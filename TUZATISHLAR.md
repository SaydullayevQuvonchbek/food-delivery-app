# 🔧 Tuzatishlar hisoboti va keyingi ishlar rejasi

**Sana:** 2026-08-26
**Qamrov:** `backend/` (Laravel 11 API) va `mobile/` (Kotlin Multiplatform + Compose)
**Holat:** kod o'zgarishlari kiritildi, ammo **kompilyatsiya qilinmadi** (sabab: [Tekshirilmagan qismlar](#-tekshirilmagan-qismlar))

---

## 📑 Mundarija

1. [Bajarilgan o'zgarishlar](#-bajarilgan-ozgarishlar)
   - [Backend — xavfsizlik](#1-backend--xavfsizlik-kritik)
   - [Backend — biznes mantiq](#2-backend--biznes-mantiq)
   - [Backend — infratuzilma](#3-backend--infratuzilma)
   - [Mobil — API ulanishi](#4-mobil--api-ulanishi)
   - [Mobil — UI muammolari](#5-mobil--ui-muammolari)
   - [O'zgargan fayllar ro'yxati](#6-ozgargan-fayllar-royxati)
2. [Ishga tushirish tartibi](#-ishga-tushirish-tartibi)
3. [Qolgan ishlar (o'zgartirish kerak)](#-qolgan-ishlar-ozgartirish-kerak)
4. [Tekshirilmagan qismlar](#-tekshirilmagan-qismlar)

---

## ✅ Bajarilgan o'zgarishlar

### 1. Backend — xavfsizlik (KRITIK)

#### 1.1 Parolni tiklashda hisobni o'g'irlash imkoni
**Muammo:** `POST /auth/reset-password` faqat `email` + yangi parolni talab qilardi. OTP tasdig'i hech qayerda tekshirilmasdi — ya'ni **istalgan odam istalgan foydalanuvchining emailini bilsa, uning parolini almashtira olardi**. `verifyOtp` esa oddiygina `9627` bilan solishtirardi.

**Yechim** (`app/Http/Controllers/Api/V1/AuthController.php`):
- `forgotPassword` — kod generatsiya qilinadi va `Cache` ga 10 daqiqaga yoziladi (`APP_DEBUG=true` bo'lsa `9627`, aks holda tasodifiy 4 xonali).
- `verifyOtp` — kodni tekshiradi, o'chiradi va **bir martalik `reset_token`** (SHA-256 hash ko'rinishida, 15 daqiqa) beradi.
- `resetPassword` — endi `reset_token` majburiy; muvaffaqiyatdan so'ng foydalanuvchining **barcha tokenlari** bekor qilinadi (boshqa qurilmalardan chiqarish).

#### 1.2 Chat IDOR (begona suhbatlarni o'qish/yozish)
**Muammo:** `GET|POST /chats/{chatId}/messages` chat kimga tegishli ekanini umuman tekshirmasdi. Har qanday autentifikatsiyadan o'tgan foydalanuvchi `chatId` ni almashtirib boshqalarning yozishmalarini o'qiy va ular nomidan yoza olardi.

**Yechim** (`ChatController.php`): `authorizedChat()` — chat faqat `user_id` yoki `courier_id` mos kelganda qaytariladi, aks holda 404. Qo'shimcha: suhbat ochilganda xabarlar "o'qildi" deb belgilanadi, ro'yxatda `unread_count` va `last_message` qaytadi.

#### 1.3 Buyurtma IDOR
**Muammo:** `GET /orders/{id}` `findOrFail($id)` bilan **istalgan** buyurtmani (boshqa mijozning manzili, telefoni, summasi bilan) qaytarardi.

**Yechim** (`OrderController.php`): so'rov `where('user_id', $request->user()->id)` bilan cheklandi.

#### 1.4 Rate-limit (brute-force himoyasi) yo'q edi
**Muammo:** Laravel 11 da API guruhiga sukut bo'yicha throttle qo'shilmaydi — login/register/OTP cheksiz urinishga ochiq edi.

**Yechim** (`routes/api.php`):
| Guruh | Limit |
|---|---|
| Auth (login, register, OTP, reset) | `throttle:6,1` |
| Ochiq katalog | `throttle:60,1` |
| Himoyalangan endpointlar | `throttle:120,1` |

#### 1.5 Kartalar
- `card_number` va CVV endi **hech qachon saqlanmaydi** (avval ham faqat `last_four` yozilardi, lekin validatsiya bo'sh edi).
- `expiry_date` uchun `MM/YY` regex validatsiyasi.
- Karta turi raqamdan aniqlanadi (Visa / Uzcard / Humo / MasterCard).
- "Asosiy karta" mantig'i: birinchi karta avtomatik asosiy bo'ladi, asosiy karta o'chirilsa keyingisi asosiyga o'tadi.
- Bitta foydalanuvchi uchun maksimal 10 ta karta.

#### 1.6 Login qattiqlashtirildi
- `status !== 'active'` bo'lgan hisob kirita olmaydi (403).
- Har login'da eski `mobile_app` tokenlari o'chiriladi (tokenlar to'planib qolmaydi).

---

### 2. Backend — biznes mantiq

#### 2.1 Buyurtma yaratish (`OrderController@store`)
| Muammo | Yechim |
|---|---|
| Buyurtma va uning elementlari **tranzaksiyasiz** yaratilardi (yarim buyurtma qolishi mumkin) | `DB::transaction()` |
| Mavjud bo'lmagan (`is_available = false`) taomga buyurtma berish mumkin edi | tekshiriladi, 422 qaytadi |
| **Boshqa foydalanuvchining `address_id`** sini yuborish mumkin edi | egalik tekshiriladi |
| Promo kodning **amal qilish muddati** (`valid_until`) tekshirilmasdi | tekshiriladi |
| Chegirma buyurtma summasidan katta bo'lishi mumkin edi | `min($discount, $subtotal)` |
| `notes` maydoni qabul qilinmasdi (mijoz izohi yo'qolardi) | qo'shildi |
| Kuryer doim ro'yxatdagi birinchi kuryer edi va `courier_id` buyurtmaga yozilmasdi | faol kuryerlardan tasodifiy tanlanadi, `courier_id` yoziladi |
| Naqd to'lovda ham `payment_status = 'paid'` | naqdda `pending` |
| Yetkazish narxi va soliq stavkasi kod ichida tarqoq | `DELIVERY_FEE` va `TAX_RATE` konstantalari |

#### 2.2 Yangi endpointlar
```
GET  /api/v1/promotions              — faol aksiyalar ro'yxati
POST /api/v1/promotions/validate     — promo kodni savat summasiga nisbatan tekshirish
POST /api/v1/notifications/{id}/read — bitta bildirishnomani o'qilgan deb belgilash
POST /api/v1/notifications/read-all  — barchasini o'qilgan deb belgilash
```
`GET /notifications` javobiga `meta.unread_count` qo'shildi.

#### 2.3 Katalog (`FoodController`)
- `show()` topilmaganda 500 emas, **404 + tushunarli xabar** qaytaradi.
- Tavsiya etilganlar orasida mavjud bo'lmagan taomlar chiqmaydi.
- Ro'yxat `is_featured` va `rating` bo'yicha tartiblanadi, `limit` parametri qo'shildi.
- Ortiqcha `with('category')` olib tashlandi (javob hajmi kichrayadi).

#### 2.4 Seeder
Ikkita ishlaydigan promo kod qo'shildi: **`WELCOME10`** (10%, min 20 000) va **`INSOF5000`** (5 000 so'm, min 30 000).

---

### 3. Backend — infratuzilma

**Muammo:** `.env.example` da `CACHE_STORE=database` va `QUEUE_CONNECTION=database` yozilgan, ammo `cache`, `cache_locks`, `jobs` jadvallari uchun migratsiya **umuman yo'q** edi. Bu holatda:
- `Cache::put()` (OTP kodlari) — xato,
- `throttle` middleware (u ham cache'dan foydalanadi) — **butun API ishlamay qolishi** mumkin edi.

**Yechim:** `database/migrations/2026_08_26_000011_create_cache_and_jobs_tables.php` — standart Laravel jadvallari `Schema::hasTable()` tekshiruvi bilan (mavjud bazaga ham xavfsiz qo'llanadi).

---

### 4. Mobil — API ulanishi

#### 4.1 HTTP klient (`data/network/KtorClient.kt`)
| Muammo | Yechim |
|---|---|
| **Timeout umuman yo'q** — tarmoq uzilsa ilova cheksiz kutib qotardi | `connect 15s / request 30s / socket 30s` |
| Har bir so'rovda `Authorization` qo'lda yozilardi (ba'zi joylarda unutilgan) | `DefaultRequest` orqali avtomatik |
| Server 4xx qaytarsa xato matni o'qilmasdi | `expectSuccess = false` + xatoni parslash |
| Loglar `LogLevel.INFO` — tokenlar logga tushardi | `LogLevel.NONE` |
| Vaqtinchalik 5xx da qayta urinish yo'q | `HttpRequestRetry` (2 marta, eksponensial) |
| `prettyPrint = true` (ortiqcha trafik/CPU) | olib tashlandi |

#### 4.2 Buyurtma "otib yuborilardi" (eng og'ir mantiqiy xato)
**Avval:** `placeOrder()` lokal `Order` obyektini tasodifiy ID bilan yaratardi, savatni tozalardi va ekranni almashtirardi; serverga so'rov esa fonda `try/catch` ichida ketardi. Server 401/422/500 qaytarsa ham foydalanuvchi **"buyurtma qabul qilindi"** ekranini ko'rardi, lekin realda hech qanday buyurtma yo'q edi.

**Endi:** `placeOrder()` — `suspend`, `Result<Order>` qaytaradi, server javobi kutiladi; buyurtma raqami, summasi, kuryeri **serverdan** olinadi; xato bo'lsa foydalanuvchiga ko'rsatiladi va savat tozalanmaydi.

#### 4.3 Demo ma'lumotlar o'rniga jonli API
| Ekran | Avval | Endi |
|---|---|---|
| Kartalar | 3 ta qattiq yozilgan soxta karta | `GET /cards` |
| Bildirishnomalar | 7 ta qattiq yozilgan element | `GET /notifications` |
| Chat ro'yxati | 2 ta qattiq yozilgan qator | `GET /chats` |
| Xabarlar | 4 ta qattiq yozilgan xabar | `GET /chats/{id}/messages` |
| Buyurtmalar | faqat lokal "oxirgi buyurtma" | `GET /orders` |
| Manzillar | umuman ishlatilmasdi | `GET/POST /addresses` |
| Foydalanuvchi | "Albert Stevano Bajefski" | `GET /auth/profile` |

#### 4.4 Boshqa API xatolari
- **Karta o'chirish noto'g'ri kartani o'chirardi:** lokal ID (`ro'yxat.size + 1`) serverga yuborilardi. Endi server bergan haqiqiy `id` ishlatiladi.
- **Karta qo'shishda soxta raqam:** klient serverga `"860012345678" + lastFour` deb **o'ylab topilgan** raqam yuborardi. Endi foydalanuvchi kiritgan raqam (faqat oxirgi 4 tasi saqlanadi).
- **Chat doim `/chats/1/messages`** ga yozardi — boshqa foydalanuvchilar uchun butunlay noto'g'ri suhbat. Endi haqiqiy `chatId`.
- **Parol tiklash qattiq yozilgan `Albertstevano@gmail.com`** ga ketardi (UI'da email kiritish maydoni ham yo'q edi).
- **Soxta muvaffaqiyat:** `forgotPassword`/`resetPassword` tarmoq xatosida ham `Result.success` qaytarardi.
- **401 (token eskirgan)** e'tiborsiz qolardi — ilova "kirgan" holatda qotib qolardi. Endi avtomatik chiqish va login ekraniga qaytish.
- **Bildirishnoma modeli mos emas edi:** server `body`, klient `message` kutardi (matn doim bo'sh chiqardi). Endi `ServerDtos.kt` da mapper.
- **Chat xabarlarida `is_from_me`** serverdan kelmasdi — endi `sender_id` va joriy foydalanuvchi ID'si taqqoslanadi.
- Chiqishda (`logout`) serverga xabar berilmasdi — token serverda tirik qolardi.

#### 4.5 Yangi fayllar
- `domain/models/ServerDtos.kt` — server javoblari uchun DTO'lar + domen modellariga mapperlar.
- `util/Format.kt` — narx/karta/muddat formatlash va validatsiya yordamchilari.
- `iosMain/.../LocalStorage.ios.kt` — **iOS uchun `actual` amalga oshirilishi yo'q edi**, ya'ni `iosX64/iosArm64/iosSimulatorArm64` target'lari umuman kompilyatsiya bo'lmasdi.

---

### 5. Mobil — UI muammolari

#### 5.1 Ishlashga to'sqinlik qiluvchilar
1. **OTP kodini kiritib bo'lmasdi.** `OtpCodeInput` faqat chizilgan katakchalar edi — matn maydoni yo'q, klaviatura ochilmasdi. Kod faqat oldindan `"9627"` yozilgani uchun "ishlagandek" ko'rinardi. Endi katakchalar ustida ko'rinmas `BasicTextField` bor.
2. **Klaviatura maydonlarni yopardi** — hech bir ekranda `imePadding()` yo'q edi (chat, login, register, karta qo'shish, profil).
3. **Kontent status bar ostida qolardi** — `Cart`, `ChatList`, `Profile`, `Notifications`, `Search`, `Cards`, `PersonalData`, `Settings`, `HelpCenter` ekranlarida `statusBarsPadding()` yo'q edi.
4. **Qorong'i tizim rejimida status bar ikonkalari ko'rinmasdi** (oq fonda oq ikonka) — `enableEdgeToEdge` majburiy yorug' rejimga o'tkazildi.

#### 5.2 Pul va hisob-kitob
5. **Narxlar so'mda saqlanib, `$` belgisi bilan** ko'rsatilardi: `$ 12230`. Endi hamma joyda `12 230 so'm`.
6. **Yakuniy summa 4 xil hisoblanardi:**
   | Joy | Formula |
   |---|---|
   | Savat ekrani | `subtotal − 10 900` (soliqsiz) |
   | Checkout ekrani | `subtotal + 2 000 + 10% soliq` (chegirmasiz) |
   | Repository | `subtotal + 2 000 − 3 000` |
   | Server | `subtotal + 0 + 10% soliq − promo` |

   Endi hammasi bitta formulada: **`subtotal + yetkazish + 10% soliq − chegirma`** (`TAX_RATE`, `DELIVERY_FEE` konstantalari).
7. **Promo kod:** "Apply" tugmasi bosilsa, hatto **bo'sh yoki noto'g'ri kod** bilan ham 10 900 so'm chegirma "berilardi" (faqat ekranda). Endi `POST /promotions/validate` orqali server tasdiqlaydi, kod buyurtma bilan birga yuboriladi, bekor qilish mumkin.
8. **Savatdagi checkbox hech narsaga ta'sir qilmasdi** — belgisi olib tashlangan taom baribir summaga kirardi va buyurtmaga ketardi. Endi tanlov holati savatda saqlanadi va faqat tanlanganlar buyurtma qilinadi.
9. Bo'sh savat bilan "Checkout" bosish mumkin edi — tugma endi bloklanadi.

#### 5.3 Navigatsiya va ma'lumot ko'rsatish
10. **Taom tafsiloti noto'g'ri taomni ochardi:** `foods.find { it.id == foodId } ?: foods.firstOrNull()` — agar taom joriy kategoriya ro'yxatida bo'lmasa (masalan qidiruvdan kirilsa), **butunlay boshqa taom** ochilardi. Endi butun katalogdan qidiriladi, topilmasa tushunarli xabar chiqadi.
11. **Qidiruv faqat tanlangan kategoriya ichidan** qidirardi (Pizza tanlangan bo'lsa, "burger" hech qachon topilmasdi). Endi butun katalog bo'ylab.
12. **Har bir harf qidiruv tarixiga yozilardi** (`b`, `bu`, `bur`, `burg`...). Endi faqat qidiruv tasdiqlanganda yoki natija bosilganda; tarix qurilmada saqlanadi.
13. **Kategoriya bo'sh bo'lsa butun ro'yxat ko'rsatilardi** — filtr ishlamayotgandek tuyulardi. Endi bo'sh holat + "Barchasini ko'rish".
14. **Bildirishnomalar ekrani doim bo'sh qolardi:** guruhlash `timeAgo == "Today"` matni bo'yicha edi, serverdan esa sana keladi. Endi sana bo'yicha guruhlanadi, o'qilmaganlar ajratib ko'rsatiladi va ochilganda o'qilgan deb belgilanadi.
15. **Qizil bildirishnoma nuqtasi doim yonib turardi** — endi faqat o'qilmagan bo'lsa.
16. **Kuzatuv ekrani** qattiq yozilgan `283` narx, `"2 Burger With Meat"` va soxta kuryer ismini ko'rsatardi — endi haqiqiy buyurtmadan olinadi.
17. **Chat:** oxirgi xabarga avtomatik tushish yo'q edi; yuborilmagan xabar yo'qolardi. Endi optimistik ko'rsatish + xato bo'lsa matn qaytariladi.
18. **Kartalar ekranidagi "o'chirish" tugmasi** qaysi karta tanlanganidan qat'i nazar **doim birinchisini** o'chirardi. Endi har bir kartaning o'z tugmasi bor.

#### 5.4 Validatsiya va demo qoldiqlari
19. **Login ekrani soxta ma'lumot bilan to'ldirilgan** edi (`Albertstevano@gmail.com` / `password123`) + **"⚡ Demo Fast-Track Sign In"** tugmasi — olib tashlandi.
20. **Karta qo'shish ekrani ham** soxta karta raqami va CVV bilan to'ldirilgan edi — endi bo'sh, validatsiyali (raqamli klaviatura, `MM/YY` formatlash, avtomatik bo'shliq qo'yish).
21. **Register 6 belgi talab qilardi, server 8** — foydalanuvchi har safar server xatosiga urilardi. Moslashtirildi.
22. **Gender erkin matn maydoni edi** — server faqat `Male/Female/Other` qabul qiladi, boshqasi 422 xato. Endi tanlov tugmalari.
23. **Email profil orqali o'zgartirilardi**, lekin server uni qabul qilmasdi — o'zgarish jimgina yo'qolardi. Endi faqat ko'rsatiladi.
24. **Profilni saqlash natijasi tekshirilmasdi** (server xatosi ko'rinmasdi) — endi xato/muvaffaqiyat xabari bor.
25. Ism/parol/email uchun to'g'ri validatsiya (`isValidEmail`), xato bannerlari.

---

### 6. O'zgargan fayllar ro'yxati

**Backend (o'zgargan):**
```
backend/app/Http/Controllers/Api/V1/AuthController.php          — OTP + reset_token oqimi
backend/app/Http/Controllers/Api/V1/ChatController.php          — IDOR tuzatildi, unread_count
backend/app/Http/Controllers/Api/V1/OrderController.php         — IDOR, tranzaksiya, promo, kuryer
backend/app/Http/Controllers/Api/V1/CardController.php          — validatsiya, asosiy karta, limit
backend/app/Http/Controllers/Api/V1/FoodController.php          — 404, tartiblash, limit
backend/app/Http/Controllers/Api/V1/NotificationController.php  — unread_count, read/read-all
backend/routes/api.php                                          — throttle + yangi marshrutlar
backend/database/seeders/DatabaseSeeder.php                     — promo kodlar
```
**Backend (yangi):**
```
backend/app/Http/Controllers/Api/V1/PromotionController.php
backend/database/migrations/2026_08_26_000011_create_cache_and_jobs_tables.php
```
**Mobil (o'zgargan):**
```
mobile/composeApp/src/commonMain/kotlin/com/fooddelivery/
    App.kt                                        — navigatsiya, sessiya tugashi
    components/Components.kt                      — OTP maydoni, header oqib ketishi
    data/network/KtorClient.kt                    — timeout, avtomatik token, retry
    data/repository/FoodDeliveryRepository.kt     — to'liq qayta yozildi (jonli API)
    domain/models/Models.kt                       — reset_token, CVV/PAN olib tashlandi
    presentation/auth/AuthScreens.kt              — parol tiklash oqimi, validatsiya
    presentation/cart/CartScreen.kt               — tanlov, promo, yagona hisob
    presentation/cart/PaymentAddressScreen.kt     — manzil, to'lov usuli, xatolar
    presentation/chat/ChatScreens.kt              — jonli chat
    presentation/food_detail/FoodDetailScreen.kt  — to'g'ri taom
    presentation/home/HomeScreen.kt               — yuklanish/xato/bo'sh holat
    presentation/navigation/Screen.kt             — chatId, contactName
    presentation/notifications/NotificationScreen.kt — server ma'lumotlari
    presentation/profile/CardsScreens.kt          — jonli kartalar, validatsiya
    presentation/profile/ProfileScreens.kt        — saqlash natijasi, gender
    presentation/search/SearchScreen.kt           — global qidiruv
    presentation/tracking/DeliveryTrackingScreen.kt — haqiqiy buyurtma
mobile/composeApp/src/androidMain/kotlin/com/fooddelivery/MainActivity.kt — status bar
```
**Mobil (yangi):**
```
mobile/composeApp/src/commonMain/kotlin/com/fooddelivery/domain/models/ServerDtos.kt
mobile/composeApp/src/commonMain/kotlin/com/fooddelivery/util/Format.kt
mobile/composeApp/src/iosMain/kotlin/com/fooddelivery/data/storage/LocalStorage.ios.kt
```

---

## 🚀 Ishga tushirish tartibi

### 1. Backend
```bash
cd backend
php artisan migrate
php artisan db:seed
php artisan config:clear && php artisan route:clear
```

### 2. Mobil ilova
```bash
cd mobile
./gradlew :composeApp:assembleDebug
```
APK: `mobile/composeApp/build/outputs/apk/debug/composeApp-debug.apk`

### 3. Serverdagi `.env` uchun majburiy tekshiruv
```env
APP_DEBUG=false          # true bo'lsa OTP kodi javobda ochiq ko'rinadi (demo_otp)
CACHE_STORE=database     # cache jadvali endi mavjud
```
> ⚠️ `APP_DEBUG=false` bo'lganda OTP tasodifiy generatsiya qilinadi, lekin **hali hech qayerga yuborilmaydi** — pastdagi "Qolgan ishlar" 1-bandiga qarang.

---

## 📋 Qolgan ishlar (o'zgartirish kerak)

### 🔴 Kritik — mahsulotga chiqarishdan oldin

| # | Ish | Izoh |
|---|---|---|
| 1 | **OTP yuborish kanalini ulash** | Hozir kod faqat cache'da yaratiladi. `MAIL_MAILER=smtp`, `MAIL_HOST=127.0.0.1` — ishlaydigan pochta serveri emas. Email (yoki SMS/Eskiz.uz) integratsiyasi kerak, aks holda `APP_DEBUG=false` da foydalanuvchi kodni ololmaydi. |
| 2 | **Haqiqiy to'lov tizimi** | `payment_status = 'paid'` hech qanday tranzaksiyasiz yoziladi. Payme / Click / Uzum integratsiyasi va webhook kerak. Karta ma'lumotlari to'lov provayderi orqali (tokenizatsiya) olinishi shart. |
| 3 | **Tokenni shifrlangan xotirada saqlash** | Hozir `SharedPreferences` da ochiq matnda. `EncryptedSharedPreferences` (Android) / Keychain (iOS) ga o'tkazish. |
| 4 | **`AndroidManifest.xml` ni tozalash** | `usesCleartextTraffic="true"` — HTTPS ishlatilayotgani uchun olib tashlash kerak; `allowBackup="true"` token zaxira nusxaga tushishiga yo'l qo'yadi (`false` yoki `backup_rules.xml`). |
| 5 | **Ilova ikonkasi** | Hozir Android tizimining standart ikonkasi (`@android:drawable/sym_def_app_icon`). |
| 6 | **Release build sozlamalari** | `isMinifyEnabled = false`, ProGuard qoidalari yo'q, imzolash (`signingConfig`) sozlanmagan, `versionCode = 1` qotib turibdi. |
| 7 | **Demo hisoblarni o'chirish** | Seeder `Albertstevano@gmail.com / password123` va `admin@insof.uz / admin12345` yaratadi — prod bazada o'chirish yoki parolni almashtirish shart. |
| 8 | **CORS** | `config/cors.php` da `allowed_origins = ['*']`. Admin panel domeni bilan cheklash tavsiya etiladi. |

### 🟠 Muhim — foydalanuvchi tajribasiga sezilarli ta'sir qiladi

| # | Ish | Izoh |
|---|---|---|
| 9 | **Taom rasmlari ko'rsatilmaydi** | Bazada `image_url` bor, `coil3` kutubxonasi `build.gradle.kts` ga ulangan, lekin UI'da hamma joyda **emoji** turibdi (🍔🌮🥤🍕). `AsyncImage` bilan almashtirish kerak. |
| 10 | **Buyurtmalar tarixi ekrani yo'q** | `repository.orders` serverdan yuklanadi, lekin uni ko'rsatadigan ekran mavjud emas (profilda faqat bitta faol buyurtma). |
| 11 | **Kuzatuv xaritasi soxta** | `Canvas` bilan chizilgan simulyatsiya; markerlar qat'iy `dp` koordinatalarda (kichik ekranlarda joyidan chiqadi). Serverdagi `deliveries.current_lat/lng` ishlatilmayapti. Google Maps / MapLibre integratsiyasi kerak. |
| 12 | **Chat real-time emas** | Xabarlar faqat ekran ochilganda yuklanadi. Polling (masalan 5 soniyada) yoki Laravel Reverb / Pusher WebSocket kerak. |
| 13 | **Push bildirishnomalar yo'q** | FCM ulanmagan — buyurtma holati o'zgarganda foydalanuvchi bilmaydi. |
| 14 | **Sevimlilar (favorite) faqat xotirada** | Server tomonda endpoint ham, jadval ham yo'q; ilova qayta ishga tushsa yo'qoladi. |
| 15 | **Manzillarni boshqarish UI'si** | `DELETE /addresses/{id}` bor, lekin ilovada manzilni tahrirlash/o'chirish/tanlash ekrani yo'q — faqat birinchi manzil ishlatiladi. Xaritadan tanlash ham yo'q. |
| 16 | **Savat qurilma xotirasida saqlanmaydi** | Ilova yopilsa savat yo'qoladi (`LocalStorage` ga yozish kerak). |
| 17 | **Til aralash** | Interfeys ingliz + o'zbek aralash ("My Cart", "Muloqotlar"). Sozlamalardagi til tanlash **hech narsani o'zgartirmaydi** — `compose.components.resources` orqali lokalizatsiya kerak. |
| 18 | **Sozlamalar tugmalari ishlamaydi** | Push/Location toggle'lari hech qayerga saqlanmaydi; "Request Account Deletion", "Add another account", "Privacy Policy", "Terms" — bo'sh `onClick`. |
| 19 | **Ijtimoiy tarmoq orqali kirish** | Google/Facebook/Apple tugmalari bezak; ulanmasa yashirish tavsiya etiladi. |
| 20 | **Pull-to-refresh yo'q** | Katalogni yangilash faqat ilova ochilganda. |

### 🟡 Yaxshilash — arxitektura va sifat

| # | Ish | Izoh |
|---|---|---|
| 21 | **DI va ViewModel qatlami** | `Koin` ulangan, lekin ishlatilmaydi; butun holat bitta `FoodDeliveryRepository` ("god object") ichida va `remember` bilan yaratiladi. Ekranlar uchun ViewModel'lar ajratish testlash va holat saqlashni yaxshilaydi. |
| 22 | **Serverdagi qidiruv ishlatilmaydi** | `GET /foods?search=` mavjud, klient esa lokal filtr qiladi — katalog kattalashsa muammo bo'ladi (pagination ham yo'q). |
| 23 | **Yetkazish narxi va soliq qattiq yozilgan** | Klientda ham, serverda ham konstanta. Sozlamalar jadvalidan (`admin/settings`) olinishi kerak. |
| 24 | **Bosh sahifadagi banner statik** | `GET /promotions` dan haqiqiy aksiyalarni ko'rsatish mumkin. |
| 25 | **Avtomatik testlar yo'q** | Backend uchun feature testlar (auth, buyurtma, IDOR ssenariylari), mobil uchun repository unit testlari. |
| 26 | **Taom tafsilotidagi rasm karuseli soxta** | Doim 3 ta nuqta, birinchisi faol. |
| 27 | **Kuryer ilovasi yo'q** | Bazada `role = courier` bor, admin panelda kuryerlar bor, lekin kuryer uchun interfeys mavjud emas. |
| 28 | **`throttle:6,1` juda qattiq bo'lishi mumkin** | Bir IP dan (masalan umumiy Wi-Fi) bir necha foydalanuvchi kirsa cheklovga urilishi mumkin — kuzatib, kerak bo'lsa `10,1` ga oshirish. |
| 29 | **iOS target sinalmagan** | `LocalStorage` qo'shildi, lekin `BackHandler` bo'sh (`iosMain`), Xcode loyihasi yig'ilishi tekshirilmagan. |

---

## ⚠️ Tekshirilmagan qismlar

1. **Kompilyatsiya qilinmadi.** Ushbu muhitda Gradle umuman ishga tushmadi (hatto `./gradlew help` ham):
   ```
   java.io.IOException: Unable to establish loopback connection
   ```
   Gradle demoni JVM ichidagi `sun.nio.ch.PipeImpl` loopback soketini yarata olmayapti (boshqa jarayonlar uchun loopback ishlaydi). Android Studio'ning `jbr` papkasi ham to'liq emas (`lib/jvm.cfg` yo'q). Shuning uchun o'zgarishlar **qo'lda statik tekshiruvdan** o'tkazildi: importlar, funksiya imzolari, `null`-xavfsizlik, qavslar balansi. Birinchi `assembleDebug` da qolgan mayda xatolar chiqishi mumkin — chiqsa, xato matni bilan murojaat qiling.

2. **PHP sintaksisi `php -l` bilan tekshirilmadi** — mashinada PHP CLI topilmadi (faqat qavslar balansi tekshirildi).

3. **Admin panel (`app/Http/Controllers/Admin/*`, `resources/views`) chuqur tahlil qilinmadi** — asosiy e'tibor mobil ilova va uning API'siga qaratildi. Admin paneldagi chat javoblari va buyurtma holatini o'zgartirish API bilan mos ishlaydi, lekin uning o'z xavfsizlik auditi alohida o'tkazilishi kerak.

4. **Jonli serverda sinalmadi** — `https://insof-kampot.uz/api/v1` ga hech qanday so'rov yuborilmadi.
