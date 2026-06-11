# Sunmi Core (expo-sunmi-core)

A high-performance, modern React Native / Expo library for natively controlling Sunmi POS devices (such as V2, V2s, T2, etc.).

This library uses the official Sunmi Java SDKs under the hood and is specifically built for modern Expo projects (SDK 50+) and Android 14+. No more outdated dependencies (`jcenter()`)!

## Features

- 🖨️ **Receipt Printer:** Print ESC/POS text effortlessly.
- 📱 **QR Codes:** Native printing of QR codes.
- 💵 **Cash Drawer:** Send RJ11 pulses to open the cash drawer.
- 📊 **Printer Status:** Read hardware sensors (out of paper, cover open, overheating).
- 📷 **Barcode Scanner:** Reactive BroadcastReceiver for background hardware laser scans (no input focus required!).

---

## Installation

Since this library is built as an Expo Native Module, you can install it directly from GitHub into your Expo project:

```bash
npm install github:xheen908/sunme-core
```

Afterwards, rebuild your native Android app:

```bash
npx expo prebuild --clean
npx expo run:android
```

---

## Sunmi Scanner Setup

To allow the hardware scanner (laser) to scan in the background without needing a hidden text input field, you must configure your Sunmi device once:

1. Open the **"Scanner"** app on the Sunmi device.
2. Go to Settings (gear icon).
3. Set the mode to **"Broadcast Intent"** (or "Übertragung per Intent" in German).
4. Ensure the Action is set to `com.sunmi.scanner.ACTION_DATA_CODE_RECEIVED` (this is the Sunmi standard).
5. Disable "Keyboard Output".

---

## API & Usage

Import the module in your React Native app:

```tsx
import SunmiCore from 'sunmi-core';
// or: import SunmiCore from './modules/sunmi-core/src/SunmiCoreModule'; (if local)
```

### 1. Scan Barcodes (Event Listener)

The scanner runs completely in the background. You can listen for the scan event anywhere in your app (e.g., on the Checkout screen):

```tsx
import { useEffect, useState } from 'react';
import SunmiCore from 'sunmi-core';

export default function Checkout() {
  const [barcode, setBarcode] = useState('');

  useEffect(() => {
    const subscription = SunmiCore.addListener('onBarcodeScanned', (event) => {
      console.log("Scanned Barcode:", event.data);
      setBarcode(event.data);
    });

    return () => subscription.remove();
  }, []);

  // ...
}
```

### 2. Use Receipt Printer

Texts and QR codes are printed natively with zero latency:

```tsx
// Simple text (with newline \n)
SunmiCore.printText("John Doe LLC\n");
SunmiCore.printText("Invoice No. 12345\n");

// Print QR Code (Text/URL, size 1-16, error correction 0-3)
SunmiCore.printQRCode("https://fiskaly.com/receipt/123", 8, 1);

// Feed paper (empty lines)
SunmiCore.printText("\n\n\n");
```

### 3. Open Cash Drawer

Opens the connected cash drawer via the RJ11 port.

```tsx
SunmiCore.openCashDrawer();
```

### 4. Check Printer Status

Before printing, you can check if the printer is ready:

```tsx
const status = SunmiCore.getPrinterStatus();

switch(status) {
  case 1: console.log("Printer is ready"); break;
  case 2: console.log("Printer is preparing"); break;
  case 3: console.log("Communication error"); break;
  case 4: console.log("Out of paper!"); break;
  case 5: console.log("Printer is overheated!"); break;
  case 6: console.log("Printer cover is open!"); break;
  default: console.log("Unknown status");
}
```

---

## Troubleshooting

- **App crashes on newer Android devices (Android 14+):**
  The library automatically sets the `RECEIVER_EXPORTED` flag for Android 14. On standard phones (like Samsung or Pixel), the print commands are safely ignored if no printer hardware is found (it will not crash).
- **Scanner does not react in the app:**
  Ensure the Sunmi Scanner app is set to "Broadcast" mode, not "Keyboard" mode.

---
*Powered by Expo Native Modules & Sunmi Official SDK.*
