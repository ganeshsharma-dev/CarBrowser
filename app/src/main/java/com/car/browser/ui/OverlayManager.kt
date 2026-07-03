package com.car.browser.ui

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.car.browser.R
import com.car.browser.bookmarks.BookmarkManager
import com.car.browser.databinding.ActivityMainBinding
import com.car.browser.startpage.StartPageManager
import com.car.browser.tabs.TabManager

class OverlayManager(
    private val activity: AppCompatActivity,
    private val binding: ActivityMainBinding,
    private val tabManager: TabManager,
    private val bookmarkManager: BookmarkManager,
    private val startPageManager: StartPageManager,
    private val uiManager: BrowserUIManager,
    private val callbacks: OverlayCallbacks
) {

    interface OverlayCallbacks {
        fun onRecreateRequested()
        fun onHomePageChanged()
        fun onPickBackgroundRequested()
        fun onVersionInfoReceived(latestUrl: String, tagName: String)
    }

    fun showQrCodeView(url: String) {
        if (url.isBlank()) {
            return
        }
        
        hideAllOverlays()
        binding.qrCodeViewRoot.visibility = View.VISIBLE
        binding.qrCodeImage.setImageBitmap(null)
        binding.qrCodeUrl.text = url
        
        activity.lifecycleScope.launch {
            val bitmap = QRUtils.generateQrCodeAsync(url)
            if (bitmap != null) {
                binding.qrCodeImage.setImageBitmap(bitmap)
            }
        }
    }

    fun hideQrCodeView() {
        binding.qrCodeViewRoot.visibility = View.GONE
        binding.menuScroll.visibility = View.VISIBLE
    }

    fun showSettingsView() {
        hideAllOverlays()
        binding.settingsViewRoot.visibility = View.VISIBLE
        ensureSettingsContentPopulated()
    }

    fun hideSettingsView() {
        binding.settingsViewRoot.visibility = View.GONE
        binding.menuScroll.visibility = View.VISIBLE
    }

    fun showCheckLatestView() {
        hideAllOverlays()
        binding.checkLatestViewRoot.visibility = View.VISIBLE
        binding.checkLatestProgressIndicator.visibility = View.VISIBLE
        
        binding.checkLatestLatestVersion.text = activity.getString(R.string.menu_checking_latest)
        binding.checkLatestLatestVersion.setTextColor(getColorFromAttr(android.R.attr.textColorPrimary))

        val packageName = activity.packageName
        binding.checkLatestInstalledVersion.text = activity.getString(
            R.string.installed_version_label, 
            "v${com.car.browser.BuildConfig.VERSION_NAME}"
        )
        
     //   fetchLatestVersion()
    }

    fun hideCheckLatestView() {
        binding.checkLatestViewRoot.visibility = View.GONE
        binding.menuScroll.visibility = View.VISIBLE
    }

    private fun hideAllOverlays() {
        val views = listOf(
            binding.menuScroll,
            binding.bookmarkManagerRoot,
            binding.tabManagerRoot,
            binding.checkLatestViewRoot,
            binding.settingsViewRoot,
            binding.qrCodeViewRoot
        )
        views.forEach { 
            it.visibility = View.GONE 
        }
    }

    private fun ensureSettingsContentPopulated() {
        if (binding.settingsContentContainer.childCount > 0) {
            return
        }

        val context = activity
        val container = binding.settingsContentContainer

        // Header
        container.addView(createSettingsHeader(activity.getString(R.string.settings_title)) {
            hideSettingsView()
        })

        // Startup Section
        container.addView(createSectionHeader(activity.getString(R.string.settings_startup)))

        val restoreTabsSwitch = createSwitchItem(
            activity.getString(R.string.settings_restore_tabs_on_launch),
            activity.getString(R.string.settings_restore_tabs_on_launch_description),
            com.car.browser.data.BrowserPreferences.shouldRestoreTabsOnLaunch(context)
        ) { checked ->
            com.car.browser.data.BrowserPreferences.setRestoreTabsOnLaunch(context, checked)
        }
        container.addView(restoreTabsSwitch)

        val resumeLastPageSwitch = createSwitchItem(
            activity.getString(R.string.settings_resume_last_page_on_launch),
            activity.getString(R.string.settings_resume_last_page_on_launch_description),
            com.car.browser.data.BrowserPreferences.shouldResumeLastPageOnLaunch(context)
        ) { checked ->
            com.car.browser.data.BrowserPreferences.setResumeLastPageOnLaunch(context, checked)
        }
        container.addView(resumeLastPageSwitch)
        
        // Appearance Section
        container.addView(createSectionHeader(activity.getString(R.string.settings_appearance)))
        
        val forceDarkSwitch = createSwitchItem(
            activity.getString(R.string.settings_beta_dark_pages),
            activity.getString(R.string.settings_beta_dark_pages_description),
            com.car.browser.data.BrowserPreferences.isBetaForceDarkPagesEnabled(context)
        ) { checked ->
            com.car.browser.data.BrowserPreferences.setBetaForceDarkPagesEnabled(context, checked)
            callbacks.onRecreateRequested()
        }
        container.addView(forceDarkSwitch)
    }

    private fun createSettingsHeader(title: String, onBack: () -> Unit): View {
        val card = com.google.android.material.card.MaterialCardView(activity).apply {
            layoutParams = android.widget.LinearLayout.LayoutParams(-1, -2).apply {
                bottomMargin = dpToPx(12)
            }
            radius = dpToPx(8).toFloat()
            cardElevation = 0f
            setCardBackgroundColor(getColorFromAttr(com.google.android.material.R.attr.colorSurfaceContainerLow))
            strokeWidth = 0
        }

        val layout = android.widget.LinearLayout(activity).apply {
            orientation = android.widget.LinearLayout.HORIZONTAL
            setPaddingRelative(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16))
            gravity = android.view.Gravity.CENTER_VERTICAL
        }

        val textLayout = android.widget.LinearLayout(activity).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            layoutParams = android.widget.LinearLayout.LayoutParams(0, -2, 1f)
        }

        val titleView = android.widget.TextView(activity).apply {
            text = title
            setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_TitleLarge)
            setTypeface(null, android.graphics.Typeface.BOLD)
            setTextColor(getColorFromAttr(com.google.android.material.R.attr.colorOnPrimaryContainer))
        }
        textLayout.addView(titleView)

        val backButton = com.google.android.material.button.MaterialButton(activity, null, com.google.android.material.R.attr.materialButtonOutlinedStyle).apply {
            layoutParams = android.widget.LinearLayout.LayoutParams(-2, -2)
            text = activity.getString(R.string.menu_back)
            setTextColor(getColorFromAttr(com.google.android.material.R.attr.colorOnPrimaryContainer))
            setStrokeColor(android.content.res.ColorStateList.valueOf(getColorFromAttr(com.google.android.material.R.attr.colorOnPrimaryContainer)))
            icon = androidx.core.content.ContextCompat.getDrawable(activity, R.drawable.arrow_back_24px)
            setIconTint(android.content.res.ColorStateList.valueOf(getColorFromAttr(com.google.android.material.R.attr.colorOnPrimaryContainer)))
            setOnClickListener { onBack() }
        }

        layout.addView(textLayout)
        layout.addView(backButton)
        card.addView(layout)
        return card
    }

    private fun createSectionHeader(title: String): View {
        return android.widget.TextView(activity).apply {
            layoutParams = android.widget.LinearLayout.LayoutParams(-1, -2).apply {
                topMargin = dpToPx(16)
                bottomMargin = dpToPx(8)
                marginStart = dpToPx(4)
            }
            text = title
            setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_LabelLarge)
            setTextColor(getColorFromAttr(android.R.attr.colorPrimary))
            setAllCaps(true)
        }
    }

    private fun createSwitchItem(title: String, description: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit): View {
        val card = com.google.android.material.card.MaterialCardView(activity).apply {
            layoutParams = android.widget.LinearLayout.LayoutParams(-1, -2).apply {
                bottomMargin = dpToPx(8)
            }
            radius = dpToPx(8).toFloat()
            cardElevation = 0f
            strokeWidth = dpToPx(1)
            setStrokeColor(android.content.res.ColorStateList.valueOf(getColorFromAttr(com.google.android.material.R.attr.colorOutlineVariant)))
            setCardBackgroundColor(getColorFromAttr(com.google.android.material.R.attr.colorSurfaceContainerLow))
        }

        val layout = android.widget.LinearLayout(activity).apply {
            orientation = android.widget.LinearLayout.HORIZONTAL
            setPaddingRelative(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16))
            gravity = android.view.Gravity.CENTER_VERTICAL
        }

        val textLayout = android.widget.LinearLayout(activity).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            layoutParams = android.widget.LinearLayout.LayoutParams(0, -2, 1f)
            setPaddingRelative(0, 0, dpToPx(16), 0)
        }

        val titleView = android.widget.TextView(activity).apply {
            text = title
            setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_BodyLarge)
            setTextColor(getColorFromAttr(com.google.android.material.R.attr.colorOnSurface))
        }

        val descView = android.widget.TextView(activity).apply {
            text = description
            setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_BodySmall)
            setTextColor(getColorFromAttr(com.google.android.material.R.attr.colorOnSurfaceVariant))
            alpha = 0.8f
        }

        textLayout.addView(titleView)
        textLayout.addView(descView)

        val switch = com.google.android.material.switchmaterial.SwitchMaterial(activity).apply {
            this.isChecked = isChecked
            setOnCheckedChangeListener { _, checked -> onCheckedChange(checked) }
        }

        layout.addView(textLayout)
        layout.addView(switch)
        card.addView(layout)
        return card
    }

    private fun dpToPx(dp: Int): Int {
        return android.util.TypedValue.applyDimension(
            android.util.TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            activity.resources.displayMetrics
        ).toInt()
    }

    private fun fetchLatestVersion() {
        Thread {
            try {
                val url = java.net.URL("https://api.github.com/")
                val conn = url.openConnection() as java.net.HttpURLConnection
                conn.setRequestProperty("Accept", "application/vnd.github.v3+json")
                
                if (conn.responseCode == 200) {
                    val response = conn.inputStream.bufferedReader().use { reader ->
                        reader.readText()
                    }
                    val json = org.json.JSONObject(response)
                    val latestUrl = json.getString("html_url")
                    val tag = json.getString("tag_name")
                    
                    activity.runOnUiThread {
                        binding.checkLatestProgressIndicator.visibility = View.GONE
                        val currentVer = com.car.browser.BuildConfig.VERSION_NAME.trim().removePrefix("v")
                        val latestVer = tag.trim().removePrefix("v")
                        
                        if (currentVer.equals(latestVer, ignoreCase = true)) {
                            binding.checkLatestLatestVersion.text = activity.getString(R.string.check_latest_up_to_date, latestVer)
                            binding.checkLatestLatestVersion.setTextColor(getColorFromAttr(androidx.appcompat.R.attr.colorPrimary))
                        } else {
                            binding.checkLatestLatestVersion.text = activity.getString(R.string.check_latest_update_available, tag)
                            binding.checkLatestLatestVersion.setTextColor(getColorFromAttr(androidx.appcompat.R.attr.colorError))
                        }
                        callbacks.onVersionInfoReceived(latestUrl, tag)
                    }
                }
            } catch (e: Exception) {
                activity.runOnUiThread { 
                    binding.checkLatestProgressIndicator.visibility = View.GONE 
                }
            }
        }.start()
    }

    private fun getColorFromAttr(attrResId: Int): Int {
        val tv = android.util.TypedValue()
        if (activity.theme.resolveAttribute(attrResId, tv, true)) {
            if (tv.resourceId != 0) {
                return androidx.core.content.ContextCompat.getColor(activity, tv.resourceId)
            }
            return tv.data
        }
        return android.graphics.Color.TRANSPARENT
    }
}
