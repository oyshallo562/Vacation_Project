package com.vp.project

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.vp.project.databinding.FragmentSearchConfirmBinding
import com.vp.project.model.CountEntity
import com.vp.project.model.PlaceUploadEntity
import com.vp.project.model.SearchResultEntity

class SearchConfirmFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //val a : SearchResultEntity? = arguments?.getParcelable("Key")
        //Log.d("data : ", a?.name.toString())
    }

    private var _binding: FragmentSearchConfirmBinding? = null
    private val binding get() = _binding!!
    private var firestore : FirebaseFirestore? = null
    var auth : FirebaseAuth? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        _binding = FragmentSearchConfirmBinding.inflate(inflater, container, false)
        val view = binding.root

        // Inflate the layout for this fragment
        val button = view?.findViewById<Button>(R.id.Confirm_Button)

        button?.setOnClickListener {
            val ContexEditText = view.findViewById<EditText>(R.id.Context_Edittext)
            var PlaceUploadDTO = PlaceUploadEntity()
            var PlaceCountDTO = CountEntity()
            auth = Firebase.auth

            setFragmentResultListener("requestKey") { requestKey, bundle ->
                val a = bundle.getParcelable<SearchResultEntity>("bundleKey")
                PlaceUploadDTO.latitude = a?.locationLatLng?.latitude
                PlaceUploadDTO.longitude = a?.locationLatLng?.longitude
                PlaceUploadDTO.name = a?.name
                PlaceCountDTO.name = a?.name
            }

            PlaceUploadDTO.uid = auth?.currentUser?.uid
            PlaceUploadDTO.text = ContexEditText.text.toString()
            PlaceCountDTO.count = 1

            firestore = Firebase.firestore
            firestore!!.collection("Users")?.document(PlaceUploadDTO.uid.toString())
                .collection("Pins").document(PlaceCountDTO.name.toString()).set(PlaceUploadDTO)

            var countDB = firestore!!.collection("PinCount").document(PlaceCountDTO.name.toString())
            countDB.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                         var count :Int = document.data?.get("count").toString().toInt() as Int
                        count = count + 1
                        PlaceCountDTO.count = count
                        firestore!!.collection("PinCount")?.document(PlaceCountDTO.name.toString())?.set(PlaceCountDTO)
                    } else {
                        firestore!!.collection("PinCount")?.document(PlaceCountDTO.name.toString())?.set(PlaceCountDTO)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("TAG", "get failed with ", exception)
                }

            //setFragmentResult("requestKey2", bundleOf("bundleKey2" to 1))
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, SearchFragment())
                .commit()

            //parentFragmentManager.commit {
            //    replace<SearchFragment>(R.id.nav_host_fragment_activity_main)
            //    setReorderingAllowed(true)
                //addToBackStack("name") // name can be null
            //}

        }
        return view
    }
}