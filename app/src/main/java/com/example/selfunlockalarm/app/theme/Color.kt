package com.example.selfunlockalarm.app.theme

import androidx.compose.ui.graphics.Color

// Gradient Colors from Screenshot
val LightBlue = Color(0xFF3B82F6) // approx blue-500 / blue-600
val LightPurple = Color(0xFF8B5CF6) // approx purple-400 / purple-500

val GradientStartBlue = LightBlue
val GradientEndPurple = LightPurple

val TextGradientStart = Color(0xFF2563EB) // approx blue-600
val TextGradientEnd = LightPurple

// Background gradient from-slate-100 via-blue-50 to-purple-50
val BgGradientVia = Color(0xFFEFF6FF)   // blue-50
val BgGradientEnd = Color(0xFFFAF5FF)   // purple-50

// Card Background
val CardBg = Color(0xFFFFFFFF)

// Other UI Colors
val TextBlue = Color(0xFF2563EB)       // blue-600 for text
val TextWhite = Color.White
val ErrorRed = Color(0xFFF44336)
val BorderBlueLight = Color(0xFFDBEAFE) // blue-100 for border

// Material Theme Colors (using single colors, gradients will be applied via Brush)
val MdBluePrimary = LightBlue
val MdPurpleSecondary = LightPurple
val MdBackground = Color.White // Default background
val MdSurface = Color.White    // Default surface