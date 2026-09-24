package com.uade.pokecompose.ui

import android.content.Intent
import android.webkit.MimeTypeMap
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.uade.pokecompose.data.Pokemon
import com.uade.pokecompose.data.PokemonRepository
import com.uade.pokecompose.logic.DiagnosticLogger
import com.uade.pokecompose.logic.PokemonLogic
import com.uade.pokecompose.ui.theme.PokeComposeTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonScreen(
    repository: PokemonRepository = PokemonRepository()
) {
    val context = LocalContext.current
    val todosLosPokemons = remember { repository.getPokemons() }
    var busqueda by remember { mutableStateOf("") }
    val capturados = remember { mutableStateListOf<Pokemon>() }

    val porcentajeProgreso = PokemonLogic.calcularPorcentajeProgreso(
        capturados = capturados.size,
        total = todosLosPokemons.size
    )

    val faltantes = PokemonLogic.calcularRestantes(
        total = todosLosPokemons.size,
        capturados = capturados.size
    )

    val listaFiltrada = if (busqueda.isNotBlank()) {
        val idFiltro = try {
            PokemonLogic.parsearIdBuscado(busqueda)
        } catch (e: Exception) {
            null
        }

        todosLosPokemons.filter { pokemon ->
            pokemon.name.contains(busqueda, ignoreCase = true) && (idFiltro != null && pokemon.id == idFiltro)
        }
    } else {
        todosLosPokemons
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Pokédex Kanto & Johto",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
            val diagnosticLogger = DiagnosticLogger()
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Capturados: ${capturados.size} de ${todosLosPokemons.size}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Faltan capturar: $faltantes Pokémon",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Progreso de Pokédex: $porcentajeProgreso%",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Button(
                        onClick = {
                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "¡He capturado ${capturados.size} Pokémon en mi Pokédex!"
                                )
                            }
                            val diagnosticLogger = DiagnosticLogger()
                            context.startActivity(sendIntent)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Compartir mi equipo")
                    }
                }
            }

            OutlinedTextField(
                value = busqueda,
                onValueChange = { nuevoTexto ->
                    busqueda = nuevoTexto
                },
                label = { Text("Buscar por nombre o número…") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(listaFiltrada) { pokemon ->
                    val yaCapturado = capturados.any { it.id == pokemon.id }
                    val generacion = PokemonLogic.clasificarGeneracion(pokemon.id)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                            .clickable { },
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            AsyncImage(
                                model = pokemon.spriteUrl,
                                contentDescription = pokemon.nameFormatted,
                                modifier = Modifier
                                    .size(64.dp)
                                    .background(Color.LightGray)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Fit
                            )

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = pokemon.nameFormatted,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "N.º ${pokemon.id} • $generacion",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Button(
                                onClick = {
                                    if (yaCapturado) {
                                        if (capturados.isNotEmpty()) {
                                            capturados.removeAt(0)
                                        }
                                    } else {
                                        capturados.add(pokemon)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (yaCapturado) Color(0xFFE53935) else MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text(if (yaCapturado) "Liberar" else "Capturar")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PokemonScreenPreview() {
    PokeComposeTheme {
        PokemonScreen()
    }
}
