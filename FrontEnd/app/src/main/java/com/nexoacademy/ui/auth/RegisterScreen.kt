package com.example.nexoacademy.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.nexoacademy.ui.theme.*

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val username by viewModel.username.collectAsState()
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val confirmPassword by viewModel.confirmPassword.collectAsState()

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is RegisterUiState.Success) {
            onRegisterSuccess()
            viewModel.resetUiState() // Corrigido de resetState() para resetUiState() para bater com o ViewModel
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CremeSuave)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Título e Boas-vindas
            Text(
                text = "Criar Conta",
                style = MaterialTheme.typography.headlineLarge,
                color = TextoPrincipal,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Junte-se ao Nexo Academy e comece sua jornada de aprendizado!",
                style = MaterialTheme.typography.bodyMedium,
                color = TextoSecundario,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Campo Username
            OutlinedTextField(
                value = username,
                onValueChange = { viewModel.username.value = it },
                label = { Text("Nome de usuário") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = BrancoPuro,
                    unfocusedContainerColor = BrancoPuro,
                    focusedBorderColor = RoxoAcolhedor,
                    unfocusedBorderColor = CinzaDesabilitado
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Email
            OutlinedTextField(
                value = email,
                onValueChange = { viewModel.email.value = it },
                label = { Text("E-mail") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = BrancoPuro,
                    unfocusedContainerColor = BrancoPuro,
                    focusedBorderColor = RoxoAcolhedor,
                    unfocusedBorderColor = CinzaDesabilitado
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Senha
            OutlinedTextField(
                value = password,
                onValueChange = { viewModel.password.value = it },
                label = { Text("Senha") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = BrancoPuro,
                    unfocusedContainerColor = BrancoPuro,
                    focusedBorderColor = RoxoAcolhedor,
                    unfocusedBorderColor = CinzaDesabilitado
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Confirmar Senha
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { viewModel.confirmPassword.value = it },
                label = { Text("Confirmar Senha") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = BrancoPuro,
                    unfocusedContainerColor = BrancoPuro,
                    focusedBorderColor = RoxoAcolhedor,
                    unfocusedBorderColor = CinzaDesabilitado
                )
            )

            // Mensagem de Erro (se houver)
            if (uiState is RegisterUiState.Error) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = (uiState as RegisterUiState.Error).message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = VermelhoErro,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Botão Principal (Cadastrar)
            Button(
                onClick = { viewModel.register() },
                enabled = uiState !is RegisterUiState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RoxoAcolhedor,
                    contentColor = BrancoPuro
                )
            ) {
                if (uiState is RegisterUiState.Loading) {
                    CircularProgressIndicator(
                        color = BrancoPuro,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = "Cadastrar",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Atalho para Ir para Login
            TextButton(onClick = onNavigateToLogin) {
                Text(
                    text = "Já tem uma conta? Faça login",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AzulNexo
                )
            }
        }
    }
}