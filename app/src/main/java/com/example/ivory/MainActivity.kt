package com.example.ivory

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import com.example.ivory.ui.theme.IvoryTheme
import com.example.ivory.ui.theme.navigation.AppNavGraph
import com.example.ivory.ui.theme.navigation.BottomNavBar
import com.example.ivory.ui.theme.screen.auth.emailVerification.EmailVerificationScreen
import com.example.ivory.ui.theme.screen.auth.emailVerified.EmailVerifiedScreen
import com.example.ivory.ui.theme.screen.auth.forgotPassword.ForgotPasswordScreen
import com.example.ivory.ui.theme.screen.auth.GetStartedScreen
import com.example.ivory.ui.theme.screen.auth.login.LoginScreen
import com.example.ivory.ui.theme.screen.auth.signUp.SignUpScreen
import com.example.ivory.ui.theme.screen.auth.SplashScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IvoryTheme {
                var appStage by rememberSaveable { mutableStateOf(AppStage.SPLASH) }
                var verificationEmail by rememberSaveable { mutableStateOf("") }
                when (appStage) {
                    AppStage.SPLASH -> SplashScreen(
                        onLoadingComplete = { appStage = AppStage.GET_STARTED }
                    )

                    AppStage.GET_STARTED -> GetStartedScreen(
                        onCreateAccount = { appStage = AppStage.SIGN_UP },
                        onLogIn = { appStage = AppStage.LOGIN }
                    )

                    AppStage.LOGIN -> LoginScreen(
                        onLogIn = { appStage = AppStage.MAIN },
                        onEmailVerification = { email ->
                            verificationEmail = email
                            appStage = AppStage.EMAIL_VERIFICATION
                        },
                        onForgotPassword = { appStage = AppStage.FORGOT_PASSWORD },
                        onSignUp = { appStage = AppStage.SIGN_UP }
                    )

                    AppStage.SIGN_UP -> SignUpScreen(
                        onSignUp = { email ->
                            verificationEmail = email
                            appStage = AppStage.EMAIL_VERIFICATION
                        },
                        onLogIn = { appStage = AppStage.LOGIN }
                    )

                    AppStage.FORGOT_PASSWORD -> ForgotPasswordScreen(
                        onBackToLogin = { appStage = AppStage.LOGIN }
                    )

                    AppStage.EMAIL_VERIFICATION -> EmailVerificationScreen(
                        email = verificationEmail,
                        onVerified = { appStage = AppStage.EMAIL_VERIFIED },
                        onBackToLogin = { appStage = AppStage.LOGIN }
                    )

                    AppStage.EMAIL_VERIFIED -> EmailVerifiedScreen(
                        email = verificationEmail,
                        onContinue = { appStage = AppStage.MAIN }
                    )

                    AppStage.MAIN -> MainContent()
                }
            }
        }
    }
}

@Composable
private fun MainContent() {
    val navController = rememberNavController()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { BottomNavBar(navController) }
    ) { innerPadding ->
        AppNavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

private enum class AppStage {
    SPLASH,
    GET_STARTED,
    LOGIN,
    SIGN_UP,
    FORGOT_PASSWORD,
    EMAIL_VERIFICATION,
    EMAIL_VERIFIED,
    MAIN
}
