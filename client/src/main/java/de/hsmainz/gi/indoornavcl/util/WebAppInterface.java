/*
 * Copyright (C) 2015 Jan "KekS" M.
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */

package de.hsmainz.gi.indoornavcl.util;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;
import de.hsmainz.gi.indoornavcl.comm.Configuration;

/**
 * Interface so JavaScript can communicate with the App.
 *
 * @author Jan 'KekS' M. <a href='mailto:keks@keksfabrik.eu'>mail</a>, 12.02.15.
 */
public class WebAppInterface {
    Context mContext;

    /** Instantiate the interface and set the context */
    public WebAppInterface(Context c) {
        mContext = c;
    }

    /** Show a toast from the web page */
    @JavascriptInterface
    public void alert(String toast) {
        showToast(toast);
    }

    /** Show a toast from the web page */
    @JavascriptInterface
    public void showToast(String toast) {
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
    }

    /** Get the TileServer {@link de.hsmainz.gi.indoornavcl.comm.Configuration#getTileServer} from the Configuration */
    @JavascriptInterface
    public String getTileServer() {
        return Configuration.getTileServer();
    }
}