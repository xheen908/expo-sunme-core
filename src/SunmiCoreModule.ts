import { NativeModule, requireNativeModule } from 'expo';

export type BarcodeEventPayload = {
  data: string;
};

declare class SunmiCoreModule extends NativeModule<{
  onBarcodeScanned: (event: BarcodeEventPayload) => void;
}> {
  printText(text: string): void;
  printQRCode(data: string, size: number, errorLevel: number): void;
  openCashDrawer(): void;
  getPrinterStatus(): number;
}

export default requireNativeModule<SunmiCoreModule>('SunmiCore');

