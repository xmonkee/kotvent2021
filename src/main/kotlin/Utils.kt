import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

object Utils {
    private val client = HttpClient.newBuilder().build();
    private val SESSION = System.getenv("ADVENT_SESSION")

    fun getRawInput(day: Int): String {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("https://adventofcode.com/2021/day/${day}/input"))
            .header("Cookie", "session=${SESSION}")
            .GET()
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assert(response.statusCode() == 200)
        return response.body();
    }

    fun getListInput(day: Int): List<String> {
       return this.getRawInput(day).split("\n").filter { x -> x.isNotEmpty() };
    }

    fun getNumInput(day: Int): List<Int> {
        return this.getRawInput(day).split("\n").filter {x -> x.isNotEmpty()}.map { x -> x.toInt() };
    }
}