package com.ifkusyoba.dicogram.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.ifkusyoba.dicogram.MainDispatcherRule
import com.ifkusyoba.dicogram.getOrAwaitValue
import com.ifkusyoba.dicogram.models.Story
import com.ifkusyoba.dicogram.models.Users
import com.ifkusyoba.dicogram.services.paging.PagingAdapter
import com.ifkusyoba.dicogram.services.repository.StoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class StoryViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var repository: StoryRepository

    // *Mengikuti prosedur pada modul Latihan Unit Testing Repository dengan Fake
    @Test
    fun `when getStory Should Not Null and Return Success`() = runTest {
        val dummyResponse = DummyData.generateDummyStoryList()
        val data: PagingData<Story> = StoryPagingSource.snapshot(dummyResponse)
        val expectedStory = MutableLiveData<PagingData<Story>>()
        expectedStory.value = data
        Mockito.`when`(repository.getStory()).thenReturn(expectedStory)
        val listStoryViewModel = StoryViewModel(repository)
        val actualStories: PagingData<Story> = listStoryViewModel.getStory().getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = PagingAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
        )

        differ.submitData(actualStories)
        assertNotNull(differ.snapshot())
        assertEquals(dummyResponse, differ.snapshot())
        assertEquals(dummyResponse[0], differ.snapshot()[0])
        /*assertEquals(dummyResponse.size, differ.snapshot().size)
        assertEquals(dummyResponse[0].id, differ.snapshot()[0]?.id)
        assertEquals(dummyResponse[0].name, differ.snapshot()[0]?.name)
        assertEquals(dummyResponse[0].description, differ.snapshot()[0]?.description)
        assertEquals(dummyResponse[0].photoUrl, differ.snapshot()[0]?.photoUrl)
        assertEquals(dummyResponse[0].createdAt, differ.snapshot()[0]?.createdAt)
        assertEquals(dummyResponse[0].lat, differ.snapshot()[0]?.lat)
        assertEquals(dummyResponse[0].lng, differ.snapshot()[0]?.lng)*/
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `when Get Story Empty Should Return No Data`() = runTest {
        val expectedStory = MutableLiveData<PagingData<Story>>()
        expectedStory.value = PagingData.empty()
        Mockito.`when`(repository.getStory()).thenReturn(expectedStory)
        val listStoryViewModel = StoryViewModel(repository)
        val actualStories: PagingData<Story> = listStoryViewModel.getStory().getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = PagingAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
        )

        differ.submitData(actualStories)
        assertNotNull(differ.snapshot())
        assertEquals(0, differ.snapshot().size)
    }
}

class StoryPagingSource : PagingSource<Int, LiveData<List<Story>>>() {
    companion object {
        fun snapshot(items: List<Story>): PagingData<Story> {
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, LiveData<List<Story>>>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<Story>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}

// *Objek data dummy ada pada Latihan Unit Testing Repository dengan Fake
object DummyData {
    fun generateDummyStoryList(): List<Story> {
        val storyList = ArrayList<Story>()
        for (index in 0..100) {
            val story = Story(
                index.toString(),
                "name + $index",
                "desc + $index",
                "$index",
                "created + $index",
                index.toDouble(),
                index.toDouble()
            )
            storyList.add(story)
        }
        return storyList
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}