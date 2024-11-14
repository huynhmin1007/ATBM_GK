package ui.view.activity;

import ui.view.custom.BaseActivity;

public class ASymmetricActivity extends BaseActivity {
    private static ASymmetricActivity INSTANCE;

    public static ASymmetricActivity getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ASymmetricActivity();
        }

        return INSTANCE;
    }

    public ASymmetricActivity() {

    }
}
