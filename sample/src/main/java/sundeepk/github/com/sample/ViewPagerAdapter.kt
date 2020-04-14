package sundeepk.github.com.sample

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class ViewPagerAdapter(fm: FragmentManager?, var titles: Array<CharSequence>, var numbOfTabs: Int) : FragmentStatePagerAdapter(fm!!) {
    override fun getItem(position: Int): Fragment {
        return if (position == 0) {
            CompactCalendarTab()
        } else {
            Tab2()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titles[position]
    }

    override fun getCount(): Int {
        return numbOfTabs
    }

}