# 🍔 Food Delivery App — Backend API (Laravel 11)

Ushbu qism **Food Delivery App** mobil ilovasi uchun ishlab chiqilgan yuqori tezlikdagi, xavfsiz va to'liq RESTful API hisoblanadi.

---

## 🚀 1. VIP Serverga Joylashtirish Qo'llanmasi (Deployment Guide)

VIP serveringizda boshqa ilovalarning API lari ham ishlayotganligi sababli, ushbu loyiha **to'liq izolyatsiya qilingan** holda ishga tushiriladi:

### 1-qadam: Kodni serverga yuklash
```bash
cd /var/www
git clone <sizning-repo-linki> food-delivery-api
cd food-delivery-api/backend
```

### 2-qadam: Kutubxonalarni o'rnatish va muhitni sozlash
```bash
composer install --optimize-autoloader --no-dev
cp .env.example .env
php artisan key:generate
```

### 3-qadam: Izolyatsiyalangan Ma'lumotlar Bazasi (MySQL)
Mavjud boshqa bazalarga ta'sir qilmaslik uchun alohida baza va foydalanuvchi yarating:
```sql
CREATE DATABASE food_delivery_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'food_delivery_user'@'localhost' IDENTIFIED BY 'Kuchli_Parol_123!';
GRANT ALL PRIVILEGES ON food_delivery_db.* TO 'food_delivery_user'@'localhost';
FLUSH PRIVILEGES;
```
`.env` faylida ushbu ma'lumotlarni kiriting:
```ini
DB_DATABASE=food_delivery_db
DB_USERNAME=food_delivery_user
DB_PASSWORD=Kuchli_Parol_123!
```

### 4-qadam: Migratsiyalar va Sinov Ma'lumotlarini yuklash (Seeder)
```bash
php artisan migrate:fresh --seed
php artisan storage:link
```

### 5-qadam: Nginx Virtual Host Konfiguratsiyasi
Serverdagi boshqa saytlarga xalaqit bermaydigan yangi subdomain (masalan: `food-api.sizningdomeningiz.uz`):
```nginx
server {
    listen 80;
    server_name food-api.sizningdomeningiz.uz;
    root /var/www/food-delivery-api/backend/public;

    add_header X-Frame-Options "SAMEORIGIN";
    add_header X-Content-Type-Options "nosniff";

    index index.php;

    charset utf-8;

    location / {
        try_files $uri $uri/ /index.php?$query_string;
    }

    location = /favicon.ico { access_log off; log_not_found off; }
    location = /robots.txt  { access_log off; log_not_found off; }

    error_page 404 /index.php;

    location ~ \.php$ {
        fastcgi_pass unix:/var/run/php/php8.2-fpm.sock; # yoki php8.3-fpm
        fastcgi_param SCRIPT_FILENAME $realpath_root$fastcgi_script_name;
        include fastcgi_params;
        fastcgi_hide_header X-Powered-By;
    }

    location ~ /\.(?!well-known).* {
        deny all;
    }
}
```

Nginx ni qayta ishga tushiring va SSL o'rnating:
```bash
sudo nginx -t && sudo systemctl reload nginx
sudo certbot --nginx -d food-api.sizningdomeningiz.uz
```

---

## 📡 2. Asosiy API Endpointlar

| Metod | URL | Tavsif | Autentifikatsiya |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/auth/register` | Ro'yxatdan o'tish | Ochiq |
| `POST` | `/api/v1/auth/login` | Tizimga kirish | Ochiq |
| `POST` | `/api/v1/auth/forgot-password` | OTP kod so'rash | Ochiq |
| `POST` | `/api/v1/auth/verify-otp` | 4 xonali OTP tekshirish | Ochiq |
| `POST` | `/api/v1/auth/reset-password` | Yangi parol o'rnatish | Ochiq |
| `GET` | `/api/v1/categories` | Kategoriyalar ro'yxati | Ochiq |
| `GET` | `/api/v1/foods` | Taomlar ro'yxati (filtrlash bilan) | Ochiq |
| `GET` | `/api/v1/foods/{id}` | Taom sahifasi + tavsiyalar | Ochiq |
| `GET` | `/api/v1/auth/profile` | Profil ma'lumotlari | Bearer Token |
| `PUT` | `/api/v1/auth/profile` | Profilni yangilash | Bearer Token |
| `POST` | `/api/v1/orders` | Buyurtma berish | Bearer Token |
| `GET` | `/api/v1/orders` | Buyurtmalar tarixi | Bearer Token |
| `GET` | `/api/v1/cards` | Saqlangan to'lov kartalari | Bearer Token |
| `POST` | `/api/v1/cards` | Yangi karta qo'shish | Bearer Token |
| `DELETE` | `/api/v1/cards/{id}` | Kartani o'chirish | Bearer Token |
| `GET` | `/api/v1/chats` | Chatlar ro'yxati | Bearer Token |
| `GET` | `/api/v1/chats/{id}/messages` | Xabarlar | Bearer Token |
| `POST` | `/api/v1/chats/{id}/messages` | Xabar yuborish | Bearer Token |
| `GET` | `/api/v1/notifications` | Bildirishnomalar | Bearer Token |