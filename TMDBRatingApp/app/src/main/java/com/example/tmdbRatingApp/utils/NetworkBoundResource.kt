import android.util.Log
import com.example.tmdbRatingApp.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend inline fun <ResultType, RequestType> networkBoundResource(
    crossinline queryLocalData: () -> ResultType,
    crossinline fetch: suspend () -> RequestType,
    queryCurrent: (RequestType) -> ResultType,
    crossinline saveFetchResult: suspend (RequestType, String?) -> Unit,
    crossinline shouldFetch: (ResultType) -> Boolean = { true },
    typeOfList: String? = null
): Resource<ResultType> {

    Log.d("Network", "in NBS")

    val data = withContext(Dispatchers.IO) { queryLocalData() }

    return if (shouldFetch(data)) {

        val response = fetch()
        if (response is Resource.Success<*>) {
            Log.d("Pagination", "Calles1")
            saveFetchResult(response, typeOfList)
            if (response.data != null)
                Resource.Success(queryCurrent(response))
            else
                Resource.Success(withContext(Dispatchers.IO) { queryLocalData() })
        } else {
            val error = response as Resource.Error<*>
            Log.d("Pagination", "Calles2")

            Resource.Error(error.error, data)
        }
    } else
        Resource.Success(data)

}