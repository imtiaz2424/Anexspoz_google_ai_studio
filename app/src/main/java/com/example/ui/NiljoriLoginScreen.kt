package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NiljoriLoginScreen(
    isBengali: Boolean,
    onLogin: (String, String, (Boolean, String?) -> Unit) -> Unit,
    onSignUp: (String, String, (Boolean, String?) -> Unit) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("niljori_settings", android.content.Context.MODE_PRIVATE) }

    var rememberMe by remember { mutableStateOf(sharedPrefs.getBoolean("remember_me", false)) }
    var email by remember { mutableStateOf(if (rememberMe) sharedPrefs.getString("saved_email", "") ?: "" else "") }
    var password by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var isTermsAgreed by remember { mutableStateOf(false) }
    
    var isSignUpMode by remember { mutableStateOf(false) }
    var isVerificationMode by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // Validation State variables
    var isEmailError by remember { mutableStateOf(false) }
    var isPasswordError by remember { mutableStateOf(false) }
    var isPhoneError by remember { mutableStateOf(false) }
    var isNameError by remember { mutableStateOf(false) }
    var emailErrorMsg by remember { mutableStateOf("") }
    var passwordErrorMsg by remember { mutableStateOf("") }
    var phoneErrorMsg by remember { mutableStateOf("") }
    var nameErrorMsg by remember { mutableStateOf("") }

    // Dialog for Forgot Password
    var isForgotPasswordDialogOpen by remember { mutableStateOf(false) }
    var forgotEmail by remember { mutableStateOf("") }
    var forgotPasswordMessage by remember { mutableStateOf<String?>(null) }

    // Social & OTP Verification additional states
    var showGoogleDialog by remember { mutableStateOf(false) }
    var showAppleDialog by remember { mutableStateOf(false) }
    var otpCode by remember { mutableStateOf("") }
    var otpErrorMsg by remember { mutableStateOf("") }
    var secondsLeft by remember { mutableStateOf(59) }
    var resendEnabled by remember { mutableStateOf(false) }
    var isEmailVerificationSelected by remember { mutableStateOf(false) }

    // Timer countdown for simulated OTP
    LaunchedEffect(isVerificationMode, secondsLeft) {
        if (isVerificationMode && secondsLeft > 0) {
            kotlinx.coroutines.delay(1000)
            secondsLeft -= 1
        } else if (secondsLeft == 0) {
            resendEnabled = true
        }
    }

    if (isForgotPasswordDialogOpen) {
        AlertDialog(
            onDismissRequest = { isForgotPasswordDialogOpen = false },
            title = {
                Text(
                    text = if (isBengali) "পাসওয়ার্ড ভুলে গেছেন?" else "Forgot Password?",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF01579B)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (isBengali) 
                            "আপনার রেজিস্টার্ড ইমেলটি লিখুন। আমরা আপনাকে একটি পাসওয়ার্ড রিসেট লিঙ্ক পাঠাব।" 
                            else "Enter your registered email address below. We'll simulate sending you a password reset link.",
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )
                    OutlinedTextField(
                        value = forgotEmail,
                        onValueChange = { forgotEmail = it },
                        label = { Text("Email Address") },
                        placeholder = { Text("example@niljori.com") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    forgotPasswordMessage?.let { msg ->
                        Text(
                            text = msg,
                            color = Color(0xFF0288D1),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (forgotEmail.isBlank()) {
                            forgotPasswordMessage = if (isBengali) "অনুগ্রহ করে ইমেল টাইপ করুন!" else "Please type your email!"
                        } else {
                            forgotPasswordMessage = if (isBengali) 
                                "একটি সিমুলেটেড পাসওয়ার্ড রিসেট লিঙ্ক আপনার ইমেলে পাঠানো হয়েছে!" 
                                else "A simulated password reset link has been successfully sent!"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1))
                ) {
                    Text(if (isBengali) "রিসেট কোড পাঠান" else "Send Reset Link")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    isForgotPasswordDialogOpen = false
                    forgotPasswordMessage = null
                }) {
                    Text(if (isBengali) "বাতিল করুন" else "Cancel", color = Color.Gray)
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE0F7FA), // Refreshing light blue mist
                        Color(0xFFFFFFFF),
                        Color(0xFFB3E5FC)  // Sky blue bottom transition
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Animated main mascot logo of the brand
            NiljoriModernLogo(
                modifier = Modifier.wrapContentSize().height(80.dp),
                showText = true,
                isBengali = isBengali
            )

            Spacer(modifier = Modifier.height(10.dp))

            // VERIFICATION SCREEN COMPONENT
            if (isVerificationMode) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFF0288D1)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("verification_card")
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Verification Ready",
                            tint = Color(0xFF0288D1),
                            modifier = Modifier.size(54.dp)
                        )

                        Text(
                            text = if (isBengali) "নিরাপত্তা ও ওটিপি ভেরিফিকেশন" else "Security & OTP Verification",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = Color(0xFF01579B),
                            textAlign = TextAlign.Center
                        )

                        // Selector between Phone SMS and Email Link
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .background(Color(0xFFF5F7F6), RoundedCornerShape(8.dp))
                                .padding(2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (!isEmailVerificationSelected) Color.White else Color.Transparent)
                                    .clickable { isEmailVerificationSelected = false },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isBengali) "মোবাইল ওটিপি (SMS)" else "SMS OTP",
                                    fontWeight = if (!isEmailVerificationSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.5.sp,
                                    color = if (!isEmailVerificationSelected) Color(0xFF0288D1) else Color.Gray
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isEmailVerificationSelected) Color.White else Color.Transparent)
                                    .clickable { isEmailVerificationSelected = true },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isBengali) "ইমেইল ভেরিফিকেশন" else "Email Link",
                                    fontWeight = if (isEmailVerificationSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.5.sp,
                                    color = if (isEmailVerificationSelected) Color(0xFF0288D1) else Color.Gray
                                )
                            }
                        }

                        Text(
                            text = if (isEmailVerificationSelected) {
                                if (isBengali) "আপনার $email ইমেইলে একটি ভেরিফিকেশন লিঙ্ক ও ৪ ডিজিটের কোড পাঠানো হয়েছে।"
                                else "We have sent a 4-digit confirmation code and a verification link to your email: $email"
                            } else {
                                if (isBengali) "আপনার $phone নাম্বারে একটি ৪ ডিজিটের ওটিপি কোড পাঠানো হয়েছে।"
                                else "We have sent a 4-digit security code via SMS to your mobile: $phone"
                            },
                            fontSize = 11.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )

                        Divider(color = Color(0xFFEEEEEE), modifier = Modifier.padding(vertical = 2.dp))

                        // OTP Numeric Input Box
                        OutlinedTextField(
                            value = otpCode,
                            onValueChange = { 
                                if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                                    otpCode = it
                                    otpErrorMsg = ""
                                }
                            },
                            label = { Text(if (isBengali) "৪-ডিজিট ভেরিফিকেশন কোড" else "4-Digit Verification Code") },
                            placeholder = { Text("e.g. 1234") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            singleLine = true,
                            isError = otpErrorMsg.isNotEmpty(),
                            supportingText = if (otpErrorMsg.isNotEmpty()) { { Text(otpErrorMsg, color = MaterialTheme.colorScheme.error, fontSize = 10.sp) } } else null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("otp_code_input"),
                            shape = RoundedCornerShape(12.dp),
                            trailingIcon = {
                                if (otpCode.length == 4) {
                                    Icon(Icons.Default.Check, contentDescription = "Valid Length", tint = Color(0xFF2E7D32))
                                }
                            }
                        )

                        // Timer text and Resend action
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (secondsLeft > 0) {
                                    if (isBengali) "কোড পুনরায় পাঠান ${secondsLeft} সে." else "Resend code in ${secondsLeft}s"
                                } else {
                                    if (isBengali) "কোড পাঠানো হয়নি?" else "Didn't receive code?"
                                },
                                fontSize = 11.5.sp,
                                color = Color.Gray
                            )

                            TextButton(
                                onClick = {
                                    if (resendEnabled) {
                                        secondsLeft = 59
                                        resendEnabled = false
                                        otpCode = ""
                                        // Simulated code dispatch
                                        successMessage = if (isBengali) "ভেরিফিকেশন কোড পুনরায় পাঠানো হয়েছে!" else "Verification code re-sent successfully!"
                                    }
                                },
                                enabled = resendEnabled,
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(
                                    text = if (isBengali) "পুনরায় পাঠান (Resend)" else "Resend OTP",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (resendEnabled) Color(0xFF0288D1) else Color.LightGray
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Submit Button
                        Button(
                            onClick = {
                                if (otpCode.length < 4) {
                                    otpErrorMsg = if (isBengali) "অনুগ্রহ করে ৪ ডিজিটের সঠিক ওটিপি দিন" else "Please enter the complete 4-digit OTP"
                                } else {
                                    // Successfully Verified
                                    isVerificationMode = false
                                    isSignUpMode = false
                                    successMessage = if (isBengali) 
                                        "আপনার অ্যাকাউন্ট সফলভাবে ভেরিফাই করা হয়েছে! লগইন করুন।" 
                                        else "Email & Phone number verified successfully! Please log in now."
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("verify_otp_button")
                        ) {
                            Text(
                                text = if (isBengali) "ভেরিফাই ও অ্যাকাউন্ট চালু করুন" else "Verify & Activate Account",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        // Back to Sign Up
                        TextButton(
                            onClick = {
                                isVerificationMode = false
                                isSignUpMode = true
                            }
                        ) {
                            Text(
                                text = if (isBengali) "ফিরে যান" else "Back to Register",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            } else {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFB3E5FC)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_card")
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = if (isSignUpMode) {
                                if (isBengali) "নতুন নীলজরি অ্যাকাউন্ট তৈরি" else "Create Niljori Account"
                            } else {
                                if (isBengali) "আপনার অ্যাকাউন্টে প্রবেশ করুন" else "Welcome Back!"
                            },
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = Color(0xFF01579B),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (isSignUpMode) {
                            Text(
                                text = if (isBengali) "আপনার পুরো নাম, ইমেল, ফোন নম্বর এবং পাসওয়ার্ড যোগ করুন" else "Add your full name, email, phone number and password",
                                fontSize = 11.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = if (isBengali) "সাইন ইন" else "Sign In",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.5.sp,
                                    color = Color(0xFF2E7D32),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = if (isBengali) "আপনার লগইন তথ্য প্রদান করুন" else "Provide your login credentials",
                                    fontSize = 11.sp,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        // Error Message
                        errorMessage?.let { error ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFFFEBEE))
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = error,
                                    color = Color(0xFFC62828),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        // Success Message
                        successMessage?.let { msg ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFE8F5E9))
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = msg,
                                    color = Color(0xFF2E7D32),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        // FULL NAME FIELD (SignUp Mode Only)
                        if (isSignUpMode) {
                            OutlinedTextField(
                                value = fullName,
                                onValueChange = { 
                                    fullName = it
                                    isNameError = false 
                                },
                                label = { Text("Full Name") },
                                placeholder = { Text("Your Complete Name") },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF2E7D32)) },
                                singleLine = true,
                                isError = isNameError,
                                supportingText = if (isNameError) { { Text(nameErrorMsg, color = MaterialTheme.colorScheme.error, fontSize = 10.sp) } } else null,
                                modifier = Modifier.fillMaxWidth().testTag("auth_full_name_field"),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        // Email Field
                        OutlinedTextField(
                            value = email,
                            onValueChange = { 
                                email = it
                                isEmailError = false 
                            },
                            label = { Text("Email Address") },
                            placeholder = { Text("example@niljori.com") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF0288D1)) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            isError = isEmailError,
                            supportingText = if (isEmailError) { { Text(emailErrorMsg, color = MaterialTheme.colorScheme.error, fontSize = 10.sp) } } else null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_email_field"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // PHONE FIELD (SignUp Mode Only)
                        if (isSignUpMode) {
                            OutlinedTextField(
                                value = phone,
                                onValueChange = { 
                                    phone = it
                                    isPhoneError = false 
                                },
                                label = { Text("Phone Number") },
                                placeholder = { Text("+880 1712-345678") },
                                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = Color(0xFF0288D1)) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                isError = isPhoneError,
                                supportingText = if (isPhoneError) { { Text(phoneErrorMsg, color = MaterialTheme.colorScheme.error, fontSize = 10.sp) } } else null,
                                modifier = Modifier.fillMaxWidth().testTag("auth_phone_field"),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        // Password Field
                        OutlinedTextField(
                            value = password,
                            onValueChange = { 
                                password = it
                                isPasswordError = false 
                            },
                            label = { Text("Account Password") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF0288D1)) },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = "Toggle password visibility",
                                        tint = Color.Gray
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            isError = isPasswordError,
                            supportingText = if (isPasswordError) { { Text(passwordErrorMsg, color = MaterialTheme.colorScheme.error, fontSize = 10.sp) } } else null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_password_field"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // REMEMBER ME & FORGOT PASSWORD (Login Mode Only)
                        if (!isSignUpMode) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { rememberMe = !rememberMe }
                                ) {
                                    Checkbox(
                                        checked = rememberMe,
                                        onCheckedChange = { rememberMe = it },
                                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFF2E7D32)),
                                        modifier = Modifier.testTag("auth_remember_me_checkbox")
                                    )
                                    Text(
                                        text = if (isBengali) "আমাকে মনে রাখুন" else "Remember Me",
                                        fontSize = 11.sp,
                                        color = Color.DarkGray,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                TextButton(
                                    onClick = { isForgotPasswordDialogOpen = true },
                                    contentPadding = PaddingValues(0.dp),
                                    modifier = Modifier.testTag("auth_forgot_password")
                                ) {
                                    Text(
                                        text = if (isBengali) "পাসওয়ার্ড ভুলে গেছেন?" else "Forgot Password?",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0288D1)
                                    )
                                }
                            }
                        }

                        // TERMS AND CONDITIONS AGREEMENT (SignUp Mode Only)
                        if (isSignUpMode) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { isTermsAgreed = !isTermsAgreed }
                                    .padding(vertical = 4.dp)
                            ) {
                                Checkbox(
                                    checked = isTermsAgreed,
                                    onCheckedChange = { isTermsAgreed = it },
                                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFF0288D1))
                                )
                                Text(
                                    text = if (isBengali)
                                        "আমি নীলজরি ব্যবহারের সব নীতিমালা ও শর্তাবলীতে সম্মতি জানাচ্ছি"
                                        else "I agree to all Niljori Terms, Conditions and Privacy policy.",
                                    fontSize = 10.5.sp,
                                    color = Color.DarkGray,
                                    lineHeight = 14.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Main Action Button
                        Button(
                            onClick = {
                                // Reset all error states
                                isEmailError = false
                                isPasswordError = false
                                isPhoneError = false
                                isNameError = false
                                emailErrorMsg = ""
                                passwordErrorMsg = ""
                                phoneErrorMsg = ""
                                nameErrorMsg = ""
                                errorMessage = null
                                successMessage = null

                                var hasError = false

                                if (isSignUpMode) {
                                    if (fullName.isBlank()) {
                                        isNameError = true
                                        nameErrorMsg = if (isBengali) "অনুগ্রহ করে আপনার পুরো নাম লিখুন।" else "Please enter your Full Name."
                                        hasError = true
                                    }
                                    if (email.isBlank()) {
                                        isEmailError = true
                                        emailErrorMsg = if (isBengali) "অনুগ্রহ করে একটি ইমেল ঠিকানা লিখুন।" else "Please enter your Email."
                                        hasError = true
                                    } else {
                                        val emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$"
                                        if (!email.trim().matches(emailPattern.toRegex())) {
                                            isEmailError = true
                                            emailErrorMsg = if (isBengali) "অনুগ্রহ করে একটি সঠিক ইমেল প্রবেশ করান।" else "Please enter a valid Email address."
                                            hasError = true
                                        }
                                    }
                                    if (phone.isBlank()) {
                                        isPhoneError = true
                                        phoneErrorMsg = if (isBengali) "অনুগ্রহ করে ফোন নম্বর লিখুন।" else "Please enter your Phone number."
                                        hasError = true
                                    }
                                    if (password.isBlank()) {
                                        isPasswordError = true
                                        passwordErrorMsg = if (isBengali) "অনুগ্রহ করে একটি পাসওয়ার্ড লিখুন।" else "Please enter a Password."
                                        hasError = true
                                    } else if (password.length < 6) {
                                        isPasswordError = true
                                        passwordErrorMsg = if (isBengali) "পাসওয়ার্ড কমপক্ষে ৬ অক্ষরের হতে হবে।" else "Password must be at least 6 characters long."
                                        hasError = true
                                    }
                                    if (!isTermsAgreed) {
                                        errorMessage = if (isBengali) "আপনাকে অবশ্যই শর্তাবলীতে সম্মতি দিতে হবে।" else "You must agree to the Terms & Conditions."
                                        hasError = true
                                    }
                                } else {
                                    if (email.isBlank()) {
                                        isEmailError = true
                                        emailErrorMsg = if (isBengali) "অনুগ্রহ করে ইমেল লিখুন।" else "Please enter your Email."
                                        hasError = true
                                    } else {
                                        val emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$"
                                        if (!email.trim().matches(emailPattern.toRegex())) {
                                            isEmailError = true
                                            emailErrorMsg = if (isBengali) "অনুগ্রহ করে একটি সঠিক ইমেল প্রবেশ করান।" else "Please enter a valid Email address."
                                            hasError = true
                                        }
                                    }
                                    if (password.isBlank()) {
                                        isPasswordError = true
                                        passwordErrorMsg = if (isBengali) "অনুগ্রহ করে পাসওয়ার্ড লিখুন।" else "Please enter your Password."
                                        hasError = true
                                    } else if (password.length < 6) {
                                        isPasswordError = true
                                        passwordErrorMsg = if (isBengali) "পাসওয়ার্ড কমপক্ষে ৬ অক্ষরের হতে হবে।" else "Password must be at least 6 characters long."
                                        hasError = true
                                    }
                                }

                                if (hasError) {
                                    errorMessage = if (isBengali) "ফর্মের তথ্যগুলো সঠিক নয়। লাল চিহ্নিত ঘরগুলো পরীক্ষা করুন।" else "Form validation failed. Please check the marked fields."
                                    return@Button
                                }

                                isLoading = true
                                errorMessage = null

                                val callback: (Boolean, String?) -> Unit = { success, error -> if (success) { if (rememberMe) { sharedPrefs.edit().putBoolean("remember_me", true).putString("saved_email", email.trim()).apply() } else { sharedPrefs.edit().putBoolean("remember_me", false).remove("saved_email").apply() } }
                                    isLoading = false
                                    if (!success) {
                                        errorMessage = error ?: "Authentication failed. Try again."
                                    } else {
                                        if (isSignUpMode) {
                                            isVerificationMode = true
                                        }
                                    }
                                }

                                if (isSignUpMode) {
                                    onSignUp(email.trim(), password.trim(), callback)
                                } else {
                                    onLogin(email.trim(), password.trim(), callback)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("auth_action_button")
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                            } else {
                                Text(
                                    text = if (isSignUpMode) {
                                        if (isBengali) "রেজিস্ট্রেশন করুন" else "Create Account"
                                    } else {
                                        if (isBengali) "প্রবেশ করুন (Secure Login)" else "Secure Sign In"
                                    },
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Mode Toggle Trigger (Hide mode toggle when in verification mode)
            if (!isVerificationMode) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isSignUpMode) {
                            if (isBengali) "ইতিমধ্যে অ্যাকাউন্ট আছে?" else "Already have an account?"
                        } else {
                            if (isBengali) "নতুন অ্যাকাউন্ট তৈরি করতে চান?" else "Don't have an account?"
                        },
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )

                    TextButton(
                        onClick = {
                            isSignUpMode = !isSignUpMode
                            errorMessage = null
                            successMessage = null
                        },
                        modifier = Modifier.testTag("auth_switch_mode")
                    ) {
                        Text(
                            text = if (isSignUpMode) {
                                if (isBengali) "লগইন করুন" else "Log In"
                            } else {
                                if (isBengali) "রেজিস্ট্রেশন করুন" else "Sign Up Now"
                            },
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0288D1),
                            fontSize = 12.sp
                        )
                    }
                }

                        // Divider
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                        ) {
                            Divider(modifier = Modifier.weight(1f), color = Color(0xFFB0BEC5))
                            Text(
                                text = if (isBengali) "অথবা সোশ্যাল সাইন-ইন" else "OR SOCIAL SIGN-IN",
                                fontSize = 10.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                            Divider(modifier = Modifier.weight(1f), color = Color(0xFFB0BEC5))
                        }

                        // Google & Apple Social Login Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Google Login Button
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, Color(0xFFECEFF1)),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .clickable { showGoogleDialog = true }
                                    .testTag("google_login_button"),
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("G", color = Color(0xFF4285F4), fontWeight = FontWeight.Black, fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Google",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.5.sp,
                                        color = Color.DarkGray
                                    )
                                }
                            }

                            // Apple Login Button
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.Black),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .clickable { showAppleDialog = true }
                                    .testTag("apple_login_button"),
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Apple ID",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.5.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Guest Mode Button
                        Button(
                            onClick = {
                                isLoading = true
                                onLogin("guest@niljori.com", "password123") { success, _ ->
                                    isLoading = false
                                    if (!success) {
                                        onSignUp("guest@niljori.com", "password123") { _, _ -> }
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF78909C)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(44.dp).testTag("guest_mode_button")
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isBengali) "গেস্ট হিসেবে প্রবেশ করুন (Guest Mode)" else "Continue as Guest",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 12.5.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Guest sandbox bypass trigger
                TextButton(
                    onClick = {
                        isLoading = true
                        successMessage = null
                        onLogin("guest@niljori.com", "password123") { success, _ ->
                            isLoading = false
                            if (!success) {
                                onSignUp("guest@niljori.com", "password123") { s, _ ->
                                    if (!s) errorMessage = "Guest bypass offline failure."
                                }
                            }
                        }
                    },
                    modifier = Modifier.testTag("auth_guest_bypass")
                ) {
                    Text(
                        text = if (isBengali) "গেস্ট মোডে প্রবেশ করুন (Offline Sandbox)" else "Continue in Sandbox Offline Mode",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Color(0xFF00ACC1)
                    )
                }
            }
        }
    }

    // Google & Apple Dialog Overlays
    if (showGoogleDialog) {
        AlertDialog(
            onDismissRequest = { showGoogleDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("G", color = Color(0xFF4285F4), fontWeight = FontWeight.Black, fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isBengali) "গুগল অ্যাকাউন্ট দিয়ে সাইন-ইন" else "Sign in with Google",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (isBengali) "একটি অ্যাকাউন্ট নির্বাচন করুন:" else "Choose an account to continue to Niljori:",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    
                    val googleAccounts = listOf(
                        "imtiazsharif2424@gmail.com" to "Imtiaz Sharif",
                        "guest_tester@niljori.com" to "Guest Tester"
                    )
                    
                    googleAccounts.forEach { (gEmail, gName) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF5F7F6))
                                .clickable {
                                    showGoogleDialog = false
                                    isLoading = true
                                    onLogin(gEmail, "password123") { success, _ ->
                                        if (!success) {
                                            onSignUp(gEmail, "password123") { s, _ ->
                                                isLoading = false
                                                if (s) {
                                                    fullName = gName
                                                    successMessage = "Signed in with Google!"
                                                }
                                            }
                                        } else {
                                            isLoading = false
                                            successMessage = "Signed in with Google!"
                                        }
                                    }
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF0288D1)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(gName.take(1), color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(gName, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.DarkGray)
                                Text(gEmail, fontSize = 10.5.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showGoogleDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }

    if (showAppleDialog) {
        var isScanning by remember { mutableStateOf(true) }
        
        LaunchedEffect(showAppleDialog) {
            kotlinx.coroutines.delay(2000)
            isScanning = false
            kotlinx.coroutines.delay(800)
            showAppleDialog = false
            isLoading = true
            val appleEmail = "apple_user@niljori.com"
            onLogin(appleEmail, "password123") { success, _ ->
                if (!success) {
                    onSignUp(appleEmail, "password123") { s, _ ->
                        isLoading = false
                        if (s) {
                            fullName = "Apple User"
                            successMessage = "Signed in with Apple ID!"
                        }
                    }
                } else {
                    isLoading = false
                    successMessage = "Signed in with Apple ID!"
                }
            }
        }
        
        AlertDialog(
            onDismissRequest = { showAppleDialog = false },
            title = null,
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = if (isScanning) Icons.Default.Face else Icons.Default.CheckCircle,
                        contentDescription = "Face ID",
                        tint = if (isScanning) Color(0xFF0288D1) else Color(0xFF2E7D32),
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        text = if (isScanning) "Face ID" else "Authenticated",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.DarkGray
                    )
                    Text(
                        text = if (isScanning) "Verifying Apple ID credentials..." else "Apple ID Sign In Confirmed",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {}
        )
    }

}