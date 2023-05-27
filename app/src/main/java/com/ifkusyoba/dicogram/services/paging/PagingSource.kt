package com.ifkusyoba.dicogram.services.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ifkusyoba.dicogram.services.ApiService
import com.ifkusyoba.dicogram.services.SharedPreferences
import com.ifkusyoba.dicogram.models.Story
import kotlinx.coroutines.flow.first

class PagingSource(private val apiService: ApiService, private val pref: SharedPreferences) : PagingSource<Int, Story>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Story> {
        return try {
            val page = params.key ?: INITIAL_PAGE_INDEX
            val token = "Bearer ${pref.getData().first().token}"
            val responseData = apiService.getStoryList(token, page, params.loadSize).listStory

            LoadResult.Page(
                data = responseData,
                prevKey = if (page == INITIAL_PAGE_INDEX) null else page - 1,
                nextKey = if (responseData.isEmpty()) null else page + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Story>): Int? {
        return state.anchorPosition?.let {
            val anchorPage = state.closestPageToPosition(it)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

}