package com.softwarelogistics.oshgeo.poc.activities;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;

import com.softwarelogistics.oshgeo.poc.R;

public class AppCompatActivityBase extends AppCompatActivity {

    protected void applyFontToMenuItem(final MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/fa-regular-400.ttf");
        Button btn = (Button) mi.getActionView();
        btn.setTypeface(font);
        btn.setText(getResources().getString(R.string.icon_add));
        btn.setTextColor(Color.WHITE);
        btn.setBackgroundColor(Color.TRANSPARENT);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onOptionsItemSelected(mi);
            }
        });
    }

    protected void setMenuItemFontIcon(Menu menu) {
        for (int i=0;i<menu.size();i++) {
            MenuItem mi = menu.getItem(i);
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }

            applyFontToMenuItem(mi);
        }
    }
}
