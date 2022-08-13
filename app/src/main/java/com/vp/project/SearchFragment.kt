package com.vp.project

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vp.project.databinding.FragmentSearchBinding
import com.vp.project.model.LocationLatLngEntity
import com.vp.project.model.SearchResultEntity
import com.vp.project.response.search.Poi
import com.vp.project.response.search.Pois
import com.vp.project.response.search.SearchPoiInfo
import com.vp.project.utility.RetrofitUtil
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import com.vp.project.SearchFragment as SearchFragment


class SearchFragment : Fragment() {

    private lateinit var job: Job

    val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
    private var _binding: FragmentSearchBinding? = null

    private val binding get() = _binding!!
    lateinit var adapter: SearchRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        val view = binding.root

        job = Job()

        initAdapter()
        initViews()
        bindViews()
        initData()
        return view
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initAdapter() {
        adapter = SearchRecyclerAdapter()
    }

    private fun bindViews() = with(binding) {

        val sb = view?.findViewById<Button>(R.id.Search_Button)
        sb?.setOnClickListener {
            searchKeyword(SearchEdittext.text.toString())
        }

        SearchEdittext.setOnKeyListener { v, keyCode, event ->
            when (keyCode) {
                KeyEvent.KEYCODE_ENTER -> {
                    searchKeyword(SearchEdittext.text.toString())
                    return@setOnKeyListener true
                }
            }
            return@setOnKeyListener false
        }
    }



    /*
    `with` scope function 사용
     */
    private fun initViews() = with(binding) {
        emptyResultTextView.isVisible = false
        recyclerView.adapter = adapter

        // 무한 스크롤 기능 구현
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                recyclerView.adapter ?: return

                val lastVisibleItemPosition =
                    (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                val totalItemCount = recyclerView.adapter!!.itemCount - 1

                // 페이지 끝에 도달한 경우
                if (!recyclerView.canScrollVertically(1) && lastVisibleItemPosition == totalItemCount) {
                    loadNext()
                }
            }
        })
    }

    private fun loadNext() {
        if (binding.recyclerView.adapter?.itemCount == 0)
            return

        searchWithPage(adapter.currentSearchString, adapter.currentPage + 1)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initData() {
        adapter.notifyDataSetChanged()
    }

    private fun setData(searchInfo: SearchPoiInfo, keywordString: String) {

        val pois: Pois = searchInfo.pois
        // mocking data
        val dataList = pois.poi.map {
            SearchResultEntity(
                name = it.name ?: "빌딩명 없음",
                fullAddress = makeMainAddress(it),
                locationLatLng = LocationLatLngEntity(
                    it.noorLat,
                    it.noorLon
                )
            )
        }
        adapter.setSearchResultList(dataList) {

            //setFragmentResult("SearchResult", bundleOf("result" to it))
            //val bundle = Bundle()
            //val SearchResult :SearchResultEntity = it
            //val NewFragment = SearchConfirmFragment()
            //bundle.putString("Name", SearchResult.name)
            //bundle.putFloat("latitude", SearchResult.locationLatLng.latitude)
            //bundle.putFloat("longitude", SearchResult.locationLatLng.longitude)
            //log.d("Test!!",SearchResult.locationLatLng.latitude.toString())
            //Toast.makeText(context,"${SearchResult.locationLatLng.latitude} + hello",Toast.LENGTH_LONG).show()


            setFragmentResult("requestKey", bundleOf("bundleKey" to it))

            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, SearchConfirmFragment())
                .commit()

            //parentFragmentManager.commit {
            //    replace<SearchConfirmFragment>(R.id.nav_host_fragment_activity_main)
            //    setReorderingAllowed(true)
               // addToBackStack("add") // name can be null
            //}

        }
        adapter.currentPage = searchInfo.page.toInt()
        adapter.currentSearchString = keywordString
    }

    private fun searchKeyword(keywordString: String) {
        searchWithPage(keywordString, 1)
    }

    private fun searchWithPage(keywordString: String, page: Int) {
        // 비동기 처리
        val uiScope = CoroutineScope(Dispatchers.Main + job)
        uiScope.launch(coroutineContext) {
            try {
                binding.progressCircular.isVisible = true // 로딩 표시
                if (page == 1) {
                    adapter.clearList()
                }
                // IO 스레드 사용
                withContext(Dispatchers.IO) {
                    val response = RetrofitUtil.apiService.getSearchLocation(
                        keyword = keywordString,
                        page = page
                    )
                    if (response.isSuccessful) {
                        val body = response.body()
                        // Main (UI) 스레드 사용
                        withContext(Dispatchers.Main) {
                            Log.e("response LSS", body.toString())
                            body?.let { searchResponse ->
                                setData(searchResponse.searchPoiInfo, keywordString)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                binding.progressCircular.isVisible = false // 로딩 표시 완료
            }
        }
    }

    private fun makeMainAddress(poi: Poi): String =
        if (poi.secondNo?.trim().isNullOrEmpty()) {
            (poi.upperAddrName?.trim() ?: "") + " " +
                    (poi.middleAddrName?.trim() ?: "") + " " +
                    (poi.lowerAddrName?.trim() ?: "") + " " +
                    (poi.detailAddrName?.trim() ?: "") + " " +
                    poi.firstNo?.trim()
        } else {
            (poi.upperAddrName?.trim() ?: "") + " " +
                    (poi.middleAddrName?.trim() ?: "") + " " +
                    (poi.lowerAddrName?.trim() ?: "") + " " +
                    (poi.detailAddrName?.trim() ?: "") + " " +
                    (poi.firstNo?.trim() ?: "") + " " +
                    poi.secondNo?.trim()
        }

}