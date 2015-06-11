package bit.mazurdm1.atmos;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;


import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class MainActivity extends ActionBarActivity
{

    public static final int FRAGMENT_ONE = 0;
    public static final int FRAGMENT_TWO = 1;
    public static final int FRAGMENT_THREE = 2;
    public static final int FRAGMENTS = 3;
    private FragmentPagerAdapter _fragmentPagerAdapter;
    private ViewPager _viewPager;

    /**
     * List of fragments.
     */
    private List<Fragment> _fragments = new ArrayList<Fragment>();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Create fragments.
        _fragments.add(FRAGMENT_ONE, new Fragment_a());
        _fragments.add(FRAGMENT_TWO, new Fragment_b());
        _fragments.add(FRAGMENT_THREE, new Fragment_c());

        // Setup the fragments, defining the number of fragments, the screens and titles.
        _fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()){
            @Override
            public int getCount() {
                return FRAGMENTS;
            }
            @Override
            public android.support.v4.app.Fragment getItem(final int position) {
                return _fragments.get(position);
            }
            @Override
            public CharSequence getPageTitle(final int position) {
                switch (position) {
                    case FRAGMENT_ONE:
                        return "Take Readings";
                    case FRAGMENT_TWO:
                        return "View Saved Readings";
                    case FRAGMENT_THREE:
                        return "Temprature Graph";
                    default:
                        return null;
                }
            }
        };
        _viewPager = (ViewPager) findViewById(R.id.pager);
        _viewPager.setOffscreenPageLimit(2);
        _viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(final int i, final float v, final int i2) {
            }
            @Override
            public void onPageSelected(final int i) {
                FragmentHasBecomeVisible fragment = (FragmentHasBecomeVisible) _fragmentPagerAdapter.instantiateItem(_viewPager, i);
                if (fragment != null) {
                    fragment.isNowVisible();
                }
            }
            @Override
            public void onPageScrollStateChanged(final int i) {
            }
        });
        _viewPager.setAdapter(_fragmentPagerAdapter);

    }

}