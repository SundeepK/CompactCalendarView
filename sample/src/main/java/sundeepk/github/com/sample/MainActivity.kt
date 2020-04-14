package sundeepk.github.com.sample

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import sundeepk.github.com.sample.SlidingTabLayout.TabColorizer


class MainActivity : AppCompatActivity() {
    private val titles = arrayOf<CharSequence>("Home", "Events")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.tool_bar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(true)
        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        val numberOfTabs = 2
        val adapter = ViewPagerAdapter(supportFragmentManager, titles, numberOfTabs)
        // Assigning ViewPager View and setting the adapter
        val pager = findViewById<ViewPager>(R.id.pager)
        pager.adapter = adapter
        // Assiging the Sliding Tab Layout View
        val tabs = findViewById<SlidingTabLayout>(R.id.tabs)
        tabs.setDistributeEvenly(true) // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width
        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(object : TabColorizer {
            override fun getIndicatorColor(position: Int): Int {
                return resources.getColor(R.color.black)
            }
        })
        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean { // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean { // Handle action bar item clicks here. The action bar will
// automatically handle clicks on the Home/Up button, so long
// as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)
    }
}