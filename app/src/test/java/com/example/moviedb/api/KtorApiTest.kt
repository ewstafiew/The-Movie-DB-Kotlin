package com.example.moviedb.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import io.qameta.allure.Description
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Severity
import io.qameta.allure.SeverityLevel
import io.qameta.allure.Story
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AllureRunner::class)
@Epic("PokéAPI")
@Feature("Ktor HTTP Client")
class KtorApiTest {

    private lateinit var client: HttpClient
    private val json = Json { ignoreUnknownKeys = true }
    private val baseUrl = "https://pokeapi.co/api/v2"

    @Before
    fun setup() {
        client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(json)
            }
            expectSuccess = false
        }
    }

    @After
    fun tearDown() {
        client.close()
    }

    // Метод 1: GET /pokemon/{name}

    /**
     * Успешный кейс (корректные данные):
     * Запрашиваем известного покемона "pikachu".
     * Ожидаем: статус 200 и поле "name" = "pikachu" в ответе.
     */
    @Test
    @Story("Pokemon")
    @Severity(SeverityLevel.CRITICAL)
    @Description("GET /pokemon/pikachu при корректном имени: ожидаем 200 и name=pikachu.")
    fun getPokemon_success_returns200AndCorrectName() {
        runBlocking {
            val response = client.get("$baseUrl/pokemon/pikachu")
            assertEquals(HttpStatusCode.OK, response.status)
            val body = json.parseToJsonElement(response.bodyAsText()).jsonObject
            val name = body["name"]?.jsonPrimitive?.content
            assertEquals("pikachu", name)
        }
    }

    /**
     * Ошибочный кейс (некорректные данные):
     * Запрашиваем несуществующего покемона "nonexistent-pokemon-12345".
     * Ожидаем: статус 404 — ресурс не найден.
     */
    @Test
    @Story("Pokemon")
    @Severity(SeverityLevel.NORMAL)
    @Description("GET /pokemon/nonexistent при несуществующем имени: ожидаем 404.")
    fun getPokemon_error_returns404ForUnknownPokemon() {
        runBlocking {
            val response = client.get("$baseUrl/pokemon/nonexistent-pokemon-12345")
            assertEquals(HttpStatusCode.NotFound, response.status)
        }
    }

    // Метод 2: GET /berry/{name}

    /**
     * Успешный кейс (корректные данные):
     * Запрашиваем ягоду "cheri".
     * Ожидаем: статус 200, поле "name" = "cheri" и наличие поля "growth_time".
     */
    @Test
    @Story("Berry")
    @Severity(SeverityLevel.CRITICAL)
    @Description("GET /berry/cheri при корректном имени: ожидаем 200 и name=cheri.")
    fun getBerry_success_returns200AndCorrectName() {
        runBlocking {
            val response = client.get("$baseUrl/berry/cheri")
            assertEquals(HttpStatusCode.OK, response.status)
            val body = json.parseToJsonElement(response.bodyAsText()).jsonObject
            assertEquals("cheri", body["name"]?.jsonPrimitive?.content)
            assertNotNull(body["growth_time"])
        }
    }

    /**
     * Ошибочный кейс (некорректные данные):
     * Запрашиваем несуществующую ягоду "unknown-berry-xyz".
     * Ожидаем: статус 404 — ресурс не найден.
     */
    @Test
    @Story("Berry")
    @Severity(SeverityLevel.NORMAL)
    @Description("GET /berry/unknown при несуществующем имени: ожидаем 404.")
    fun getBerry_error_returns404ForUnknownBerry() {
        runBlocking {
            val response = client.get("$baseUrl/berry/unknown-berry-xyz")
            assertEquals(HttpStatusCode.NotFound, response.status)
        }
    }


    // Метод 3: GET /move/{name}
    /**
     * Успешный кейс (корректные данные):
     * Запрашиваем атаку "pound".
     * Ожидаем: статус 200, поле "name" = "pound" и наличие поля "power".
     */
    @Test
    @Story("Move")
    @Severity(SeverityLevel.CRITICAL)
    @Description("GET /move/pound при корректном имени: ожидаем 200 и name=pound.")
    fun getMove_success_returns200AndCorrectName() {
        runBlocking {
            val response = client.get("$baseUrl/move/pound")
            assertEquals(HttpStatusCode.OK, response.status)
            val body = json.parseToJsonElement(response.bodyAsText()).jsonObject
            assertEquals("pound", body["name"]?.jsonPrimitive?.content)
            assertTrue(body.containsKey("power"))
        }
    }

    /**
     * Ошибочный кейс (некорректные данные):
     * Запрашиваем несуществующую атаку "superdupernonexistentmove".
     * Ожидаем: статус 404 — ресурс не найден.
     */
    @Test
    @Story("Move")
    @Severity(SeverityLevel.NORMAL)
    @Description("GET /move/unknown при несуществующем имени: ожидаем 404.")
    fun getMove_error_returns404ForUnknownMove() {
        runBlocking {
            val response = client.get("$baseUrl/move/superdupernonexistentmove")
            assertEquals(HttpStatusCode.NotFound, response.status)
        }
    }
}
