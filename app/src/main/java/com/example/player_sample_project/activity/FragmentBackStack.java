package com.example.player_sample_project.activity;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Used this Class{FragmentBackStack} to handle the backstack in bottomNavigation
 * */
public final class FragmentBackStack {
    private HashMap<String, Fragment> hashMap = new HashMap<>();
    private ArrayList<String> arrayList = new ArrayList<String>();
    private String CURRENT_TAG =null;
    private int bottomMaxCount;
    private int container_id;
    private String lastFragToStay;
    private FragmentManager supportFragmentManager;
    public static final String CHILD_TAG = "child";
    public static final String DO_BACK_STACK = "dobackstack";
    private FragmentBackStack() {}

    public static class Builder {
        private int bottomMaxCount = 5;
        private int container_id = -1;
        private String lastFragToStay;
        private FragmentBackStack fragmentStackNew = null;

        public Builder bottomMaxCount(int bottomMaxCount) {
            this.bottomMaxCount = bottomMaxCount;
            return this;
        }

        public Builder container_id(int container_id) {
            this.container_id = container_id;
            return this;
        }

        public Builder lastFragmentToStay(@NonNull Class<?> modelClass) {
            this.lastFragToStay = modelClass.getCanonicalName();
            return this;
        }

        public FragmentBackStack build(FragmentActivity context) {
            if (fragmentStackNew == null) { fragmentStackNew=new FragmentBackStack(); }

            fragmentStackNew.bottomMaxCount = bottomMaxCount;
            fragmentStackNew.container_id = container_id;
            fragmentStackNew.lastFragToStay = lastFragToStay;
            fragmentStackNew.supportFragmentManager = context.getSupportFragmentManager();
            return fragmentStackNew; //return instance of FragmentStackNew Instance
        }
    }

    public void updateFrag(@NonNull Class<?> modelClass, Bundle extraData) {
        if (hashMap.get(modelClass.getCanonicalName()) == null) {
            try {
                Fragment instance= (Fragment) modelClass.newInstance();
                if (extraData != null) { instance.setArguments(extraData); }
                hashMap.put(modelClass.getCanonicalName(),instance);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) { e.printStackTrace(); }
        }

        if (CURRENT_TAG != null && CURRENT_TAG.equals(modelClass.getCanonicalName())) {
            return;
        }

        Fragment prev_fragment=supportFragmentManager.findFragmentByTag(modelClass.getCanonicalName());
        if (prev_fragment==null) { // if fragment is not present in stack
            if (hashMap.get(CURRENT_TAG) == null) {
                supportFragmentManager.beginTransaction()
                        .add(container_id, hashMap.get(modelClass.getCanonicalName()), modelClass.getCanonicalName())
                        .commit();
            } else {
                supportFragmentManager.beginTransaction()
                        .hide(hashMap.get(CURRENT_TAG))
                        .add(container_id, hashMap.get(modelClass.getCanonicalName()), modelClass.getCanonicalName())
                        .commit();
            }
        } else { // if fragment is already added
            supportFragmentManager.beginTransaction().hide(hashMap.get(CURRENT_TAG)).show(prev_fragment).commit();
        }
        CURRENT_TAG = modelClass.getCanonicalName();
    }

    public String backHandling() {
        Fragment currentFrag = supportFragmentManager.findFragmentByTag(CURRENT_TAG);
        if (currentFrag != null && currentFrag.getChildFragmentManager() != null && currentFrag.getChildFragmentManager().getBackStackEntryCount() > 0) {
            currentFrag.getChildFragmentManager().popBackStackImmediate();
            return CHILD_TAG;
        } else {
            String TAG = pop(CURRENT_TAG);
            if (TAG != null) {
                Fragment fragment = supportFragmentManager.findFragmentByTag(TAG);
                supportFragmentManager.beginTransaction().hide(hashMap.get(CURRENT_TAG)).show(fragment).commit();
                CURRENT_TAG = TAG;
                return CURRENT_TAG;
            } else {
                if (CURRENT_TAG.equals(lastFragToStay)) {
                    return DO_BACK_STACK;
                } else {
                    Fragment lastFrag = supportFragmentManager.findFragmentByTag(lastFragToStay);
                    supportFragmentManager.beginTransaction().hide(hashMap.get(CURRENT_TAG)).show(lastFrag).commit();
                    CURRENT_TAG = lastFragToStay;
                    return CURRENT_TAG;
                }
            }
        }
    }

    private String pop(String tag) {
        if (remove(tag)) {
            if (arrayList.size() == 0) return null;
            else return arrayList.get(arrayList.size() - 1);
        } else { return null; }
    }

    private Boolean remove( String tag) {
        try {
            int index_value = arrayList.indexOf(tag);
            arrayList.remove(index_value);
        } catch (Exception e) { return false; }
        return true;
    }

    public void savedInstanceState(Bundle savedInstanceState) {
        ArrayList<String> stack = savedInstanceState.getStringArrayList("fragStack");
        for (int i = 0; i <= stack.size() - 1; i++) {
            Fragment fragment = supportFragmentManager.findFragmentByTag(stack.get(i));
            if (fragment != null) {
                hashMap.put(stack.get(i),fragment);
                supportFragmentManager.beginTransaction().remove(fragment).commitNow();
                updateFrag(fragment.getClass(),null);
            }
        }
    }
}
