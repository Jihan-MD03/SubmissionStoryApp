package com.dicoding.picodiploma.loginwithanimation.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dicoding.picodiploma.loginwithanimation.data.remote.StoryApiService
import com.dicoding.picodiploma.loginwithanimation.data.remote.responses.ListStoryItem

class StoryPagingSource(
    private val storyApiService: StoryApiService
) : PagingSource<Int, ListStoryItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val currentPage = params.key ?: 1
            val response = storyApiService.getStories(page = currentPage, size = params.loadSize)

            LoadResult.Page(
                data = response.listStory, // Sesuaikan dengan struktur JSON
                prevKey = if (currentPage == 1) null else currentPage - 1,
                nextKey = if (response.listStory.isEmpty()) null else currentPage + 1
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
