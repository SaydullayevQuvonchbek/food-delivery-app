<!DOCTYPE html>
<html lang="uz">
<head>
    <meta charset="UTF-8">
    <title>Chek #{{ $order->order_number }}</title>
    <style>
        @page {
            size: 80mm auto;
            margin: 0;
        }
        body {
            font-family: 'Courier New', Courier, monospace;
            width: 72mm;
            margin: 0 auto;
            padding: 10px 0;
            color: #000;
            font-size: 12px;
            line-height: 1.3;
        }
        .text-center { text-align: center; }
        .text-right { text-align: right; }
        .bold { font-weight: bold; }
        .divider {
            border-top: 1px dashed #000;
            margin: 8px 0;
        }
        .divider-double {
            border-top: 2px dashed #000;
            margin: 8px 0;
        }
        table {
            width: 100%;
            border-collapse: collapse;
        }
        th, td {
            padding: 3px 0;
        }
        @media print {
            .no-print { display: none; }
        }
    </style>
</head>
<body onload="window.print()">

    <div class="no-print" style="text-align: center; margin-bottom: 10px;">
        <button onclick="window.print()" style="padding: 8px 16px; background: #FF6B00; color: #fff; border: none; font-weight: bold; border-radius: 6px; cursor: pointer;">
            🖨️ Chop etish (Print)
        </button>
    </div>

    <!-- Header -->
    <div class="text-center">
        <h2 style="margin: 0; font-size: 18px;" class="bold">INSOF DELIVERY</h2>
        <p style="margin: 2px 0;">Restoran & Yetkazib Berish</p>
        <p style="margin: 2px 0;">Tel: +998 71 200 00 00</p>
    </div>

    <div class="divider"></div>

    <!-- Order Info -->
    <div>
        <div><span class="bold">Buyurtma №:</span> {{ $order->order_number }}</div>
        <div><span class="bold">Sana:</span> {{ $order->created_at->format('d.m.Y H:i') }}</div>
        <div><span class="bold">Mijoz:</span> {{ $order->user->full_name ?? 'Mijoz' }}</div>
        <div><span class="bold">Tel:</span> {{ $order->user->phone ?? '-' }}</div>
        <div><span class="bold">Manzil:</span> {{ $order->address->address_line ?? 'Standart Manzil' }}</div>
    </div>

    <div class="divider"></div>

    <!-- Items Table -->
    <table>
        <thead>
            <tr class="bold">
                <th style="text-align: left;">Nomi</th>
                <th style="text-align: center;">Soni</th>
                <th class="text-right">Summa</th>
            </tr>
        </thead>
        <tbody>
            @foreach($order->items as $item)
            <tr>
                <td>{{ $item->food->name ?? 'Taom' }}</td>
                <td style="text-align: center;">{{ $item->quantity }}</td>
                <td class="text-right">{{ number_format($item->total_price, 0, '.', ' ') }}</td>
            </tr>
            @endforeach
        </tbody>
    </table>

    <div class="divider"></div>

    <!-- Summary -->
    <table>
        <tr>
            <td>Mahsulotlar:</td>
            <td class="text-right">{{ number_format($order->subtotal, 0, '.', ' ') }} so'm</td>
        </tr>
        <tr>
            <td>Yetkazib berish:</td>
            <td class="text-right">{{ number_format($order->delivery_fee, 0, '.', ' ') }} so'm</td>
        </tr>
        @if($order->discount_amount > 0)
        <tr>
            <td>Chegirma:</td>
            <td class="text-right">-{{ number_format($order->discount_amount, 0, '.', ' ') }} so'm</td>
        </tr>
        @endif
        <tr class="bold" style="font-size: 14px;">
            <td>JAMI:</td>
            <td class="text-right">{{ number_format($order->total_amount, 0, '.', ' ') }} so'm</td>
        </tr>
    </table>

    <div class="divider-double"></div>

    <div class="text-center">
        <p class="bold" style="margin: 2px 0;">To'lov turi: {{ strtoupper($order->payment_method) }}</p>
        <p style="margin: 4px 0; font-size: 11px;">Yoqimli ishtaha tilaymiz!</p>
        <p style="margin: 2px 0; font-size: 10px;">insof-kampot.uz</p>
    </div>

</body>
</html>