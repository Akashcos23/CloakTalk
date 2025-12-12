// kotlin
package com.example.cloaktalk.ui.screens

import androidx.compose.ui.platform.LocalContext

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
             import androidx.compose.ui.graphics.Brush
             import androidx.compose.ui.graphics.Color
             import androidx.compose.ui.text.font.FontWeight
             import androidx.compose.ui.text.input.PasswordVisualTransformation
             import androidx.compose.ui.text.input.VisualTransformation
             import androidx.compose.ui.unit.dp
             import androidx.compose.ui.unit.sp
             import androidx.credentials.CredentialManager
             import androidx.credentials.GetCredentialRequest
             import androidx.credentials.exceptions.GetCredentialException
             import com.google.android.libraries.identity.googleid.GetGoogleIdOption
             import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
             import com.example.cloaktalk.ui.theme.OrangeTheme
             import com.example.cloaktalk.auth.FirebaseAuthManager
             import kotlinx.coroutines.launch
             import androidx.compose.foundation.Image
             import androidx.compose.foundation.clickable
             import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.res.painterResource
import com.example.cloaktalk.R

             @OptIn(ExperimentalMaterial3Api::class)
             @Composable
             fun LoginScreen(onLogin: () -> Unit, onSignUp: () -> Unit) {
                 var email by remember { mutableStateOf("") }
                 var password by remember { mutableStateOf("") }
                 var passwordVisible by remember { mutableStateOf(false) }
                 val context = LocalContext.current
                 val credentialManager = remember { CredentialManager.create(context) }
                 val scope = rememberCoroutineScope()

                 fun tryEmailLogin() {
                     // keep it simple: use Firebase's sign in with email/password if you add it later
                     Toast.makeText(context, "Email login not implemented", Toast.LENGTH_SHORT).show()
                 }

                 CardLayout(title = "Welcome Back", subtitle = "Securely sign in to CloakTalk") {
                     OutlinedTextField(
                         value = email,
                         onValueChange = { email = it },
                         label = { Text("Email") },
                         modifier = Modifier.fillMaxWidth(),
                         shape = RoundedCornerShape(12.dp),
                         colors = OutlinedTextFieldDefaults.colors(
                             focusedBorderColor = OrangeTheme.Primary, unfocusedBorderColor = OrangeTheme.Border
                         )
                     )

                     Spacer(Modifier.height(12.dp))

                     OutlinedTextField(
                         value = password,
                         onValueChange = { password = it },
                         label = { Text("Password") },
                         visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                         trailingIcon = {
                             IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                 Icon(
                                     imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                     contentDescription = null
                                 )
                             }
                         },
                         modifier = Modifier.fillMaxWidth(),
                         shape = RoundedCornerShape(12.dp)
                     )

                     Spacer(Modifier.height(16.dp))

                     Button(
                         onClick = { tryEmailLogin() },
                         modifier = Modifier.fillMaxWidth(),
                         colors = ButtonDefaults.buttonColors(containerColor = OrangeTheme.Primary),
                         shape = RoundedCornerShape(12.dp)
                     ) {
                         Text("Sign in", fontWeight = FontWeight.Bold)
                     }

                     Spacer(Modifier.height(12.dp))

                     GoogleSignButton(
                         onClick = {
                             scope.launch {
                                 try {
                                     val googleIdOption = GetGoogleIdOption.Builder()
                                         .setFilterByAuthorizedAccounts(false)
                                         .setServerClientId(context.getString(R.string.default_web_client_id))
                                         .setAutoSelectEnabled(false)
                                         .build()
                                     val request = GetCredentialRequest.Builder()
                                         .addCredentialOption(googleIdOption)
                                         .build()
                                     val result = credentialManager.getCredential(request = request, context = context)
                                     val credential = result.credential
                                     val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                                     val idToken = googleIdTokenCredential.idToken
                                     FirebaseAuthManager.signInWithGoogle(idToken) { success, _, message ->
                                         if (success) {
                                             Toast.makeText(context, "Signed in", Toast.LENGTH_SHORT).show()
                                             onLogin()
                                         } else {
                                             Toast.makeText(context, message ?: "Google sign-in failed", Toast.LENGTH_LONG).show()
                                         }
                                     }
                                 } catch (e: GetCredentialException) {
                                     Log.e("LoginScreen", "Google Sign-In failed: ${e.message}")
                                     if (!e.message.toString().contains("cancelled")) {
                                         Toast.makeText(context, "Sign in failed: ${e.message}", Toast.LENGTH_SHORT).show()
                                     }
                                 } catch (e: Exception) {
                                     Log.e("LoginScreen", "Unexpected", e)
                                     Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                                 }
                             }
                         }
                     )

                     Spacer(Modifier.height(12.dp))

                     Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                         Text("Don't have an account?", color = OrangeTheme.TextSecondary)
                         Text(
                             " Sign up",
                             color = OrangeTheme.Primary,
                             modifier = Modifier.clickable { onSignUp() }
                         )
                     }
                 }
             }

             @Composable
             private fun CardLayout(title: String, subtitle: String, content: @Composable ColumnScope.() -> Unit) {
                 Box(
                     modifier = Modifier
                         .fillMaxSize()
                         .background(
                             Brush.verticalGradient(listOf(Color(0xFF111827), Color(0xFF1F2937), Color(0xFF78350F)))
                         ), contentAlignment = Alignment.Center
                 ) {
                     Card(
                         modifier = Modifier
                             .fillMaxWidth()
                             .padding(16.dp),
                         colors = CardDefaults.cardColors(containerColor = OrangeTheme.Surface),
                         shape = RoundedCornerShape(24.dp)
                     ) {
                         Column(
                             modifier = Modifier
                                 .fillMaxWidth()
                                 .padding(28.dp),
                             horizontalAlignment = Alignment.CenterHorizontally
                         ) {
                             Box(
                                 modifier = Modifier
                                     .size(72.dp)
                                     .background(
                                         Brush.horizontalGradient(listOf(OrangeTheme.Primary, OrangeTheme.PrimaryDark)),
                                         CircleShape
                                     ),
                                 contentAlignment = Alignment.Center
                             ) {
                                 Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
                             }

                             Spacer(Modifier.height(12.dp))

                             Text(title, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = OrangeTheme.TextPrimary)
                             Text(subtitle, fontSize = 14.sp, color = OrangeTheme.TextSecondary)

                             Spacer(Modifier.height(18.dp))

                             content()
                         }
                     }
                 }
             }

             @Composable
              fun GoogleSignButton(onClick: () -> Unit) {
                 OutlinedButton(
                     onClick = onClick,
                     modifier = Modifier
                         .fillMaxWidth()
                         .height(48.dp),
                     colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                     shape = RoundedCornerShape(12.dp)
                 ) {
                     Row(verticalAlignment = Alignment.CenterVertically) {
                         Image(painter = painterResource(id = R.drawable.google_logo), contentDescription = "Google", modifier = Modifier.size(20.dp))
                         Spacer(Modifier.width(12.dp))
                         Text("Continue with Google", color = Color(0xFF202124))
                     }
                 }
             }