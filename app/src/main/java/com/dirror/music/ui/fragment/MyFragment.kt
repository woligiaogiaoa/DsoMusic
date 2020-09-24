package com.dirror.music.ui.fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dirror.music.CloudMusic
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.adapter.PlaylistAdapter
import com.dirror.music.data.UserDetailData
import com.dirror.music.data.UserPlaylistData
import com.dirror.music.ui.activity.LoginActivity
import com.dirror.music.ui.base.BaseFragment
import com.dirror.music.util.*
import kotlinx.android.synthetic.main.fragment_my.*

class MyFragment : BaseFragment() {
    override fun getLayoutId(): Int {
        return R.layout.fragment_my
    }

    override fun initData() {

    }

    override fun initView() {
        getUserDetail()


        getPlaylist()

    }

    override fun initListener() {
        itemAccount.setOnClickListener {
            val intent = Intent(context, LoginActivity::class.java)
            startActivityForResult(intent, 0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            0 -> if (resultCode == RESULT_OK) {
                val user = data?.getBooleanExtra("boolean_user", true)
                getUserDetail()
                getPlaylist()
            }
        }
    }

    override fun onStart() {
        super.onStart()

    }

    private fun getUserDetail() {
        CloudMusic.getLoginStatus {  }

        val uid = StorageUtil.getInt(StorageUtil.CLOUD_MUSIC_UID, -1)
        if (uid != -1) {
            CloudMusic.getUserDetail(StorageUtil.getInt(StorageUtil.CLOUD_MUSIC_UID, -1), {
                refreshUserDetail(it)
            }, {
                toast(it)
            })
        } else {
            toast("请先登录")
            // startActivity(Intent(context, LoginActivity::class.java))
        }
    }

    /**
     * 获取用户详细信息后刷新界面数据
     */
    private fun refreshUserDetail(userDetailData: UserDetailData) {
        runOnMainThread {
            Glide.with(MyApplication.context)
                .load(http2https(userDetailData.profile?.avatarUrl.toString()))
                .into(ivPhoto)
            tvNickname.text = userDetailData.profile?.nickname
            tvLevel.text = "Lv.${userDetailData.level}"
        }
    }

    private fun getPlaylist() {
        CloudMusic.getPlaylist(StorageUtil.getInt(StorageUtil.CLOUD_MUSIC_UID, -1), object : CloudMusic.PlaylistCallback {
            override fun success(userPlaylistData: UserPlaylistData) {

                val playlist = userPlaylistData.playlist
                runOnMainThread {
                    val linearLayoutManager: LinearLayoutManager =
                        object : LinearLayoutManager(context) {
                            override fun canScrollVertically(): Boolean {
                                return false
                            }

                            override fun onMeasure(
                                recycler: RecyclerView.Recycler,
                                state: RecyclerView.State,
                                widthSpec: Int,
                                heightSpec: Int
                            ) {
                                super.onMeasure(recycler, state, widthSpec, heightSpec)
                                setMeasuredDimension(widthSpec, (playlist.size * dp2px(64f)).toInt())
                            }
                        }

                    // rvPlaylist.setHasFixedSize(true)
                    // rvPlaylist.isNestedScrollingEnabled = false
                    rvPlaylist.layoutManager =  linearLayoutManager//FullyLinearLayoutManager(context)
                    rvPlaylist.adapter = PlaylistAdapter(playlist)


                }

            }

        })
    }

}