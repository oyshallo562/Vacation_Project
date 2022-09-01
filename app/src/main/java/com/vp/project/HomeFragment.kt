package com.vp.project

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.vp.project.databinding.FragmentHomeBinding
import com.vp.project.model.CountEntity

class HomeFragment : Fragment() {
    private var binding: FragmentHomeBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private var firestore : FirebaseFirestore? = null
    val PinList = arrayListOf<CountEntity>()    // 아이템 배열
    val PinterList = arrayListOf<CountEntity>()    // 아이템 배열2
    val PinAdapter = HomeRecyclerAdapter(PinList)         // 리사이클러 뷰 어댑터
    val PinterAdapter = HomeRecyclerAdapter(PinterList)         // 리사이클러 뷰 어댑터

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        firestore = FirebaseFirestore.getInstance()
        binding = FragmentHomeBinding.inflate(layoutInflater)
        val view = binding!!.root

        binding!!.PinterRV.adapter = PinterAdapter
        binding!!.PlaceRV.adapter = PinAdapter


            firestore!!.collection("UserCount").orderBy("followers", Query.Direction.DESCENDING)   // 작업할 컬렉션
                .get()      // 문서 가져오기
                .addOnSuccessListener { result ->
                    // 성공할 경우
                    PinterList.clear()
                    for (document in result) {  // 가져온 문서들은 result에 들어감
                        val item = CountEntity(document["followers"].toString().toInt() , document["name"].toString())
                        PinterList.add(item)
                    }
                    PinterAdapter.notifyDataSetChanged()  // 리사이클러 뷰 갱신
                }
                .addOnFailureListener { exception ->
                    // 실패할 경우
                    Log.w("TAG", "Error getting documents: $exception")
                }

        firestore!!.collection("PinCount").orderBy("count", Query.Direction.DESCENDING)   // 작업할 컬렉션
            .get()      // 문서 가져오기
            .addOnSuccessListener { result ->
                // 성공할 경우
                PinList.clear()
                for (document in result) {  // 가져온 문서들은 result에 들어감
                    val item = CountEntity(document["count"].toString().toInt(), document["name"] as String)
                    PinList.add(item)
                }
                PinAdapter.notifyDataSetChanged()  // 리사이클러 뷰 갱신
            }
            .addOnFailureListener { exception ->
                // 실패할 경우
                Log.w("TAG", "Error getting documents: $exception")
            }







        return view

    }




}