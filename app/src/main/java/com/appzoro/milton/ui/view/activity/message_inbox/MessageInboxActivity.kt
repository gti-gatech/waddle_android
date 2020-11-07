package com.appzoro.milton.ui.view.activity.message_inbox

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.appzoro.milton.R
import com.appzoro.milton.base.BaseActivity
import com.appzoro.milton.base.MiltonApplication
import com.appzoro.milton.base.PreferenceManager
import com.appzoro.milton.databinding.ActivityMessageInboxBinding
import com.appzoro.milton.model.DialogMessage
import com.appzoro.milton.model.MessageListResponse
import com.appzoro.milton.model.MessageModel
import com.appzoro.milton.network.ApiEndPoint
import com.appzoro.milton.utility.AppLogger
import com.appzoro.milton.utility.Constant
import com.appzoro.milton.utility.Event
import com.appzoro.milton.utility.Utils
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_message_inbox.*
import org.json.JSONObject

class MessageInboxActivity : BaseActivity() {
    object NewInstance {
        fun getIntent(
            context: Context, mMessageModel: MessageModel
        ): Intent {
            val intent = Intent(context, MessageInboxActivity::class.java)
            intent.putExtra("mMessageModel", mMessageModel)
            return intent
        }
    }
    private var mAdapter: MessageInboxAdapter? = null
    private var mMessageList: ArrayList<MessageModel>? = null
    private lateinit var dataBinding: ActivityMessageInboxBinding
    private lateinit var mViewModel: MessageInboxViewModel
    private var mMessageModel: MessageModel? = null
    private var mActivity: Activity? = null
    private var mSharedPreferences: PreferenceManager? = null
    private var parentId: String? = null
    private var lastMessageId: Int = 0

    //for socket
    private var mSocket: Socket? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_message_inbox)
        mViewModel = ViewModelProviders.of(this).get(MessageInboxViewModel::class.java)
        dataBinding.messageInboxViewModel = mViewModel
        dataBinding.lifecycleOwner = this
        AppLogger.e("activity MessageInbox onCreate")
        initUi()
    }

    private fun initUi() {
        mActivity = this@MessageInboxActivity
        mSharedPreferences = PreferenceManager(mActivity!!)
        parentId = mSharedPreferences?.getString(Constant.parentId) ?: ""
        mRecyclerView?.setHasFixedSize(false)
        mAdapter = MessageInboxAdapter(this, parentId!!)
        mRecyclerView?.adapter = mAdapter
        if (intent.hasExtra("mMessageModel")) {
            mMessageModel = intent.getSerializableExtra("mMessageModel") as MessageModel
            val image = mMessageModel?.image ?: ""
            Utils.loadImage(mActivity!!, image, ivProfile, mProgress)
            tvGroupName.text = mMessageModel?.groupName ?: ""
            AppLogger.e("mMessageModel >> ${Gson().toJson(mMessageModel)}")
            mViewModel.callApiForGroupMessages((mMessageModel?.groupId ?: 0).toString())
        }

        imageViewBack.setOnClickListener {
            onBackPressed()
        }
        mSendChat.setOnClickListener {
            sendMessage()
        }
        manageCallbacks()
        Handler(Looper.getMainLooper()).postDelayed({
            connectSocketConnection()
        }, 500)

    }

    private fun manageCallbacks() {
        mViewModel.callback.observe(this, Observer { it ->
            it.getContentIfNotHandled()?.let {
                if (it is String) Utils.alertDialog(
                    this,
                    it
                ) else if (it is DialogMessage) Utils.alertDialog(
                    this,
                    it.message
                ) else if (it is Boolean) {
                    if (it) showLoading() else hideLoading()
                } else if (it is MessageListResponse) setMessageDataList(it.data) else AppLogger.e(" else ")
            }
        })

    }

    private fun connectSocketConnection() {
        val app = application as MiltonApplication
        mSocket = app.getSocket()
        mSocket?.on("connect") {
            AppLogger.e("MessageInbox connect >> ${mSocket?.connected()}")
            mSocket?.off("connect")
            manageSocketConnection()
        }

    }

    private fun manageSocketConnection() {
        val jsonObject = JSONObject()
        jsonObject.put("groupId", mMessageModel?.groupId ?: 0)
        jsonObject.put("parentId", parentId ?: "")
        mSocket?.emit(ApiEndPoint.joinMessageUpdates, jsonObject)
        mSocket?.on(ApiEndPoint.newMessageListener, mMessageListener())

        if ((mMessageModel?.totalUnRead ?: 0) > 0) readMessage((mMessageModel?.messageId ?: 0).toString())

    }

    private fun sendMessage() {
        AppLogger.e("mSocket connected >> ${mSocket?.connected()}")
        if (mSocket?.connected() != true) {
            if (Utils.isOnline(this)) {
                mSocket?.connect()
                val message = getString(R.string.server_not_connected_try_again_after)
                Utils.showToast(this, message)
            } else mViewModel.callbackData.value = Event(getString(R.string.no_internet_connection))

        } else {
            val message = mChatMessage.text.toString().trim()
            if (message.isEmpty()) {
                Utils.alertDialog(mActivity!!, getString(R.string.please_enter_your_message))
                return
            }
            val jsonObject = JSONObject()
            jsonObject.put("groupId", mMessageModel?.groupId ?: "")
            jsonObject.put("parentId", parentId ?: "")
            jsonObject.put("message", message)
            AppLogger.e("sendMessage jsonObject $jsonObject")
            mChatMessage.setText("")
            mSocket?.emit(ApiEndPoint.addMessage, jsonObject)

        }
    }

    private fun readMessage(messageId: String) {
        val jsonObject = JSONObject()
        jsonObject.put("groupId", mMessageModel?.groupId ?: "")
        jsonObject.put("parentId", parentId ?: "")
        jsonObject.put("messageId", messageId)
        AppLogger.e("readMessage jsonObject $jsonObject")
        mSocket?.emit(ApiEndPoint.readMessage, jsonObject)

    }

    /* getting new message*/
    private fun mMessageListener(): Emitter.Listener {
        return Emitter.Listener { args ->
            AppLogger.e("mChatMessageListener data size ${args.size} ")

            mActivity?.runOnUiThread {
                if (args.isNotEmpty()) {
                    try {
                        val mObject = JSONObject(args[0].toString())
                        AppLogger.e("mMessageListener jsonObject >> $mObject")
                        val messageId = mObject.optInt("messageId")
                        if (lastMessageId != messageId) {
                            lastMessageId = messageId
                            val groupId = mObject.optInt("groupId")
                            val senderId = mObject.optString("senderId")
                            val senderName = mObject.optString("fullName")
                            val message = mObject.optString("message")
                            val status = mObject.optString("status")
                            val createdOn = mObject.optString("createdOn")
                            val mMessageModel = MessageModel()
                            mMessageModel.messageId = messageId
                            mMessageModel.groupId = groupId
                            mMessageModel.senderId = senderId
                            mMessageModel.senderName = senderName
                            mMessageModel.message = message
                            mMessageModel.status = status
                            mMessageModel.createdOn = createdOn
                            mMessageList?.add(mMessageModel)
                            lastMessageId = messageId
                            mAdapter?.setList(mMessageList)
                            mAdapter?.notifyDataSetChanged()
                            if ((mMessageList?.size ?: 0) > 1) {
                                mRecyclerView?.scrollToPosition((mMessageList?.size ?: 0) - 1)
                            }
                            AppLogger.e("parentId $parentId senderId $senderId")
                            if (parentId != senderId) {
                                AppLogger.e("not equal")
                                readMessage(messageId.toString())
                            } else AppLogger.e("both equal")
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            }
        }
    }

    private fun setMessageDataList(dataList: ArrayList<MessageModel>?) {
        mMessageList = dataList ?: ArrayList()
        mAdapter?.setList(mMessageList)
        mAdapter?.notifyDataSetChanged()
        if ((mMessageList?.size ?: 0) > 1) mRecyclerView?.scrollToPosition((mMessageList?.size ?: 0) - 1)
    }

    override fun onDestroy() {
        mSocket?.disconnect()
        super.onDestroy()
    }

}


