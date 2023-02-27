package com.sw.mobsale.online.ui;


import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.sw.mobsale.online.fragment.DetailFragment;
import com.sw.mobsale.online.fragment.FinishFragment;
import com.sw.mobsale.online.fragment.UnFinishFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * FragmentPagerAdapter
 */
public class MyPagerAdapter extends FragmentPagerAdapter {
    List<Fragment> list = new ArrayList<Fragment>();
    FragmentManager fm;
    List<String> tagList = new ArrayList<String>();

    public MyPagerAdapter(FragmentManager fm) {
        super(fm);
    }
    public MyPagerAdapter(FragmentManager fm, ArrayList<Fragment>list) {
        super(fm);
        this.list = list;
        this.fm = fm;
    }
    @Override
    public Fragment getItem(int i) {
        return list.get(i);

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        tagList.remove(makeFragmentName(container.getId(), getItemId(position)));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        tagList.add(makeFragmentName(container.getId(), getItemId(position)));
        return super.instantiateItem(container, position);
    }
//
//    /**
//     * //这个是真正的更新Fragment的内容
//     * @param position
//     */
//    public void update(int position){
//        Fragment fragment = fm.findFragmentByTag(tagList.get(position));
//        switch (position){
//            case 0:
//                break;
//            case 1:
//                ((DetailFragment)fragment).update();
//                break;
//            case 2:
//                break;
//        }
//    }
//
//    /**
//     * 这个是真正的更新Fragment的内容
//     * @param position
//     */
//    public void updateTwo(int position){
//        Fragment fragment = fm.findFragmentByTag(tagList.get(position));
//        switch (position){
//            case 0:
//                ((UnFinishFragment)fragment).update();
//                break;
//            case 1:
//                ((FinishFragment)fragment).update();
//                break;
//
//        }
//    }

    private static String makeFragmentName(int viewId, long id) {
        return "android:switcher:" + viewId + ":" + id;
    }

}
