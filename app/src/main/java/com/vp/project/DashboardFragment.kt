package com.vp.project

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.vp.project.databinding.FragmentDashboardBinding
import com.vp.project.model.UserInfoDownloadEntity

class DashboardFragment : Fragment() {

    // This property is only valid between onCreateView and
    // onDestroyView.
    private var firestore : FirebaseFirestore? = null
    private var auth : FirebaseAuth? = null
    private var binding: FragmentDashboardBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.

    val UserList = arrayListOf<UserInfoDownloadEntity>()    // 아이템 배열
    val UserAdapter = DashUserRecyclerAdapter(UserList)         // 리사이클러 뷰 어댑터

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDashboardBinding.inflate(layoutInflater)
        val v = binding!!.root
        binding!!.UserRV.adapter = UserAdapter

        val followsView  = v.findViewById<TextView>(R.id.Follows_Textview)
        val followersView  = v.findViewById<TextView>(R.id.Followers_Textview)
        val pinsView  = v.findViewById<TextView>(R.id.Pin_Textview)
        val nameView = v.findViewById<TextView>(R.id.Name_Textview)

        firestore = FirebaseFirestore.getInstance()
        auth = Firebase.auth
        val uid = auth?.currentUser?.uid
        var follows :String = "0"
        var followers :String = "0"
        var name :String = "0"
        var pins :Int = 0


        val db = firestore?.collection("UserCount")!!.document(uid.toString())
        db.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    follows = document.get("follows").toString()
                    followers = document.get("followers").toString()
                    name = document.get("name").toString()
                    nameView?.text = name
                    followersView.text = followers
                    followsView.text = follows
                } else {
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }

        val db2 = firestore!!.collection("Users").document(uid!!).collection("Pins")
        db2.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                task.result?.apply {
                    pins = size()
                    pinsView.text = pins.toString()
                }
            } else {
                task.exception?.message?.let {
                    print(it)
                }
            }
        }

        val button = v.findViewById<Button>(R.id.UserSearch_Button)
        button.setOnClickListener {
            firestore!!.collection("Users").whereNotEqualTo("name",name)
                .get()      // 문서 가져오기
                .addOnSuccessListener { result ->
                    // 성공할 경우
                    UserList.clear()
                    for (document in result) {  // 가져온 문서들은 result에 들어감
                        val item = UserInfoDownloadEntity(document["uid"] as String, document["text"] as String, document["name"] as String, document["username"] as String, document["email"] as String)
                        UserList.add(item)
                    }
                    UserAdapter.notifyDataSetChanged()  // 리사이클러 뷰 갱신
                }
                .addOnFailureListener { exception ->
                    // 실패할 경우
                    Log.w("TAG", "Error getting documents: $exception")
                }
        }

            UserAdapter.setOnItemClickListener(object: DashUserRecyclerAdapter.OnItemClickListener{
            override fun onItemClick(itemView: View, position: Int) {
                setFragmentResult("requestKey", bundleOf("bundleKey" to UserList[position].uid.toString()))
                parentFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment_activity_main, UserSearchFragment())
                    .commit()

                //intent.putExtra("searchData", dataset.get(position))
                //startActivity(intent)
            }
        })
        return v
    }
}