# Sunmi Core (expo-sunmi-core)

Eine hochleistungsfähige, moderne React Native / Expo Library zur nativen Ansteuerung von Sunmi POS-Geräten (wie V2, V2s, T2, etc.). 

Diese Bibliothek nutzt die offiziellen Sunmi Java SDKs unter der Haube und wurde speziell für moderne Expo Projekte (SDK 50+) und Android 14+ entwickelt. Keine Veralteten Abhängigkeiten (`jcenter()`) mehr!

## Funktionen

- 🖨️ **Bondrucker:** ESC/POS Text drucken
- 📱 **QR-Codes:** Nativer Druck von QR-Codes
- 💵 **Kassenlade:** RJ11 Impuls an die Cash Drawer senden
- 📊 **Druckerstatus:** Hardware-Sensoren auslesen (Papier leer, Klappe offen, Überhitzt)
- 📷 **Barcode-Scanner:** Reaktiver BroadcastReceiver für Hardware-Laser-Scans im Hintergrund (ohne Input-Fokus!)

---

## Installation

Da diese Bibliothek als Expo Native Module gebaut wurde, kannst du sie direkt in dein Expo Projekt installieren:

```bash
npm install github:xheen908/sunme-core
```

Danach musst du die native Android-App neu kompilieren:

```bash
npx expo prebuild --clean
npx expo run:android
```

---

## Einrichtung des Sunmi Scanners

Damit der Hardware-Scanner (Laser) im Hintergrund scannt, ohne dass du ein verstecktes Textfeld fokussieren musst, musst du das Sunmi-Gerät einmalig konfigurieren:

1. Öffne die App **"Scanner"** auf dem Sunmi-Gerät.
2. Gehe in die Einstellungen (Zahnrad).
3. Setze den Modus auf **"Broadcast Intent"** (oder "Übertragung per Intent").
4. Stelle sicher, dass die Aktion (Action) auf `com.sunmi.scanner.ACTION_DATA_CODE_RECEIVED` steht (das ist der Sunmi Standard).
5. Deaktiviere die "Tastatur-Eingabe" (Keyboard Output).

---

## API & Nutzung

Importiere das Modul in deiner React Native App:

```tsx
import SunmiCore from 'sunmi-core';
// oder: import SunmiCore from './modules/sunmi-core/src/SunmiCoreModule'; (wenn lokal)
```

### 1. Barcodes scannen (Event Listener)

Der Scanner läuft komplett im Hintergrund. Du kannst einfach an beliebiger Stelle in deiner App (z.B. im Checkout-Screen) auf das Scan-Event lauschen:

```tsx
import { useEffect, useState } from 'react';
import SunmiCore from 'sunmi-core';

export default function Checkout() {
  const [barcode, setBarcode] = useState('');

  useEffect(() => {
    const subscription = SunmiCore.addListener('onBarcodeScanned', (event) => {
      console.log("Gescannter Barcode:", event.data);
      setBarcode(event.data);
    });

    return () => subscription.remove();
  }, []);

  // ...
}
```

### 2. Bondrucker verwenden

Texte und QR-Codes werden nativ und ohne Latenz gedruckt:

```tsx
// Einfacher Text (mit Zeilenumbruch \n)
SunmiCore.printText("Max Mustermann GmbH\n");
SunmiCore.printText("Rechnung Nr. 12345\n");

// QR-Code drucken (Text/URL, Größe 1-16, Fehlerkorrektur 0-3)
SunmiCore.printQRCode("https://fiskaly.com/receipt/123", 8, 1);

// Papier vorschieben (leere Zeilen)
SunmiCore.printText("\n\n\n");
```

### 3. Kassenlade öffnen (Cash Drawer)

Öffnet die verbundene Kassenschublade über den RJ11 Anschluss.

```tsx
SunmiCore.openCashDrawer();
```

### 4. Druckerstatus abfragen

Vor dem Drucken kannst du prüfen, ob der Drucker bereit ist:

```tsx
const status = SunmiCore.getPrinterStatus();

switch(status) {
  case 1: console.log("Drucker bereit"); break;
  case 2: console.log("Drucker wird vorbereitet"); break;
  case 3: console.log("Kommunikationsfehler"); break;
  case 4: console.log("Papier leer!"); break;
  case 5: console.log("Drucker überhitzt!"); break;
  case 6: console.log("Druckerklappe ist offen!"); break;
  default: console.log("Unbekannter Status");
}
```

---

## Fehlerbehebung (Troubleshooting)

- **App stürzt auf neueren Android Geräten ab (Android 14+):**
  Die Bibliothek setzt das Flag `RECEIVER_EXPORTED` automatisch für Android 14. Auf Samsung oder Pixel-Handys wird das Drucken ignoriert, wenn keine Drucker-Hardware gefunden wird (es stürzt nicht ab).
- **Scanner reagiert nicht in der App:**
  Stelle sicher, dass in der Sunmi-eigenen Scanner-App der Modus "Broadcast" und nicht "Keyboard" eingestellt ist.

---
*Powered by Expo Native Modules & Sunmi Official SDK.*
