/*
 * Copyright 2013 JNRain
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.jnrain.mobile.ui.base;

import org.jnrain.mobile.ui.JNRainActivityHelper;
import org.jnrain.mobile.ui.OptionsMenuProvider;
import org.jnrain.mobile.util.SpicedRoboAccountAuthenticatorActivity;

import android.os.Bundle;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;


public abstract class JNRainAccountAuthenticatorActivity<T>
        extends SpicedRoboAccountAuthenticatorActivity<T> {
    JNRainActivityHelper _helper;

    public JNRainAccountAuthenticatorActivity() {
        super();

        _helper = new JNRainActivityHelper(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        JNRainActivityHelper.setUpActionBar(getSupportActionBar());
    }

    @Override
    protected void onStart() {
        _helper.doPreOnStart();
        super.onStart();
        _helper.doPostOnStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        OptionsMenuProvider optionsMenuProvider = OptionsMenuProvider
            .getOptionsMenuProvider();
        return optionsMenuProvider.createOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        OptionsMenuProvider optionsMenuProvider = OptionsMenuProvider
            .getOptionsMenuProvider();
        return optionsMenuProvider.optionsItemSelected(item, this);
    }
}
