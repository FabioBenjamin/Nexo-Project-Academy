package com.nexo.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nexo.R
import com.nexo.data.api.AuthApiService
import com.nexo.dto.ForgotPasswordRequest
import com.nexo.dto.ResetPasswordRequest
import com.nexo.ui.theme.AppStrings
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordBottomSheet(
    authApiService: AuthApiService,
    initialEmail: String = "",
    onDismiss: () -> Unit,
    onResetSuccess: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var step by remember { mutableIntStateOf(1) } // 1 = Enviar e-mail, 2 = Validar código + nova senha
    var email by remember { mutableStateOf(initialEmail) }
    var code by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.mascote_calma),
                contentDescription = "Mascote Nexo Calmo",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(84.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (step == 1) "Recuperar Senha" else "Criar Nova Senha",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (step == 1) {
                    "Digite o e-mail da sua conta para receber um código de verificação de 6 dígitos."
                } else {
                    "Enviamos um código para $email. Insira-o abaixo junto com sua nova senha."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (step == 1) {
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        errorMessage = null
                    },
                    label = { Text(AppStrings.labelEmail) },
                    singleLine = true,
                    trailingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                OutlinedTextField(
                    value = code,
                    onValueChange = {
                        if (it.length <= 6) code = it.filter { ch -> ch.isDigit() }
                        errorMessage = null
                    },
                    label = { Text("Código de 6 dígitos") },
                    singleLine = true,
                    trailingIcon = { Icon(Icons.Default.Key, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.NumberPassword,
                        imeAction = ImeAction.Next
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = newPassword,
                    onValueChange = {
                        newPassword = it
                        errorMessage = null
                    },
                    label = { Text(AppStrings.labelNewPassword) },
                    singleLine = true,
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        errorMessage = null
                    },
                    label = { Text(AppStrings.labelConfirmPassword) },
                    singleLine = true,
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = errorMessage!!,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    errorMessage = null
                    if (step == 1) {
                        val trimmed = email.trim()
                        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(trimmed).matches()) {
                            errorMessage = AppStrings.errEmailInvalid
                            return@Button
                        }
                        scope.launch {
                            isLoading = true
                            try {
                                val resp = authApiService.forgotPassword(ForgotPasswordRequest(trimmed))
                                if (resp.isSuccessful) {
                                    step = 2
                                } else {
                                    errorMessage = "Não foi possível enviar o código (${resp.code()})"
                                }
                            } catch (e: Exception) {
                                errorMessage = AppStrings.errNetwork
                            } finally {
                                isLoading = false
                            }
                        }
                    } else {
                        if (code.length != 6) {
                            errorMessage = "Informe o código de 6 dígitos"
                            return@Button
                        }
                        if (newPassword.length < 6) {
                            errorMessage = AppStrings.errPasswordLength
                            return@Button
                        }
                        if (newPassword != confirmPassword) {
                            errorMessage = AppStrings.errPasswordMismatchSpecific
                            return@Button
                        }
                        scope.launch {
                            isLoading = true
                            try {
                                val resp = authApiService.resetPassword(
                                    ResetPasswordRequest(
                                        email = email.trim(),
                                        code = code,
                                        newPassword = newPassword,
                                        confirmNewPassword = confirmPassword
                                    )
                                )
                                if (resp.isSuccessful) {
                                    onResetSuccess()
                                } else {
                                    errorMessage = "Código inválido ou expirado"
                                }
                            } catch (e: Exception) {
                                errorMessage = AppStrings.errNetwork
                            } finally {
                                isLoading = false
                            }
                        }
                    }
                },
                enabled = !isLoading,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                } else {
                    Text(
                        text = if (step == 1) "Enviar Código" else "Redefinir Senha",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            if (step == 2) {
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = { step = 1; errorMessage = null }) {
                    Text("Alterar e-mail ou reenviar código")
                }
            }
        }
    }
}