package com.ucne.cinetix.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ucne.cinetix.data.remote.TheMovieDbApi
import com.ucne.cinetix.data.remote.dto.FilmDto
import com.ucne.cinetix.util.FilmType
import retrofit2.HttpException
import java.io.IOException

class NowPlayingFilmSource(private val theMovieDbApi: TheMovieDbApi, private val filmType: FilmType) :
    PagingSource<Int, FilmDto>() {
    override fun getRefreshKey(state: PagingState<Int, FilmDto>): Int? = state.anchorPosition

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FilmDto> {
        return try {
            val nextPage = params.key ?: 1
            val nowPlayingMovies =
                if (filmType == FilmType.MOVIE) theMovieDbApi.getNowPlayingMovies(page = nextPage)
                else theMovieDbApi.getOnTheAirTvShows(page = nextPage)

            LoadResult.Page(
                data = nowPlayingMovies.results,
                prevKey = if (nextPage == 1) null else nextPage - 1,
                nextKey = if (nowPlayingMovies.results.isEmpty()) null else nowPlayingMovies.page + 1
            )
        } catch (e: IOException) {
            return LoadResult.Error(e)
        } catch (e: HttpException) {
            return LoadResult.Error(e)
        }
    }
}