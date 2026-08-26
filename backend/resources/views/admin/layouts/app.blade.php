<!DOCTYPE html>
<html lang="uz">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>@yield('title', 'Admin Panel') - Insof Delivery</title>
    <!-- Tailwind CSS -->
    <script src="https://cdn.tailwindcss.com"></script>
    <script>
        tailwind.config = {
            theme: {
                extend: {
                    colors: {
                        primary: '#FF6B00',
                        'primary-dark': '#E05E00',
                        'primary-light': '#FFF0E6',
                        dark: '#1E293B',
                    }
                }
            }
        }
    </script>
    <!-- FontAwesome Icons -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <!-- Alpine.js -->
    <script defer src="https://cdn.jsdelivr.net/npm/alpinejs@3.x.x/dist/cdn.min.js"></script>
    <style>
        [x-cloak] { display: none !important; }
        @keyframes pulse-ring {
            0% { transform: scale(0.95); opacity: 0.8; }
            50% { transform: scale(1.15); opacity: 0.3; }
            100% { transform: scale(0.95); opacity: 0.8; }
        }
        .pulse-active { animation: pulse-ring 2s infinite ease-in-out; }
    </style>
</head>
<body class="bg-slate-100 text-slate-800 antialiased font-sans flex h-screen overflow-hidden" x-data="adminApp()">

    <!-- Sidebar -->
    <aside class="w-64 bg-slate-900 text-slate-300 flex flex-col flex-shrink-0 z-20 shadow-xl transition-all duration-300">
        <!-- Logo -->
        <div class="h-16 flex items-center justify-between px-6 bg-slate-950 border-b border-slate-800">
            <div class="flex items-center space-x-3">
                <div class="w-9 h-9 rounded-xl bg-primary flex items-center justify-center text-white font-bold text-xl shadow-lg shadow-orange-500/30">
                    <i class="fa-solid fa-utensils"></i>
                </div>
                <div>
                    <h1 class="font-extrabold text-white text-base tracking-wide">INSOF</h1>
                    <p class="text-[10px] text-orange-400 font-semibold tracking-wider uppercase">Delivery Admin</p>
                </div>
            </div>
        </div>

        <!-- Navigation Links -->
        <nav class="flex-1 px-4 py-6 space-y-1.5 overflow-y-auto">
            <a href="{{ route('admin.dashboard') }}" class="flex items-center space-x-3 px-3.5 py-3 rounded-xl text-sm font-medium transition-all {{ request()->routeIs('admin.dashboard') ? 'bg-primary text-white shadow-lg shadow-orange-500/30 font-semibold' : 'text-slate-400 hover:bg-slate-800 hover:text-white' }}">
                <i class="fa-solid fa-chart-pie w-5 text-center text-base"></i>
                <span>Dashboard</span>
            </a>

            <a href="{{ route('admin.orders.index') }}" class="flex items-center justify-between px-3.5 py-3 rounded-xl text-sm font-medium transition-all {{ request()->routeIs('admin.orders.*') ? 'bg-primary text-white shadow-lg shadow-orange-500/30 font-semibold' : 'text-slate-400 hover:bg-slate-800 hover:text-white' }}">
                <div class="flex items-center space-x-3">
                    <i class="fa-solid fa-receipt w-5 text-center text-base"></i>
                    <span>Buyurtmalar</span>
                </div>
                <template x-if="pendingCount > 0">
                    <span class="bg-red-500 text-white text-xs font-bold px-2 py-0.5 rounded-full animate-bounce" x-text="pendingCount"></span>
                </template>
            </a>

            <a href="{{ route('admin.foods.index') }}" class="flex items-center space-x-3 px-3.5 py-3 rounded-xl text-sm font-medium transition-all {{ request()->routeIs('admin.foods.*') ? 'bg-primary text-white shadow-lg shadow-orange-500/30 font-semibold' : 'text-slate-400 hover:bg-slate-800 hover:text-white' }}">
                <i class="fa-solid fa-burger w-5 text-center text-base"></i>
                <span>Taomlar Menyusi</span>
            </a>

            <a href="{{ route('admin.categories.index') }}" class="flex items-center space-x-3 px-3.5 py-3 rounded-xl text-sm font-medium transition-all {{ request()->routeIs('admin.categories.*') ? 'bg-primary text-white shadow-lg shadow-orange-500/30 font-semibold' : 'text-slate-400 hover:bg-slate-800 hover:text-white' }}">
                <i class="fa-solid fa-layer-group w-5 text-center text-base"></i>
                <span>Kategoriyalar</span>
            </a>

            <a href="{{ route('admin.couriers.index') }}" class="flex items-center space-x-3 px-3.5 py-3 rounded-xl text-sm font-medium transition-all {{ request()->routeIs('admin.couriers.*') ? 'bg-primary text-white shadow-lg shadow-orange-500/30 font-semibold' : 'text-slate-400 hover:bg-slate-800 hover:text-white' }}">
                <i class="fa-solid fa-motorcycle w-5 text-center text-base"></i>
                <span>Kuryerlar</span>
            </a>

            <a href="{{ route('admin.chat.index') }}" class="flex items-center space-x-3 px-3.5 py-3 rounded-xl text-sm font-medium transition-all {{ request()->routeIs('admin.chat.*') ? 'bg-primary text-white shadow-lg shadow-orange-500/30 font-semibold' : 'text-slate-400 hover:bg-slate-800 hover:text-white' }}">
                <i class="fa-solid fa-comments w-5 text-center text-base"></i>
                <span>Mijozlar bilan Chat</span>
            </a>

            <a href="{{ route('admin.settings.index') }}" class="flex items-center space-x-3 px-3.5 py-3 rounded-xl text-sm font-medium transition-all {{ request()->routeIs('admin.settings.*') ? 'bg-primary text-white shadow-lg shadow-orange-500/30 font-semibold' : 'text-slate-400 hover:bg-slate-800 hover:text-white' }}">
                <i class="fa-solid fa-gear w-5 text-center text-base"></i>
                <span>Sozlamalar</span>
            </a>
        </nav>

        <!-- User Profile & Logout -->
        <div class="p-4 border-t border-slate-800 bg-slate-950/60">
            <div class="flex items-center justify-between">
                <div class="flex items-center space-x-3 overflow-hidden">
                    <div class="w-9 h-9 rounded-full bg-slate-700 flex items-center justify-center font-bold text-white uppercase text-sm flex-shrink-0">
                        {{ substr(Auth::user()->full_name ?? 'A', 0, 1) }}
                    </div>
                    <div class="truncate">
                        <p class="text-xs font-semibold text-white truncate">{{ Auth::user()->full_name ?? 'Admin' }}</p>
                        <p class="text-[10px] text-slate-400 capitalize">{{ Auth::user()->role ?? 'Manager' }}</p>
                    </div>
                </div>
                <form action="{{ route('admin.logout') }}" method="POST" class="inline">
                    @csrf
                    <button type="submit" title="Chiqish" class="p-2 text-slate-400 hover:text-red-400 hover:bg-slate-800 rounded-lg transition-colors">
                        <i class="fa-solid fa-right-from-bracket"></i>
                    </button>
                </form>
            </div>
        </div>
    </aside>

    <!-- Main Content Area -->
    <div class="flex-1 flex flex-col min-w-0 overflow-hidden">
        <!-- Top Navbar -->
        <header class="h-16 bg-white border-b border-slate-200 flex items-center justify-between px-6 z-10">
            <div class="flex items-center space-x-4">
                <h2 class="text-lg font-bold text-slate-800">@yield('title')</h2>
                <div class="flex items-center space-x-2 text-xs font-medium text-emerald-600 bg-emerald-50 px-2.5 py-1 rounded-full border border-emerald-200">
                    <span class="w-2 h-2 rounded-full bg-emerald-500 animate-pulse"></span>
                    <span>Tizim Faol</span>
                </div>
            </div>

            <div class="flex items-center space-x-4">
                <!-- Sound Toggle -->
                <button @click="toggleSound()" class="flex items-center space-x-2 px-3 py-1.5 rounded-lg border text-xs font-medium transition" :class="soundEnabled ? 'border-orange-200 bg-orange-50 text-primary' : 'border-slate-200 bg-slate-50 text-slate-500'">
                    <i class="fa-solid" :class="soundEnabled ? 'fa-volume-high' : 'fa-volume-xmark'"></i>
                    <span x-text="soundEnabled ? 'Ovoz: Yoqiq' : 'Ovoz: O\'chiq'"></span>
                </button>

                <!-- Current Clock -->
                <div class="text-xs font-semibold text-slate-500 bg-slate-100 px-3 py-1.5 rounded-lg" x-text="currentTime"></div>
            </div>
        </header>

        <!-- Alert messages -->
        @if(session('success'))
            <div class="mx-6 mt-4 p-4 bg-emerald-50 border border-emerald-200 text-emerald-800 rounded-xl flex items-center justify-between text-sm shadow-sm">
                <div class="flex items-center space-x-3">
                    <i class="fa-solid fa-circle-check text-emerald-500 text-base"></i>
                    <span>{{ session('success') }}</span>
                </div>
                <button onclick="this.parentElement.remove()" class="text-emerald-500 hover:text-emerald-700"><i class="fa-solid fa-xmark"></i></button>
            </div>
        @endif

        @if($errors->any())
            <div class="mx-6 mt-4 p-4 bg-red-50 border border-red-200 text-red-800 rounded-xl flex items-center justify-between text-sm shadow-sm">
                <div class="flex items-center space-x-3">
                    <i class="fa-solid fa-triangle-exclamation text-red-500 text-base"></i>
                    <span>{{ $errors->first() }}</span>
                </div>
                <button onclick="this.parentElement.remove()" class="text-red-500 hover:text-red-700"><i class="fa-solid fa-xmark"></i></button>
            </div>
        @endif

        <!-- Main View Content -->
        <main class="flex-1 overflow-y-auto p-6">
            @yield('content')
        </main>
    </div>

    <!-- Live Polling & Sound Script -->
    <script>
        function adminApp() {
            return {
                pendingCount: 0,
                lastOrderId: null,
                soundEnabled: true,
                currentTime: '',
                init() {
                    this.updateTime();
                    setInterval(() => this.updateTime(), 1000);
                    this.pollNewOrders();
                    setInterval(() => this.pollNewOrders(), 5000);
                },
                updateTime() {
                    const now = new Date();
                    this.currentTime = now.toLocaleTimeString('uz-UZ', { hour: '2-digit', minute: '2-digit', second: '2-digit' });
                },
                toggleSound() {
                    this.soundEnabled = !this.soundEnabled;
                },
                playBellSound() {
                    if (!this.soundEnabled) return;
                    try {
                        const ctx = new (window.AudioContext || window.webkitAudioContext)();
                        const osc = ctx.createOscillator();
                        const gain = ctx.createGain();
                        osc.type = 'sine';
                        osc.frequency.setValueAtTime(880, ctx.currentTime); // A5 note
                        osc.frequency.exponentialRampToValueAtTime(440, ctx.currentTime + 0.5);
                        gain.gain.setValueAtTime(0.3, ctx.currentTime);
                        gain.gain.exponentialRampToValueAtTime(0.01, ctx.currentTime + 0.5);
                        osc.connect(gain);
                        gain.connect(ctx.destination);
                        osc.start();
                        osc.stop(ctx.currentTime + 0.5);
                    } catch (e) {
                        console.log('Audio error', e);
                    }
                },
                pollNewOrders() {
                    fetch('{{ route('admin.live-stats') }}')
                        .then(r => r.json())
                        .then(data => {
                            this.pendingCount = data.pending_count;
                            if (this.lastOrderId !== null && data.latest_order_id && data.latest_order_id > this.lastOrderId) {
                                this.playBellSound();
                                if (Notification.permission === "granted") {
                                    new Notification("Yangi Buyurtma!", {
                                        body: `№ ${data.latest_order_number} - ${data.latest_order_total}`,
                                        icon: "/favicon.ico"
                                    });
                                }
                            }
                            this.lastOrderId = data.latest_order_id;
                        })
                        .catch(err => console.log('Poll error', err));
                }
            }
        }
    </script>
</body>
</html>