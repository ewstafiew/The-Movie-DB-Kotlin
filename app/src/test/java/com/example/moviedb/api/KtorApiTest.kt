package com.example.moviedb.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AllureRunner::class)
@Epic("TMDB API")
@Feature("Ktor HTTP Client")
class KtorApiTest {

    private lateinit var client: HttpClient
    private val json = Json { ignoreUnknownKeys = true }

    @Before
    fun setup() {
        val mockEngine = MockEngine { request ->
            val url = request.url.toString()
            when {
                "/discover/movie" in url && "api_key=VALID" in url -> {
                    respond(
                        """{"results": [{"id": 1, "title": "Movie A"}]}""",
                        HttpStatusCode.OK,
                        headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }
                "/discover/movie" in url && "api_key=INVALID" in url -> {
                    respond(
                        """{"success": false, "status_code": 401, "status_message": "Invalid API key"}""",
                        HttpStatusCode.Unauthorized,
                        headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }
                "/movie/550" in url && "credits" !in url && "api_key=VALID" in url -> {
                    respond(
                        """{"id": 550, "title": "Fight Club", "overview": "Desc"}""",
                        HttpStatusCode.OK,
                        headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }
                "/movie/invalid" in url && "credits" !in url -> {
                    respond(
                        """{"success": false, "status_code": 404, "status_message": "The resource you requested could not be found."}""",
                        HttpStatusCode.NotFound,
                        headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }
                "/movie/550/credits" in url && "api_key=VALID" in url -> {
                    respond(
                        """{"id": 550, "cast": [{"id": 10, "name": "Actor A"}], "crew": [{"id": 20, "name": "Director A"}]}""",
                        HttpStatusCode.OK,
                        headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }
                "/movie/invalid/credits" in url -> {
                    respond(
                        """{"success": false, "status_code": 404, "status_message": "Credits not found"}""",
                        HttpStatusCode.NotFound,
                        headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }
                else -> {
                    respond(
                        """{"error": "Not found"}""",
                        HttpStatusCode.NotFound,
                        headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }
            }
        }

        client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(json)
            }
        }
    }

    @After
    fun tearDown() {
        client.close()
    }

    // Успешный кейс: корректные входные данные.
    @Test
    @Story("Discover Movies")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Проверяет метод /discover/movie при корректном API ключе: ожидаем 200 и непустой список.")
    fun discoverMovie_success_returnsMovieListAnd200() {
        runBlocking {
            val response = client.get("https://api.themoviedb.org/3/discover/movie?api_key=VALID")
            assertEquals(HttpStatusCode.OK, response.status)
            val payload = json.parseToJsonElement(response.bodyAsText()).jsonObject
            val results = payload.getValue("results").jsonArray
            assertTrue(results.isNotEmpty())
            val title = results.first().jsonObject.getValue("title").jsonPrimitive.content
            assertEquals("Movie A", title)
        }
    }

    // Ошибочный кейс: при возникновении ошибки.
    @Test
    @Story("Discover Movies")
    @Severity(SeverityLevel.NORMAL)
    @Description("Проверяет метод /discover/movie при неверном API ключе: ожидаем 401 и код ошибки в теле.")
    fun discoverMovie_error_returns401() {
        runBlocking {
            val response = client.get("https://api.themoviedb.org/3/discover/movie?api_key=INVALID")
            assertEquals(HttpStatusCode.Unauthorized, response.status)
            val payload = json.parseToJsonElement(response.bodyAsText()).jsonObject
            assertEquals("401", payload.getValue("status_code").toString())
        }
    }

    // Успешный кейс: корректные входные данные.
    @Test
    @Story("Movie Details")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Проверяет метод /movie/{id} при валидном id: ожидаем 200 и корректные поля фильма.")
    fun movieDetails_success_returnsMovieAnd200() {
        runBlocking {
            val response = client.get("https://api.themoviedb.org/3/movie/550?api_key=VALID")
            assertEquals(HttpStatusCode.OK, response.status)
            val payload = json.parseToJsonElement(response.bodyAsText()).jsonObject
            assertEquals("550", payload.getValue("id").toString())
            assertEquals("Fight Club", payload.getValue("title").jsonPrimitive.content)
        }
    }

    // Ошибочный кейс: при возникновении ошибки.
    @Test
    @Story("Movie Details")
    @Severity(SeverityLevel.NORMAL)
    @Description("Проверяет метод /movie/{id} при невалидном id: ожидаем 404 и код ошибки в ответе.")
    fun movieDetails_error_returns404() {
        runBlocking {
            val response = client.get("https://api.themoviedb.org/3/movie/invalid?api_key=VALID")
            assertEquals(HttpStatusCode.NotFound, response.status)
            val payload = json.parseToJsonElement(response.bodyAsText()).jsonObject
            assertEquals("404", payload.getValue("status_code").toString())
        }
    }

    // Успешный кейс: корректные входные данные.
    @Test
    @Story("Movie Credits")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Проверяет метод /movie/{id}/credits при валидном id: ожидаем 200 и непустые cast/crew.")
    fun movieCredits_success_returnsCastAndCrew() {
        runBlocking {
            val response = client.get("https://api.themoviedb.org/3/movie/550/credits?api_key=VALID")
            assertEquals(HttpStatusCode.OK, response.status)
            val payload = json.parseToJsonElement(response.bodyAsText()).jsonObject
            assertTrue(payload.getValue("cast").jsonArray.isNotEmpty())
            assertTrue(payload.getValue("crew").jsonArray.isNotEmpty())
        }
    }

    // Ошибочный кейс: при возникновении ошибки.
    @Test
    @Story("Movie Credits")
    @Severity(SeverityLevel.NORMAL)
    @Description("Проверяет метод /movie/{id}/credits при невалидном id: ожидаем 404 и код ошибки.")
    fun movieCredits_error_returns404() {
        runBlocking {
            val response = client.get("https://api.themoviedb.org/3/movie/invalid/credits?api_key=VALID")
            assertEquals(HttpStatusCode.NotFound, response.status)
            val payload = json.parseToJsonElement(response.bodyAsText()).jsonObject
            assertEquals("404", payload.getValue("status_code").toString())
        }
    }
}
