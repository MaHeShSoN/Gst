import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.content.FileProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File

class WhatsAppHelper(private val activity: Activity) {
    companion object {
        private const val WHATSAPP_PACKAGE = "com.whatsapp"
        private const val WHATSAPP_BUSINESS_PACKAGE = "com.whatsapp.w4b"
    }

    private fun findWhatsAppPackages(): List<String> {
        val packageManager = activity.packageManager
        return listOf(WHATSAPP_PACKAGE, WHATSAPP_BUSINESS_PACKAGE)
            .filter { packageName ->
                try {
                    packageManager.getPackageInfo(packageName, 0)
                    true
                } catch (e: PackageManager.NameNotFoundException) {
                    false
                }
            }
    }

    private fun getAppName(packageName: String): String {
        return when (packageName) {
            WHATSAPP_PACKAGE -> "WhatsApp"
            WHATSAPP_BUSINESS_PACKAGE -> "WhatsApp Business"
            else -> packageName
        }
    }

    private fun shareToSpecificApp(pdfFile: File, packageName: String, authority: String) {
        try {
            val contentUri = FileProvider.getUriForFile(activity, authority, pdfFile)

            // Grant read permission
            activity.grantUriPermission(
                packageName,
                contentUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                `package` = packageName
            }

            // Verify file exists and is readable
            if (!pdfFile.exists() || !pdfFile.canRead()) {
                throw IllegalStateException("PDF file doesn't exist or isn't readable")
            }

            // Start activity directly without chooser since we already specify the package
            activity.startActivity(intent)

        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    fun sharePdfToWhatsApp(pdfFile: File, authority: String, onError: ((String) -> Unit)? = null): Boolean {
        val availablePackages = findWhatsAppPackages()

        try {
            when {
                availablePackages.isEmpty() -> {
                    onError?.invoke("No WhatsApp installed")
                    return false
                }
                availablePackages.size == 1 -> {
                    shareToSpecificApp(pdfFile, availablePackages.first(), authority)
                }
                else -> {
                    MaterialAlertDialogBuilder(activity)
                        .setTitle("Select WhatsApp Version")
                        .setItems(
                            availablePackages.map { getAppName(it) }.toTypedArray()
                        ) { dialog, which ->
                            try {
                                shareToSpecificApp(pdfFile, availablePackages[which], authority)
                                dialog.dismiss()
                            } catch (e: Exception) {
                                onError?.invoke("Error sharing: ${e.message}")
                            }
                        }
                        .show()
                }
            }
            return true
        } catch (e: Exception) {
            onError?.invoke("Error: ${e.message}")
            return false
        }
    }
}