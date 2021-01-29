package com.kevin.codelib.adapter

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.kevin.codelib.bean.AlbumData
import com.kevin.codelib.fragment.AlbumPreviewFragment

/**
 * Created by Kevin on 2021/1/27<br/>
 *
 * Blog:http://student9128.top/
 *
 * 公众号：前线开发者Kevin
 *
 * Describe:<br/>
 */
class AlbumPreviewAdapter(var data: ArrayList<AlbumData>, fm: FragmentManager, behavior: Int) :
    FragmentStatePagerAdapter(fm, behavior) {

    fun refreshData(d: ArrayList<AlbumData>) {
        data = d
        notifyDataSetChanged()
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        return super.instantiateItem(container, position)
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object`)
    }

    override fun getCount(): Int {
       return data.size
    }

    override fun getItem(position: Int): Fragment {
      return AlbumPreviewFragment.newInstance(data[position])
    }
}