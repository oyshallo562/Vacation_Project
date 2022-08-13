package com.vp.project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.vp.project.model.UserCountEntity
import com.vp.project.model.UserInfoUploadEntity

class UserInfoActivity : AppCompatActivity() {
    private var firestore : FirebaseFirestore? = null
    var auth : FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)
        val UserNameEdittext = findViewById<EditText>(R.id.UserName_Edittext)
        val UserInfoEdittext = findViewById<EditText>(R.id.UserInfo_Edittext)
        val NameEdittext = findViewById<EditText>(R.id.Name_Edittext)
        var UserInfoUploadDTO = UserInfoUploadEntity()
        var UserCountDTO = UserCountEntity()
        val button = findViewById<Button>(R.id.UserInfo_Button)
        auth = Firebase.auth
        firestore = Firebase.firestore

        button.setOnClickListener {
            UserInfoUploadDTO.uid = auth?.currentUser?.uid
            UserInfoUploadDTO.email = auth?.currentUser?.email
            UserInfoUploadDTO.text = UserInfoEdittext.text.toString()
            UserInfoUploadDTO.username = UserNameEdittext.text.toString()
            UserInfoUploadDTO.name = NameEdittext.text.toString()
            UserCountDTO.name = UserNameEdittext.text.toString()
            UserCountDTO.followers = 0
            UserCountDTO.follows = 0

            if(UserInfoUploadDTO.name != null && UserInfoUploadDTO.username != null ){
                firestore!!.collection("UserCount")?.document(UserInfoUploadDTO.uid.toString())?.set(UserCountDTO)
                firestore!!.collection("Users")?.document(UserInfoUploadDTO.uid.toString())?.set(UserInfoUploadDTO)
                startActivity(Intent(this,MainActivity::class.java))
                finish()
            }
            else Toast.makeText(this.applicationContext,"입력값이 잘못되었습니다.",Toast.LENGTH_LONG)
        }

    }

}