package ed.doron.pedometer;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import ed.doron.pedometer.ui.main.ProgressFragment;

public class SectionPagerAdapter extends FragmentPagerAdapter {

    SectionPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 1:
                return ProgressFragment.newInstance();
            //break;
            case 2:
                return ProgressFragment.newInstance();
            //break;
            default:
                return ProgressFragment.newInstance();
            //break;
        }
    }


    @Override
    public int getCount() {
        return 3;
    }
}