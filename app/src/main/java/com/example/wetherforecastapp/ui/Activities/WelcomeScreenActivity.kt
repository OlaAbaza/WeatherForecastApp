package com.example.wetherforecastapp.ui.Activities

import android.graphics.Color
import com.example.wetherforecastapp.R
import com.stephentuso.welcome.*

class WelcomeScreenActivity : WelcomeActivity() {

    override fun configuration(): WelcomeConfiguration {
        return WelcomeConfiguration.Builder(this)
            .defaultBackgroundColor(R.color.Transpernt)
            .page(
                FullscreenParallaxPage(
                    R.layout.welcome_page1
                )
            )
            .page(
                FullscreenParallaxPage(
                    R.layout.welcome_page2
                )
            )
            .page(
                FullscreenParallaxPage(
                    R.layout.welcome_page3
                )
            )

            .swipeToDismiss(true)
            .build()
    }
}
