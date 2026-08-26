<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\Hash;
use App\Models\User;
use App\Models\Category;
use App\Models\Food;
use App\Models\UserAddress;
use App\Models\SavedCard;
use App\Models\AppNotification;
use App\Models\Chat;
use App\Models\Message;

class DatabaseSeeder extends Seeder
{
    public function run(): void
    {
        // 1. Create Customer
        $user = User::create([
            'full_name' => 'Albert Stevano Bajefski',
            'email' => 'Albertstevano@gmail.com',
            'phone' => '+1 325-433-7656',
            'date_of_birth' => '19/06/1999',
            'gender' => 'Male',
            'password' => Hash::make('password123'),
            'role' => 'customer',
            'status' => 'active'
        ]);

        // 2. Create Couriers
        $courier1 = User::create([
            'full_name' => 'Cristopert Dastin',
            'email' => 'cristopert.courier@fooddelivery.com',
            'phone' => '+1 234 567 8900',
            'password' => Hash::make('password123'),
            'role' => 'courier',
            'status' => 'active'
        ]);

        $courier2 = User::create([
            'full_name' => 'Stevano Clirover',
            'email' => 'stevano.courier@fooddelivery.com',
            'phone' => '+1 234 567 8901',
            'password' => Hash::make('password123'),
            'role' => 'courier',
            'status' => 'active'
        ]);

        // 3. User Address
        UserAddress::create([
            'user_id' => $user->id,
            'label' => 'Home',
            'address_line' => 'New York',
            'house_number' => 'BC54 Berlin',
            'city' => 'New York City',
            'latitude' => 40.7128,
            'longitude' => -74.0060,
            'is_default' => true
        ]);

        // 4. Categories
        $burgerCat = Category::create(['name' => 'Burger', 'icon' => '🍔', 'order_index' => 1, 'is_active' => true]);
        $tacoCat = Category::create(['name' => 'Taco', 'icon' => '🌮', 'order_index' => 2, 'is_active' => true]);
        $drinkCat = Category::create(['name' => 'Drink', 'icon' => '🥤', 'order_index' => 3, 'is_active' => true]);
        $pizzaCat = Category::create(['name' => 'Pizza', 'icon' => '🍕', 'order_index' => 4, 'is_active' => true]);

        // 5. Foods
        Food::create([
            'category_id' => $burgerCat->id,
            'name' => 'Burger With Meat 🍔',
            'description' => 'Burger With Meat is a typical food from our restaurant that is much in demand by many people, this is very recommended for you.',
            'price' => 12230.00,
            'original_price' => 15000.00,
            'rating' => 4.5,
            'review_count' => 240,
            'distance' => '190m',
            'delivery_time' => '20 - 30 min',
            'is_free_delivery' => true,
            'is_featured' => true,
            'is_available' => true
        ]);

        Food::create([
            'category_id' => $burgerCat->id,
            'name' => 'Ordinary Burgers',
            'description' => 'Classic beef burger with fresh crisp lettuce, juicy tomatoes, and our signature secret sauce.',
            'price' => 17230.00,
            'original_price' => 17230.00,
            'rating' => 4.9,
            'review_count' => 180,
            'distance' => '190m',
            'delivery_time' => '15 - 25 min',
            'is_free_delivery' => true,
            'is_featured' => true,
            'is_available' => true
        ]);

        Food::create([
            'category_id' => $tacoCat->id,
            'name' => 'Crispy Beef Taco',
            'description' => 'Crispy corn taco shell packed with seasoned ground beef, shredded cheddar, lettuce and salsa.',
            'price' => 9800.00,
            'rating' => 4.7,
            'review_count' => 95,
            'distance' => '300m',
            'delivery_time' => '15 - 20 min',
            'is_free_delivery' => true,
            'is_available' => true
        ]);

        Food::create([
            'category_id' => $drinkCat->id,
            'name' => 'Fresh Citrus Cooler',
            'description' => 'Chilled freshly squeezed orange and lime juice with crushed ice and mint.',
            'price' => 4500.00,
            'rating' => 4.9,
            'review_count' => 75,
            'distance' => '190m',
            'delivery_time' => '10 - 15 min',
            'is_free_delivery' => true,
            'is_available' => true
        ]);

        Food::create([
            'category_id' => $pizzaCat->id,
            'name' => 'Pepperoni Supreme Pizza',
            'description' => 'Hand-tossed pizza crust loaded with rich tomato sauce, mozzarella and spicy pepperoni.',
            'price' => 28000.00,
            'rating' => 4.9,
            'review_count' => 420,
            'distance' => '400m',
            'delivery_time' => '25 - 40 min',
            'is_free_delivery' => true,
            'is_featured' => true,
            'is_available' => true
        ]);

        // 6. Saved Cards
        SavedCard::create([
            'user_id' => $user->id,
            'card_holder_name' => 'Albert Stevano Bajefski',
            'last_four' => '8374',
            'expiry_date' => '11/24',
            'card_type' => 'MasterCard',
            'is_default' => true
        ]);
        SavedCard::create([
            'user_id' => $user->id,
            'card_holder_name' => 'Albert Stevano',
            'last_four' => '7873',
            'expiry_date' => '08/26',
            'card_type' => 'MasterCard',
            'is_default' => false
        ]);
        SavedCard::create([
            'user_id' => $user->id,
            'card_holder_name' => 'Albert Stevano',
            'last_four' => '4672',
            'expiry_date' => '05/27',
            'card_type' => 'Paypal',
            'is_default' => false
        ]);
        SavedCard::create([
            'user_id' => $user->id,
            'card_holder_name' => 'Albert Stevano',
            'last_four' => '4672',
            'expiry_date' => '05/27',
            'card_type' => 'Apple Pay',
            'is_default' => false
        ]);

        // 7. Notifications
        AppNotification::create([
            'user_id' => $user->id,
            'title' => '30% Special Discount!',
            'body' => 'Special promotion only valid today',
            'type' => 'DISCOUNT'
        ]);
        AppNotification::create([
            'user_id' => $user->id,
            'title' => 'Your Order Has Been Taken by the Driver',
            'body' => 'Recently',
            'type' => 'ORDER_TAKEN'
        ]);
        AppNotification::create([
            'user_id' => $user->id,
            'title' => 'Your Order Has Been Canceled',
            'body' => '19 Jun 2023',
            'type' => 'ORDER_CANCELED'
        ]);
        AppNotification::create([
            'user_id' => $user->id,
            'title' => '35% Special Discount!',
            'body' => 'Special promotion only valid today',
            'type' => 'DISCOUNT'
        ]);
        AppNotification::create([
            'user_id' => $user->id,
            'title' => 'Account Setup Successfull!',
            'body' => 'Special promotion only valid today',
            'type' => 'ACCOUNT'
        ]);

        // 8. Chat & Messages
        $chat = Chat::create([
            'user_id' => $user->id,
            'courier_id' => $courier2->id
        ]);

        Message::create([
            'chat_id' => $chat->id,
            'sender_id' => $courier2->id,
            'text' => 'Just to order',
            'is_read' => true
        ]);
        Message::create([
            'chat_id' => $chat->id,
            'sender_id' => $user->id,
            'text' => 'Okay, for what level of spiciness?',
            'is_read' => true
        ]);
        Message::create([
            'chat_id' => $chat->id,
            'sender_id' => $courier2->id,
            'text' => 'Okay, Wait a minute 🙏',
            'is_read' => true
        ]);
        Message::create([
            'chat_id' => $chat->id,
            'sender_id' => $user->id,
            'text' => "Okay, I'm waiting 🙌",
            'is_read' => true
        ]);
    }
}