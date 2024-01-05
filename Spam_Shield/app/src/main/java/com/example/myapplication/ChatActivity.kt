package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class ChatActivity: AppCompatActivity() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendButton: ImageView

    // Initialise adapter
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>

    private lateinit var mDbRef: DatabaseReference

    // Create unique room for sender and reciever so that no one else can read it
    var recieverRoom: String? = null
    var senderRoom: String? = null

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val name = intent.getStringExtra("name")
        val receiverUid = intent.getStringExtra("uid")

        mDbRef = FirebaseDatabase.getInstance().getReference()


        val senderUid = FirebaseAuth.getInstance().currentUser?.uid

        senderRoom = receiverUid + senderUid
        recieverRoom = senderUid + receiverUid


        supportActionBar?.title = name // Set the title as the name

        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageBox = findViewById(R.id.messageBox)
        sendButton = findViewById(R.id.sentButton)

        messageList = ArrayList()
        messageAdapter = MessageAdapter(this, messageList)

        // We need to set layout manager to recycler view and layout manager to it as well
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = messageAdapter

        // Logic to add data to the recycler view
        mDbRef.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    messageList.clear()

                    // Loop through messages in snapshot
                    for(postSnapshot in snapshot.children){
                        val message = postSnapshot.getValue(Message::class.java)
                        messageList.add(message!!)
                    }
                    // Notify the adapter
                    messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })


        // When send is clicked, send the message to database
        sendButton.setOnClickListener {

            val message = messageBox.text.toString() // Get the message which is in the message box
            val messageObject = Message(message, senderUid)

            mDbRef.child("chats").child(senderRoom!!).child("messages").push()
                .setValue(messageObject).addOnSuccessListener {
                    // If successful, we need to update reciever UI as well
                    mDbRef.child("chats").child(recieverRoom!!).child("messages").push()
                        .setValue(messageObject)
                }
            messageBox.setText("") // Clear the typing area

        }

        val reportButton: Button = findViewById(R.id.reportButton)
        reportButton.setOnClickListener {
            // Get the last message from the messageList
            val lastMessage: Message? = messageList.lastOrNull { it.senderId != senderUid }

            if (lastMessage != null) {
                mDbRef.child("reports").push().setValue(lastMessage).addOnSuccessListener {
                    // Create a popup dialog to inform the user that they have been reported
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage("User reported!")
                    builder.setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                    val dialog = builder.create()
                    dialog.show()
                }
            }
        }
    }
}