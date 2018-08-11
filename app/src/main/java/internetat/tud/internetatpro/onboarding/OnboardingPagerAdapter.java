package internetat.tud.internetatpro.onboarding;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class OnboardingPagerAdapter extends FragmentPagerAdapter{


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */


        private final int pageCount = 3;

        public OnboardingPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a OnboardingFragment (defined as a static inner class below).
            return OnboardingFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return pageCount;
        }

    }
