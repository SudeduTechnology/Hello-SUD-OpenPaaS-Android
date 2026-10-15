package global.sud.gi.hello.app.ui.main;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.EditText;
import android.widget.CheckBox;

import global.sud.gi.hello.R;
import global.sud.gi.hello.app.common.base.BaseActivity;
import global.sud.gi.hello.app.common.utils.SUDGILanguageConfig;

/** 主动切换 Demo 语言，以验证 Wrapper 默认文案。 */
public class SUDGILanguageActivity extends BaseActivity {

    private ListView languageList;
    private EditText customLanguage;
    private CheckBox customCheck;

    @Override
    protected int getLayoutId() {
        return R.layout.sud_gi_language_settings;
    }

    @Override
    protected void initWidget() {
        languageList = findViewById(R.id.sud_gi_language_list);
        languageList.setAdapter(new ArrayAdapter<>(this, R.layout.sud_gi_language_item,
                SUDGILanguageConfig.getLabels(this)));
        customLanguage = findViewById(R.id.sud_gi_language_custom_input);
        customCheck = findViewById(R.id.sud_gi_language_custom_check);
        customLanguage.setText(SUDGILanguageConfig.getCustomLanguageTag());
        int selectedIndex = SUDGILanguageConfig.getSelectedIndex();
        if (selectedIndex >= 0) {
            languageList.setItemChecked(selectedIndex, true);
        }
        customCheck.setChecked(selectedIndex < 0);
    }

    @Override
    protected void setListeners() {
        findViewById(R.id.sud_gi_language_back).setOnClickListener(view -> finish());
        languageList.setOnItemClickListener((parent, view, position, id) -> {
            customCheck.setChecked(false);
            SUDGILanguageConfig.selectLanguage(position);
        });
        customCheck.setOnClickListener(view -> {
            String tag = customLanguage.getText().toString().trim();
            if (tag.isEmpty()) {
                customCheck.setChecked(SUDGILanguageConfig.getSelectedIndex() < 0);
                customLanguage.setError(getString(R.string.sud_gi_language_custom_empty));
                return;
            }
            try {
                SUDGILanguageConfig.selectCustomLanguage(tag);
                customLanguage.setError(null);
                languageList.clearChoices();
                languageList.requestLayout();
                customCheck.setChecked(true);
            } catch (IllegalArgumentException e) {
                customCheck.setChecked(SUDGILanguageConfig.getSelectedIndex() < 0);
                customLanguage.setError(getString(R.string.sud_gi_language_custom_invalid));
            }
        });
    }
}
