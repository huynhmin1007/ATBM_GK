package ui.view.activity;

import ui.view.custom.BaseActivity;

public class HashActivity extends BaseActivity {
    private static HashActivity INSTANCE;

    public static HashActivity getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HashActivity();
        }

        return INSTANCE;
    }

    public HashActivity() {

    }
}
