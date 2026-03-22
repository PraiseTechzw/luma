package app.lumalabs.luma.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.googlefonts.Font
import app.lumalabs.luma.R

// Google Font Provider
val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val DMSansFont = GoogleFont("DM Sans")
val DMSerifFont = GoogleFont("DM Serif Display")

val DMSansFamily = FontFamily(
    Font(googleFont = DMSansFont, fontProvider = provider),
    Font(googleFont = DMSansFont, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = DMSansFont, fontProvider = provider, weight = FontWeight.Bold)
)

val DMSerifFamily = FontFamily(
    Font(googleFont = DMSerifFont, fontProvider = provider)
)

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = DMSansFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    titleLarge = TextStyle(
        fontFamily = DMSansFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = DMSansFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    displayLarge = TextStyle(
        fontFamily = DMSerifFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    )
)
