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
package org.jnrain.mobile.ui.ux;

import org.jnrain.mobile.R;
import org.jnrain.mobile.accounts.kbs.KBSLogoutRequest;
import org.jnrain.mobile.accounts.kbs.KBSLogoutRequestListener;
import org.jnrain.mobile.config.ConfigConstants.ExitBehavior;
import org.jnrain.mobile.config.ConfigHub;
import org.jnrain.mobile.config.UIConfigUtil;
import org.jnrain.mobile.ui.base.JNRainSlidingFragmentActivity;
import org.jnrain.mobile.util.AccountStateListener;

import roboguice.inject.InjectResource;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemClock;

import com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivityHelper.SlidingActivityBackAction;


public class ExitPointActivity<T> extends JNRainSlidingFragmentActivity<T>
        implements AccountStateListener {
    // private static final String TAG = "ExitPoint";

    @InjectResource(R.string.dlg_exit_confirm_title)
    String _exitDlgTitle;
    @InjectResource(R.string.dlg_exit_confirm_msg)
    String _exitDlgMsg;
    @InjectResource(R.string.dlg_exit_confirm_yes)
    String _exitDlgYes;
    @InjectResource(R.string.dlg_exit_confirm_no)
    String _exitDlgNo;

    protected long lastBackPressed = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Renren-like back to menu first behavior
        setBackAction(SlidingActivityBackAction.BACK_TO_MENU);
    }

    @Override
    public void onBackPressed() {
        // invoke user-selected exit behavior
        UIConfigUtil uiUtil = ConfigHub.getUIUtil(getApplicationContext());
        ExitBehavior exitBehavior = uiUtil.getExitBehavior();

        switch (exitBehavior) {
            case DIRECT:
                // Directly exit w/o confirmation.
                doExit();
                break;

            case DOUBLECLICK:
                long timeout = uiUtil.getExitDoubleclickTimeout();
                long curtime = SystemClock.elapsedRealtime();
                long delay = curtime - lastBackPressed;

                // update last press time
                lastBackPressed = curtime;

                if (delay < timeout * 1000) {
                    // Double click happened, trigger exit.
                    doExit();
                } else {
                    // Toast the confirmation message.
                    ToastHelper.makeTextToast(
                            getApplicationContext(),
                            R.string.msg_exit_doubleclick,
                            timeout);
                }

                break;

            case DIALOG:
                // Fire up a confirmation dialog.
                new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(_exitDlgTitle)
                    .setMessage(_exitDlgMsg)
                    .setPositiveButton(
                            _exitDlgYes,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(
                                        DialogInterface dialog,
                                        int which) {
                                    doExit();
                                }
                            })
                    .setNegativeButton(_exitDlgNo, null)
                    .show();

                break;

            default:
                // Should not happen; let's treat it as a direct exit.
                doExit();
                break;
        }
    }

    protected void doExit() {
        // this SHOULD be the last activity on the task stack. logout
        // super.onBackPressed() is called via shim in listener
        spiceManager.execute(
                new KBSLogoutRequest(),
                new KBSLogoutRequestListener(this, false));
    }

    @Override
    public void onAccountLoggedIn(String uid) {
        // intentionally left blank, since this is an activity that deals
        // with logouts
    }

    @Override
    public void onAccountLoggedOut() {
        // Equivalent to a delayed "press" of Back button d-:
        // super.onBackPressed();
        finish();
    }
}
