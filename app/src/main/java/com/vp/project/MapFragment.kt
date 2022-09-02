package com.vp.project

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mapView : MapView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_map,container,false)
        mapView = rootView.findViewById(R.id.mapview) as MapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        return rootView
    }

    override fun onMapReady(map: GoogleMap) {
        var followPinList = ArrayList<String>()
        var follows : MutableMap<String, Boolean> = HashMap()
        val auth = Firebase.auth
        val firestore = Firebase.firestore
        map.moveCamera(CameraUpdateFactory.newLatLng(LatLng(37.52487, 126.92723)))

        var DB = firestore?.collection("follow")?.document(auth?.currentUser?.uid.toString())

        DB.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    follows = document.data?.get("followings") as MutableMap<String, Boolean>
                    //follows.put(auth?.currentUser?.uid.toString(), true)
                    //follows = document.data?.get("followers") as MutableMap<String, Boolean>
                    val tmp = follows.forEach {
                        var tmpuid = it.key
                        //tmp.substring(1 until tmp.length-1)
                        followPinList?.add(tmpuid)
                    }


                    followPinList?.add(auth.currentUser?.uid.toString())
                    //Log.d("for!!!!",followPinList[0].toString())
                    for(i in 0 until followPinList.size) {
                        //Toast.makeText(context, i.toString(), Toast.LENGTH_LONG).show()
                        //Log.d("for!!!!",i.toString())

                        var DB = firestore?.collection("Users")?.document(followPinList[i]).collection("Pins")
                        //Toast.makeText(context,i.toString(),Toast.LENGTH_LONG).show()
                        DB.get()
                            .addOnSuccessListener { documents ->
                                for(document in documents) {
                                    val latitude = document.data.get("latitude")
                                    val longitude = document.data.get("longitude")
                                    val name = document.data.get("name")
                                    val text = document.data.get("text")
                                    val point = LatLng(latitude.toString().toDouble(), longitude.toString().toDouble())
                                    map.addMarker(
                                        MarkerOptions()
                                        .position(point)
                                        .title(name.toString())
                                        .snippet(text.toString())
                                        //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                                        //.alpha(0.5f)
                                    )
                                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(point,12f))
                                    Log.d("GET", "get")
                                }
                            }
                            .addOnFailureListener { exception ->
                                Log.d("TAG", "get failed with ", exception)
                            }
                    }


                    //Log.d("TAG", follows.keys.toString())
                } else {
                    Log.d("TAG", "no db")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }
}
