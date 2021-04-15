package com.stonecoders.stockinteligenterfid.room.viewmodels

import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rscja.deviceapi.RFIDWithUHFBLE
import com.rscja.deviceapi.entity.UHFTAGInfo
import com.stonecoders.stockinteligenterfid.entities.TagInfo
import com.stonecoders.stockinteligenterfid.fragments.FragmentReadElements
import com.stonecoders.stockinteligenterfid.fragments.ReadingFlags
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TagViewModel : ViewModel() {

    private val _tagList: MutableLiveData<MutableList<TagInfo>> =
            MutableLiveData(mutableListOf())
    val tagList get() : MutableLiveData<MutableList<TagInfo>> = _tagList
    private var loopFlag = false
    var isScanning = false


    init {
        val uhf = RFIDWithUHFBLE.getInstance()
        //TODO: Set filter with uhf.setFilter()
    }

    fun clearData() {
        _tagList.value = mutableListOf()
    }

    fun stopScan() {
        loopFlag = false
        isScanning = false
        val uhf = RFIDWithUHFBLE.getInstance()
        uhf.stopInventory()

    }


    suspend fun startScan(): ReadingFlags {
        loopFlag = !loopFlag
        val uhf = RFIDWithUHFBLE.getInstance()
        uhf.startInventoryTag()
        loopFlag = true
        isScanning = true

        withContext(Dispatchers.IO) {
            while (loopFlag) {

                isScanning = true
                try {
                    val temporal = _tagList.value
                    temporal?.addAll(uhf.readTagFromBufferList().map {
                        TagInfo.fromLegacyTag(it)
                    })
                    _tagList.postValue(temporal?.distinct()?.toMutableList())
                    SystemClock.sleep(1)
                } catch (e: NullPointerException) {
                    Log.d(FragmentReadElements.TAG, e.toString())
                }

                SystemClock.sleep(1)

            }

        }

        return ReadingFlags.FLAG_START
    }

    fun scanSingleTag() {
        viewModelScope.launch {
            val uhf = RFIDWithUHFBLE.getInstance()
            if (isScanning) {
                uhf.stopInventory()
            }
            try {
                val uhftagInfo: UHFTAGInfo = uhf.inventorySingleTag()
                val newTag = TagInfo.fromLegacyTag(uhftagInfo)
                newTag.epc?.let { Log.d(FragmentReadElements.TAG, it) }
                if (_tagList.value?.contains(newTag) == false) {
                    _tagList.value?.add(newTag)
                    tagList.value = _tagList.value
                    Log.d(FragmentReadElements.TAG, _tagList.value?.size.toString())
                }

            } catch (npe: NullPointerException) {
                npe.message?.let { it1 -> Log.e(FragmentReadElements.TAG, it1) }
            }
        }
    }

}