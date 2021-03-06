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
package org.jnrain.mobile.accounts.kbs;

import org.jnrain.kbs.entity.SimpleReturnCode;
import org.jnrain.mobile.R;
import org.jnrain.mobile.network.listeners.ContextRequestListener;
import org.jnrain.mobile.ui.base.LoginPoint;
import org.jnrain.mobile.ui.ux.ToastHelper;

import android.accounts.Account;
import android.app.Activity;
import android.util.Log;

import com.octo.android.robospice.persistence.exception.SpiceException;


public class KBSLoginRequestListener
        extends ContextRequestListener<SimpleReturnCode> {
    private static final String TAG = "LoginRequestListener";
    private LoginPoint _ui;
    private Account _account;
    private String _uid;
    private String _psw;

    public KBSLoginRequestListener(
            Activity activity,
            Account account,
            String uid,
            String psw) {
        super(activity);
        _ui = (LoginPoint) activity;
        _account = account;
        _uid = uid;
        _psw = psw;
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        _ui.getLoadingDialog().dismiss();
        Log.d(TAG, "err on req: " + spiceException.toString());
        ToastHelper.makeTextToast(ctx, R.string.msg_network_fail);
    }

    @Override
    public void onRequestSuccess(SimpleReturnCode result) {
        _ui.getLoadingDialog().dismiss();
        int status = result.getStatus();

        switch (status) {
            case 0:
                // callback into activity for code simplicity
                _ui.onAuthenticationSuccess(_account, _uid, _psw);
                break;
            case 1:
                ToastHelper.makeTextToast(ctx, R.string.msg_login_fail);
                break;
            case 2:
                ToastHelper.makeTextToast(ctx, R.string.msg_login_no_uid);
                break;
            case 3:
                ToastHelper.makeTextToast(ctx, R.string.msg_login_toomany);
                break;
            case 4:
                ToastHelper.makeTextToast(ctx, R.string.msg_login_disabled);
                break;
            case 5:
                ToastHelper.makeTextToast(ctx, R.string.msg_login_ip_denied);
                break;
            case 6:
                ToastHelper.makeTextToast(ctx, R.string.msg_login_freq);
                break;
            case 7:
                ToastHelper.makeTextToast(ctx, R.string.msg_login_capacity);
                break;
            case 8:
                ToastHelper.makeTextToast(ctx, R.string.msg_login_ipacl);
                break;
            default:
                ToastHelper.makeTextToast(
                        ctx,
                        R.string.msg_unknown_status,
                        status);
                break;
        }
    }
}
