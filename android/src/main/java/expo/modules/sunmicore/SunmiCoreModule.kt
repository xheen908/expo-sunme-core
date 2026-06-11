package expo.modules.sunmicore

import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.BroadcastReceiver
import com.sunmi.peripheral.printer.InnerPrinterManager
import com.sunmi.peripheral.printer.InnerPrinterCallback
import com.sunmi.peripheral.printer.SunmiPrinterService

class SunmiCoreModule : Module() {
  private var sunmiPrinterService: SunmiPrinterService? = null
  
  private val scannerReceiver = object : BroadcastReceiver() {
      override fun onReceive(context: Context, intent: Intent) {
          val code = intent.getStringExtra("data") ?: return
          sendEvent("onBarcodeScanned", mapOf("data" to code))
      }
  }

  override fun definition() = ModuleDefinition {
    Name("SunmiCore")

    Events("onBarcodeScanned")

    OnCreate {
      val context = appContext.reactContext ?: return@OnCreate
      
      // Init Printer
      try {
        InnerPrinterManager.getInstance().bindService(context, object : InnerPrinterCallback() {
            override fun onConnected(service: SunmiPrinterService) {
                sunmiPrinterService = service
            }
            override fun onDisconnected() {
                sunmiPrinterService = null
            }
        })
      } catch (e: Exception) {
        // Ignored
      }
      
      // Register Scanner Receiver
      val filter = IntentFilter("com.sunmi.scanner.ACTION_DATA_CODE_RECEIVED")
      if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
          context.registerReceiver(scannerReceiver, filter, Context.RECEIVER_EXPORTED)
      } else {
          context.registerReceiver(scannerReceiver, filter)
      }
    }
    
    OnDestroy {
      val context = appContext.reactContext ?: return@OnDestroy
      try {
        context.unregisterReceiver(scannerReceiver)
      } catch (e: Exception) {}
    }

    Function("printText") { text: String ->
      sunmiPrinterService?.printText(text + "\n", null)
    }
    
    Function("printQRCode") { data: String, size: Int, errorLevel: Int ->
      sunmiPrinterService?.printQRCode(data, size, errorLevel, null)
    }

    Function("openCashDrawer") {
      sunmiPrinterService?.openDrawer(null)
    }

    Function("getPrinterStatus") { ->
      return@Function sunmiPrinterService?.updatePrinterState() ?: -1
    }
  }
}

