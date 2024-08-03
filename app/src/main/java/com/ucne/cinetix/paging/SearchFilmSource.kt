package com.ucne.cinetix.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ucne.cinetix.data.remote.TheMovieDbApi
import com.ucne.cinetix.data.remote.dto.FilmDto
import retrofit2.HttpException
import java.io.IOException

class SearchFilmSource(
    private val theMovieDbApi: TheMovieDbApi,
    private val searchParams: String
) : PagingSource<Int, FilmDto>() {
    override fun getRefreshKey(state: PagingState<Int, FilmDto>): Int? = state.anchorPosition

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FilmDto> {
        return try {
            val nextPage = params.key ?: 1
            val searchMovies = theMovieDbApi.multiSearch(
                page = nextPage,
                searchParams = searchParams
            )
            LoadResult.Page(
                data = searchMovies.results,
                prevKey = if (nextPage == 1) null else nextPage - 1,
                nextKey = if (searchMovies.results.isEmpty()) null else searchMovies.page + 1
            )
        } catch (e: IOException) {
            return LoadResult.Error(e)
        } catch (e: HttpException) {
            return LoadResult.Error(e)
        }
    }
}