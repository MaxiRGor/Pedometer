package ed.doron.pedometer.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import ed.doron.pedometer.ui.DiagramFragment;
import ed.doron.pedometer.ui.ProgressFragment;
import ed.doron.pedometer.ui.StatisticsFragment;

public class FragmentsPagerAdapter extends FragmentPagerAdapter {

    public FragmentsPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return ProgressFragment.newInstance();
            case 1:
                return StatisticsFragment.newInstance();
            default:
                return DiagramFragment.newInstance();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}