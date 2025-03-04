package com.getepay.smartcitycheckin;


import android.app.Activity;
import android.app.Fragment;
import android.graphics.drawable.ColorDrawable;
import androidx.legacy.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class   NavigationDrawerFragment extends Fragment implements View.OnClickListener {

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private View mFragmentContainerView;

    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;

    private LinearLayout view;
    private LinearLayout homeItem;
    private TextView homeTitle;
    private LinearLayout listItem;
    private TextView listTitle;
    private LinearLayout settingsItem;
    private TextView settingsTitle;
    private LinearLayout infoItem;
    private TextView infoTitle;
    private LinearLayout signoutItem;
    private TextView signoutTitle;

    private ArrayList<LinearLayout> menuItems = new ArrayList<LinearLayout>();

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("DRAWER", "onCreate");
        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        // Select either the default item (0) or the last selected item.
        Globals.getPreferences(this.getActivity());
        if (!Globals.key.equals(""))
            mCurrentSelectedPosition = 0;
        else mCurrentSelectedPosition = 2;

        selectItem(mCurrentSelectedPosition);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        mDrawerListView = (ListView) inflater.inflate(
//                R.layout.fragment_navigation_drawer, container, false);
//        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                selectItem(position);
//            }
//        });
//        mDrawerListView.setAdapter(new ArrayAdapter<String>(
//                getActionBar().getThemedContext(),
//                android.R.layout.simple_list_item_activated_1,
//                android.R.id.text1,
//                new String[]{
//                        getString(R.string.title_section1),
//                        getString(R.string.title_section2),
//                        getString(R.string.title_section3),
//                }));
//        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
//        return mDrawerListView;
        view = (LinearLayout) inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        homeItem = (LinearLayout) view.findViewById(R.id.menu_home);
        homeTitle = (TextView) view.findViewById(R.id.menu_home_title);
        listItem = (LinearLayout) view.findViewById(R.id.menu_list);
        listTitle = (TextView) view.findViewById(R.id.menu_list_title);
        settingsItem = (LinearLayout) view.findViewById(R.id.menu_settings);
        settingsTitle = (TextView) view.findViewById(R.id.menu_settings_title);
        infoItem = (LinearLayout) view.findViewById(R.id.menu_info);
        infoTitle = (TextView) view.findViewById(R.id.menu_info_title);
        signoutItem = (LinearLayout) view.findViewById(R.id.menu_logout);
        signoutTitle = (TextView) view.findViewById(R.id.menu_logout_title);

        translate();

        homeItem.setOnClickListener(this);
        listItem.setOnClickListener(this);
        settingsItem.setOnClickListener(this);
        infoItem.setOnClickListener(this);
        signoutItem.setOnClickListener(this);

        menuItems.add(homeItem);
        menuItems.add(listItem);
        menuItems.add(settingsItem);
        menuItems.add(infoItem);
        menuItems.add(signoutItem);

        return view;
    }

    @Override
    public void onClick(View view) {
        for (int i=0; i<menuItems.size(); i++){
            if (menuItems.get(i) == (LinearLayout) view){
                selectItem(i);
            }
        }
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        androidx.appcompat.app.ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(0x252525));
        actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                false,
                R.drawable.menu_icon,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
                translate();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void translate(){
        if (homeTitle!=null && listTitle!=null && signoutTitle!=null) {
//            homeTitle.setText(Globals.getTranslation("HOME_STATS"));
            listTitle.setText(Globals.getTranslation("LIST"));
            //settingsTitle.setText(Globals.getTranslation("SETTINGS"));
            signoutTitle.setText(Globals.getTranslation("SIGN_OUT"));
        }
    }

    private void selectItem(int position) {
        Globals.getPreferences(this.getActivity());
        translate();
        if (Globals.key.equals("")) position = 2;

        mCurrentSelectedPosition = position;
        for (int i=0; i<menuItems.size(); i++){
            menuItems.get(i).setBackgroundDrawable(getResources().getDrawable(R.drawable.drawer_bg));
            if (i == position){
                menuItems.get(i).setBackgroundDrawable(getResources().getDrawable(R.drawable.pressed));
            }
        }

        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(mCurrentSelectedPosition);
        }

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
            translate();
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        //if (mDrawerLayout != null && isDrawerOpen()) {
            //inflater.inflate(R.menu.global, menu);
        showGlobalContextActionBar();
        //}
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        if (item.getItemId() == R.id.action_example) {
            Toast.makeText(getActivity(), "Example action.", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        androidx.appcompat.app.ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(androidx.appcompat.app.ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setIcon(R.drawable.menu_icon);
        actionBar.setBackgroundDrawable(new ColorDrawable(0x252525));
        //actionBar.setTitle();
    }

    private androidx.appcompat.app.ActionBar getActionBar() {
        return ((AppCompatActivity)getActivity()).getSupportActionBar();
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);
    }
}
